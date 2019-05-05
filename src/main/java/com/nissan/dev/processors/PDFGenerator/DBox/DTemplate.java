package com.nissan.dev.processors.PDFGenerator.DBox;

import org.apache.commons.collections4.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.commons.collections4.multimap.*;
import com.fasterxml.jackson.core.*;
import java.awt.*;
import com.fasterxml.jackson.databind.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class DTemplate
{
    private MultiValuedMap<String, DBox> m_boxList;
    private PDDocument m_doc;
    private PDPage m_page;
    private LinkedHashMap<Integer, PDPage> m_pageList;
    private PDType0Font m_font;

    public DTemplate(final PDDocument doc) {
        this.m_doc = doc;
        this.m_boxList = (MultiValuedMap<String, DBox>)new ArrayListValuedHashMap();
        this.m_pageList = new LinkedHashMap<Integer, PDPage>();
    }

    public DTemplate(final PDDocument doc, final String fontFile) {
        this.m_doc = doc;
        try {
            this.m_font = PDType0Font.load(doc, new File(fontFile));
        }
        catch (IOException ex) {}
        this.m_boxList = (MultiValuedMap<String, DBox>)new ArrayListValuedHashMap();
        this.m_pageList = new LinkedHashMap<Integer, PDPage>();
    }

    public void parse(final String filePath) {
        try {
            System.out.println("--->" + filePath);
            final JsonFactory jsonFactory = (JsonFactory)new MappingJsonFactory();
            final JsonParser jsonParser = jsonFactory.createParser(new File(filePath));
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                final String fieldName = jsonParser.getCurrentName();
                if ("templateName".equals(fieldName)) {
                    jsonParser.nextToken();
                    System.out.println(fieldName + "--->" + jsonParser.getValueAsString());
                }
                if ("objList".equals(fieldName)) {
                    System.out.println(fieldName + "--->");
                    if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                        continue;
                    }
                    while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                        final JsonNode rootNode = (JsonNode)jsonParser.readValueAsTree();
                        this.parseSingleBox(rootNode);
                    }
                }
            }
            jsonParser.close();
        }
        catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    private void parseSingleBox(final JsonNode rootNode) {
        final JsonNode idNode = rootNode.path("dataMappingId");
        final JsonNode typeNode = rootNode.path("type");
        final JsonNode xNode = rootNode.path("x");
        final JsonNode yNode = rootNode.path("y");
        final JsonNode widthNode = rootNode.path("width");
        final JsonNode heightNode = rootNode.path("height");
        final JsonNode textNode = rootNode.path("text");
        final JsonNode pageNode = rootNode.path("pageNumber");
        final JsonNode urlNode = rootNode.path("url");
        final PDPage page = this.retrievePageStream(pageNode.asInt());
        System.out.println("page" + this.m_page + "=" + page + "--->" + idNode.asText());
        int isCenter = 0;
        if (rootNode.has("isCenter")) {
            isCenter = rootNode.path("isCenter").asInt();
        }
        int isVerCenter = 0;
        if (rootNode.has("isVCenter")) {
            isVerCenter = rootNode.path("isVCenter").asInt();
        }
        int isVertical = 0;
        if (rootNode.has("isVertical")) {
            isVertical = rootNode.path("isVertical").asInt();
        }
        if (typeNode.asText().equals("header")) {
            final DHeader h1 = new DHeader(this.m_doc, page, textNode.asText(), this.m_font, (float)xNode.asInt(), (float)yNode.asInt(), widthNode.asInt(), heightNode.asInt(), isVertical, isCenter);
            this.m_boxList.put(idNode.asText(), h1);
        }
        else if (typeNode.asText().equals("cellLabel")) {
            final DCellLabel c1 = new DCellLabel(this.m_doc, page, textNode.asText(), this.m_font, (float)xNode.asInt(), (float)yNode.asInt(), widthNode.asInt(), heightNode.asInt(), isVertical, isCenter);
            this.m_boxList.put(idNode.asText(), c1);
        }
        else if (typeNode.asText().equals("DCellValue")) {
            final DHeader v1 = new DHeader(this.m_doc, page, textNode.asText(), this.m_font, (float)xNode.asInt(), (float)yNode.asInt(), widthNode.asInt(), heightNode.asInt(), isVertical, isCenter);
            this.m_boxList.put(idNode.asText(), v1);
        }
        else if (typeNode.asText().equals("image")) {
            final DImage i1 = new DImage(this.m_doc, page, urlNode.asText(), (float)xNode.asInt(), (float)yNode.asInt(), widthNode.asInt(), heightNode.asInt());
            this.m_boxList.put(idNode.asText(), i1);
        }
        else {
            final JsonNode backgroundRNode = rootNode.path("backgroundColorR");
            final JsonNode backgroundGNode = rootNode.path("backgroundColorG");
            final JsonNode backgroundBNode = rootNode.path("backgroundColorB");
            final JsonNode fontRNode = rootNode.path("fontColorR");
            final JsonNode fontGNode = rootNode.path("fontColorG");
            final JsonNode fontBNode = rootNode.path("fontColorB");
            final JsonNode fontSizeNode = rootNode.path("fontSize");
            final Color fontColor = new Color(fontRNode.asInt(), fontRNode.asInt(), fontRNode.asInt());
            final Color backgroundColor = new Color(backgroundRNode.asInt(), backgroundGNode.asInt(), backgroundBNode.asInt());
            final DTextBox t1 = new DTextBox(this.m_doc, page);
            t1.setFont(this.m_font);
            t1.setPosition((float)xNode.asDouble(), (float)yNode.asDouble());
            t1.setBoxSize((float)widthNode.asDouble(), (float)heightNode.asDouble());
            t1.setBackground(backgroundColor);
            t1.setFontColor(fontColor);
            t1.setFontSize(fontSizeNode.asInt());
            t1.setText(textNode.asText());
            t1.setCenterAlign(isCenter == 1);
            t1.setVerticalText(isVertical == 1);
            t1.setVerCenterAlign(isVerCenter == 1);
            t1.setBorders(rootNode.path("borderTop").asInt() == 1, rootNode.path("borderBottom").asInt() == 1, rootNode.path("borderLeft").asInt() == 1, rootNode.path("borderRight").asInt() == 1);
            this.m_boxList.put(idNode.asText(), t1);
        }
    }

    public PDPage retrievePageStream(final int pageNumber) {
        if (!this.m_pageList.containsKey(pageNumber)) {
            final PDPage page = new PDPage();
            this.m_doc.addPage(page);
            this.m_pageList.put(pageNumber, page);
            System.out.println("New:" + pageNumber);
            return page;
        }
        final PDPage page = this.m_pageList.get(pageNumber);
        System.out.println("Old:" + pageNumber);
        return page;
    }

    public void setDefaultPage(final PDPage page) {
        this.m_page = page;
        this.m_pageList.put(1, this.m_page);
    }

    public void render() {
        final Set<String> keys = (Set<String>)this.m_boxList.keySet();
        for (final String k : keys) {
            final List<DBox> itemsWithKey = (List<DBox>)this.m_boxList.get(k);
            itemsWithKey.parallelStream().forEach(DBox::render);
        }
    }

    public void getValuesFromJson(final InputStream in) {
        try {
            System.out.println("---> InputStream");
            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode rootNode = mapper.readTree(in);
            this.parseValues(rootNode);
        }
        catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    public void getValuesFromJson(final String filePath) {
        try {
            System.out.println("--->" + filePath);
            final JsonFactory jsonFactory = (JsonFactory)new MappingJsonFactory();
            final File initialFile = new File(filePath);
            final InputStream targetStream = new FileInputStream(initialFile);
            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode rootNode = mapper.readTree(targetStream);
            this.parseValues(rootNode);
        }
        catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    public void parseValues(final JsonNode rootNode) {
        final Iterator<Map.Entry<String, JsonNode>> fieldsIterator = (Iterator<Map.Entry<String, JsonNode>>)rootNode.fields();
        while (fieldsIterator.hasNext()) {
            final Map.Entry<String, JsonNode> field = fieldsIterator.next();
            final String key = field.getKey();
            final JsonNode value = field.getValue();
            final List<DBox> itemsWithKey = (List<DBox>)this.m_boxList.get(key);
            final JsonNode jsonNode;
            itemsWithKey.parallelStream().forEach(v -> {
                if (v instanceof DTextBox || v instanceof DHeader || v instanceof DCellLabel) {
                    v.setText(jsonNode.asText());
                }
            });
        }
    }
}

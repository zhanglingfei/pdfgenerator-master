package com.nissan.dev;

import org.apache.pdfbox.pdmodel.PDDocument;

//--linked hashmap
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;

//--file manipulation
import java.nio.file.Files;
import java.nio.file.Paths;

//--Json parser
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.commons.collections4.*;
import org.apache.commons.collections4.multimap.*;
import java.util.*;



import java.awt.Color;

/**
 * @author Damien Contreras 2018
 * Parse template & add data into pages
 *
 * m_boxList contains the list of all boxes generated dynamically from the JSON template
 * m_pageList contains the page index and the page object of each page
 * PDType0Font is the font to use
 **/

public class DTemplate {

    private MultiValuedMap<String, DBox> m_boxList; // all box element used to generate the template passed as a parameter to parse
    private PDDocument m_doc; // PDF doc
    private PDPage m_page; // default page
    private LinkedHashMap<Integer, PDPage> m_pageList; //pages idx = page number
    private PDType0Font m_font;
    /**
     * Standard constructor
     *
     * @param doc represents the PDF document
     */
    public DTemplate(PDDocument doc){
        m_doc = doc;
        m_boxList = new ArrayListValuedHashMap<>();
        m_pageList = new LinkedHashMap<>();
    }

    /**
     * Standard constructor
     *
     * @param doc represents the PDF document

     */
    public DTemplate(PDDocument doc, String fontFile){
        m_doc = doc;

        try {
            m_font = PDType0Font.load(doc, new File(fontFile));
        }catch(java.io.IOException ex){}

        //m_boxList = new LinkedHashMap<>();
        //m_pageList = new LinkedHashMap<>();

        m_boxList = new ArrayListValuedHashMap<>(); //MultiValuedMap
        m_pageList = new LinkedHashMap<>();
    }


    /**
     * parse JSON to collection of objects ready to be rendered, we are using Streaming to limit memory footprint
     *
     * @param filePath full path to the JSON template to be used
     */
    public void parse(String filePath){

        try {
            System.out.println("--->" + filePath);

            JsonFactory jsonFactory = new MappingJsonFactory();
            JsonParser jsonParser = jsonFactory.createParser(new File(filePath));

            JsonToken  current;

            while(jsonParser.nextToken() != JsonToken.END_OBJECT) {

                String fieldName = jsonParser.getCurrentName();

                if ("templateName".equals(fieldName)) {
                    jsonParser.nextToken();
                    System.out.println( fieldName + "--->" + jsonParser.getValueAsString() );
                }

                if ("objList".equals(fieldName)) {

                    System.out.println( fieldName + "--->");
                    //jsonParser.nextToken();
                    if (jsonParser.nextToken() == JsonToken.START_ARRAY) {
                        // For each of the records in the array
                        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                            JsonNode rootNode = jsonParser.readValueAsTree();
                            parseSingleBox(rootNode);
                        }
                    }
                }
            }
            jsonParser.close();

        }catch(java.io.IOException ex){
            System.out.println(ex.toString());
        }
    }

    /**
     * parse JSON to collection of objects ready to be rendered, we are using Streaming to limit memory footprint
     *
     * @param rootNode full path to the JSON template to be used
     */
    private void parseSingleBox( JsonNode rootNode){
        JsonNode idNode = rootNode.path("dataMappingId");
        JsonNode typeNode = rootNode.path("type");
        JsonNode xNode = rootNode.path("x");
        JsonNode yNode = rootNode.path("y");
        JsonNode widthNode = rootNode.path("width");
        JsonNode heightNode = rootNode.path("height");
        JsonNode textNode = rootNode.path("text");
        JsonNode pageNode = rootNode.path("pageNumber");
        JsonNode urlNode = rootNode.path("url");



        PDPage page = retrievePageStream(pageNode.asInt());
        // page = m_pageList.get(1);

        System.out.println("page" +m_page +"=" + page + "--->" + idNode.asText());

        int isCenter = 0;
        if(rootNode.has("isCenter")) isCenter = rootNode.path("isCenter").asInt();

        int isVerCenter = 0;
        if(rootNode.has("isVCenter")) isVerCenter = rootNode.path("isVCenter").asInt();
        int isVertical = 0;
        if(rootNode.has("isVertical")) isVertical = rootNode.path("isVertical").asInt();

        if(typeNode.asText().equals("header")){ //table header
            DHeader h1 = new DHeader(m_doc, page, textNode.asText(), m_font, xNode.asInt(), yNode.asInt(),  widthNode.asInt(), heightNode.asInt(),isVertical,isCenter);
            m_boxList.put(idNode.asText(), h1);
        } else if(typeNode.asText().equals("cellLabel")) { //cell label
            DCellLabel c1 = new DCellLabel(m_doc, page, textNode.asText(), m_font, xNode.asInt(), yNode.asInt(), widthNode.asInt(), heightNode.asInt(),isVertical,isCenter);
            m_boxList.put(idNode.asText(), c1);
        }else if (typeNode.asText().equals("DCellValue")){ //cell value
            DHeader v1 = new DHeader(m_doc, page, textNode.asText(), m_font, xNode.asInt(), yNode.asInt(),  widthNode.asInt(), heightNode.asInt(),isVertical,isCenter);
            m_boxList.put(idNode.asText(), v1);
        }else if (typeNode.asText().equals("image")){ //cell value
            DImage i1 = new DImage(m_doc, page, urlNode.asText(), xNode.asInt(), yNode.asInt(),  widthNode.asInt(), heightNode.asInt());
            m_boxList.put(idNode.asText(), i1);
        } else{ //custom box
            JsonNode backgroundRNode = rootNode.path("backgroundColorR");
            JsonNode backgroundGNode = rootNode.path("backgroundColorG");
            JsonNode backgroundBNode = rootNode.path("backgroundColorB");
            JsonNode fontRNode = rootNode.path("fontColorR");
            JsonNode fontGNode = rootNode.path("fontColorG");
            JsonNode fontBNode = rootNode.path("fontColorB");
            JsonNode fontSizeNode = rootNode.path("fontSize");

            //JsonNode borderTopNode = rootNode.path("borderTop");

            Color fontColor = new Color(fontRNode.asInt(),fontRNode.asInt(),fontRNode.asInt());
            Color backgroundColor = new Color(backgroundRNode.asInt(),backgroundGNode.asInt(),backgroundBNode.asInt());

            String strText = textNode.asText();

            DTextBox t1 = new DTextBox(m_doc, page);
            t1.setFont(m_font);
            t1.setPosition((float)xNode.asDouble(),(float) yNode.asDouble());
            t1.setBoxSize((float)widthNode.asDouble(), (float)heightNode.asDouble());
            //change to pdf generator 2.0
            if(strText.trim().equalsIgnoreCase("-") || strText.trim().equalsIgnoreCase("ー") || strText.trim().equalsIgnoreCase("―") ){
                backgroundColor = new Color(189,189,189);
                t1.setBackground(backgroundColor);
            }else{
                t1.setBackground(backgroundColor);
            }


            t1.setFontColor(fontColor);
            t1.setFontSize(fontSizeNode.asInt());
            t1.setText(strText);
            //t1.setText(textNode.asText());
            t1.setCenterAlign(isCenter==1);
            t1.setVerticalText(isVertical==1);
            t1.setVerCenterAlign(isVerCenter==1);
            t1.setBorders(rootNode.path("borderTop").asInt()==1, rootNode.path("borderBottom").asInt()==1, rootNode.path("borderLeft").asInt()==1, rootNode.path("borderRight").asInt()==1);

            m_boxList.put(idNode.asText(), t1);

        }
    }



    /**
     * Get the page to be used to render the elements, if already existing it will return the right page
     * @param pageNumber page number of the page on which to render
     */
    public  PDPage retrievePageStream(int pageNumber){

        if(!m_pageList.containsKey(pageNumber)) {

            PDPage page = new PDPage();
            m_doc.addPage(page);

            //   PDPageContentStream page = new PDPageContentStream(m_doc, p);
            //   used_page = page;
            m_pageList.put(pageNumber, page );

            System.out.println("New:" + pageNumber);
            return page;

        }else {
            PDPage page = m_pageList.get(pageNumber);
            System.out.println("Old:" + pageNumber);
            return page;
        }
        //return used_page;
    }



    /**
     * set the current page (to-do need to be able to add pages
     *
     */
    public void setDefaultPage(PDPage page){
        m_page = page;
        m_pageList.put(1, m_page );
    }

    /**
     * Render all elements, using multi keys
     *
     */
    public void render(){
        Set<String> keys = m_boxList.keySet();
        //--to handle multiple times the same tag

        for(String k : keys) {
            List<DBox> itemsWithKey = (List<DBox>) m_boxList.get(k);
            itemsWithKey.parallelStream().forEach(DBox::render);
        }
           /*
           itemsWithKey.forEach((key,value) -> {
               ((DBox)value).render() ;
            });*/


        // m_boxList.get(k);

        // boolean doesExists = itemsWithKey.contains(itemToL);


        // System.out.println("does " + itemToLookFor + " exists for key " + k + ": " +doesExists);



        /* //--for single tags
        m_boxList.forEach((key,value) -> {
                value.render();
        });*/
    }


    /**
     * Add values from a JSON file from a file
     *@param in an InputStream
     */
    public void getValuesFromJson(InputStream in) {
        try {
            System.out.println("---> InputStream");

            //JsonFactory jsonFactory = new MappingJsonFactory();
            //JsonParser jsonParser = jsonFactory.createParser(in);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(in);

            parseValues(rootNode);

        } catch (java.io.IOException ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * Add values from a JSON file
     * @param filePath
     */
    public void getValuesFromJson(String filePath){
        try {
            System.out.println("--->" + filePath);

            JsonFactory jsonFactory = new MappingJsonFactory();


            File initialFile = new File(filePath);
            InputStream targetStream = new FileInputStream(initialFile);

            //JsonParser jsonParser = jsonFactory.createParser(targetStream);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(targetStream);


            parseValues(rootNode);

        }catch(java.io.IOException ex){
            System.out.println(ex.toString());
        }


    }

    /**
     * Add values from a JSON file from a file
     * @param rootNode
     */
    public void parseValues(JsonNode rootNode){

        Iterator<Map.Entry<String,JsonNode>> fieldsIterator = rootNode.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String,JsonNode> field = fieldsIterator.next();
            String key = field.getKey();
            JsonNode value = field.getValue();
            System.out.println("Key: " + key);
            System.out.println("Value: " + value);


            List<DBox> itemsWithKey = (List<DBox>) m_boxList.get(key);
            itemsWithKey.parallelStream().forEach((v) -> {
                if(v instanceof DTextBox || v instanceof DHeader || v instanceof DCellLabel)
                {
                    ((DTextBox)v).setText(value.asText());
                }
            });




            /* //--for single tags
        m_boxList.forEach((key,value) -> {
                value.render();
        });*/



            /*
            if(m_boxList.get(key) instanceof DTextBox || m_boxList.get(key) instanceof DHeader || m_boxList.get(key) instanceof DCellLabel)
            {
                ((DTextBox)m_boxList.get(key)).setText(value.asText());
            }*/

        }
    }
}

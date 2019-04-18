package com.nissan.dev;

import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This heritate from DBox and add the possibility to add text
 *
 */
public class DTextBox extends DBox {

    private static PDFont FONT = PDType1Font.HELVETICA;
    private static float FONT_SIZE = 14;
    private static final float LINE_SOACING = -1.5f;
    private static float LEADING = LINE_SOACING * FONT_SIZE;

    @Getter
    @Setter
    private Color m_textColor;
    @Getter @Setter protected String m_text;
    private PDType0Font m_font;
    protected int m_fontSize;

    /**
     * Standard constructor for this class that fixes the color & fontSize
     *
     * @param doc PDFDoc base document
     * @param page Represent the page we will render into
     */
    public DTextBox(PDDocument doc, PDPageContentStream page){
        try {
            m_doc = doc;
            m_page = page;
            m_fontSize = 10;
            m_font = PDType0Font.load(doc, new File("/Users/sarathbabu/Desktop/kosugi_Maru/kosugimaru-regular.ttf"));

        }catch(java.io.IOException ex){}
    }


    /**
     * Add a fontSize to the box, need to be called before rendering the object
     *
     * @param fontSize Define the fontSize to be used
     */
    void setFontSize(int fontSize)
    {
        m_fontSize = fontSize;
    }

    protected void addText(String text){
        m_text = text;
    }


    /**
     * Add text to the class
     * Code need to be moved to the render function
     *
     *
     */
    protected void renderText(){
        try {

            m_page.beginText();

            m_page.setFont(m_font, m_fontSize);
            m_page.setNonStrokingColor(0, 0, 0);
            //m_page.moveTextPositionByAmount(m_x+5, m_y+5);
            m_page.newLineAtOffset(m_x+5, m_y+5);
            //contents.newLineAtOffset(x, y);
            m_page.showText(m_text);
            m_page.endText();

        }catch(java.io.IOException ex){System.err.println("Error");}
    }


    /**
     * Standard constructor for this class that fixes the color & fontSize
     *
     * @param doc PDFDoc base document
     * @param page Represent the page we will render into
     * @param text Text to display in the cell
     * @param x coordinates of the box
     * @param y coordinates of the box
     * @param width of the box
     * @param height of the box
     * @param background specify the color of the background for the box (R,G,B)
     */
    public DTextBox(PDDocument doc, PDPageContentStream page, String text, float x, float y, int width,  int height, Color background) {

            m_doc = doc;
            m_page = page;

            addSize(width, height);
            addPosition(x, y);
            setFontSize(10);
            addBackground(background);
            addText(text);

            try {
                m_font = PDType0Font.load(m_doc, new File("/Users/sarathbabu/Desktop/kosugi_Maru/kosugimaru-regular.ttf"));
            }catch(IOException ex){
                System.err.println("Error");
                ex.printStackTrace();
            }
    }

    @Override
    /**
     * Rendering function that needs to be implemented
     *
     */
    public void render(){

        //--display border & box
       super.render();

       //-- display text
        renderText();

    }

    // this is method to call number of lines loop document to write

    public void writeText(PDPageContentStream contentStream, float width,float height, float sx,
                           float sy, String text, boolean justify) throws IOException {
        java.util.List<String> lines = loopLines(text,width,height);
        contentStream.setFont(FONT, FONT_SIZE);
        System.out.println("######### FONT_SIZE : "+FONT_SIZE);
        System.out.println("######### lines : "+lines.size());
        contentStream.newLineAtOffset(sx, (sy+(height-FONT_SIZE)));// cursore first line to print to adject into box
        for (String line: lines) { // number of lines to loop
            float charSpacing = 0;
            if (justify){ // text wrapp
                if (line.length() > 1) {
                    float size = FONT_SIZE * FONT.getStringWidth(line) / 1000;
                    float free = width - size;
                    if (free > 0 && !lines.get(lines.size() - 1).equals(line)) {
                        charSpacing = free / (line.length() - 1);
                    }
                }
            }
            contentStream.setCharacterSpacing(charSpacing);
            contentStream.showText(line);
            contentStream.newLineAtOffset(0, LEADING);// next line to move cursore
        }
    }

    public String heightwidthString(String text, float width,float height) throws IOException {
        java.util.List<String> lines;// = parseLines(text, width);
        boolean isTrue=true;
        do {
            FONT_SIZE = FONT_SIZE - 1;
            LEADING = LINE_SOACING * FONT_SIZE;
            lines = parseText2Lines(text, width);
            float fheight = ( FONT.getFontDescriptor().getCapHeight()) / 1000 * FONT_SIZE;
            System.out.println("######### (lines.size()*height) : "+(lines.size()*(fheight*1.7)));
            System.out.println("######### (lines.size()) : "+lines.size());
            System.out.println("######### height : "+fheight);
            System.out.println("######### FONT_SIZE : "+FONT_SIZE);
            System.out.println("***********************************************************************");
            if(!((lines.size()*(fheight*1.7))>height)) isTrue= false;
        }while(isTrue);
        System.out.println("######### (lines.size()) : "+lines.size());

        String tempString ="";
        for (int i=0; i<lines.size(); i++)
            tempString+=lines.get(i)+"<br />";
        return tempString;
    }



    // this method is used to loop height width, to adject the height and to check height
    public java.util.List<String> loopLines(String text, float width, float height) throws IOException {
        java.util.List<String> lines;// = parseLines(text, width);
        boolean isTrue=true;
        do {
            FONT_SIZE = FONT_SIZE - 1;
            LEADING = LINE_SOACING * FONT_SIZE;
            lines = parseText2Lines(text, width);
            float fheight = ( FONT.getFontDescriptor().getCapHeight()) / 1000 * FONT_SIZE;
            System.out.println("######### (lines.size()*height) : "+(lines.size()*(fheight*1.7)));
            System.out.println("######### (lines.size()) : "+lines.size());
            System.out.println("######### height : "+fheight);
            System.out.println("######### FONT_SIZE : "+FONT_SIZE);
            System.out.println("***********************************************************************");
            if(!((lines.size()*(fheight*1.7))>height)) isTrue= false; // @ check height
            // FONT_SIZE = FONT_SIZE - 1;
            //LEADING = LINE_SOACING * FONT_SIZE;

        }while(isTrue);
        System.out.println("######### (lines.size()) : "+lines.size());
        return lines;
    }

    // passing line to check width in to box number of line to wrapping function

    public java.util.List<String> parseText2Lines(String text, float width) throws IOException {
        List<String> lines = new ArrayList<String>();
        int lastSpace = -1;
        System.out.println("######### FONT_SIZE : "+FONT_SIZE);
        while (text.length() > 0) {
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = text.length();
            String subString = text.substring(0, spaceIndex);

            float size = FONT_SIZE * FONT.getStringWidth(subString) / 1000;
            if (size > width) {
                if (lastSpace < 0){
                    lastSpace = spaceIndex;
                }
                subString = text.substring(0, lastSpace);
                lines.add(subString);
                text = text.substring(lastSpace).trim();
                lastSpace = -1;
            } else if (spaceIndex == text.length()) {
                lines.add(text);
                text = "";
            } else {
                lastSpace = spaceIndex;
            }
        }
        return lines;
    }

    // return string

    public String parseStrings(String text, float width) throws IOException {
        String lines = "";
        int lastSpace = -1;
        System.out.println("######### FONT_SIZE : "+FONT_SIZE);
        while (text.length() > 0) {
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = text.length();
            String subString = text.substring(0, spaceIndex);

            float size = FONT_SIZE * FONT.getStringWidth(subString) / 1000;
            if (size > width) {
                if (lastSpace < 0){
                    lastSpace = spaceIndex;
                }
                subString = text.substring(0, lastSpace);
                lines+=subString+"<br />";
                text = text.substring(lastSpace).trim();
                lastSpace = -1;
            } else if (spaceIndex == text.length()) {
                lines+=text+"<br />";
                text = "";
            } else {
                lastSpace = spaceIndex;
            }
        }
        return lines;
    }

}


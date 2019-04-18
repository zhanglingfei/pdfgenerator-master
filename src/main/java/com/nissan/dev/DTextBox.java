package com.nissan.dev;

import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.pdmodel.PDPage;
import sun.misc.BASE64Decoder;

import java.io.IOException;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Damien Contreras 2018
 * This heritate from DBox and add the possibility to add text
 *
 */
public class DTextBox extends DBox {

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
    public DTextBox(PDDocument doc, PDPage page){

        super(doc, page);
        try {
            m_fontSize = 10;
            m_font = PDType0Font.load(doc, new File("./src/main/resources/KosugiMaru-Regular.ttf"));
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


    /**
     * Add a fontSize to the box, need to be called before rendering the object
     *
     * @param font Define the font to be used
     */
    public void setFont(PDType0Font font){
        m_font = font;
    }

    void setFontColor(Color c){
        m_textColor = c;

    }

    protected void setText(String text){
        m_text = text;
    }


    /**
     * Add text to the class
     * Code need to be moved to the render function
     *
     *
     */
    protected void renderText(PDPageContentStream contentStream){
        try {
            contentStream.beginText();
            contentStream.setFont(m_font, m_fontSize);
            contentStream.setNonStrokingColor(0, 0, 0);
            //m_page.moveTextPositionByAmount(m_x+5, m_y+5);
            contentStream.newLineAtOffset(m_x+5, m_y+5);
            //contents.newLineAtOffset(x, y);
            contentStream.showText(m_text);
            contentStream.endText();

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
    public DTextBox(PDDocument doc, PDPage page, String text, float x, float y, int width,  int height, Color background) {
        super(doc, page);
        //  m_doc = doc;
        //  m_page = page;

        setBoxSize(width, height);
        setPosition(x, y);
        setFontSize(10);
        setBackground(background);
        setText(text);

        try {
            m_font = PDType0Font.load(m_doc, new File("./src/main/resources/KosugiMaru-Regular.ttf"));
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
    protected void render(){



        //CommonMethods commonMethod = new CommonMethods();
        //--display border & box
        super.render();

        try (PDPageContentStream contentStream = new PDPageContentStream(m_doc, m_page,true,false)) {

            contentStream.beginText();
            contentStream.setNonStrokingColor(0, 0, 0);

            writeText(contentStream,m_text,m_font,m_fontSize,m_width,m_height,m_x,m_y,m_vertical_text,m_center_align,false);

               /* m_page.setFont(m_font, m_fontSize);
                m_page.setNonStrokingColor(0, 0, 0);
                //m_page.moveTextPositionByAmount(m_x+5, m_y+5);
                m_page.newLineAtOffset(m_x+5, m_y+5);
                //contents.newLineAtOffset(x, y);
                m_page.showText(m_text);*/
            contentStream.endText();
        }catch (IOException ex){
            System.out.println("Error");
        }
        //-- display text
        //renderText();

    }

    public void render(PDPageContentStream page, String text,PDType0Font m_font, int m_fontSize, float x, float y, int width,  int height){

        try {
            page.beginText();
            writeText(page,text,m_font,m_fontSize,width,height,x,y,false,false,false);

            page.endText();


        }catch(java.io.IOException ex){System.err.println("Error");}
    }



    public void writeText(PDPageContentStream contentStream,String text, PDType0Font m_font, int m_fontSize1, float width, float height, float sx,
                          float sy,boolean isVertical,boolean isCenter, boolean justify)  {
        try {
            if(text==null || text.trim().isEmpty()) return;
            float font_size = m_fontSize1;
            boolean isTrue=true;

            List<String> lines = null; //loopLines(text, m_font, font_size, width, height);
            do {

                //m_fontSize = m_fontSize - 1;
                //float newLine = 1.5f * m_fontSize;
                if(isVertical){
                    lines = parseText2Lines(text, m_font, font_size, height);
                }else {
                    lines = parseText2Lines(text, m_font, font_size, width);
                }
                float fheight = ( m_font.getFontDescriptor().getCapHeight()) / 1000 * font_size;
                /*
                System.out.println("######### (lines.size()*height) : "+(lines.size()*(fheight*1.7)));
                System.out.println("######### (lines.size()) : "+lines.size());
                System.out.println("######### height : "+fheight);
                System.out.println("######### FONT_SIZE : "+font_size);
                System.out.println("***********************************************************************");
                */

                if(!(((lines.size()*(fheight*(1f*lines.size())))>height && !isVertical) || ((text.length()*(fheight*(0.5f*text.length())))>height) && isVertical)) isTrue= false; // @ check height
                if(!isTrue && text.indexOf(" ")==-1) {
                    float lineWidth = getStringWidth(text, m_font, font_size);
                    if(!isVertical) {
                        if (lineWidth > width) isTrue = true;
                    } else {
                        if(lineWidth  > height) isTrue=true;
                    }
                }
                font_size = font_size - 0.5f;
                //float newLine = 1.5f * m_fontSize;
            } while(isTrue);
            float newLine = -1.1f * font_size;
            contentStream.setFont(m_font, font_size);
            float fheight = ( m_font.getFontDescriptor().getCapHeight()) / 1000 * font_size;
         //   System.out.println("######### FONT_SIZE : " + font_size);
         //   System.out.println("######### lines : " + lines.size());
            //float sy_x = ((sy+(height/(lines.size()+1))) - ((((lines.size()*fheight)/2)+1.5f)-(0.5f*(lines.size()))));

            //float sy_x = sy + (height/2f * (lines.size()==1?1:(lines.size()/1.3f))) - ((lines.size()*(1.1f+fheight)) / (lines.size()+1));

            float sy_x =  sy + height + newLine;
         //   System.out.println("######### lines sx : " + sx);
         //   System.out.println("######### lines sy : " + sy_x);
            // page.getMediaBox().getHeight - marginTop - titleheight
            //contentStream.transform(Matrix.getRotateInstance(40.0f, sx,sy_x));
            //contentStream.setTextRotation(Math.PI/2,sx,sy_x)

            String line = lines.get(0);
            float lineWidth = getStringWidth(line,m_font, font_size);
            float sx_y = sx;

            // for (String line : lines) { // number of lines to l
            if(isVertical) {
                if(m_ver_center_align) sy_x = sy + height/2f +1.1f;//+ ((lines.size() * (fheight + 1.5f))/2f);
                //sy + height/2f + (lines.size() * fheight/2f) + newLine/2f;
                // sy + height + newLine + (lines.size() * fheight * 0.6f) - ((fheight + lines.size())  / lines.size());
                if (isCenter) sx_y = (width / 2f) - fheight + sx;
                //contentStream.setTextRotation((Math.PI/2)*3,sx_y,sy)
                contentStream.newLineAtOffset(sx_y, sy_x);
                   for (int i = 0; i <lines.size();i++) {
                    line = lines.get(i);
                    float charSpacing = 0;
                    // String strline = toVerticalStringIterator(line);
                    contentStream.setCharacterSpacing(charSpacing);
                    for(char ch : line.toCharArray()) {
                     //   System.out.println("######### ch : " + ch);
                        if (ch=='-') ch = '|';
                        contentStream.showText(ch+"");
                        contentStream.newLineAtOffset(0, newLine);// next line to move cursor
                    }
                }
            } else {
                float sx_l=0;
                sy_x =  sy + height +newLine;
                if(m_ver_center_align) sy_x = sy + height/2f + (lines.size() * fheight/2f) + newLine/2f;
                // sy + height + newLine + (lines.size() * fheight * 0.6f) - ((fheight + lines.size())  / lines.size());
                    //sy_x = sy + height/2f + (lines.size() * (fheight/2f)) + (newLine);
                // sy + height/2f + (lines.size() * fheight/2f) + newLine/2f;// sy + height + newLine + (lines.size() * fheight * 0.6f) - ((fheight + lines.size())  / lines.size());
                if (isCenter) sx_y = (width / 2f) - lineWidth / 2f + sx;
                contentStream.newLineAtOffset(sx_y+1.8f, sy_x);// + ((height - (lines.size()*fheight)/2)));// cursor first line to print to adjust into box
                //for (int i = lines.size() - 1; i >= 0; i--) {
                for (int i = 0; i <lines.size();i++) {
                    line = lines.get(i);
                    float charSpacing = 0;
                    if (justify) { // text wrap
                        if (line.length() > 1) {
                            float size = font_size * m_font.getStringWidth(line) / 1000;
                            float free = width - size;
                            if (free > 0 && !lines.get(lines.size() - 1).equals(line)) {
                                charSpacing = free / (line.length() - 1);
                            }
                        }
                    }
                    contentStream.setCharacterSpacing(charSpacing);
                //    System.out.println("######### line : " + line);
                    contentStream.showText(line);
                /*sx_y=0;
                if(i>0) {
                    line = lines.get(i-1);
                    lineWidth = getStringWidth(line, m_font, font_size);
                    sx_y = (width / 2f) - lineWidth / 2f + sx;
                }*/
                //    sx_l =   sx_y - line.length();
                   // lineWidth = line.length();
                    contentStream.newLineAtOffset(sx_l, newLine);// next line to move cursor

                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    float getStringWidth(String text, PDFont font, float fontSize) throws IOException {
        return font.getStringWidth(text) * fontSize / 1000F;
    }

    // this method is used to loop height width, to adjust the height and to check height
    public List<String> loopLines(String text,PDType0Font m_font, int m_fontSize, float width, float height) throws IOException {
        List<String> lines;// = parseLines(text, width);
        boolean isTrue=true;
        do {
            //m_fontSize = m_fontSize - 1;
            //float newLine = 1.5f * m_fontSize;
            lines = parseText2Lines(text,m_font,m_fontSize, width);
            float fheight = ( m_font.getFontDescriptor().getCapHeight()) / 1000 * m_fontSize;
            /*
            System.out.println("######### (lines.size()*height) : "+(lines.size()*(fheight*1.7)));
            System.out.println("######### (lines.size()) : "+lines.size());
            System.out.println("######### height : "+fheight);
            System.out.println("######### FONT_SIZE : "+m_fontSize);
            System.out.println("***********************************************************************");
            */
            if(!((lines.size()*(fheight*2))>height)) isTrue= false; // @ check height
            m_fontSize = m_fontSize - 1;
            //float newLine = 1.5f * m_fontSize;

        }while(isTrue);
        System.out.println("######### (lines.size()) : "+lines.size());
        return lines;
    }


    public String heightwidthString(String text,PDType0Font m_font, int m_fontSize, float width,float height) throws IOException {
        List<String> lines;// = parseLines(text, width);
        boolean isTrue=true;
        do {
            //m_fontSize = m_fontSize - 1;
            //float newLine = 1.5f * m_fontSize;
            lines = parseText2Lines(text,m_font,m_fontSize, width);
            float fheight = ( m_font.getFontDescriptor().getCapHeight()) / 1000 * m_fontSize;
            /*
            System.out.println("######### (lines.size()*height) : "+(lines.size()*(fheight*1.7)));
            System.out.println("######### (lines.size()) : "+lines.size());
            System.out.println("######### height : "+fheight);
            System.out.println("######### FONT_SIZE : "+m_fontSize);
            System.out.println("***********************************************************************");
            */
            if(!((lines.size()*(fheight*1.7))>height)) isTrue= false;
            m_fontSize = m_fontSize - 1;
        }while(isTrue);
        System.out.println("######### (lines.size()) : "+lines.size());

        String tempString ="";
        for (int i=0; i<lines.size(); i++)
            tempString+=lines.get(i)+"<br />";
        return tempString;
    }

    // passing line to check width in to box number of line to wrapping function
    public java.util.List<String> parseText2Lines(String text,PDType0Font m_font, float m_fontSize, float width) throws IOException {
        List<String> lines = new ArrayList<String>();
        int lastSpace = -1;

        while (text.length() > 0) {
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = text.length();
            String subString = text.substring(0, spaceIndex);

            float size = m_fontSize * m_font.getStringWidth(subString) / 1000;
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


    public static String toVerticalStringIterator(String str){
        String returnStr="";
        for(char ch : str.toCharArray()){
            returnStr += ch+"\n";
        }
        return returnStr;
    }
    // return string

    public String parseStrings(String text,PDType0Font m_font, int m_fontSize, float width) throws IOException {
        String lines = "";
        int lastSpace = -1;
       // System.out.println("######### FONT_SIZE : "+m_fontSize);
        while (text.length() > 0) {
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0)
                spaceIndex = text.length();
            String subString = text.substring(0, spaceIndex);

            float size = m_fontSize * m_font.getStringWidth(subString) / 1000;
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
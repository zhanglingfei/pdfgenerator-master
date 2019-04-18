package com.nissan.dev;

import java.io.File;
import java.io.IOException;

//import ch.qos.logback.core.net.SyslogOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import be.quodlibet.boxable.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.awt.Color;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;


/**
 *  @author Damien Contreras 2018
 * This class DBox represent the primitive to display an element
 *
 */
public class DBox{
    @Getter @Setter protected PDDocument m_doc; // PDF document
    @Getter @Setter protected PDPage m_page; // PDF page
    @Getter @Setter protected float m_x; //x coordinate
    @Getter @Setter protected float m_y; //y coordinate
    @Getter @Setter protected float m_width; // width of the box
    @Getter @Setter protected float m_height; //height of the box
    @Getter @Setter protected Color m_background; //background color of the box
    @Getter @Setter protected boolean m_border_top; //top border yes/no
    @Getter @Setter protected boolean m_border_bottom; //bottom border yes/no
    @Getter @Setter protected boolean m_border_left; //left border yes/no
    @Getter @Setter protected boolean m_border_right; // right border yes/no

    @Getter @Setter protected boolean m_center_align; // center align yes/no
    @Getter @Setter protected boolean m_vertical_text; // vertical text align yes/no
    @Getter @Setter protected boolean m_ver_center_align; // center align yes/no

    @Getter @Setter protected String m_image_url;

    static int count=0;


    public DBox(PDDocument doc, PDPage page){
        m_doc = doc;
        m_page = page;
    }

    public void setImageURL(String url) {
        m_image_url = url;
    }

    void renderImage() {

    }
    /**
     * Launch the generation of the rendering of the element
     */
    void render(){

        try (PDPageContentStream contentStream = new PDPageContentStream(m_doc, m_page,true,false)) {

            //background
            renderBackground(contentStream);

            //render borders
            if (m_border_top) addBorderTop(contentStream);
            if (m_border_bottom) addBorderBottom(contentStream);
            if (m_border_left) addBorderLeft(contentStream);
            if (m_border_right) addBorderRight(contentStream);


            /*//background
            renderBackground(contentStream);*/

        }catch (IOException ex){
            System.out.println("Error");
        }
    }

    /**
     * @param x1 floats representing the x,y of the first point to draw a line
     * @param y1 floats representing the x,y of the first point to draw a line
     * @param x2 floats representing the x,y of the second point to draw a line
     * @param y2 floats representing the x,y of the second point to draw a line
     * @return nothing
     */
    protected void border(PDPageContentStream contentStream, float x1, float y1, float x2, float y2){
        try {
            contentStream.moveTo(x1, y1);
            contentStream.lineTo(x2, y2);
            contentStream.stroke();
        }catch(java.io.IOException ex){}
    }


    /**
     * Generate a border at the top of the box
     *
     */
    protected void addBorderTop(PDPageContentStream contentStream){

        border(contentStream, m_x, m_y+m_height+0.1f, m_x+m_width, m_y+m_height+0.1f);
    }
    /**
     * Generate a border at the bottom of the box
     *
     */
    protected void addBorderBottom(PDPageContentStream contentStream){
        border(contentStream, m_x, m_y-0.1f, m_x+m_width, m_y-0.1f);
    }
    /**
     * Generate a border at the left of the box
     *
     */
    protected void addBorderLeft(PDPageContentStream contentStream){
        border(contentStream, m_x+0.1f, m_y, m_x+0.1f, m_y+m_height);
    }
    /**
     * Generate a border at the right of the box
     *
     */
    protected void addBorderRight(PDPageContentStream contentStream){
        border(contentStream, (m_x+m_width) - 0.2f, m_y, (m_x+m_width) - 0.2f, m_y+m_height);
    }

    /**
     * Add a colored background to the box
     *
     *
     */
    protected void renderBackground(PDPageContentStream contentStream){
        try {
            contentStream.setLineWidth(0.5f);
            contentStream.setNonStrokingColor(m_background.getRed(), m_background.getGreen(), m_background.getBlue()); //gray background
            contentStream.addRect(m_x, m_y, m_width, m_height);
            contentStream.fill();
        }catch(java.io.IOException ex){}
    }

    protected void setBackground(Color bg){
        m_background = bg;
    }




    protected void setCenterAlign(boolean isAlign){
        m_center_align=isAlign;
    }

    protected void setVerCenterAlign(boolean isAlign){
        m_ver_center_align=isAlign;
    }

    protected void setVerticalText(boolean isVertical){
        m_vertical_text = isVertical;
    }

    /**
     * Add to the object its x,y position
     * @param x coordinate
     *
     */
    protected void setPosition(float x, float y){
        m_x = x;
        m_y = y;
    }
    /**
     * Add flag to display borders
     * @param top border
     * @param bottom border
     * @param left border
     * @param right border
     */
    protected void setBorders(boolean top, boolean bottom, boolean left, boolean right){
        m_border_top = top;
        m_border_bottom = bottom;
        m_border_left = left;
        m_border_right = right;
    }

    /**
     * Add to the box its size
     * @param width in pixel
     * @param height in pixel
     */
    protected void setBoxSize(float width, float height){
        m_width = width;
        m_height = height;
    }


}
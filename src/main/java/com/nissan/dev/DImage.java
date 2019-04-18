package com.nissan.dev;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import java.util.Base64;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import sun.misc.BASE64Decoder;


import java.awt.*;
import java.io.IOException;

/**
 * @author Damien Contreras 2018
 * This heritate from DTextBox and fix the color of the background to yellow and is used to represent labels
 *
 */
public class DImage extends DBox{
    /**
     * Standard constructor for this class that fixes the color & fontSize
     *
     * @param doc PDFDoc base document
     * @param page Represent the page we will render into
     * @param url Text to display in the cell
     * @param x coordinates of the box
     * @param y coordinates of the box
     * @param width of the box
     * @param height of the box
     */

    public DImage(PDDocument doc, PDPage page, String url, float x, float y, int width, int height) {
        super(doc, page);
        setImageURL(url);

        setBoxSize(width, height);
        setPosition(x, y);

    }

    @Override
    public void render(){
        super.renderImage();
        try (PDPageContentStream contentStream = new PDPageContentStream(m_doc, m_page,true,false)) {
            writeImage(m_doc,contentStream,m_image_url,m_width,m_height,m_x,m_y);
        }catch (IOException ex){
            System.out.println("Error");
        }
    }

    public void writeImage(PDDocument doc,PDPageContentStream contentStream,String imageUrl, float width, float height, float sx, float sy) {
        try {
            if(imageUrl==null || imageUrl.trim().isEmpty()) return;
            PDImageXObject pdImage = null;
            try {
                if (imageUrl.indexOf("base64,") != -1) {
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] decodedBytes = decoder.decodeBuffer(imageUrl.split(",")[1]);
                    pdImage = PDImageXObject.createFromByteArray(doc,decodedBytes,"png");
                } else {
                    pdImage = PDImageXObject.createFromFile(imageUrl, doc);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
            if(pdImage==null) return;
            float scale = 0.99f;
            contentStream.drawImage(pdImage, sx, sy, width * scale, height * scale);
        } catch (IOException ex){
            System.out.println("Error");
        }
    }


}
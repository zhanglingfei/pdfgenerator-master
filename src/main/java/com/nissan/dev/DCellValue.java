
package com.nissan.dev;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.*;


/**
 * DcellValue is used to display a cell with a value in it
 *
**/

public class DCellValue extends DTextBox {
    /**
     *  @author Damien Contreras 2018
     * Standard constructor for this class that fixes the color & fontSize
     *
     * @param doc PDFDoc base document
     * @param page Represent the page we will render into
     * @param text Text to display in the cell
     * @param x coordinates of the box
     * @param y coordinates of the box
     * @param width of the box
     * @param height of the box
     */
    public DCellValue(PDDocument doc, PDPage page, String text, float x, float y, int width, int height) {
        super(doc,page);
        setPosition(x, y);
        setBoxSize(width,height);
        setBackground(new Color(221, 235, 247));
        setFontSize(6);
        setText(text);
    }



    @Override
    public void render(){
        super.render();
    }


}


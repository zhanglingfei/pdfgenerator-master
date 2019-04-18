package com.nissan.dev;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.awt.*;

/**
 * @author Damien Contreras 2018
 * This heritate from DTextBox and fix the color of the background to yellow and is used to represent labels
 *
 */
public class DHeader extends DTextBox{
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
     */

    public DHeader(PDDocument doc, PDPage page, String text, PDType0Font font, float x, float y, int width, int height,int isVertical,int isCenter) {


        super(doc, page);
        setBoxSize(width, height);
        setPosition(x, y);
        setBackground(new Color(255, 192,0));
        setBorders(true, true, true, true);
        setFont(font);
        setFontSize(8);
        setText(text);
        setVerticalText(isVertical==1);
        setCenterAlign(isCenter==1);
    }

    @Override
    public void render(){
        super.render();
        //renderText();
    }
}
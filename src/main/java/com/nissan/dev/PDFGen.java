package com.nissan.dev;

import java.io.File;
import java.io.IOException;
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
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.List;

import static javax.swing.text.html.CSS.Attribute.FONT_SIZE;


/**
 * @author Damien Contreras 2018
 * Main class to generate PDF
 *
*
 */
public class PDFGen  {
    /**
     * Standard constructor for this class that fixes the color & fontSize
     *
     * @param inputFile base file to be used
     * @param imagePath used to specify the image to be used as a stamp
     * @param outputFile filename to be generated, if it already exists it will be replaced
     */
    Logger logger = LoggerFactory.getLogger(PDFGen.class);

    public void createPDFFromImage(String inputFile, String imagePath, String outputFile) throws IOException {

        // try (PDDocument doc = PDDocument.load(new File(inputFile))) {
        try(PDDocument doc = new PDDocument()){

            PDPage page = new PDPage();
            doc.addPage(page);

           // PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {

               // DTemplate tp = new DTemplate(doc);

                DTemplate tp = new DTemplate(doc, "./src/main/resources/KosugiMaru-Regular.ttf");

                tp.setDefaultPage(page); //TODO : replace by an automatic generation of pages within the DTemplate JSON parsing
                tp.parse("./src/main/resources/template8.json"); //template
                tp.getValuesFromJson("./src/main/resources/data_test.json"); //add values




                tp.render();
            }
            //encryption
            int keyLength = 256;
            AccessPermission ap = new AccessPermission();
            StandardProtectionPolicy spp = new StandardProtectionPolicy("12345", "", ap);

            spp.setEncryptionKeyLength(keyLength);
            spp.setPermissions(ap);
            doc.protect(spp);

            doc.save(outputFile);
            doc.close();
        }




    }
    /**
     * For test purpose only
     *
     */
    public void createBox(PDPageContentStream contents, PDType0Font font,  String boxContent, float x, float y, int width, int height, int borderLeft, int borderRight, int borderTop, int borderBottom){
        try {
            contents.setNonStrokingColor(221, 235, 247); //gray background
            contents.fillRect(x, y, width, height);

            //borders
            contents.drawLine(x, y, x+width, y); // border bottom
            contents.drawLine(x, y+height, x+width, y+height); // border top
            contents.drawLine(x, y, x, y+height); // border left
            contents.drawLine(x+width, y, x+width, y+height); //border right

            //text content
            contents.beginText();

            //writeText(contents, width,height, x, y, boxContent, false);
            /*contents.setFont(font, 20);
            contents.setNonStrokingColor(0, 0, 0);
            contents.moveTextPositionByAmount(x+5, y+5);
            //contents.newLineAtOffset(x, y);
            contents.drawString(boxContent);*/
            contents.endText();

        }catch (IOException ex){
            System.err.println("Error");
        }


    }

    /**
     * Main function no parameters expected
     *
     */
    public static void main(String[] args) {
        PDFGen app = new PDFGen();
        try {

            app.createPDFFromImage("/Users/damiencontreras/Desktop/Kylo-Edge.pdf", "/Users/damiencontreras/Desktop/anko.png", "/Users/damiencontreras/Desktop/test2.pdf");
           // app.createPDFFromImage("C:/Test/Kylo-Edge.pdf", "C:/Test/minions.png", "C:/Test/test2.pdf");
        }catch (IOException ex){
            System.err.println("Error");
            ex.printStackTrace();
        }

    }


}
package com.nissan.dev;

import java.io.*;

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
 * This class is in charge to expose the PDF generation to NiFi
 *
 */
public class DPDFGenerator {


/**
 * Standard constructor for this class that fixes the color & fontSize
 *
 * @param in inputStream coming from nifi that represents a JSON file with key/values
 * @param templateFile defines which template to be used to generate the PDF
 * @param out defines the output stream in which to put the data
 * */
    public void generatePDF(InputStream in, String templateFile, String fontFile, OutputStream out){
        try {

            try(PDDocument doc = new PDDocument()){

                PDPage page = new PDPage();
                doc.addPage(page);

                // PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);

                try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {

                    DTemplate tp = new DTemplate(doc, fontFile);

                    tp.setDefaultPage(page);
                    tp.parse(templateFile); //template
                    tp.getValuesFromJson(in); //add values
                    tp.render(); // rendering of all items
                }
                //encryption
                int keyLength = 256;
                AccessPermission ap = new AccessPermission();
                StandardProtectionPolicy spp = new StandardProtectionPolicy("nissan2355", "", ap);

                spp.setEncryptionKeyLength(keyLength);
                spp.setPermissions(ap);
                doc.protect(spp);

                doc.save(out);
                doc.close();
            }
        }catch (IOException ex){
            System.err.println("Error");
            ex.printStackTrace();
        }
    }


} //class

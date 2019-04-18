package com.nissan.dev;


import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

public class SigUtils {

    private SigUtils()
	    {
        	    }
        	    /**
 38	     * Get the access permissions granted for this document in the DocMDP transform parameters
 39	     * dictionary. Details are described in the table "Entries in the DocMDP transform parameters
 40	     * dictionary" in the PDF specification.
 41	     *
 42	     * @param doc document.
 43	     * @return the permission value. 0 means no DocMDP transform parameters dictionary exists. Other
 44	     * return values are 1, 2 or 3. 2 is also returned if the DocMDP transform parameters dictionary
 45	     * is found but did not contain a /P entry, or if the value is outside the valid range.
 46	     */
        	    public static int getMDPPermission(PDDocument doc)
	    {
        	        COSBase base = doc.getDocumentCatalog().getCOSObject().getDictionaryObject(COSName.PERMS);
               if (base instanceof COSDictionary)
            	        {
            	            COSDictionary permsDict = (COSDictionary) base;
            	            base = permsDict.getDictionaryObject(COSName.DOCMDP);
            	            if (base instanceof COSDictionary)
                	            {
                	                COSDictionary signatureDict = (COSDictionary) base;
                	                base = signatureDict.getDictionaryObject("Reference");
                	                if (base instanceof COSArray)
                    	                {
                    	                    COSArray refArray = (COSArray) base;
                    	                    for (int i = 0; i < refArray.size(); ++i)
                                            {
                                                base = refArray.getObject(i);
                        	                        if (base instanceof COSDictionary)
                            	                        {
                            	                            COSDictionary sigRefDict = (COSDictionary) base;
                            	                            if (COSName.DOCMDP.equals(sigRefDict.getDictionaryObject("TransformMethod")))
                                	                            {
                                	                                base = sigRefDict.getDictionaryObject("TransformParams");
                                	                                if (base instanceof COSDictionary)
                                    	                                {
                                    	                                    COSDictionary transformDict = (COSDictionary) base;
                                    	                                    int accessPermissions = transformDict.getInt(COSName.P, 2);
                                    	                                    if (accessPermissions < 1 || accessPermissions > 3)
                                        	                                    {
                                        	                                        accessPermissions = 2;
                                        	                                    }
                                    	                                    return accessPermissions;
                                    	                                }
                                	                            }
                            	                        }
                        	                    }
                    	                }
                	            }
            	        }
        	        return 0;
        	    }

       	    /**
 90	     * Set the access permissions granted for this document in the DocMDP transform parameters
 91	     * dictionary. Details are described in the table "Entries in the DocMDP transform parameters
 92	     * dictionary" in the PDF specification.
 93	     *
 94	     * @param doc The document.
 95	     * @param signature The signature object.
 96	     * @param accessPermissions The permission value (1, 2 or 3).
 97	     */
    static public void setMDPPermission(PDDocument doc, PDSignature signature, int accessPermissions)
 {
        	        COSDictionary sigDict = signature.getCOSObject();

        	        // DocMDP specific stuff
        	        COSDictionary transformParameters = new COSDictionary();
        	        transformParameters.setItem(COSName.TYPE, COSName.getPDFName("TransformParams"));
        	        transformParameters.setInt(COSName.P, accessPermissions);
        	        transformParameters.setName(COSName.V, "1.2");
        	        transformParameters.setNeedToBeUpdated(true);

                    COSDictionary referenceDict = new COSDictionary();
        	        referenceDict.setItem(COSName.TYPE, COSName.getPDFName("SigRef"));
        	        referenceDict.setItem("TransformMethod", COSName.DOCMDP);
        	        referenceDict.setItem("DigestMethod", COSName.getPDFName("SHA1"));
        	        referenceDict.setItem("TransformParams", transformParameters);
        	        referenceDict.setNeedToBeUpdated(true);

        	        COSArray referenceArray = new COSArray();
        	        referenceArray.add(referenceDict);
        	        sigDict.setItem("Reference", referenceArray);
        	        referenceArray.setNeedToBeUpdated(true);

        	        // Catalog
        	        COSDictionary catalogDict = doc.getDocumentCatalog().getCOSObject();
        	        COSDictionary permsDict = new COSDictionary();
        	        catalogDict.setItem(COSName.PERMS, permsDict);
        	        permsDict.setItem(COSName.DOCMDP, signature);
        	        catalogDict.setNeedToBeUpdated(true);
        	        permsDict.setNeedToBeUpdated(true);
        }


}

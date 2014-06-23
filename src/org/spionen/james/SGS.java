package org.spionen.james;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JOptionPane;

/**
 * This class does not seem to be used at all
 * @author Maxim
 */
public class SGS {
    
    private static String referenceFolderPath = GetFile.jamesReferencePath;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        
        prepareSGS();
    }
    
    public static void prepareSGS() throws FileNotFoundException, IOException {
        
        ArrayList<String> fileContent = new ArrayList<String>();
        ArrayList<SGSAdress> sgsLista = new ArrayList<SGSAdress>();
        SGSAdress sgsAdress;
        
        File sgsFile = new File(referenceFolderPath + "SGS-Adresser.csv");
        
        ListHelpers.copyRows(sgsFile, fileContent);
        
        //DEBUG
        System.out.println("File contains " + fileContent.size() + " rows.");    
        
        String row;
        String[] rowFields;
        String gatuAdress   = null;
        String adressSpec   = null; 
        String postNr       = null; 
        String pOrt         = null;
        
        for (int i = 1; i < fileContent.size(); i++) {
        
            row = fileContent.get(i).toUpperCase();
            rowFields = row.split(";");
            if (row.contains(" LÄG")) {
                gatuAdress = rowFields[0].split(" LÄG")[0].trim();
                adressSpec = "LÄG " + rowFields[0].split(" LÄG")[1].trim();
            } else {
                if (row.contains(" RUM ")) {
                    gatuAdress = rowFields[0].split(" RUM ")[0].trim();
                    adressSpec = "RUM " + rowFields[0].split(" RUM ")[1].trim();
                } else {
                    if (row.contains("/")) {
                        gatuAdress = rowFields[0].split("/")[0].trim();
                        adressSpec = "LÄG " + rowFields[0].split("/")[1].trim();
                    } else {
                        JOptionPane.showMessageDialog(null, "Jag kollar igenom SGS adresser.\n"+
                                "Denna adressraden ("+row+")\n är felaktig. Vänligen korrigera den och försök igen.");
                    }
                }
            }
            
            String postAdress = rowFields[2];
            if (postAdress.length() > 5) {
                postNr  = rowFields[2].substring(0, 5);
                pOrt    = rowFields[2].substring(6);
            } else {
                postNr  = "empty";
                pOrt    = "empty";
            }

            System.out.println(gatuAdress + ";" + 
                               adressSpec + ";" +
                               postNr + ";" +
                               pOrt);
            
            sgsAdress = new SGSAdress(gatuAdress, adressSpec, postNr, pOrt);
            sgsLista.add(sgsAdress);
        }
        
        System.out.println("SGS-listan innehåller " + sgsLista.size() + " unika adresser.");
        System.out.println(sgsLista.get(1).getAll());
        Collections.sort(sgsLista);
        System.out.println(sgsLista.get(1).getAll());
        
        String a1 = null;
        String a2 = null;
        int counter = 1;
        
        for (int i = 0; i < sgsLista.size(); i++) {
            a2 = sgsLista.get(i).getGatuAdress() + ";" + sgsLista.get(i).getPostNr();
            if (a2.equals(a1)) {
                counter++;
            } else {
                System.out.println(a1 + ";" + counter);
                counter = 1;
                a1 = a2;
            }
        }
    }
}

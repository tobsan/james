package org.spionen.james;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Maxim
 */

public class Import {

	private static String inPath = GetFile.jamesImportPath;
    
    public static void importAll(int year, int issue) 
                  throws FileNotFoundException, 
                         IOException {
               
       // Log Status
        Helpers.logStatusMessage("Importing Files - Started.");

        //Skapa ArrayList
        ArrayList<String>       fileRows    = new ArrayList<String>();
        ArrayList<Prenumerant>  masterList  = new ArrayList<Prenumerant>();
        ArrayList<Prenumerant>  rejectList  = new ArrayList<Prenumerant>();
        
        
        // Kolla Importfolder och skapa lista med filer f�r import. 
             
        String filter = "txt";
         
        ArrayList<File> listOfFilesInFolder = 
                Helpers.makeArrayListOfFiles(inPath, filter);
        
        for (int i = 0; i < listOfFilesInFolder.size(); i++) {
               
            System.out.println(listOfFilesInFolder.get(i));
            
            // Kollar upp filens namn s� k�llan kan anges i masterfilen
            String fileName = listOfFilesInFolder.get(i).getName();       
            fileName = fileName.substring(0, fileName.indexOf("."));
                 
            ListHelpers.copyRows(listOfFilesInFolder.get(i), fileRows);
            
            // Snygga till s� alla rader har lika m�nga f�lt
            fileRows = justifyRows(fileRows);
            
            // Konvertera raderna och �verf�ra till masterList
            convertRows(fileRows, masterList, fileName);
            fileRows.clear();
            
            System.out.println("Mastern = " + masterList.size());
            
        }
  
       //Sort
       Collections.sort(masterList);
       System.out.println("Master AL sorterad.");
        
  
        // Log Status
        Helpers.logStatusMessage("Importing Files - Completed.");
        
       
       //FOLLOWING IS CODE FROM OLD METHOD
        
        
       // Log Status
       Helpers.logStatusMessage("Cleaning Master: Incomplete Rows - Started.");

       //Clean away incorrect addresses

       int cc = 0; //Counter correct addresses.
       int ic = 0; //Counter incorrect addresses.

       for (int i = 0; i < masterList.size(); i++) {

           Prenumerant p = masterList.get(i);

           if (p.correctAdress()) {

                cc++;

           } else {

                   ic++;
                   p.setDistributor("I");
                   rejectList.add(p);
                   masterList.remove(i);
                   i--;
           }

       }

       System.out.println("Master AL st�dad. " + cc + " Korrekta, " +
                                                 ic + " inkorrekta");

       System.out.println("Master AL best�r av: " + masterList.size() + " Objekt.");
       System.out.println("Rejects AL best�r av: " + rejectList.size() + " Objekt.");

       // Log Status
       Helpers.logStatusMessage("Cleaning Master: Incomplete Rows - Finished.");

       // Log Status
       Helpers.logStatusMessage("Cleaning Master: Duplicate Rows - Started.");

       //Clean away duplicates

       int up = 0; //Counter unique objects;
       int dp = 0; //Counter duplicate objects;

       Prenumerant p1 = null;
       Prenumerant p2 = null;

       for (int i = 0; i < masterList.size(); i++) {

           p1 = masterList.get(i);

           if (masterList.size() == i+1)    {p2 = masterList.get(0);}
           else                         {p2 = masterList.get(i+1);}

           if (p1.getAbNr().equals(p2.getAbNr())
               && !(p1.getAbNr().equals("000"))) {

               if (p1.getAbNr().length() >= 9) {

                   if (p1.getTyp().contains("STUDENT")
                       || p1.getTyp().contains("DOKTORAND")) {

                       p2.setDistributor("D");
                       rejectList.add(p2);
                       masterList.remove(i+1);
                       i--;

                   } else {

                       p1.setDistributor("D");
                       rejectList.add(p1);
                       masterList.remove(i);

                   }

               }

               dp++;
           } else {up++;}

       } // END For-loop


       System.out.println("Master AL st�dad. " + up + " unika, " +
                                                 dp + " dubletter");

       System.out.println("Master AL best�r av: " + masterList.size() + " Objekt.");
       System.out.println("Rejects AL best�r av: " + rejectList.size() + " Objekt.");

       // Log Status
       Helpers.logStatusMessage("Cleaning Master: Duplicate Rows - Finished.");

       // Log Status
       Helpers.logStatusMessage("Filter Master: NoThanks-List - Started.");

       //Rensa noThanks

       // Create and Populate the ArraysList.
       ArrayList<Prenumerant> noThanks = new ArrayList<Prenumerant>();

       ListHelpers.readFromFileToList(GetFile.noThanksFilePath, noThanks);

       int nc = 0; //NoThanks counter.

       System.out.println("NoThanks AL best�r av: " + noThanks.size() + " Objekt.");


       for (int i = 0; i < noThanks.size(); i++) {

           String abNr = noThanks.get(i).getAbNr();

           int index = ListHelpers.searchListForAbNr(masterList, abNr);

           if (index >= 0) {

               masterList.get(index).setDistributor("N");
               masterList.get(index).setNote("Vill ej ha Spionen");
               nc++;
           }

       }

       System.out.println("No Thanks = " + nc);
       System.out.println("Master AL best�r av: " + masterList.size() + " Objekt.");
       System.out.println("Rejects AL best�r av: " + rejectList.size() + " Objekt.");
       System.out.println("NoThanks AL best�r av: " + noThanks.size() + " Objekt.");

       // Log Status
       Helpers.logStatusMessage("Filter Master: NoThanks-List - Started.");

       // Log Status
       Helpers.logStatusMessage("Saving Files: Started.");

       //Save MasterFile

       ListHelpers.stateSaveListAsFile(masterList, GetFile.currentMaster(year, issue));
//       ListHelpers.stateSaveListAsFile(rejectList, rejectsFilePath);
       ListHelpers.stateSaveListAsFile(rejectList, GetFile.rejects(year, issue));

       System.out.println("Master AL best�r av: " + masterList.size() + " Objekt.");

       // Log Status
       Helpers.logStatusMessage("Saving Files: Finished.");
       
       Exporter.prepareMasterForExport(year, issue);

    }
    
    
    public static ArrayList<String> justifyRows(ArrayList<String> inArrayList) {
                
        int nrOfFields = 0;
        int high = -100; 
        String row; 

        
        // Ta reda p� hur m�nga f�lt det finns i l�ngsta raden 
        
        for (int i = 0; i < inArrayList.size(); i++) {

            row = inArrayList.get(i);
            
            // Making sure all rows end with ; 
            if (!(row.endsWith(";"))) {row = row + ";";}
            
            // Getting the number of fields in row
            nrOfFields = Helpers.numberOfFieldsInRow(row, ";");

            if (nrOfFields > high) {high = nrOfFields;}

        }

        // Se till att alla rader har samma antal f�lt
    
        for (int i = 0; i < inArrayList.size(); i++) {

            row = inArrayList.get(i); 
            nrOfFields = Helpers.numberOfFieldsInRow(row, ";");

            if (nrOfFields < high) {

                for (int y = nrOfFields; y < high; y++) {

                    row = row + ";";

                }

            }

            
            //System.out.println(row);
            
            row = row.replaceAll(";;", "; ;");
            
            //System.out.println(row);
            
            
            inArrayList.set(i, row);

        }
        
        return inArrayList; 
    
    }
    
    
    public static void convertRows(ArrayList<String> fromArrayList, 
                                   ArrayList<Prenumerant> toArrayList, 
                                   String sourceFile) {
        
        String  prenNr      = "",
                namn        = "",
                eNamn       = "",
                coAdress    = "",
                gatuAdress  = "",
                postNr      = "",
                postOrt     = "",
                land        = "",
                source      = sourceFile;

        int  indexOf_prenNr,
             indexOf_namn,
             indexOf_eNamn,
             indexOf_coAdress,
             indexOf_gatuAdress,
             indexOf_postNr,
             indexOf_postOrt,
             indexOf_land;
        
        // prenNr ; namn ; eNamn ; coAdress ; gatuAdress ; postNr ; postOrt ; land
        
        String indexRow = fromArrayList.get(0);
        
        indexOf_prenNr      = findIndexOf("prenNr", indexRow, ";");
        indexOf_namn        = findIndexOf("namn", indexRow, ";");
        indexOf_eNamn       = findIndexOf("eNamn", indexRow, ";");
        indexOf_coAdress    = findIndexOf("coAdress", indexRow, ";");
        indexOf_gatuAdress  = findIndexOf("gatuAdress", indexRow, ";");
        indexOf_postNr      = findIndexOf("postNr", indexRow, ";");
        indexOf_postOrt     = findIndexOf("postOrt", indexRow, ";");
        indexOf_land        = findIndexOf("land", indexRow, ";");

        
        //DEBUG
        System.out.println("indexOf_prenNr is " + indexOf_prenNr);
        System.out.println("indexOf_namn is " + indexOf_namn);
        System.out.println("indexOf_eNamn is " + indexOf_eNamn);
        System.out.println("indexOf_coAdress is " + indexOf_coAdress);
        System.out.println("indexOf_gatuAdress is " + indexOf_gatuAdress);
        System.out.println("indexOf_postNr is " + indexOf_postNr);
        System.out.println("indexOf_postOrt is " + indexOf_postOrt);
        System.out.println("indexOf_land is " + indexOf_land);
        
        String fromRow, toRow;
        String[] rowFields;
        Prenumerant prenumerant; 
                
        for (int i = 1; i < fromArrayList.size(); i++) {
        
            fromRow = fromArrayList.get(i);
            
            rowFields = fromRow.split(";");
            
            if (indexOf_prenNr >= 0) {
                prenNr = rowFields[indexOf_prenNr];
                prenNr = Helpers.transformPNr(prenNr);
            } else {prenNr = "000";} 

            if (indexOf_namn >= 0) {
                namn = rowFields[indexOf_namn];
            } 

            if (indexOf_eNamn >= 0) {
                eNamn = rowFields[indexOf_eNamn];
            } 
            
            if (indexOf_coAdress >= 0) {
                coAdress = rowFields[indexOf_coAdress];
            } 

            if (indexOf_gatuAdress >= 0) {
                gatuAdress = rowFields[indexOf_gatuAdress];
            } 

            if (indexOf_postNr >= 0) {
                postNr = rowFields[indexOf_postNr];
                postNr = Helpers.cleanPostNr(postNr);
            } 
            if (indexOf_postOrt >= 0) {
                postOrt = rowFields[indexOf_postOrt];
            } 

            if (indexOf_land >= 0) {
                land = rowFields[indexOf_land];
            } 

            toRow = prenNr + ";" +
                    namn + ";" +
                    eNamn + ";" +
                    coAdress + ";" +
                    gatuAdress + ";" +
                    postNr + ";" +
                    postOrt + ";" +
                    land + ";" +
                    "0" + ";" +
                    source + ";" +
                    "0";

            // M�STE ST�DA RADEN F�RST
            // Om raden �r tom s� skall den inte l�ggas in. 
            
            toRow = Helpers.cleanRow(toRow);
            
            if (toRow.length() > 15) {
            
                prenumerant = new Prenumerant(toRow);

                toArrayList.add(prenumerant);
                
                //System.out.println(prenumerant.masterFormat());
            }
            
            
            
        }
                
    }
 
    public static int findIndexOf(String searchTerm, 
                                  String inRow, 
                                  String withRowDelimiter) {
      
        String[] indexes = inRow.split(withRowDelimiter);
      
        int index = -1;
  
        for (int i = 0; i < indexes.length; i++) {
      
            if (indexes[i].equalsIgnoreCase(searchTerm)) {index = i;}
      
        }
      
        return index;
  
    }
    
}

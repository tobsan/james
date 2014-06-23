package org.spionen.james;

/**
 * Write a description of class NoThanks here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

import org.spionen.james.subscriber.Subscriber;

public class VTD_NoFind
{

//    private static String masterFilePath = James.masterFilePath;
//    private static String notForVTDFilePath = James.notForVTDFilePath;
    private static String vtdRejectsPath = GetFile.jamesVTDRejectsPath;
    private static String filterPath = GetFile.jamesFilterPath;

    public static void noFind(int year, int issue) throws FileNotFoundException, IOException {
        
        String masterFilePath = GetFile.currentMaster(year, issue);
        String notForVTDFilePath = GetFile.notForVTDFilePath;

        //Create and populate Lists
        ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> notForVTD    = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);
        ListHelpers.readFromFileToList(notForVTDFilePath, notForVTD);

        System.out.println("Mastern..: " + master.size() + " poster.");
        System.out.println("NotVTD...: " + notForVTD.size() + " poster.");

        // Set up filter (Bring)
        File bring  = new File(filterPath + "Bring.txt");
        ArrayList<String> filterBring   = new ArrayList<String>();
        filterBring = Filter.createFilterArray(bring);
        
        //
        String abNr = JOptionPane.showInputDialog(null,
                "Skriv in prenumerationsnummer på den som \nVTD ej hittar.");

        if (abNr.length() <= 10 && !(abNr.length() < 4)) {

            JOptionPane.showMessageDialog(null,
                    "Du har anggivit prenumerationsnummer: " + abNr);

            int index = ListHelpers.searchListForAbNr(master, abNr);

            if (index >= 0) {

                Subscriber p = master.get(index);
                String postNr = p.getZipCode();

                JOptionPane.showMessageDialog(null, "Prenumerant: "
                            + p.getFullName());

                System.out.println(p.toString());
                
                if (Filter.checkIfInRange(postNr, filterBring)) {
                    p.setDistributor("B");
                } else {
                    p.setDistributor("P");
                }
                
// TODO Remove old code after funtion noFind() is tested with new code. 
//                if (p.isBring()) {
//
//                    p.setDistributor("B");
//
//                } else {
//
//                    p.setDistributor("P");
//
//                }

                p.setNote("VTD kan ej hantera/hitta adressen.");

                index = ListHelpers.searchListForAbNr(notForVTD, abNr);

                if (index >= 0) {
                    notForVTD.remove(index);
                    notForVTD.add(p);
                } else {
                    notForVTD.add(p);
                }

                System.out.println(p.toString());

            } else {
                JOptionPane.showMessageDialog(null,
                        "Hittar ingen prenumerant med prenumerationsnummer "
                        + abNr + "\nVänligen kontrollera och försök igen. ");
            }

        } else {
            JOptionPane.showMessageDialog(null,
                "Något verkar vara fel på prenumerationsnumret.\n" +
                "Vänligen kontrollera prenumerationsnumret och försök igen.\n" +
                "Angivet prenumerationnummer: " + abNr);
        }

        System.out.println("Mastern..: " + master.size() + " poster.");
        System.out.println("NotVTD...: " + notForVTD.size() + " poster.");

        //Save Lists
        ListHelpers.stateSaveListAsFile(master, masterFilePath);
        ListHelpers.stateSaveListAsFile(notForVTD, notForVTDFilePath);
    }
    
    public static void registerFromList(int year, int issue) throws IOException {
        
        String masterFilePath = GetFile.currentMaster(year, issue);
        String notForVTDFilePath = GetFile.notForVTDFilePath;

        String rejectsFilePath = vtdRejectsPath + "VTD-Rejects.txt";
  
        // Set up filter (Bring)
        File bring  = new File(filterPath + "Bring.txt");
        ArrayList<String> filterBring   = new ArrayList<String>();
        filterBring = Filter.createFilterArray(bring);
        
        // Kolla om källfilen finns
        if (Helpers.checkIfFileExists(rejectsFilePath)) {
        
            File source = new File(rejectsFilePath); 
            ArrayList<String> vtdMissar = new ArrayList<String>();
            
            ListHelpers.copyRows(source, vtdMissar);
            
            if (Helpers.checkIfStringIsNumeric(vtdMissar.get(0))) {

                JOptionPane.showMessageDialog(null, "Jag börjar kolla igenom listan. Den innehåller " + vtdMissar.size() + " rader s� ha lite t�lamod.");
                
                // �ppna referensfilerna.
                ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
                ArrayList<Subscriber> notForVTD    = new ArrayList<Subscriber>();

                ListHelpers.readFromFileToList(masterFilePath, master);
                ListHelpers.readFromFileToList(notForVTDFilePath, notForVTD);

                System.out.println("Mastern..: " + master.size() + " poster.");
                System.out.println("NotVTD...: " + notForVTD.size() + " poster.");
                
                // Counters
                int found       = 0;
                int notFound    = 0;
                
                for (int i = 0; i < vtdMissar.size(); i++) {
                
                    String abNr = vtdMissar.get(i);
                    int index = ListHelpers.searchListForAbNr(master, abNr);
                    
                    if (index >= 0) {
                        found++; 
                        Subscriber p = master.get(index);
                        String postNr = p.getZipCode();

                        if (Filter.checkIfInRange(postNr, filterBring)) {
                            p.setDistributor("B");
                        } else {
                            p.setDistributor("P");
                        }
                        
// TODO Remove old code after funtion registerFromList() is tested with new code.                         
//                        if (p.isBring()) {
//
//                            p.setDistributor("B");
//
//                        } else {
//
//                            p.setDistributor("P");
//
//                        }

                        p.setNote("VTD kan ej hantera/hitta adressen.");
                        index = ListHelpers.searchListForAbNr(notForVTD, abNr);
                        if (index >= 0) {
                            notForVTD.remove(index);
                            notForVTD.add(p);
                        } else {
                            notForVTD.add(p);
                        }
                    } else {
                        notFound++;
                    }
                }
                
                String message; 
                if (notFound > 0) {
                    message = found + " prenumeranter hittades och fixades.\n"
                        + notFound + " prenumeranter hittade inte ens jag.\n"
                        + "Förstår inte varför VTD försökte hitta dem?"; 
                
                } else {
                    message = found + " prenumeranter hittades och fixades.\n"
                            		+ "Yay för när allt går som det ska!"; 
                } 

                System.out.println("Mastern..: " + master.size() + " poster.");
                System.out.println("NotVTD...: " + notForVTD.size() + " poster.");

                //Save Lists
                ListHelpers.stateSaveListAsFile(master, masterFilePath);
                ListHelpers.stateSaveListAsFile(notForVTD, notForVTDFilePath);

                JOptionPane.showMessageDialog(null,  "Mission accomplished!\n" + message);
            } else {
                JOptionPane.showMessageDialog(null, "Booooo!!!");
            } 
        } else {
        
            JOptionPane.showMessageDialog(null, "Hmm...?\nJag hittar inte filen med listan över\n"
                    + "prenumeranter som VTD inte hittar till.\n"
                    + "Jag skapar en folder med filen så vi kan lösa detta smidigt.");
            
            // Skapa folder
            Helpers.makeSureFolderExists(vtdRejectsPath);
            
            // Skapa målfil med instruktioner i sig.
            PrintWriter targetFile = new PrintWriter(new BufferedWriter
                                      (new FileWriter(rejectsFilePath)));
            
            String message = "Hej! \n\n"
                    + "Listen carefully 'cause I'm only gonna say this once.\n\n"
                    + "F�r att skapa en lista �ver de prenumeranter som VTD inte hittade s� g�r du helt enkelt s� att du �ppnar deras Excel-dokument och kopierar kolumnen med prenumerantnummer. Bara den."
                    + " Klistra sedan in den i detta dokumentet. Klistra �ver denna text. Jag vill inte ha n�got annat �n siffror i hela dokumentet n�r du �r f�rdig. S� se �ven till att ta bort eventuell rubrik fr�n kolumnen. "
                    + " Annars kommer jag bli ledsen och vr�ka ur mig n�got konstigt felmeddelande och kanske crasha. Lite pinsamt... men s� �r det."
                    + "\n\n"
                    + "N�r allt �r inklistrat sparar du filen och trycker p� \"Reg. VTD misslista\"-knappen igen."
                    + " N�r jag �r f�rdig med att registrera adresserna som VTD missade s� hojtar jag till. "
                    + " Det borde inte ta mer �n n�gon sekund."
                    + "\n\n"
                    + "Hej s� l�nge!\n"
                    + "///James"; 
            
            targetFile.print(message);
            targetFile.close();  
            JOptionPane.showMessageDialog(null, "Fixat!\n"
                    + "Nu finns foldern (VTD Rejects) p� ditt skrivbord.\n"
                    + "I foldern hittar du en fil (VTD-Rejects.txt).\n"
                    + "�ppna filen och l�s instruktionerna. Noga.\n\n"
                    + "N�r du �r klar kan du klicka p� knappen\n"
                    + "s� f�rs�ker vi igen."); 
        }
        
        // Om K�llfilen finns s� skall den l�sas in
            
            // N�r f�rdig Dialog - Registrering av adresser VTD inte kan 
            // leverera till �r f�rdig. + Statistik. 
            
        // Om k�llfilen inte finns s� skall den skapas. 
           
            // N�r f�rdig Dialog - V�nligen kopiera in listan i K�llfilen. 
    
    }
}

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
import org.spionen.james.subscriber.Subscriber.Distributor;

public class VTD_NoFind {
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
        Filter filterBring = new Filter(Distributor.Bring, filterPath + "Bring.txt");
        
        long abNr = Long.parseLong(JOptionPane.showInputDialog(null,
                "Skriv in prenumerationsnummer på den som \nVTD ej hittar."));
        
        // if(abNr.length() <= 10 && !(abNr.length() < 4)) {
        if(abNr <= 9999999999L && !(abNr < 1000)) {
            JOptionPane.showMessageDialog(null,"Du har anggivit prenumerationsnummer: " + abNr);

            int index = ListHelpers.searchListForAbNr(master, abNr);
            if(index >= 0) {

                Subscriber p = master.get(index);
                JOptionPane.showMessageDialog(null, "Prenumerant: " + p.getFullName());

                System.out.println(p.toString());
                
                if(!filterBring.apply(p)) {
                	p.setDistributor(Distributor.Posten);
                }

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
        /*
        String masterFilePath = GetFile.currentMaster(year, issue);
        String notForVTDFilePath = GetFile.notForVTDFilePath;

        String rejectsFilePath = GetFile.jamesVTDRejectsPath + "VTD-Rejects.txt";
  
        // Set up filter (Bring)
        Filter filterBring = new Filter(Distributor.Bring, filterPath + "Bring.txt");
        
        // Kolla om källfilen finns
        if (Helpers.checkIfFileExists(rejectsFilePath)) {
            File source = new File(rejectsFilePath); 
            ArrayList<String> vtdMissar = new ArrayList<String>();
            ListHelpers.copyRows(source, vtdMissar);
            if (Helpers.checkIfStringIsNumeric(vtdMissar.get(0))) {

                JOptionPane.showMessageDialog(null, "Jag börjar kolla igenom listan. Den innehåller " + vtdMissar.size() + " rader så ha lite tålamod.");
                
                // Öppna referensfilerna.
                ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
                ArrayList<Subscriber> notForVTD    = new ArrayList<Subscriber>();

                ListHelpers.readFromFileToList(masterFilePath, master);
                ListHelpers.readFromFileToList(notForVTDFilePath, notForVTD);

                System.out.println("Mastern..: " + master.size() + " poster.");
                System.out.println("NotVTD...: " + notForVTD.size() + " poster.");
                
                // Counters
                int found       = 0;
                int notFound    = 0;
                
                for(int i = 0; i < vtdMissar.size(); i++) {
                    long abNr = Long.parseLong(vtdMissar.get(i));
                    int index = ListHelpers.searchListForAbNr(master, abNr);
                    if (index >= 0) {
                        found++; 
                        Subscriber p = master.get(index);
                        if(!filterBring.apply(p) ) {
                        	p.setDistributor(Distributor.Posten);
                        }
                        
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
            Helpers.makeSureFolderExists(GetFile.jamesVTDRejectsPath);
            
            // Skapa målfil med instruktioner i sig.
            PrintWriter targetFile = new PrintWriter(new BufferedWriter
                                      (new FileWriter(rejectsFilePath)));
            
            String message = "Hej! \n\n"
                + "Listen carefully 'cause I'm only gonna say this once.\n\n"
                + "För att skapa en lista över de prenumeranter som VTD inte hittade så gör du helt enkelt så att du öppnar deras Excel-dokument och kopierar kolumnen med prenumerantnummer. Bara den."
                + " Klistra sedan in den i detta dokumentet. Klistra över denna text. Jag vill inte ha något annat än siffror i hela dokumentet när du är färdig. Så se även till att ta bort eventuell rubrik från kolumnen. "
                + " Annars kommer jag bli ledsen och vräka ur mig något konstigt felmeddelande och kanske crasha. Lite pinsamt... men sä är det."
                + "\n\n"
                + "När allt är inklistrat sparar du filen och trycker på \"Reg. VTD misslista\"-knappen igen."
                + " När jag är färdig med att registrera adresserna som VTD missade så hojtar jag till. "
                + " Det borde inte ta mer än någon sekund."
                + "\n\n"
                + "Hej så länge!\n"
                + "///James"; 
            
            targetFile.print(message);
            targetFile.close();  
            JOptionPane.showMessageDialog(null, "Fixat!\n"
                    + "Nu finns foldern (VTD Rejects) på ditt skrivbord.\n"
                    + "I foldern hittar du en fil (VTD-Rejects.txt).\n"
                    + "Qppna filen och läs instruktionerna. Noga.\n\n"
                    + "När du är klar kan du klicka på knappen\n"
                    + "så försöker vi igen."); 
        }
        */
    }
}

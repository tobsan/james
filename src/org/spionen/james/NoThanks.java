package org.spionen.james;

/**
 *
 * @author  Maxim Fris
 * @version 2011-01-31
 */

import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class NoThanks
{

//    private static String masterFilePath = James.masterFilePath;
//    private static String noThanksFilePath = James.noThanksFilePath;

    public static void noThanks(int year, int issue) throws FileNotFoundException, IOException {

        String masterFilePath = GetFile.currentMaster(year, issue);
        String noThanksFilePath = GetFile.noThanksFilePath;
        
        //Create and popupate Lists
        ArrayList<Subscriber> master   = new ArrayList<Subscriber>();
        ArrayList<Subscriber> noThanks = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);
        ListHelpers.readFromFileToList(noThanksFilePath, noThanks);

        System.out.println("Mastern...: " + master.size() + " poster.");
        System.out.println("NoThanks..: " + noThanks.size() + " poster.");

        //
        String abNr = JOptionPane.showInputDialog(null,
                "Skriv in prenumerationsnummer på den som"
              + "\nönskar bli borttagen som mottagare av Spionen.");

        if (abNr.length() <= 10 && !(abNr.length() < 4)) {
            JOptionPane.showMessageDialog(null, "Du har angivit prenumerationsnummer: " + abNr);

            int index = ListHelpers.searchListForAbNr(master, abNr);
            if (index >= 0) {
                Subscriber p = master.get(index);
                JOptionPane.showMessageDialog(null, "Prenumerant: " + p.getFullName());

                System.out.println(p.masterFormat());

                p.setDistributor("N");
                p.setNote("Vill ej ha Spionen");
                noThanks.add(p);
                System.out.println(p.masterFormat());
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

        System.out.println("Mastern...: " + master.size() + " poster.");
        System.out.println("NoThanks..: " + noThanks.size() + " poster.");

        //Save Lists
        //ListHelpers.stateSaveListAsFile(master, masterFilePath);
        ListHelpers.stateSaveListAsFile(noThanks, noThanksFilePath);

    }

}

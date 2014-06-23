package org.spionen.james;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import javax.swing.JOptionPane;

import org.spionen.james.subscriber.Subscriber;
import org.spionen.james.subscriber.TBSubscriber;
import org.spionen.james.subscriber.VTDSubscriber;
import org.spionen.james.subscriber.VTabSubscriber;

/**
 *
 * @author Maxim
 */

public class ListHelpers {    
    
	// WTH?
   public static void main(String[] args) {
       

       DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
       String nowAsString = df.format(new Date());
       
       System.out.println(nowAsString);
       
       String distDate = null;
       int date = 0;
       boolean okDate = false;
       
       while (!okDate) {
       
           distDate = JOptionPane.showInputDialog(null,
              "Ange utdelningsdatum."
              + "\n(OBS!!!: FORMAT YYYY-MM-DD)");
       
           distDate = distDate.replaceAll("-", "");
       
           //Kolla så inmatat datum är i siffror
           try { date = Integer.parseInt(distDate); } 
           catch (NumberFormatException e) { continue; }
                      
           distDate = date + "000000";
           
           if (distDate.length() == 14) { okDate = true; }
       }
       
       System.out.println(distDate);   
   }
           
   public static void readFromFileToList(String filePath, ArrayList<Subscriber> al) throws FileNotFoundException, IOException {
	   /*
       BufferedReader fIn = new BufferedReader(new FileReader(filePath));
       String row = fIn.readLine();
       Subscriber p = null;

       while (row != null) {
           p = new Subscriber(row);
           al.add(p);
           row = fIn.readLine();
       }

       Collections.sort(al);
       fIn.close();
       */
   }

   /**
    * Method to save an ArrayList of Prenumerant-objects as a .txt file
    *
    * @param al             ArrayList for saving
    * @param fileName       String containing the wished fileName
    *
    * @throws IOException
    */
   
    public static void stateSaveListAsFile(ArrayList<Subscriber> al, String filePath) throws IOException {

       Collections.sort(al);
       String targetFilePath = filePath;

       PrintWriter targetFile = new PrintWriter(new BufferedWriter
                                (new FileWriter(targetFilePath)));

       int tc = 0; //Terstcounter
       for (int i = 0; i < al.size(); i++) {
           targetFile.println(al.get(i).toString());
           tc++;
       }

       targetFile.close();
       System.out.println("Testräknaren för (" + filePath + ") visar " + tc
                           + " objekt");
   }


    /**
     * Implements a binary search
     */
    public static int searchListForAbNr(ArrayList<Subscriber> al, String a) {

        int first = 0;                  // Första index
        int last = al.size() - 1;       // Sista index
        int middle = (first + last)/2;  // Mitten index
        int index = -1;                 // Returnerat index

        while ((first <= last) && !(al.get(middle).getAbNr().compareTo(a)==0)) {
            if (al.get(middle).getAbNr().compareTo(a)< 0) {first = middle + 1;}
            if (al.get(middle).getAbNr().compareTo(a)> 0) {last = middle - 1;}
            middle = (first + last) / 2;
        }

        if (al.get(middle).getAbNr().compareTo(a)==0) {index = middle;}
        return index;

    }



   /**
    * Method to save an ArrayList of Prenumerant-objects as a .txt file
    * for Export to V-Tab
    *
    * @param al             ArrayList for saving
    * @param fileName       String containing the wished fileName
    *
    * @throws IOException
    */

    public static void exportListForVTAB(ArrayList<Subscriber> al, String filePath) throws IOException {

       Collections.sort(al);
       String targetFilePath = filePath;
       PrintWriter targetFile = new PrintWriter(new BufferedWriter
                                (new FileWriter(targetFilePath)));

       int tc = 0; //Testcounter

       for (int i = 0; i < al.size(); i++) {
    	   VTabSubscriber vts = new VTabSubscriber(al.get(i));
           targetFile.println(vts.toString());
           tc++;
       }
       targetFile.close();
       System.out.println("Testräknaren för (" + filePath + ") visar " + tc
                           + " objekt");
   }

   /**
    * Method to save an ArrayList of Prenumerant-objects as a .txt file
    * for Export to Tidningsbärarna
    *
    * @param al             ArrayList for saving
    * @param fileName       String containing the wished fileName
    * @param distributionsdatum String in TB dateformat YYYYMMDD000000
    * 
    * @throws IOException
    */

    public static void exportListAsTB(ArrayList<Subscriber> startList, ArrayList<Subscriber> stopList,  
                                        String filePath, String distributionsdatum) throws IOException {

       Collections.sort(startList);
       Collections.sort(stopList);
       
       // Fixa datum för transaktion - Formattering
       DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
             
       String targetFilePath = filePath;       
       PrintWriter pw = new PrintWriter(targetFilePath, "ISO-8859-1");
       
       String prefix = "";
       String distributionDate = distributionsdatum;
       String transaktionTS    = df.format(new Date());
       String testKod = "  ";
       
       String line = "0005             " + distributionDate + transaktionTS + " SPI0VTD" + testKod +"           ";
       

       // Skriv Start-rader
       prefix = "A100";
       for (int i = 0; i < startList.size(); i++) {
    	   TBSubscriber tbs = new TBSubscriber(startList.get(i));
           pw.print(prefix + line + tbs.toString() + "\n");
       }

       // Skriv Stopp-rader
       prefix = "A101";
       for (int i = 0; i < stopList.size(); i++) {
    	   TBSubscriber tbs = new TBSubscriber(stopList.get(i));
           pw.print(prefix + line + tbs.toString() + "\n");
       }

       pw.close();
   }
   
    
   /**
    * Method to save an ArrayList of Prenumerant-objects as a .txt file
    * for Export to VTD
    *
    * @param al             ArrayList for saving
    * @param fileName       String containing the wished fileName
    *
    * @throws IOException
    */

    /*
    public static void exportListForVTD(ArrayList<Subscriber> al, String filePath) throws IOException {

       Collections.sort(al);
       String targetFilePath = filePath;
       PrintWriter targetFile = new PrintWriter(new BufferedWriter
                                (new FileWriter(targetFilePath)));

       int tc = 0; //Testcounter
       for (int i = 0; i < al.size(); i++) {
    	   VTDSubscriber vts = new VTDSubscriber(al.get(i));
           targetFile.println(vts.toString());
           tc++;
       }
       targetFile.close();
       System.out.println("Testräknaren för (" + filePath + ") visar " + tc
                           + " objekt");
   }
    */
    
   public static void copyRows(File fromFile, ArrayList<String> toArrayList)
                               throws FileNotFoundException, IOException {

       BufferedReader fIn = new BufferedReader(new FileReader(fromFile));
       String row = fIn.readLine();
       while (row != null) {
           toArrayList.add(row);
           row = fIn.readLine();
       }
       fIn.close();
   }
       
}




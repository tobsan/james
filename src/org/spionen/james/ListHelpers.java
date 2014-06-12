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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Maxim
 */

public class ListHelpers {    
    
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
       
           //Kolla s� inmatat datum �r i siffror
           try { date = Integer.parseInt(distDate); } 
           catch (NumberFormatException e) { continue; }
                      
           distDate = date + "000000";
           
           if (distDate.length() == 14) { okDate = true; }
       }
       
       System.out.println(distDate);   
   }
           
    
   public static void readFromFileToList(String filePath, ArrayList<Subscriber> al) throws FileNotFoundException, IOException {

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
           targetFile.println(al.get(i).masterFormat());
           tc++;
       }

       targetFile.close();
       System.out.println("Testräknaren för (" + filePath + ") visar " + tc
                           + " objekt");
   }


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
           targetFile.println(al.get(i).vTabFormat());
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

       PrintWriter targetFile = new PrintWriter(new BufferedWriter
                                (new FileWriter(targetFilePath)));
       
       PrintWriter pw = new PrintWriter(targetFilePath, "ISO-8859-1");
       
       String prefix = "";
       String distributionDate = distributionsdatum;
       String transaktionTS    = df.format(new Date());
       String testKod = "  ";
       
       String line = "0005             " + distributionDate + transaktionTS + " SPI0VTD" + testKod +"           ";
       

       // Skriv Start-rader
       prefix = "A100";
       
       for (int i = 0; i < startList.size(); i++) {
           pw.print(prefix + line + startList.get(i).tbFormat() + "\n");
           
           //targetFile.println(prefix + line + al.get(i).tbFormat());
       }

       // Skriv Stopp-rader
       prefix = "A101";
       
       for (int i = 0; i < stopList.size(); i++) {
           pw.print(prefix + line + stopList.get(i).tbFormat() + "\n");
           
           //targetFile.println(prefix + line + al.get(i).tbFormat());
       }

       pw.close();
       //targetFile.close();
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

    public static void exportListForVTD(ArrayList<Subscriber> al, String filePath) throws IOException {

       Collections.sort(al);
       String targetFilePath = filePath;
       PrintWriter targetFile = new PrintWriter(new BufferedWriter
                                (new FileWriter(targetFilePath)));

       int tc = 0; //Testcounter
       for (int i = 0; i < al.size(); i++) {
           targetFile.println(al.get(i).vtdFormat());
           tc++;
       }
       targetFile.close();
       System.out.println("Testräknaren för (" + filePath + ") visar " + tc
                           + " objekt");


   }
    
   public static void copyRows(File fromFile, ArrayList<String> toArrayList)
                               throws FileNotFoundException, IOException {

       BufferedReader fIn = new BufferedReader(new FileReader(fromFile));

       String row = fIn.readLine();

       while (row != null) {

           toArrayList.add(row);

           row = fIn.readLine();
       }

   }
       
   public static void getAllDistributorStatistic(int year, int issue) 
           throws FileNotFoundException, IOException {
       
       String masterFilePath    = GetFile.currentMaster(year, issue);

       ArrayList<Subscriber> master         = new ArrayList<Subscriber>();
       ListHelpers.readFromFileToList(masterFilePath, master);

        int i = 0;
       
        int vtd      = 0;
        int tb       = 0;
        int bring    = 0;
        int posten   = 0;
        int no       = 0;
        int special  = 0; 
        
        Subscriber p = null; //Temp-object
        String d = "";
        int l = 0;
        
        while (i < master.size()) {

            p = master.get(i);
            
            d = p.getDistributor();
            l = p.getAbNr().length();

            if (d.equals("V")) {vtd++;}
            else if (d.equals("T")) {tb++;}
            else if (d.equals("B")) {bring++;}
            else if (d.equals("P")) {
                posten++;
                if (l <= 5) {special++;}
            }
            else if (d.equals("N")) {no++;}          
                       
            i++;
            
        }
        
                JOptionPane.showMessageDialog(null,
                 "Statistik enligt nedan:\n"
                 + "Totalt: " + master.size() + " prenumeranter.\n"
                 + "Varav\n"
                 + "         VTD     : " + vtd + " st\n"
                 + "         TB       : " + tb + " st\n"
                 + "         Bring   : " + bring + " st\n"
                 + "         Posten : " + posten + " st (" + special + " special)\n"
                 + "och " + no + " som inte vill ha. tidningen alls.");
    
        
   }
   
   public static String toTB(String compoundAddress)
   {
       // -----------------------------------*****------****-----*----------
       // CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCNNNNNCCCCCCCCCCNNNNNCNNNNNNNNNN
       
       String formattedAddress = "";

       //System.out.println(compoundAddress);
       
       compoundAddress = prepareCompoundAddress(compoundAddress);
       
       //System.out.println(compoundAddress);
       
       String[] arr = compoundAddress.split(" ");
       String s = "";
                    
       boolean lgh = false;
       
       String gata = ""; //35
       int gatuNr = -1; //5
       String littra = ""; //6
       String door = ""; //4
       int floor = -1; //5
       String floorType = ""; //1
       int appNr = -1; //10
       //String appartment = "";
       
       if (arr.length > 0)
       {
           gata = arr[0];
           
           if (arr.length > 1)
           {
               for(int i = 1; i < arr.length; i++)
               {
                   s = arr[i];
                   
                   if (isInt(s))
                   {
                       if (lgh == false)
                       {
                           if (gatuNr == -1)
                           {
                               gatuNr = Integer.parseInt(s);
                           }
                           
                           else if(s.length() > 3) 
                           {
                               appNr = Integer.parseInt(s);
                           }
                                   
                           else 
                           {
                               floor = Integer.parseInt(s);
                           }
                       }
                       
                       else if (lgh == true)
                       {
                           appNr = Integer.parseInt(s);
                       }
                   }
                   
                   else 
                   {
                       if (isLGH(s)) { lgh = true; }
                       
                       else if (s.equalsIgnoreCase(":") || s.equalsIgnoreCase("-")) 
                       { 
                           if (i > 2 && i < arr.length)
                           {
                               if (isInt(arr[i+1]))
                               {
                                   if (arr[i+1].length() < 4)
                                   {
                                       littra = s + arr[i+1];
                                   }
                               }
                           }
                           
                           if (isInt(arr[i-1]))
                           {
                               littra = arr[i-1] + littra;
                           }
                           
                           i++;
                            
                       }
                       
                       else if (isFloor(s)) {floorType = "V";}
                       else if (isFlight(s)) {floorType = "T";}
                       
                       else if (s.equalsIgnoreCase("�G")) {door = "�G";}
                       else if (s.equalsIgnoreCase("UV")) {door = "UV";}
                       else if (s.equalsIgnoreCase("UH")) {door = "UH";}
                       else if (s.equalsIgnoreCase(":U")) { door = "U" + arr[i+1]; }
                       else if (s.equalsIgnoreCase("U")) { door = s + arr[i+1]; }

                       else if (littra.isEmpty() && s.length() == 1) { littra = s; }
                       
                       else if (gata.length() == 0) 
                       {
                           gata = s;
                       }
                       
                       else if (gatuNr == -1)
                       {
                           gata = gata + " " + s;
                       }
                       
                   }
                   

               }
           }
       }
       
       if (gatuNr == -1) { gatuNr = 0; }
       if (floor == -1) { floor = 0; }
       if (appNr == -1) { appNr = 0; }
       
       
       //if (appartment.isEmpty())
       //{
       //   if (appNr == -1) { appartment = ""; }
       //   else { appartment = appNr + "" ; }
       //}
       
       formattedAddress = String.format("%-35s%05d%-6s%-4s%05d%-1s%010d", gata, gatuNr, littra, door, floor, floorType, appNr);
       
       return formattedAddress;
       
   }
   
   
   public static boolean isInt(String s)
   {
       try {
           
           Integer.parseInt(s);
           return true;
       }
       catch(NumberFormatException nfe) {
           return false;
       }
   }
   
   public static boolean isLGH(String s)
   {
       if (s.equalsIgnoreCase("lgh")) { return true; }
       if (s.equalsIgnoreCase("l�g")) { return true; }
       else { return false; }
   }
   
   public static boolean isMixed(String s)
   {       
       boolean haveChar = false;
       boolean haveInt = false;
       
       String s1 = "";
       
       int sLength = s.length();
       
       for (int i = 0; i < sLength; i++)
       {
          s1 = s.substring(i, i+1);
          
          if (isInt(s1)) { haveInt = true; }
          else { haveChar = true; }
       }
       
       if (haveInt == true && haveChar == true) { return true; }
       else { return false; }
       
   }
   
   public static String splitMixed(String s)
   {
       String splitMix = "";
       String lastWas = "";
      
       String s1 = "";
       
       int sLength = s.length();
       
       for (int i = 0; i < sLength; i++)
       {
          s1 = s.substring(i, i+1);

          if (s1.equalsIgnoreCase(" "))
          {
              splitMix = splitMix + s1;
          }
          
          else if (isInt(s1)) 
          {        
              if (lastWas.equalsIgnoreCase("")) 
              {
                  splitMix = splitMix + s1;
              }
              
              else if (lastWas.equalsIgnoreCase("I")) 
              {
                  splitMix = splitMix + s1;
              }
              
              else 
              {
                  splitMix = splitMix + " " + s1;
              }
              
              lastWas = "I";
          }
          
          else 
          {
              if (lastWas.equalsIgnoreCase("")) 
              {
                  splitMix = splitMix + s1;
              }
              
              else if (lastWas.equalsIgnoreCase("C")) 
              {
                  splitMix = splitMix + s1;
              }
              
              else 
              {
                  splitMix = splitMix + " " + s1;
              }
              
              lastWas = "C";
          }
          
       }
       
       splitMix = splitMix.replaceAll("  ", " ");
       
       return splitMix;
       
   }
   
   public static String prepareCompoundAddress(String ca)
   {
       ca = ca.replaceAll(",", " ");
       ca = ca.replaceAll("\\.", " ");
       ca = ca.replaceAll("\\/", " ");
       ca = ca.replaceAll("N:A", "NORRA");
       ca = ca.replaceAll("V:A", "V�STRA");
       ca = ca.replaceAll("S:T", "SANKT");
       ca = ca.replaceAll("1:A", "F�RSTA");
       ca = ca.replaceAll("2:A", "ANDRA");
       ca = ca.replaceAll("3:E", "TREDJE");
       ca = ca.replaceAll("4:E", "FJ�RDE");
       ca = ca.replaceAll("5:E", "FEMTE");

       
       ca = splitMixed(ca);
       
       return ca;
   }
   
   public static boolean isFloor(String s)
   {
       if (s.equalsIgnoreCase("V�N")) {return true;}
       else {return false;}
   }
       
   public static boolean isFlight(String s)
   {
       if (s.equalsIgnoreCase("TR")) {return true;}
       else {return false;}
   }
       
}




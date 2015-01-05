package org.spionen.james.deprecated;

import java.io.*;
import java.util.*;
import javax.swing.*;

import org.spionen.james.subscriber.Subscriber;
import org.spionen.james.subscriber.Subscriber.Distributor;

/**
 * This class is only here for reference nowadays. Most of it is commented out
 * because the classes that were used have been changed or removed. Use only
 * for reference on what James is supposed to do. 
 * 
 * @author Maxim
 * @author Tobias (minor changes)
 *
 */
public class Exporter {

    private static String exportFolder = null; // GetFile.jamesExportPath;
    private static String filterPath = null; // GetFile.jamesFilterPath;
    
    /**
     * This method does two things:
     * 1) Run all filters on the master
     * 		a) NoThanks
     * 		b) VTD
     * 		c) TB
     * 		d) Bring
     * 		e) Posten (if it didn't match anything else) 
     * 
     * 2) Check the master against the VTD misslist, set Bring or Posten 
     * 	  as distributors for any matching subscribers instead.
     * 
     * @param year
     * @param issue
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void prepareMasterForExport(int year, int issue) throws FileNotFoundException, IOException {

        String masterFilePath = null; // GetFile.currentMaster(year, issue);
        String notForVTDFilePath = null; // GetFile.notForVTDFilePath;
        
        /*
        // Filters
        Helpers.logStatusMessage("Activating Filters.");
        Filter filterVTD = new Filter(Distributor.VTD, filterPath + "VTD.txt");
        Filter filterTB = new Filter(Distributor.TB, filterPath + "TB.txt");
        Filter filterBring = new Filter(Distributor.BRING, filterPath + "Bring.txt");
        */
        
        // Log status
        Helpers.logStatusMessage("Preparing Master for Export - Started.");

        ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> notForVTD    = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);
        ListHelpers.readFromFileToList(notForVTDFilePath, notForVTD);
        
        System.out.println("Master AL består av: " + master.size() + " Objekt.");

        // Filter Addresses

        int t = 0; //TB Counter
        int v = 0; //VTD Counter
        int b = 0; //Bring Counter
        int p = 0; //Posten Counter
        int n = 0; //NoThanks Counter

        // First, check all subscribers against the available filters
        for(Subscriber s : master) {
            int postNr = Integer.parseInt(s.getZipCode());
            
            /*
            if(s.getDistributor() != Distributor.NONE) {
                if (s.isOKforPaperRoute()) {
                	if(filterVTD.matches(postNr)) {
                		s.setDistributor(Distributor.VTD);
                		v++;
                	} else if(filterTB.matches(postNr)) {
                        s.setDistributor(Distributor.TB);
                        t++;
                    } else if(filterBring.matches(postNr)) {
                            s.setDistributor(Distributor.BRING);
                            b++;
                    } else {
                        s.setDistributor(Distributor.POSTEN);
                        p++;
                    }
                } else if(filterBring.matches(postNr)) {
                        s.setDistributor(Distributor.BRING);
                        b++;
                } else {
                    s.setDistributor(Distributor.POSTEN);
                    p++;
                }
            } else {
                n++;
            }
            */
        }

        // TODO Fixa så Not For VTD filtreras efter nya filtren.
        
        // Filter Not For VTD
        System.out.println("NotForVTD AL består av: " + notForVTD.size() + " Objekt.");

        int nv = 0; //NotForVTD counter.
        for(Subscriber s : notForVTD) {
            long abNr = s.getAbNr();
            int index = ListHelpers.searchListForAbNr(master, abNr);
            if (index >= 0) {
                s = master.get(index);
                int postNr = Integer.parseInt(s.getZipCode());

                /*
                if (s.getDistributor() != Distributor.NONE) {
                    if(filterBring.matches(postNr)) {
                        s.setDistributor(Distributor.BRING);
                        s.setNote("VTD-No-GO");
                        v--;
                        b++;
                    } else {
                        s.setDistributor(Distributor.POSTEN);
                        s.setNote("VTD-No-GO");
                        v--;
                        p++;
                    }
                    nv++;
                }
                */
           }
       }

        System.out.println("NotForVTD = " + nv);

        System.out.println("Filter: VTD      = " + v);
        System.out.println("Filter: TB       = " + t);
        System.out.println("Filter: Bring    = " + b);
        System.out.println("Filter: Posten   = " + p);
        System.out.println("Filter: NoThanks = " + n);
        int total = v+t+b+p+n;
        System.out.println("Filter: Total    = " + total);


        // Save Master
         ListHelpers.stateSaveListAsFile(master, masterFilePath);

        // Log status
         Helpers.logStatusMessage("Preparing Master for Export - Finished.");
         
         // ListHelpers.getAllDistributorStatistic(year, issue);
         
         /**
         
         JOptionPane.showMessageDialog(null,
                    "F�rdig!\n"
                 + "Statistik enligt nedan:\n"
                 + "Totalt: " + total + " prenumeranter.\n"
                 + "Varav\n"
                 + "         VTD     : " + v + " st\n"
                 + "         TB       : " + t + " st\n"
                 + "         Bring   : " + b + " st\n"
                 + "         Posten : " + p + " st\n"
                 + "och " + n + " som inte vill ha. tidningen alls.");
         **/
    }

    public static void exportToVTDetTB(int year, int issue) throws FileNotFoundException, IOException {
       // Fixa datum för utdelning - Popupfråga.
       String distDate = null;
       int date = 0;
       boolean okDate = false;
       
       while (!okDate) {
       
           distDate = JOptionPane.showInputDialog(null,
              "Ange utdelningsdatum."
              + "\n(OBS!!!: FORMAT YYYY-MM-DD)");
       
           distDate = distDate.replaceAll("-", "");
       
           //Kolla så inmatat datum är i siffror
           try { 
        	   date = Integer.parseInt(distDate); 
           } catch (NumberFormatException e) { 
        	   continue; 
           }
                    
           distDate = date + "000000";
           if (distDate.length() == 14) { 
        	   okDate = true; 
           }
       }
       exportToVTD(year, issue, distDate);
       exportToTB(year, issue, distDate);
    }

    public static void exportToVTD(int year, int issue, String distributionsdatum) throws FileNotFoundException, IOException {

        String masterFilePath = null; // GetFile.currentMaster(year, issue);
        
        Helpers.logStatusMessage("exportToVTD Called");
        
        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);

        String today = Helpers.todaysDate();

        ArrayList<Subscriber> master         = new ArrayList<Subscriber>();
        ArrayList<Subscriber> prevMaster     = new ArrayList<Subscriber>();
        ArrayList<Subscriber> vtdStart       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> vtdStopp       = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);
        // ListHelpers.readFromFileToList(GetFile.previousMaster(year, issue), prevMaster);

        // Clear non VTD entrys from both arrays.

        int i = 0; //counter
        Subscriber p = null; //Temp-object

        System.out.println("Array-Size = " + master.size() + " CM-Size(Total)");

        while (i < master.size()) {
            p = master.get(i);
            if (!(p.getDistributor().equals("V"))) {
                master.remove(p);
                i--;
            }
            i++;
        }

        System.out.println("Array-Size = " + master.size() + " CM-Size(VTD)");
        i = 0; //Counter reset
        System.out.println("Array-Size = " + prevMaster.size() + " PM-Size(Total)");

        while (i < prevMaster.size()) {
            p = prevMaster.get(i);
            if (!(p.getDistributor().equals("V"))) {
                prevMaster.remove(p);
                i--;
            }
            i++;
        }
        System.out.println("Array-Size = " + prevMaster.size() + " PM-Size(VTD)");
        Subscriber pNew, pOld;
        pNew = null;
        pOld = null;

        //Create VTD-Start-List
        for (i = 0; i < master.size(); i++) {
            pNew = master.get(i);
            long abNr = pNew.getAbNr();
            int j = ListHelpers.searchListForAbNr(prevMaster, abNr);
            if (j >= 0) {
                pOld = prevMaster.get(j);
                if (!(pNew.equals(pOld))) {
                    vtdStart.add(pNew);
                }
            } else {vtdStart.add(pNew);}
        }

        System.out.println("StartLista = " + vtdStart.size());
        //Create VTD-Stopp-List
        for (i = 0; i < prevMaster.size(); i++) {
        
            pOld = prevMaster.get(i);
            long abNr = pOld.getAbNr();
            
            int j = ListHelpers.searchListForAbNr(master, abNr);
            if (j >= 0) {
                pNew = master.get(j);
                if (!(pOld.equals(pNew))) {
                    vtdStopp.add(pOld);
                }             
            } else {vtdStopp.add(pOld);}
        }

        System.out.println("StoppLista = " + vtdStopp.size());
        String targetFilePath = exportFolder + "VTD_spionen_"+today+".txt";
        ListHelpers.exportListAsTB(vtdStart, vtdStopp, targetFilePath, distributionsdatum);
        
        // User Feedback
        JOptionPane.showMessageDialog(null,
                    "Färdig med VTD listorna.\n"
                 + "Statistik enligt nedan:\n"
                 + "Totalt: " + master.size() + " prenumeranter.\n"
                 + "StartLista = " + vtdStart.size() + " st\n"
                 + "StoppLista = " + vtdStopp.size() + " st\n"
                 + "\nNu fortsätter jag med TB.");
        
    }

    public static void exportToTB(int year, int issue, String distributionsdatum) throws FileNotFoundException, IOException {

        String masterFilePath = null; // GetFile.currentMaster(year, issue);
        Helpers.logStatusMessage("exportToTB Called");
        
        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);
        String today = Helpers.todaysDate();

        ArrayList<Subscriber> master        = new ArrayList<Subscriber>();
        ArrayList<Subscriber> prevMaster    = new ArrayList<Subscriber>();
        ArrayList<Subscriber> tbStart       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> tbStopp       = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);
//        ListHelpers.readFromFileToList(previousIssueMasterFilePath, prevMaster);
        // ListHelpers.readFromFileToList(GetFile.previousMaster(year, issue), prevMaster);
        // Clear non TB entrys from both arrays.

        int i = 0; //counter
        Subscriber p = null; //Temp-object

        System.out.println("Array-Size = " + master.size() + " CM-Size(Total)");

        while (i < master.size()) {
            p = master.get(i);
            if (p.getDistributor() != Distributor.TB) {
                master.remove(p);
                i--;
            }
            i++;
        }

        System.out.println("Array-Size = " + master.size() + " CM-Size(TB)");
        i = 0; //Counter reset

        System.out.println("Array-Size = " + prevMaster.size() + " PM-Size(Total)");
        while (i < prevMaster.size()) {
            p = prevMaster.get(i);
            if (p.getDistributor() != Distributor.TB) {
                prevMaster.remove(p);
                i--;
            }
            i++;
        }

        System.out.println("Array-Size = " + prevMaster.size() + " PM-Size(TB)");
        Subscriber pNew, pOld;
        pNew = null;
        pOld = null;

        //Create TB-Start-List
        for (i = 0; i < master.size(); i++) {
            pNew = master.get(i);
            long abNr = pNew.getAbNr();
            
            int j = ListHelpers.searchListForAbNr(prevMaster, abNr);
            if (j >= 0) {
                pOld = prevMaster.get(j);
                if (!pNew.equals(pOld)) {
                    tbStart.add(pNew);
                }
            } else {
            	tbStart.add(pNew);
            }
        }

        System.out.println("StartLista = " + tbStart.size());

        //Create TB-Stopp-List
        for (i = 0; i < prevMaster.size(); i++) {
            pOld = prevMaster.get(i);
            long abNr = pOld.getAbNr();
            
            int j = ListHelpers.searchListForAbNr(master, abNr);
            if (j >= 0) {
                pNew = master.get(j);
                if (!(pOld.equals(pNew))) {
                    tbStopp.add(pOld);
                }             
            } else {tbStopp.add(pOld);}
        }

        System.out.println("StoppLista = " + tbStopp.size());
        String targetFilePath = exportFolder + "TB_spionen_"+today+".txt";
        ListHelpers.exportListAsTB(tbStart, tbStopp, targetFilePath, distributionsdatum);
        
        // User Feedback
        JOptionPane.showMessageDialog(null,
            "Färdig med TB listorna.\n"
         + "Statistik enligt nedan:\n"
         + "Totalt: " + master.size() + " prenumeranter.\n"
         + "StartLista = " + tbStart.size() + " st\n"
         + "StoppLista = " + tbStopp.size() + " st\n");
    }
    
    
    public static void exportToVTAB_Complete(int year, int issue) throws FileNotFoundException, IOException {
        String masterFilePath = null; // GetFile.currentMaster(year, issue);
        
        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);

        ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> export       = new ArrayList<Subscriber>();
        ListHelpers.readFromFileToList(masterFilePath, master);

        int b = 0; //Bring Counter
        int p = 0; //Posten Counter
        for (int i = 0;  i < master.size(); i++) {
            Distributor filter = master.get(i).getDistributor();
                if (filter == Distributor.BRING) {
                    export.add(master.get(i));
                    b++;
                }
                if (filter == Distributor.POSTEN) {
                    export.add(master.get(i));
                    p++;
                }
        }

        //Save Export List
        String targetFilePath = null; // GetFile.vTabExportFile(year, issue, "Komplett");
        ListHelpers.exportListForVTAB(export, targetFilePath);

        //Log
        System.out.println("Export klar. " + (b+p) + " poster exporterades " +
                            "varav " + b + " Bring och " + p + " Posten.");
        
        // User Feedback
        JOptionPane.showMessageDialog(null,
            "Färdig med V-Tab listan (Total).\n"
         + "Statistik enligt nedan:\n"
         + "Totalt: " + (b + p)  + " prenumeranter.\n"
         + "Varav Bring  = " + b + " st\n"
         + "Varav Posten = " + p + " st");
    }

    public static void exportToVTAB_JustBring(int year, int issue) throws FileNotFoundException, IOException {
        String masterFilePath = null; // GetFile.currentMaster(year, issue);

        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);

        ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> export       = new ArrayList<Subscriber>();
        ListHelpers.readFromFileToList(masterFilePath, master);

        int b = 0; //Bring Counter
        for (int i = 0;  i < master.size(); i++) {
            Distributor filter = master.get(i).getDistributor();
                if (filter == Distributor.BRING) {
                    export.add(master.get(i));
                    b++;
                }
        }

        //Save Export List
        String targetFilePath = null; // GetFile.vTabExportFile(year, issue, "Bring");
        ListHelpers.exportListForVTAB(export, targetFilePath);

        //Log
        System.out.println("Export klar. " + (b) + " poster exporterades.");
        
        // User Feedback
        JOptionPane.showMessageDialog(null,
            "Färdig med V-Tab listan (Bara Bring).\n"
         + "Statistik enligt nedan:\n"
         + "Totalt: " + b + " prenumeranter.\n"
         + "Varav Bring  = " + b + " st");

    }

    public static void exportToVTAB_JustPosten(int year, int issue) throws FileNotFoundException, IOException {
        String masterFilePath = null; // GetFile.currentMaster(year, issue);        
 
        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);
        ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> export       = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);

        int p = 0; //Posten Counter
        for (int i = 0;  i < master.size(); i++) {
            Distributor filter = master.get(i).getDistributor();
            if (filter == Distributor.POSTEN) {
                export.add(master.get(i));
                p++;
            }
        }

        //Save Export List
        String targetFilePath = null; // GetFile.vTabExportFile(year, issue, "Posten");
        ListHelpers.exportListForVTAB(export, targetFilePath);

        //Log
        System.out.println("Export klar. " + (p) + " poster exporterades.");
        
        // User Feedback
        JOptionPane.showMessageDialog(null,
            "Färdig med V-Tab listan (Bara Posten).\n"
         + "Statistik enligt nedan:\n"
         + "Totalt: " + p + " prenumeranter.\n"
         + "Varav Posten = " + p + " st");
    }

    public static void exportToVTAB_BringAndSpecials(int year, int issue) throws FileNotFoundException, IOException {

        String masterFilePath = null; // GetFile.currentMaster(year, issue);
        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);
 
        ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> export       = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);

        int b = 0; //Bring Counter
        int p = 0; //Posten Counter

        for (int i = 0;  i < master.size(); i++) {
            Distributor dist = master.get(i).getDistributor();
            if(dist == Distributor.BRING) {
                export.add(master.get(i));
                b++;
            } else if(dist == Distributor.POSTEN) {
                if (master.get(i).getAbNr() <= 99999) { // Length <= 5
                    export.add(master.get(i));
                    p++;
                }
            }
        }

        //Save Export List
        String targetFilePath = null; // GetFile.vTabExportFile(year, issue, "Special");
        ListHelpers.exportListForVTAB(export, targetFilePath);

        //Log
        System.out.println("Export klar. " + (b+p) + " poster exporterades " +
                            "varav " + b + " Bring och " + p + " Posten.");
        
        // User Feedback
        JOptionPane.showMessageDialog(null,
            "Färdig med V-Tab listan (Bring + Special).\n"
         + "Statistik enligt nedan:\n"
         + "Totalt: " + (b + p) + " prenumeranter.\n"
         + "Varav Bring  = " + b + " st\n"
         + "Varav Posten = " + p + " st");

    }
}

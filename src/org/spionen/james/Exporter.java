package org.spionen.james;

import java.io.*;
import java.util.*;
import javax.swing.*;

import org.spionen.james.subscriber.Subscriber;
import org.spionen.james.subscriber.Subscriber.Distributor;

public class Exporter {

    private static String exportFolder = GetFile.jamesExportPath;
    private static String filterPath = GetFile.jamesFilterPath;
    
    public static void prepareMasterForExport(int year, int issue) throws FileNotFoundException, IOException {

        String masterFilePath = GetFile.currentMaster(year, issue);
        String notForVTDFilePath = GetFile.notForVTDFilePath;
        
        Helpers.logStatusMessage("Activating Filters.");
        
        // Filterfiles
        File vtd    = new File(filterPath + "VTD.txt");
        File tb     = new File(filterPath + "TB.txt");
        File bring  = new File(filterPath + "Bring.txt");
        
        // FilterArrays 
        ArrayList<String> filterVTD     = new ArrayList<String>();
        ArrayList<String> filterTB      = new ArrayList<String>();
        ArrayList<String> filterBring   = new ArrayList<String>();
        
        filterVTD = Filter.createFilterArray(vtd);
        filterTB = Filter.createFilterArray(tb);
        filterBring = Filter.createFilterArray(bring);
        
        Helpers.logStatusMessage("Filters Activated.");
        
        // Log status
        Helpers.logStatusMessage("Preparing Master for Export - Started.");

        ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> notForVTD    = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);
        ListHelpers.readFromFileToList(notForVTDFilePath, notForVTD);
        
        System.out.println("Master AL består av: " + master.size() + " Objekt.");

        //Placeholder Prenumerant
        Subscriber pren = null;

        // Filter Addresses

        int t = 0; //TB Counter
        int v = 0; //VTD Counter
        int b = 0; //Bring Counter
        int p = 0; //Posten Counter
        int n = 0; //NoThanks Counter

        for (int i = 0; i < master.size(); i++) {

            pren = master.get(i);
            String postNr = pren.getZipCode();

            if (pren.getDistributor() != Distributor.NoThanks) {
                if (pren.isOKforPaperRoute()) {
                    if (Filter.checkIfInRange(postNr, filterTB)) {
                        pren.setDistributor(Distributor.TB);
                        t++;
                    } else if (Filter.checkIfInRange(postNr, filterVTD)) {
                            pren.setDistributor(Distributor.VTD);
                            v++;
                    } else if (Filter.checkIfInRange(postNr, filterBring)) {
                            pren.setDistributor(Distributor.Bring);
                            b++;
                    } else {
                        pren.setDistributor(Distributor.Posten);
                        p++;
                    }
                } else if (Filter.checkIfInRange(postNr, filterBring)) {
                        pren.setDistributor(Distributor.Bring);
                        b++;
                } else {
                    pren.setDistributor(Distributor.Posten);
                    p++;
                }
            } else {
                n++;
            }
        }

        // TODO Fixa så Not For VTD filtreras efter nya filtren.
        
        // Filter Not For VTD
        System.out.println("NotForVTD AL består av: " + notForVTD.size() + " Objekt.");

        int nv = 0; //NotForVTD counter.
        for (int i = 0; i < notForVTD.size(); i++) {
            String abNr = notForVTD.get(i).getAbNr();
            int index = ListHelpers.searchListForAbNr(master, abNr);
            if (index >= 0) {

                pren = master.get(index);
                String postNr = pren.getZipCode();

                if (pren.getDistributor() != Distributor.NoThanks) {

                    if (Filter.checkIfInRange(postNr, filterBring)) {
                        pren.setDistributor(Distributor.Bring);
                        pren.setNote("VTD-No-GO");
                        v--;
                        b++;
                    } else {
                        pren.setDistributor(Distributor.Posten);
                        pren.setNote("VTD-No-GO");
                        v--;
                        p++;
                    }
                                       
//                    if (master.get(index).isBring()) {
//
//                        master.get(index).setDistributor("B");
//                        v--;
//                        b++;
//
//                    } else {
//
//                        master.get(index).setDistributor("P");
//                        v--;
//                        p++;
//                    }

                    nv++;
                }
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

        String masterFilePath = GetFile.currentMaster(year, issue);
        
        Helpers.logStatusMessage("exportToVTD Called");
        
        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);

        String today = Helpers.todaysDate();

        ArrayList<Subscriber> master         = new ArrayList<Subscriber>();
        ArrayList<Subscriber> prevMaster     = new ArrayList<Subscriber>();
        ArrayList<Subscriber> vtdStart       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> vtdStopp       = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);
        ListHelpers.readFromFileToList(GetFile.previousMaster(year, issue), prevMaster);

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
            String abNr = pNew.getAbNr();
            int j = ListHelpers.searchListForAbNr(prevMaster, abNr);
            if (j >= 0) {
                pOld = prevMaster.get(j);
                if (!(pNew.comparePrenumerant(pOld))) {
                    vtdStart.add(pNew);
                }
            } else {vtdStart.add(pNew);}
        }

        System.out.println("StartLista = " + vtdStart.size());
        //Create VTD-Stopp-List
        for (i = 0; i < prevMaster.size(); i++) {
        
            pOld = prevMaster.get(i);
            String abNr = pOld.getAbNr();
            
            int j = ListHelpers.searchListForAbNr(master, abNr);
            if (j >= 0) {
                pNew = master.get(j);
                if (!(pOld.comparePrenumerant(pNew))) {
                    vtdStopp.add(pOld);
                }             
            } else {vtdStopp.add(pOld);}
        }

        System.out.println("StoppLista = " + vtdStopp.size());

        //Save Export List
        String targetFilePathStart = exportFolder +
                                            "VTD_spionen_start_"+today+".txt";
        //ListHelpers.exportListForVTD(vtdStart, targetFilePathStart);
        //ListHelpers.exportListAsTB(vtdStart, targetFilePathStart, true);

        String targetFilePathStopp = exportFolder +
                                            "VTD_spionen_stopp_"+today+".txt";
        //ListHelpers.exportListForVTD(vtdStopp, targetFilePathStopp);
        //ListHelpers.exportListAsTB(vtdStopp, targetFilePathStopp, false);

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

        String masterFilePath = GetFile.currentMaster(year, issue);
        Helpers.logStatusMessage("exportToTB Called");
        
        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);
        String today = Helpers.todaysDate();

        ArrayList<Subscriber> master         = new ArrayList<Subscriber>();
        ArrayList<Subscriber> prevMaster     = new ArrayList<Subscriber>();
        ArrayList<Subscriber> tbStart       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> tbStopp       = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);
//        ListHelpers.readFromFileToList(previousIssueMasterFilePath, prevMaster);
        ListHelpers.readFromFileToList(GetFile.previousMaster(year, issue), prevMaster);
        // Clear non TB entrys from both arrays.

        int i = 0; //counter
        Subscriber p = null; //Temp-object

        System.out.println("Array-Size = " + master.size() + " CM-Size(Total)");

        while (i < master.size()) {
            p = master.get(i);
            if (!(p.getDistributor().equals("T"))) {
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
            if (!(p.getDistributor().equals("T"))) {
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
            String abNr = pNew.getAbNr();
            
            int j = ListHelpers.searchListForAbNr(prevMaster, abNr);
            if (j >= 0) {
                pOld = prevMaster.get(j);
                if (!(pNew.comparePrenumerant(pOld))) {
                    tbStart.add(pNew);
                }
            } else {tbStart.add(pNew);}
        }

        System.out.println("StartLista = " + tbStart.size());

        //Create TB-Stopp-List
        for (i = 0; i < prevMaster.size(); i++) {
            pOld = prevMaster.get(i);
            String abNr = pOld.getAbNr();
            
            int j = ListHelpers.searchListForAbNr(master, abNr);
            if (j >= 0) {
                pNew = master.get(j);
                if (!(pOld.comparePrenumerant(pNew))) {
                    tbStopp.add(pOld);
                }             
            } else {tbStopp.add(pOld);}
        }

        System.out.println("StoppLista = " + tbStopp.size());

        //Save Export List
        String targetFilePathStart = exportFolder +
                                            "TB_spionen_start_"+today+".txt";
        //ListHelpers.exportListForVTD(tbStart, targetFilePathStart);
        //ListHelpers.exportListAsTB(tbStart, targetFilePathStart, true);

        String targetFilePathStopp = exportFolder +
                                            "TB_spionen_stopp_"+today+".txt";
        //ListHelpers.exportListForVTD(tbStopp, targetFilePathStopp);
        //ListHelpers.exportListAsTB(tbStopp, targetFilePathStopp, false);

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
        String masterFilePath = GetFile.currentMaster(year, issue);
        
        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);

        ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> export       = new ArrayList<Subscriber>();
        ListHelpers.readFromFileToList(masterFilePath, master);

        int b = 0; //Bring Counter
        int p = 0; //Posten Counter
        for (int i = 0;  i < master.size(); i++) {
            Distributor filter = master.get(i).getDistributor();
                if (filter == Distributor.Bring) {
                    export.add(master.get(i));
                    b++;
                }
                if (filter == Distributor.Posten) {
                    export.add(master.get(i));
                    p++;
                }
        }

        //Save Export List
        String targetFilePath =  GetFile.vTabExportFile(year, issue, "Komplett");
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
        String masterFilePath = GetFile.currentMaster(year, issue);

        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);

        ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> export       = new ArrayList<Subscriber>();
        ListHelpers.readFromFileToList(masterFilePath, master);

        int b = 0; //Bring Counter
        for (int i = 0;  i < master.size(); i++) {
            Distributor filter = master.get(i).getDistributor();
                if (filter == Distributor.Bring) {
                    export.add(master.get(i));
                    b++;
                }
        }

        //Save Export List
        String targetFilePath =  GetFile.vTabExportFile(year, issue, "Bring");
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
        String masterFilePath = GetFile.currentMaster(year, issue);        
 
        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);
        ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> export       = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);

        int p = 0; //Posten Counter
        for (int i = 0;  i < master.size(); i++) {
            Distributor filter = master.get(i).getDistributor();
                if (filter == Distributor.Posten) {
                    export.add(master.get(i));
                    p++;
                }
        }

        //Save Export List
        String targetFilePath =  GetFile.vTabExportFile(year, issue, "Posten");
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

        String masterFilePath = GetFile.currentMaster(year, issue);
        //Check if Exportfolder-exists and create it if it doesen't
        Helpers.makeSureFolderExists(exportFolder);
 
        ArrayList<Subscriber> master       = new ArrayList<Subscriber>();
        ArrayList<Subscriber> export       = new ArrayList<Subscriber>();

        ListHelpers.readFromFileToList(masterFilePath, master);

        int b = 0; //Bring Counter
        int p = 0; //Posten Counter

        for (int i = 0;  i < master.size(); i++) {
            Distributor filter = master.get(i).getDistributor();
                if (filter == Distributor.Bring) {
                    export.add(master.get(i));
                    b++;
                }
                if (filter == Distributor.Posten) {
                    if (master.get(i).getAbNr().length() <= 5) {
                        export.add(master.get(i));
                        p++;
                    }
                }
        }

        //Save Export List
        String targetFilePath = GetFile.vTabExportFile(year, issue, "Special");
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

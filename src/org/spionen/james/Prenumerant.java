package org.spionen.james;

import java.util.*;

public class Prenumerant implements Comparable<Prenumerant>
{
    
    //TODO Make abNr to Long.
    
    private String abNr;
    private String fNamn;
    private String eNamn;
    private String coAdress;
    private String gatuAdress;
    private String postNr;
    private String postOrt;
    private String land;
    private String distributor;
    private String typ;
    private String note;
    
    //private String gatuNummer;
    //private String littra;
    //private String floor;
    //private String floorType;

    public Prenumerant(String masterFileRow) {

        Scanner sc = new Scanner(masterFileRow);
        sc = sc.useDelimiter(";");

        abNr            = sc.next();
        fNamn           = sc.next().trim();
        eNamn           = sc.next().trim();
        coAdress        = sc.next().trim();
        gatuAdress      = sc.next().trim();
        postNr          = sc.next().trim();
        postOrt         = sc.next().trim();
        land            = sc.next().trim();
        distributor     = sc.next().trim();
        typ             = sc.next();
        note            = sc.next();
        
        sc.close();
    }

    public String getAbNr() {return abNr;}
    public String getFullName() {return fNamn + " " + eNamn;}
    public String getPostNr() {return postNr;}
    public String getAdress() {return gatuAdress;}
    public String getDistributor() {return distributor;}
    public String getTyp() {return typ;}
    public void setDistributor(String distributorCode) {distributor = distributorCode;}
    public void setNote(String noteAsString) {note = noteAsString;}

    public String masterFormat() {
        String row = abNr + ";" + 
                     fNamn + ";" +
                     eNamn + ";" +
                     coAdress + ";" +
                     gatuAdress + ";" +
                     postNr + ";" +
                     postOrt + ";" +
                     land  + ";" +
                     distributor + ";" +
                     typ  + ";" +
                     note;
        
        return row;
    }

    public String vtdFormat() {
        String namn = eNamn + " " + fNamn;
        namn = namn.trim();

        String row = abNr + ";" + namn + ";" + coAdress + ";" + gatuAdress + ";" + postNr + ";" + postOrt;
        return row;
    }

    public String vTabFormat() {
        String namn = fNamn + " " + eNamn;
        namn = namn.trim();

        String row = namn + ";" + coAdress + ";" + gatuAdress + ";" + postNr + ";" + postOrt;
        return row;
    }

    public boolean correctAdress() {

        if (postNr.equals("0")) {return false;}
        if (postNr.startsWith("0")) {return false;}
        if (postNr.equals("40530")) {
            if (gatuAdress.length() == 0) {
                return false;
            }
        }
        if (postNr.length() != 5) {
            note = "Felaktigt Postnummer";
            return false;
        } else {
            if (land.equals("")) {
                    return true;
            } else {
                if (land.equals("SVERIGE") || land.equals("SWEDEN")) {
                    return true;
                }
                else {
                    note = "Icke Svensk adress";
                    return false;
                }
            }
        }
    }
    
    public boolean isOKforPaperRoute() {
    
        if (gatuAdress.startsWith("BOX ") || gatuAdress.startsWith("PL ")) {
            note = "Ej VTD/TB adress";
            return false;  
        } 
        
        if (gatuAdress.length() > 35) {
            note = "För lång gatuadress för VTD/TB";
            return false;
        }

        if (gatuAdress.length() < 3) {
            note = "För kort adress för VTD/TB";
            return false;
        }

        if (coAdress.length() > 30) {
            note = "För lång COAdress för VTD/TB";
            return false;
        }

        if ((fNamn.length() + eNamn.length()) > 30) {
            note = "För långt namn för VTD/TB";
            return false;
        }

        if (abNr.equals("000")) {
            note = "Ej VTD/TB adress";
            return false;
        } 
        return true;
    }

    public String tbFormat()
    {
        //int abb = Integer.parseInt(abNr);
        int pn = Integer.parseInt(postNr);
        //Double prenNr = Double.parseDouble(abNr);
        Long prenNr = Long.parseLong(abNr);
        
        //System.out.println(prenNr);
        
        String out1 = String.format("%7s%010d%05d%-20s%09d", "L ",prenNr,pn,postOrt,0);
        String out2 = ListHelpers.toTB(gatuAdress);
        String out3 = String.format("%1s%14s%-30s%-30s%-60s%06d", "","", eNamn + " " + fNamn, coAdress, "", 1);
        
        //System.err.print(out1);
        //System.err.print(ListHelpers.toTB(gatuAdress));
        //System.err.print(out3 + "\n");
        
        String line = out1 + out2 + out3;
        
        return line;
    }

    public boolean comparePrenumerant(Prenumerant p) {
        if (abNr.equals(p.getAbNr())) {
           if (postNr.equals(p.getPostNr())) {
               if (gatuAdress.equals(p.getAdress())) {
                   return true;
                }
           }
        }
        return false;
    }

    @Override
    public int compareTo(Prenumerant p) {
        return this.getAbNr().compareTo(p.getAbNr());
    }

}

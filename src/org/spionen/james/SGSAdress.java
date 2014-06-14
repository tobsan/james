/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spionen.james;

/**
 *
 * @author Maxim
 */
public class SGSAdress implements Comparable<SGSAdress> {
    
    //Instance variables
    private String gatuAdress;
    private String adressSpecifikation;
    private String postNr; 
    private String postOrt;
    
    //Constructors
    public SGSAdress(String ga, String as, String pn, String po) {
        
        gatuAdress = ga;
        adressSpecifikation = as;
        postNr = pn;
        postOrt = po;

    }
    
    //Getters
    public String getGatuAdress() {return gatuAdress;}
    public String getAdressSpecifikation() {return adressSpecifikation;}
    public String getPostNr() {return postNr;}
    public String getPostOrt() {return postOrt;}
    public String getAll() {
    	return gatuAdress + " " + 
    		   adressSpecifikation + " " + 
               postNr + " " + 
               postOrt;
    }
        
    @Override 
    public int compareTo(SGSAdress sgsAdress) {
        int result = this.getPostNr().compareTo(sgsAdress.getPostNr());
        if (result != 0) {
            return result;
        }
        return this.getGatuAdress().compareTo(sgsAdress.getGatuAdress());
    }    
}

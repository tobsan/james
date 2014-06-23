package org.spionen.james.subscriber;

public class TBSubscriber extends SubscriberDecorator {
	
	/*
	 *  TODO: Turn into a decorator instead?
	 */
	public TBSubscriber(Subscriber s) {
		super(s);
		setDistributor(Distributor.TB);
	}
	
	public String toString() {
        int pn = Integer.parseInt(getZipCode());
        Long prenNr = Long.parseLong(getAbNr());
        
        String out1 = String.format("%7s%010d%05d%-20s%09d", "L ",prenNr,pn,getCity(),0);
        String out2 = streetFormat();
        String out3 = String.format("%1s%14s%-30s%-30s%-60s%06d", "","", getLastName() + " " + getFirstName(), getCoAddress(), "", 1);
        
        return out1 + out2 + out3;
	}
	
	private String streetFormat() {
		   // -----------------------------------*****------****-----*----------
	       // CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCNNNNNCCCCCCCCCCNNNNNCNNNNNNNNNN
	       
	       String formattedAddress = "";
	       String compoundAddress = prepareCompoundAddress(getStreetAddress());
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
	       //String apartment = "";
	       
	       if (arr.length > 0) {
	           gata = arr[0];
	           if (arr.length > 1) {
	               for(int i = 1; i < arr.length; i++) {
	                   s = arr[i];
	                   if (isInt(s)) {
	                       if (lgh == false) {
	                           if (gatuNr == -1) {
	                               gatuNr = Integer.parseInt(s);
	                           } else if(s.length() > 3) {
	                               appNr = Integer.parseInt(s);
	                           } else {
	                               floor = Integer.parseInt(s);
	                           }
	                       } else if (lgh == true) {
	                           appNr = Integer.parseInt(s);
	                       }
	                   } else {
	                       if (isLGH(s)) { 
	                    	   lgh = true; 
	                       } else if (s.equalsIgnoreCase(":") || s.equalsIgnoreCase("-")) { 
	                           if (i > 2 && i < arr.length) {
	                               if (isInt(arr[i+1])) {
	                                   if (arr[i+1].length() < 4) {
	                                       littra = s + arr[i+1];
	                                   }
	                               }
	                           }
	                           
	                           if (isInt(arr[i-1])) {
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
	                       else if (gata.length() == 0) {
	                           gata = s;
	                       } else if (gatuNr == -1) {
	                           gata = gata + " " + s;
	                       }
	                   }
	               }
	           }
	       }
	       
	       if (gatuNr == -1) { gatuNr = 0; }
	       if (floor == -1) { floor = 0; }
	       if (appNr == -1) { appNr = 0; }
	       
	       
	       //if (apartment.isEmpty())
	       //{
	       //   if (appNr == -1) { appartment = ""; }
	       //   else { appartment = appNr + "" ; }
	       //}
	       
	       formattedAddress = String.format("%-35s%05d%-6s%-4s%05d%-1s%010d", gata,
	    		   							gatuNr, littra, door, floor, floorType,
	    		   							appNr);
	       return formattedAddress;
	   
	}
	
   private String prepareCompoundAddress(String ca) {
       ca = ca.replaceAll(",", " ");
       ca = ca.replaceAll("\\.", " ");
       ca = ca.replaceAll("\\/", " ");
       ca = ca.replaceAll("N:A", "NORRA");
       ca = ca.replaceAll("V:A", "VÄSTRA");
       ca = ca.replaceAll("S:T", "SANKT");
       ca = ca.replaceAll("1:A", "FÖRSTA");
       ca = ca.replaceAll("2:A", "ANDRA");
       ca = ca.replaceAll("3:E", "TREDJE");
       ca = ca.replaceAll("4:E", "FJÄRDE");
       ca = ca.replaceAll("5:E", "FEMTE");
       ca = splitMixed(ca);
       return ca;
   }
   
   private String splitMixed(String s) {
       String splitMix = "";
       String lastWas = "";
      
       String s1 = "";
       int sLength = s.length();
       for (int i = 0; i < sLength; i++) {
          s1 = s.substring(i, i+1);

          if (s1.equalsIgnoreCase(" ")) {
              splitMix = splitMix + s1;
          } else if (isInt(s1)) {        
              if (lastWas.equalsIgnoreCase("")) {
                  splitMix = splitMix + s1;
              } else if (lastWas.equalsIgnoreCase("I")) {
                  splitMix = splitMix + s1;
              } else {
                  splitMix = splitMix + " " + s1;
              }
              lastWas = "I";
          } else {
              if (lastWas.equalsIgnoreCase("")) {
                  splitMix = splitMix + s1;
              } else if (lastWas.equalsIgnoreCase("C")) {
                  splitMix = splitMix + s1;
              } else {
                  splitMix = splitMix + " " + s1;
              }
              lastWas = "C";
          }
       }
       
       splitMix = splitMix.replaceAll("  ", " ");
       return splitMix;
   }
   
   private boolean isInt(String s) {
       try {
           Integer.parseInt(s);
       } catch(NumberFormatException nfe) {
           return false;
       }
       return true;
   }
   
   private boolean isLGH(String s) {
	   return s.equalsIgnoreCase("lgh") || s.equalsIgnoreCase("läg");
   }
   
   private boolean isFloor(String s) {
	   return s.equalsIgnoreCase("VÅN"); 
   }
       
   private boolean isFlight(String s) {
	   return s.equalsIgnoreCase("TR");
   }
}

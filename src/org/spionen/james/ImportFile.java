package org.spionen.james;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public abstract class ImportFile {
	
	public abstract List<Subscriber> readFile(File file);
	public List<Subscriber> readFile(String filename) throws IOException, FileNotFoundException {
		File f = new File(filename);
		// Check for common things
		if(!f.isFile()) {
			throw new FileNotFoundException("Input is not a file: " + filename);
		} else if(!f.exists()) {
			throw new FileNotFoundException("The file does not exist: " + filename);
		} else if(!f.canRead()) {
			throw new IOException("Can't read file: " + filename);
		}
		return readFile(f);
	}
	
	public abstract void writeFile(List<Subscriber> subscribers, File file);
	public void writeFile(List<Subscriber> subscribers, String filename) throws IOException, FileNotFoundException {
		File f = new File(filename);
		if(!f.exists()) {
			boolean b = f.createNewFile();
			if(!b || !f.canWrite()) {
				throw new IOException("Can't write to file: " + filename);
			}
		}
		System.out.println("Writing subscribers to " + filename);
		writeFile(subscribers, f);
	}
	
	public FieldType[] order(String[] order) {
		FieldType[] ft = new FieldType[order.length];
		for(int i = 0; i < order.length; i++) {
			ft[i] = getFieldType(order[i]);
		}
		return ft;
	}
	
	public FieldType[] standardOrder() {
		FieldType[] ft = { 
				FieldType.SubscriberID, FieldType.FirstName, FieldType.LastName, 
				FieldType.CoAddress, FieldType.Address, FieldType.ZipCode, 
				FieldType.City, FieldType.Country, FieldType.Category,
				FieldType.Distributor, FieldType.Note
		};
		return ft;
	}
	
	public String getByField(FieldType ft, Subscriber s) {
		switch(ft) {
		case SubscriberID: return s.getAbNr();
		case FirstName: return s.getFirstName();
		case LastName: return s.getLastName();
		case CoAddress: return s.getCoAddress();
		case Address: return s.getStreetAddress();
		case ZipCode: return s.getZipCode();
		case City: return s.getCity();
		case Country: return s.getCountry();
		case Category: return s.getType();
		case Distributor: return s.getDistributor();
		case Note: return s.getNote();
		default: return "";
		}
	}
	
	public void setByField(FieldType ft, Subscriber s, String data) {
		switch(ft) {
		case SubscriberID: s.setAbNr(data); break;
		case FirstName: s.setFirstName(data); break;
		case LastName: s.setLastName(data); break;
		case CoAddress: s.setCoAddress(data); break;
		case Address: s.setStreetAddress(data); break;
		case ZipCode: s.setZipCode(data); break;
		case City: s.setCity(data); break;
		case Country: s.setCountry(data);
		case Category: s.setType(data); break;
		case Distributor: s.setDistributor(data); break;
		case Note: s.setNote(data); break;
		default: return; // Discard anything else
		}		
	}
	
	public FieldType getFieldType(String str) {
		switch(str.toLowerCase()) {
		case "pnr":
		case "prenumerantid":
		case "prennr":
		case "personnummer":
			return FieldType.SubscriberID;
		case "namn":
		case "fnamn":
		case "förnamn":
			return FieldType.FirstName;
		case "efternamn":
		case "enamn":
			return FieldType.LastName;
		case "c/o":
		case "c / o":
		case "coadress":
			return FieldType.CoAddress;
		case "adress":
		case "gatuadress":
			return FieldType.Address;
		case "postnummer":
		case "postnr":
			return FieldType.ZipCode;
		case "ort":
		case "postort":
		case "stad":
			return FieldType.City;
		case "land": return FieldType.Country;
		case "distributör": return FieldType.Distributor;
		case "typ":
		case "kategori":
			return FieldType.Category;
		case "not":
		case "note": 
			return FieldType.Note;
		default: return FieldType.Unknown;
		}
	}
	
	public enum FieldType {
		SubscriberID ("PrenumerantID"),
		FirstName ("Förnamn"),
		LastName ("Efternamn"),
		CoAddress ("c/o"),
		Address ("Adress"),
		ZipCode ("Postnummer"),
		City ("Stad"),
		Country ("Land"),
		Distributor ("Distributör"),
		Category ("Kategori"),
		Note ("Note"),
		Unknown ("Unknown field");
		
		private String desc;
		private FieldType(String desc) {
			this.desc = desc;
		}
		
		public String getDesc() {
			return desc;
		}
		
		@Override
		public String toString() {
			return getDesc();
		}
	};
}

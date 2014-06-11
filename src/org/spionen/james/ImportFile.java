package org.spionen.james;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public abstract class ImportFile {
	
	public abstract ArrayList<Prenumerant> readFile(File file);
	public ArrayList<Prenumerant> readFile(String filename) throws IOException, FileNotFoundException {
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
	
	public abstract void writeFile(ArrayList<Prenumerant> subscribers, File file);
	public void writeFile(ArrayList<Prenumerant> subscribers, String filename) throws IOException, FileNotFoundException {
		File f = new File(filename);
		if(!f.isFile()) {
			throw new FileNotFoundException("Input is not a file: " + filename);
		} else if(!f.exists()) {
			throw new FileNotFoundException("The file does not exist: " + filename);
		} else if(!f.canWrite()) {
			throw new IOException("Can't write to file: " + filename);
		}
		writeFile(subscribers, f);
	}
	
	public FieldType[] order(String[] order) {
		FieldType[] ft = new FieldType[order.length];
		for(int i = 0; i < order.length; i++) {
			ft[i] = getFieldType(order[i]);
		}
		return ft;
	}
	
	public FieldType getFieldType(String str) {
		switch(str.toLowerCase()) {
		case "pnr":
		case "personnummer":
			return FieldType.SubscriberID;
		case "namn":
		case "fnamn":
		case "förnamn":
			return FieldType.FirstName;
		case "efternamn":
		case "enamn":
			return FieldType.LastName;
		case "adress":
		case "gatuadress":
			return FieldType.Address;
		case "postnummer":
		case "postnr":
			return FieldType.ZipCode;
		case "ort":
		case "postort":
			return FieldType.City;
		case "land": return FieldType.Country;
		case "distributör": return FieldType.Distributor;
		case "typ":
		case "kategori":
			return FieldType.Category;
		case "not": return FieldType.Note;
		default: return FieldType.Unknown;
		}
	}
	
	public enum FieldType {
		SubscriberID,
		FirstName,
		LastName,
		Address,
		ZipCode,
		City,
		Country,
		Distributor,
		Category,
		Note,
		Unknown
	};
}

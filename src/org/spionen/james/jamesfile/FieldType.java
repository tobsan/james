package org.spionen.james.jamesfile;

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
	
	public static FieldType[] order(String[] order) {
		FieldType[] ft = new FieldType[order.length];
		for(int i = 0; i < order.length; i++) {
			ft[i] = getFieldType(order[i]);
		}
		return ft;
	}
	
	public static FieldType[] standardOrder() {
		FieldType[] ft = { 
				FieldType.SubscriberID, FieldType.FirstName, FieldType.LastName, 
				FieldType.CoAddress, FieldType.Address, FieldType.ZipCode, 
				FieldType.City, FieldType.Country, FieldType.Category,
				FieldType.Distributor, FieldType.Note
		};
		return ft;
	}
	
	public static FieldType getFieldType(String str) {
		if(str != null && str.length() > 0) {
			switch(str.toLowerCase()) {
			case "pnr":
			case "prenumerantid":
			case "prennr":
			case "personnr":
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
			case "c/o adress":
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
		} else {
			return FieldType.Unknown;
		}
	}
}

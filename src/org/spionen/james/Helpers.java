package org.spionen.james;

/**
 *
 * @author      Maxim Fris
 * @version     2011-01-23
 *
 */

import java.io.*;
import java.util.*;
import java.text.*;

public class Helpers {
	// Metod för att rensa personnummer
	public static String transformPNr(String pNr) {
		pNr = pNr.replaceAll("-", "");
		pNr = pNr.replaceAll("P", "");
		pNr = pNr.replaceAll("T", "");
		pNr = pNr.replaceAll("R", "");
		if (pNr.length() == 0) {
			pNr = "000";
		}
		return pNr;
	}

	public static String cleanRow(String row) {

		row = row.replaceAll("�", "�");
		row = row.replaceAll("�", "�");
		row = row.replaceAll("�", "�");
		row = row.replaceAll("�", "�");
		row = row.replaceAll("�", "�");
		row = row.replaceAll("�", "�");
		row = row.replaceAll("�", "�");
		row = row.replaceAll("�", "�");
		row = row.replaceAll("�", "�");
		row = row.replaceAll("�", "�");
		row = row.replaceAll("�", "�");

		row = row.toUpperCase(new Locale("sv", "SE"));
		return row;
	}

	// Metod f�r att rensa postnummer
	public static String cleanPostNr(String pNr) {

		pNr = pNr.replaceAll(" ", "");
		pNr = pNr.replaceAll("�", "");
		pNr = pNr.replaceAll("-", "");
		pNr = pNr.replaceAll("SE", "");

		if (pNr.length() == 0) {
			pNr = "0";
		}

		return pNr;
	}

	/**
	 * En metod f�r att l�sa prenumerantsobjekt fr�n en fil till en array.
	 * 
	 * @param f
	 *            �r ett filobjekt inneh�llandes prenumerantsobjekt
	 * @param p
	 *            �r en array av klassen Prenumerant
	 * @return Returnerar antalet fyllda poster i Array p
	 * @throws IOException
	 *             om inl�sningen g�r fel
	 * 
	 */

	public static int _readFromFileToArray(File f, Subscriber[] p) throws IOException {
		int i = 0;

		Scanner fin = new Scanner(f);

		while (fin.hasNext()) {

			String listRow = fin.nextLine();
			p[i] = new Subscriber(listRow);

			i++;

		}

		fin.close();
		return i;

	}

	/**
	 * Method for checking if and where a Prenumerant object is in an array of
	 * Prenumerant objects. Searches by looking for matches of ABNR.
	 * 
	 * @param p
	 *            A Prenumerant Array - The Array to search in.
	 * @param n
	 *            An int - The length of the Prenumerant Array to search in.
	 * @param a
	 *            A String - The string to search for.
	 * @return An int - The position of the Object in the Array if present.
	 */

	public static int binSearch(Subscriber[] p, int n, String a) {

		int first = 0; // F�rsta index
		int last = n - 1; // Sista index
		int middle = (first + last) / 2; // Mitten index
		int index = -1; // Returnerat index

		while ((first <= last) && !(p[middle].getAbNr().compareTo(a) == 0)) {

			if (p[middle].getAbNr().compareTo(a) < 0) {
				first = middle + 1;
			}

			if (p[middle].getAbNr().compareTo(a) > 0) {
				last = middle - 1;
			}

			middle = (first + last) / 2;

		}

		if (p[middle].getAbNr().compareTo(a) == 0) {
			index = middle;
		}

		return index;
	}

	public static String todaysDate() {
		DateFormat nuSE = new SimpleDateFormat("yyyyMMdd");
		return nuSE.format(new Date());
	}

	/**
	 * En metod f�r att kontrollera hur m�nga f�lt det finns representerade i en
	 * rad.
	 * 
	 * @param row
	 *            string med ett antal f�lt som �r avgr�nsade med delimiter.
	 * @param delimiter
	 *            Representerar det som anv�nds som delimiter i raden.
	 * 
	 * @return antalet f�lt i raden
	 */

	public static int numberOfFieldsInRow(String row, String delimiter) {
		int numberOfFields = 0;
		for (int i = 0; i < row.length(); i++) {
			Character c = row.charAt(i);
			Character d = delimiter.charAt(0);
			if(c.compareTo(d) == 0) {
				numberOfFields++;
			}
		}
		return numberOfFields;
	}

	/**
	 * En metod f�r att kontrollera och f�rbereda s� varje rad har korrekt antal
	 * f�lt.
	 * 
	 * @param row
	 *            string med ett antal f�lt som �r avgr�nsade med delimiter.
	 * @param numberOfFields
	 *            int med antalet f�lt som raden skall inneh�lla.
	 * 
	 * @return Rad med korrekt antal f�lt.
	 */

	public static String prepareRowForImport(String row, int numberOfFields) {

		// int numberOfDelimiters = numberOfFields-1;

		int numberOfDelimiters = Helpers.numberOfFieldsInRow(row, ";");
		while (numberOfDelimiters < numberOfFields) {
			row = row + ";";
			numberOfDelimiters++;
		}
		return row;
	}

	/**
	 * En metod f�r att kontrollera om en fil finns p� en specifik adress.
	 * 
	 * @param filePath
	 *            string med filepath till filen.
	 * 
	 * @return true om filen finns och false om den inte finns.
	 */

	public static boolean checkIfFileExists(String filePath) {
		return new File(filePath).exists();
	}

	/**
	 * En metod f�r att ta bort ett filobjekt
	 * 
	 * @param filePath
	 *            String med s�kv�g till filen som skall tas bort.
	 * 
	 */

	public static void _deleteFile(String filePath) {
		File temp = new File(filePath);
		temp.delete();

	}

	/**
	 * En metod f�r att ta bort en gammal fil och d�pa om den uppdaterade filen
	 * med den gamla filens namn
	 * 
	 * @param oldFile
	 *            String med s�kv�g till originalfilen som skall tas bort.
	 * @param newFile
	 *            String med s�kv�g till den nya filen som skall ers�tta
	 *            originalfilen.
	 * 
	 */

	public static void _replace_OldFile_with_NewFile(String oldFile, String newFile) {

		File oldSource = new File(oldFile);
		File newSource = new File(newFile);
		oldSource.delete();
		newSource.renameTo(oldSource);

	}

	/**
	 * Metod f�r att avg�ra hur m�nga rader det finns i en fil.
	 * 
	 * @param filePath
	 *            String med s�kv�g till filen.
	 * 
	 * @return Int med antalet rader.
	 */
	public static int numberOfRowsInFile(String filePath) throws IOException {
		int i = 0;

		// Open Source File
		BufferedReader sourceFile = new BufferedReader(new FileReader(filePath));
		while(sourceFile.readLine() != null) {
			i++;
		}
		sourceFile.close();
		return i;
	}

	public static void logStatusMessage(String message) {

		System.out.println("\n========================================"
				         + "========================================\n" + message + "\n"
				         + "========================================"
				         + "========================================\n");
	}

	/**
	 * Metod f�r att kontrollera att en folder finns. Om den inte finns s�
	 * skapas foldern.
	 * 
	 * @param folderPath
	 *            String med s�kv�g till foldern.
	 * 
	 */

	public static void makeSureFolderExists(String folderPath) {
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	// TODO Write Doc for checkIfStringIsNumeric
	public static boolean checkIfStringIsNumeric(String theString) {

		try {
			double d = Double.parseDouble(theString);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * Misc functions concerning handling of MasterFiles
	 */

	/**
	 * 
	 * @param masterList
	 * @return
	 */
	public static MasterFile latestFileInList(ArrayList<MasterFile> masterList) {
		Collections.sort(masterList, Collections.reverseOrder());
		return masterList.get(0);
	}

	public static ArrayList<MasterFile> makeArrayListFromPath(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		ArrayList<MasterFile> masterFiles = new ArrayList<MasterFile>();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String fileName = listOfFiles[i].getName();
				if (fileName.endsWith("_Master.txt")) {
					MasterFile mf = new MasterFile(listOfFiles[i].getName());
					masterFiles.add(mf);
				}
			}
		}
		return masterFiles;
	}

	public static ArrayList<File> makeArrayListOfFiles(String inFolder, String byFilter) {
		File folder = new File(inFolder);
		File[] listOfFiles = folder.listFiles();
		ArrayList<File> filesArray = new ArrayList<File>();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String fileName = listOfFiles[i].getName();
				if (fileName.matches("(?i).*" + byFilter + ".*")) {
					filesArray.add(listOfFiles[i]);
				}
			}
		}
		return filesArray;
	}

	public static String previousIssue(int year, int issue) {
		int piYear;
		int piNumber;

		// Check if same year.
		if (issue == 1) {
			piYear = year - 1;
			piNumber = 8;
		} else {
			piYear = year;
			piNumber = issue - 1;
		}
		return piYear + "-" + piNumber;
	}

	public static String todaysYear() {
		DateFormat nuSE = new SimpleDateFormat("yyyy");
		return nuSE.format(new Date());
	}

	public static String latestIssueYear() {
		String path = GetFile.libraryPath;
		ArrayList<MasterFile> masterFiles = Helpers.makeArrayListFromPath(path);
		MasterFile latest = Helpers.latestFileInList(masterFiles);

		return latest.getYear() + "";
	}

	public static String latestIssueNumber() {
		String path = GetFile.libraryPath;
		ArrayList<MasterFile> masterFiles = Helpers.makeArrayListFromPath(path);
		MasterFile latest = Helpers.latestFileInList(masterFiles);

		return latest.getNumber() + "";
}

	public static int latestIssueYearInt() {
		return Integer.parseInt(latestIssueYear());
	}

	public static int latestIssueNumberInt() {
		return Integer.parseInt(latestIssueNumber());
	}

}
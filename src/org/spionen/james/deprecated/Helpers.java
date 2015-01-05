package org.spionen.james.deprecated;

/**
 *
 * @author      Maxim Fris
 * @version     2011-01-23
 *
 */

import java.io.*;
import java.util.*;
import java.text.*;

import org.spionen.james.MasterFile;

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

	/**
	 * TODO: Lol what?
	 * @param row
	 * @return
	 */
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
		return row.split(delimiter).length;
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

	public static String todaysYear() {
		DateFormat nuSE = new SimpleDateFormat("yyyy");
		return nuSE.format(new Date());
	}

}
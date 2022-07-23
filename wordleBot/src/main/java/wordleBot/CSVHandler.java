package wordleBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.print.DocFlavor.URL;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public class CSVHandler {
	
	// name is hash key, "stats" is value
	
	private String name;
	private String[] stats;

	public CSVHandler(String name, String[] stats) {
		this.name = name;
		this.stats = stats;
	}
	
	
	public CSVHandler () {
		// TODO Auto-generated constructor stub
	}
	
	
	// imperative on bot start as server crash would reset hash
	public HashMap<String, String[]> readCSVAndUpdateHash () {
		
     	int i, j; 
		String[] stats = new String[5];
     	String playerName;
     	HashMap<String, String[]> playerInfo = new HashMap<String, String[]>();

     	InputStream is = getClass().getResourceAsStream("infoCatalog.csv");
     	System.out.println(is);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
			  
			  // BIG JUICER ADD RIGHT HERE... SAVED THE DAY?? TBD
			  // https://www.youtube.com/watch?v=d02PK8C5EaA look at this
		CSVReader csvReader = new CSVReader(br);
		String[] nextLine;
			
		try {
			
			while ((nextLine = csvReader.readNext()) != null) {	
//				System.out.println(Arrays.toString(nextLine));
				playerName = "";
				i = 0;
				j = 0;
				stats = new String[5];
						 
					for (String value : nextLine) {
					    	 
						if (j == 0) {
							playerName = value;
						}
						else {
							stats[i] = value;
					    	i ++;
					    }

					    	j ++;
					}
					     // final step, key and value have been initialized
						playerInfo.put(playerName, stats);  
			}
					  
			br.close();
			isr.close();
			is.close();
			csvReader.close();
					 
		} catch (Exception e) {		
			e.printStackTrace();
		}

		return playerInfo;
	}
	
	
	
	


//	// TODO need to see how this works when other existing names are in the csv already
//	public void writeToCSV () {
//		 
//		String[] info1 = {this.name, this.stats[0], 
//				 			this.stats[1], this.stats[2], 
//				 			this.stats[3], this.stats[4]
//				 			};
//		 
//	    List<String[]> allStats = new ArrayList<>();
//	    allStats.add(info1);
//
//	     
////	     https://www.geeksforgeeks.org/writing-a-csv-file-in-java-using-opencsv/
//	    File file = new File("src/main/resources/infoCatalog.csv");
//	     
//	    try {
//
//	        FileWriter fileWriter = new FileWriter(file);
//	        CSVWriter csvWriter = new CSVWriter(fileWriter);
//	         	         
//	        csvWriter.writeAll(allStats);
//	   
////	         // adding header to csv
////	         String[] header = { "Name", "Class", "Marks" };
////	         writer.writeNext(header);
////	   
////	         // add data to csv
////	         String[] data1 = { "Aman", "10", "620" };
////	         writer.writeNext(data1);
////	         String[] data2 = { "Suraj", "10", "630" };
////	         writer.writeNext(data2);
//	   
//	         // closing writer connection
//	        csvWriter.close();
//	    }
//	    catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	      
//	 }
//	
//	
//	
//	 
//	 
//	// TODO should have one of these updates for each field
//	public void updateCSV (String gamesPlayed, String winPercentage, String currentStreak, String maxStreak, String avgGuess){
//		   
//	    try {
//	   
//	        // Create an object of filereader
//	        // class with CSV file as a parameter.
//	        FileReader filereader = new FileReader("src/main/resources/infoCatalog.csv");
//	   
//	        try (// create csvReader object passing
//				         // file reader as a parameter
//			CSVReader csvReader = new CSVReader(filereader)) {
//	        	 
//	        	List<String[]> csvBody = csvReader.readAll();
//	        	csvBody.get(0)[5] = avgGuess;
//	        	filereader.close();
//	        	 
//	        	  File file = new File("src/main/resources/infoCatalog.csv");
//	    	     
//	    	     try {
//	    	         // create FileWriter object with file as parameter
//	    	         FileWriter outputfile = new FileWriter(file);
//	    	   
//	    	         // create CSVWriter object filewriter object as parameter
//	    	         CSVWriter writer = new CSVWriter(outputfile);
//	    	         
//	    	         
//	    	         writer.writeAll(csvBody);
//	    	   
////	    	         // adding header to csv
////	    	         String[] header = { "Name", "Class", "Marks" };
////	    	         writer.writeNext(header);
////	    	   
////	    	         // add data to csv
////	    	         String[] data1 = { "Aman", "10", "620" };
////	    	         writer.writeNext(data1);
////	    	         String[] data2 = { "Suraj", "10", "630" };
////	    	         writer.writeNext(data2);
//	    	   
//	    	         // closing writer connection
//	    	         writer.close();
//	    	     }
//	    	     catch (Exception e) {
//	    	         e.printStackTrace();
//	    	     }
//	        	 
//			}
//	     }
//	     catch (Exception e) {
//	         e.printStackTrace();
//	     }
//	 }
	 
		 
}

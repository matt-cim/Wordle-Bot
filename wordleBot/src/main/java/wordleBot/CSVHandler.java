package wordleBot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class CSVHandler {
	
	private String name;
	private String[] stats;

	public CSVHandler(String name, String[] stats) {
		this.name = name;
		this.stats = stats;
	}
	
	
	
	 public void writeToCSV () {
		 
		 String[] info1 = {this.name, this.stats[0], 
				 			this.stats[1], this.stats[2], 
				 			this.stats[3], this.stats[4]
				 			};
		 
	     List<String[]> entries = new ArrayList<>();
	     entries.add(info1);

	     
//	     https://www.geeksforgeeks.org/writing-a-csv-file-in-java-using-opencsv/
	     File file = new File("src/main/java/infoCatalog.csv");
	     
	     try {
	         // create FileWriter object with file as parameter
	         FileWriter outputfile = new FileWriter(file);
	   
	         // create CSVWriter object filewriter object as parameter
	         CSVWriter writer = new CSVWriter(outputfile);
	         
	         
	         writer.writeAll(entries);
	   
//	         // adding header to csv
//	         String[] header = { "Name", "Class", "Marks" };
//	         writer.writeNext(header);
//	   
//	         // add data to csv
//	         String[] data1 = { "Aman", "10", "620" };
//	         writer.writeNext(data1);
//	         String[] data2 = { "Suraj", "10", "630" };
//	         writer.writeNext(data2);
	   
	         // closing writer connection
	         writer.close();
	     }
	     catch (IOException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	     }
	     
	     
	 }
	 
	 
	 public void updateCSV (String gamesPlayed, String winPercentage, String currentStreak, String maxStreak, String avgGuess){
		   
	     try {
	   
	         // Create an object of filereader
	         // class with CSV file as a parameter.
	         FileReader filereader = new FileReader("src/main/java/infoCatalog.csv");
	   
	         try (// create csvReader object passing
				         // file reader as a parameter
			CSVReader csvReader = new CSVReader(filereader)) {
	        	 
	        	 List<String[]> csvBody = csvReader.readAll();
	        	 csvBody.get(0)[5] = avgGuess;
	        	 filereader.close();
	        	 
	        	 
	        	 
	    	     File file = new File("src/main/java/infoCatalog.csv");
	    	     
	    	     try {
	    	         // create FileWriter object with file as parameter
	    	         FileWriter outputfile = new FileWriter(file);
	    	   
	    	         // create CSVWriter object filewriter object as parameter
	    	         CSVWriter writer = new CSVWriter(outputfile);
	    	         
	    	         
	    	         writer.writeAll(csvBody);
	    	   
//	    	         // adding header to csv
//	    	         String[] header = { "Name", "Class", "Marks" };
//	    	         writer.writeNext(header);
//	    	   
//	    	         // add data to csv
//	    	         String[] data1 = { "Aman", "10", "620" };
//	    	         writer.writeNext(data1);
//	    	         String[] data2 = { "Suraj", "10", "630" };
//	    	         writer.writeNext(data2);
	    	   
	    	         // closing writer connection
	    	         writer.close();
	    	     }
	    	     catch (IOException e) {
	    	         // TODO Auto-generated catch block
	    	         e.printStackTrace();
	    	     }
	        	 
			}
	     }
	     catch (Exception e) {
	         e.printStackTrace();
	     }
	 }
	 
	 
	 
}

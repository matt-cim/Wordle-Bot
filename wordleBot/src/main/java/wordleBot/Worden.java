package wordleBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//https://zetcode.com/java/opencsv/
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

//**************************************************
//	bot token- OTQ2NjAzNDQ2ODc2OTI1OTUz.GtpxCN.nLenrS-VZ-aDFT41tPU4duVMKQjhavW22U76yo

//	https://github.com/DV8FromTheWorld/JDA/wiki - wiki section for examples

//	add roles on join

//	actual javadoc for JDA

// hash map- key is userID value is all stats boom
//**************************************************



public class Worden extends ListenerAdapter {

	public static void main (String[] args) {
		
//		using create default because otherwise would have to specify intent ex "listening"
//		build can be unsuccessful thus try catch
		try {
			JDABuilder.createDefault("OTQ2NjAzNDQ2ODc2OTI1OTUz.G-FB97.G07JfmKBxUOaWceZhRsotba5NGlot5FmEV4ZpY").addEventListeners(new Worden()).build();
		} catch (Exception e) {
			return;
		}
		
	}
	
	
//	recognizes a message has been set in the appropriate channel and responds accordingly
	 @Override
	 public void onMessageReceived (MessageReceivedEvent event) {
		 
		 MessageChannel channel = event.getChannel();
		 Pattern regex = Pattern.compile(".*let-the-games-begin.*");
		 Matcher correctChannel = regex.matcher(channel.getName());
	
		 if (!event.getAuthor().isBot() && correctChannel.matches()) {
//			 getMessage() has other misc info, but must extract raw content
		        String text = event.getMessage().getContentRaw(); 
		        System.out.println("content   " + text);
	            channel.sendMessage("I have recieved your Wordle").queue();
	            
	            writeToCSV(event.getAuthor().toString());
	            readCSV();
	            
	            
	            if (text.equals("update")) {
	            	updateCSV();
	            }
		 }
	        
	 }
	 
	 
	 public static void writeToCSV (String name) {
		 
		 String[] info1 = {name, "change me"};
		 
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
	 
	 
	 
	 public static void readCSV (){
	   
	     try {
	   
	         // Create an object of filereader
	         // class with CSV file as a parameter.
	         FileReader filereader = new FileReader("src/main/java/infoCatalog.csv");
	   
	         try (// create csvReader object passing
				         // file reader as a parameter
			CSVReader csvReader = new CSVReader(filereader)) {
				String[] nextRecord;
   
				 // we are going to read data line by line
				 while ((nextRecord = csvReader.readNext()) != null) {
				     for (String cell : nextRecord) {
				         System.out.print(cell + "\t");
				     }
				     System.out.println();
				 }
				 filereader.close();
			}
	     }
	     catch (Exception e) {
	         e.printStackTrace();
	     }
	     
	 }
	 
	 
	 
	 public static void updateCSV (){
		   
	     try {
	   
	         // Create an object of filereader
	         // class with CSV file as a parameter.
	         FileReader filereader = new FileReader("src/main/java/infoCatalog.csv");
	   
	         try (// create csvReader object passing
				         // file reader as a parameter
			CSVReader csvReader = new CSVReader(filereader)) {
	        	 
	        	 List<String[]> csvBody = csvReader.readAll();
	        	 csvBody.get(0)[1] = "magic";
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
	 
	 

	 
	 


//TODO 1. create regex that matches wordle score 2. auto assign permissions on join 3. extract stats from  text in private channel
//	 lowkey could just constantly write stats to text file and if server goes down call function that sets hash
//	back up

	 
	
	
}

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//https://zetcode.com/java/opencsv/
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

//**************************************************
//	https://github.com/DV8FromTheWorld/JDA/wiki - wiki section for examples

//	add roles on join

//	actual javadoc for JDA

// hash map- key is userID value is all stats boom

// games played, win percentage, current streak, max streak, guess distribution-> ones present on wordle
//**************************************************



public class Worden extends ListenerAdapter {
	
	// main data structure for users and respective statistics	
	private static HashMap<String, String[]> playerInfo = new HashMap<String, String[]>();

	
	public static void main (String[] args) {
	
		// using create default because otherwise would have to specify intent ex "listening"
		try {
			JDABuilder.createDefault("OTQ2NjAzNDQ2ODc2OTI1OTUz.G-FB97.G07JfmKBxUOaWceZhRsotba5NGlot5FmEV4ZpY").addEventListeners(new Worden()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Worden bot is up and running!");
		
		readCSVAndUpdateHash();
		
		printHash();
	}
	
	
	private static void printHash () {
		
		for (String name: playerInfo.keySet()) {
		    String key = name.toString();
		    String[] value = playerInfo.get(name);
		    System.out.println(key + " -> " + Arrays.toString(value) + "\n");
		}
		
	}
	
	
	// imperative on bot start as server crash would reset hash
	public static void readCSVAndUpdateHash () { 
		 	
     	int i, j; 
		String[] stats = new String[5];
     	String playerName;

     	
	     try {
	   
	    	 FileReader filereader = new FileReader("src/main/java/infoCatalog.csv");
	   
	         try (CSVReader csvReader = new CSVReader(filereader)) {
	        	 // read each line of CSV and parse so that user name and statistics are separate
	        	 String[] nextLine;
   
				 while ((nextLine = csvReader.readNext()) != null) {
					 playerName = "";
					 i = 0;
					 j = 0;
					 
				     for (String cell : nextLine) {
				    	 
				    	 if (j > 0) {
				    		 stats[i] = cell;
				    		 i ++;
				    	 }
				    	 else {
				    		 playerName = cell;
				    	 }
				    	 j ++;
				     }
				     // final step, key and value have been initialized
				     playerInfo.put(playerName, stats);  
				 }
				 filereader.close();
			}
	     }
	     catch (Exception e) {
	         e.printStackTrace();
	     }
	     
	 }
	
	
	 //	recognizes a message has been set in the appropriate channel and responds accordingly
	@Override
	public void onMessageReceived (MessageReceivedEvent event) {
		 
		MessageChannel channel = event.getChannel();
		Pattern regex = Pattern.compile(".*let-the-games-begin.*");
		Matcher correctChannel = regex.matcher(channel.getName());
		 
	
		if (!event.getAuthor().isBot() && correctChannel.matches()) {
			 
			// getMessage() has other misc info, but must extract raw content
			String text = event.getMessage().getContentRaw(); 
	        channel.sendMessage("I have recieved your Wordle").queue();
	            
	        String playerName = event.getAuthor().toString();
	            
	            
	   		Pattern wordleRegex = Pattern.compile("[W|w]ordle (\\d+) (1|2|3|4|5|6)\\/6\\*?(\\n.*){2,}");
	   		Matcher correctWordle = wordleRegex.matcher(text);
	   		 
	   		String[] defaultStats = {"0", "0", "0", "0", "0"};
	   		playerInfo.put(playerName, defaultStats);
            	
	   		CSVHandler csvHandler = new CSVHandler(playerName, defaultStats);
            	
	   		csvHandler.writeToCSV();
            
		            
//		            if (text.equals("update")) {
//		            	csvHandler.updateCSV("","","","",correctWordle.group(1));
//		            }
	   		 
	   		if (playerExists(playerName) && correctWordle.matches()) {
	   			System.out.println(correctWordle.group(1));
	   			csvHandler.updateCSV("","","","",correctWordle.group(2));
	   		}
	            	            
	   		System.out.println(correctWordle.matches());     
		 }
	        
	 }
	 
	 
	 private boolean playerExists (String playerName) { return playerInfo.containsKey(playerName); }
	 
	 private void clearHash () { playerInfo.clear(); }
	 

	 
//TODO 1. auto assign permissions on join
//	 	2. create separate method for each statistics one by one, add more indices for other stats, get inspiration from no longer existing wordle stats bot
//      3. test adding other members and reading multiple lines for hash initialization

	
}

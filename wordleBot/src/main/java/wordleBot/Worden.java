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
import java.util.HashMap;
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

// games played, win percentage, current streak, max streak, guess distribution-> ones present on wordle
//**************************************************



public class Worden extends ListenerAdapter {
	
	private HashMap<String, String[]> playerInfo = new HashMap<String, String[]>();

	
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
//		        System.out.println("content   " + text);
	            channel.sendMessage("I have recieved your Wordle").queue();
	            
	            String playerName = event.getAuthor().toString();
	            
	            
	   		 	Pattern wordleRegex = Pattern.compile("Wordle \\d+ (1|2|3|4|5|6)\\/6.");
	   		 	Matcher correctWordle = wordleRegex.matcher(text);
	    
	            
	            if (!playerExists(playerName) && !correctWordle.matches()) {
	            	
	            	String[] defaultStats = {"0", "0", "0", "0", "0"};
	            	playerInfo.put(playerName, defaultStats);
	            	
	            	CSVHandler csvHandler = new CSVHandler(playerName, defaultStats);
	            	
		            csvHandler.writeToCSV();
		            
		            csvHandler.readCSV();
		                 
		            if (text.equals("update")) {
		            	csvHandler.updateCSV();
		            }
	            	
	            }
//	            else if (playerExists(playerName) && correctWordle.matches()) {
//	            	System.out.println(playerInfo.get(playerName));
//	            }
	            
	            
	            System.out.println(correctWordle.matches());
	            
	            
		 }
	        
	 }
	 
	 
	 private boolean playerExists (String name) { return playerInfo.containsKey(name); }
	 
	 private void clearHash () { playerInfo.clear(); }
	 

	 
//TODO 1. create regex that matches wordle score 2. auto assign permissions on join 3. extract stats from  text in private channel
//	 lowkey could just constantly write stats to text file and if server goes down call function that sets hash
//	back up

	 
	
	
}

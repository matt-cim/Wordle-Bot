package wordleBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.mysql.cj.xdevapi.Statement;
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
	private static Connection connection = null;

	
	public static void main (String[] args) {
		
		// gets every column from the table
		String sql = "SELECT * " + "FROM info_catalog";

	
		// using create default because otherwise would have to specify intent ex "listening"
		try {
			JDABuilder.createDefault("OTQ2NjAzNDQ2ODc2OTI1OTUz.G-FB97.G07JfmKBxUOaWceZhRsotba5NGlot5FmEV4ZpY")
			.enableIntents(GatewayIntent.GUILD_MEMBERS).addEventListeners(new Worden()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// https://stackoverflow.com/questions/8146793/no-suitable-driver-found-for-jdbcmysql-localhost3306-mysql
		// https://stackoverflow.com/questions/17484764/java-lang-classnotfoundexception-com-mysql-jdbc-driver-in-eclipse
		// https://www.youtube.com/watch?v=BjfqKK24Ruk
		// 	loading the driver
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String dbURL = "jdbc:mysql://customer_338853_wordendb:Ovechkin8$$@na02-sql.pebblehost.com/customer_338853_wordendb";
			String username = "customer_338853_wordendb";
			String password = "Ovechkin8$$";
			connection = DriverManager.getConnection(dbURL, username, password);
			
			java.sql.Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			
			// iterate all rows of the table
			while (resultSet.next()) {
				String playerName = resultSet.getString("name");
				String gamesPlayed = String.valueOf(resultSet.getInt("games_played"));
				String winPercentage = String.valueOf(resultSet.getDouble("win_percentage"));
				String currentStreak = String.valueOf(resultSet.getInt("current_streak"));
				String maxStreak = String.valueOf(resultSet.getInt("max_streak"));
				String lastFourteen = resultSet.getString("last_fourteen");
				String bestScore = String.valueOf(resultSet.getInt("best_score"));
				String median = String.valueOf(resultSet.getInt("median"));
				String mode = String.valueOf(resultSet.getInt("mode"));
				String standardDev = String.valueOf(resultSet.getDouble("standard_deviation"));
				String wins = String.valueOf(resultSet.getInt("wins"));
				
				String[] stats = {gamesPlayed, winPercentage, currentStreak, maxStreak, 
									lastFourteen, bestScore, median, mode, standardDev, wins};
				
				
				playerInfo.put(playerName, stats);
			}

			// close all connections
			resultSet.close();
			statement.close();
			connection.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		
		System.out.println("Main finished running");
		
		printHash();
	}
	
	
	// have not yet seen it to be necessary
	@Override
	public void onReady(ReadyEvent e) { System.out.println("Worden bot is up and running!"); }
	
	
	// helpful when making sure Query took place successfully
	private static void printHash () {
		
		for (String name: playerInfo.keySet()) {
		    String key = name.toString();
		    String[] value = playerInfo.get(name);
		    System.out.println(key + " -> " + Arrays.toString(value));
		}
		System.out.println("\n");
	}
	
	
	// new player joins server & channel, respond and auto assign role
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		
		
        final List<TextChannel> channelList = event.getGuild().getTextChannelsByName
        												("let-the-games-begin", true);
        
        if (channelList.isEmpty()) {
        	System.out.println("Could not find the right channel");
            return;
        }

        final TextChannel derivedChannel = channelList.get(0); 
        final String playerName =  event.getMember().getUser().getAsTag();
        
        // adds "player" role
        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("994624121050763314")).queue();
        System.out.println("Player" + playerName + " has role updated");

        final String botResponse = String.format("Welcome %s to the %s",
        		playerName, event.getGuild().getName());

        derivedChannel.sendMessage(botResponse).queue();
        System.out.println("Player " + playerName + " has joined");
        
        
        // init. default stats for this new player, only if person is not in hash (hasnt left server then joined again)
        try {
			if (!playerExists(playerName)) {
				initNewPlayer(playerName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	// init -> initialize
	public static void initNewPlayer(String playerName) throws SQLException {
		
   		String[] defaultStats = {"0", "101.1", "0", "0", "NULL", "0", "0", "0", "1001", "0"};
   		playerInfo.put(playerName, defaultStats);
   		
   		// both data base and hash map are adding this player
   		MySQLConnection task = new MySQLConnection();
   		task.addPlayerToDatabase(playerName);
   		task.closeConnection();	
	}
	

	
	 //	recognizes a message has been set in the appropriate channel and responds accordingly
	@Override
	public void onMessageReceived (MessageReceivedEvent event) {
		 
		// filter channel
		MessageChannel channel = event.getChannel();
		Pattern regex = Pattern.compile(".*let-the-games-begin.*");
		Matcher correctChannel = regex.matcher(channel.getName());

	
		// message from non-bot user
		if (!event.getAuthor().isBot() && correctChannel.matches()) {
		
			String text = event.getMessage().getContentRaw(); 
	        channel.sendMessage("I have recieved your Wordle").queue();
	     
	        String playerName = event.getMember().getUser().getAsTag();  
	            
	        // match standard Wordle format
	   		Pattern wordleRegex = Pattern.compile("[W|w]ordle (\\d+) (1|2|3|4|5|6|X)\\/6\\*?(\\n.*){2,}");
	   		Matcher correctWordle = wordleRegex.matcher(text);
	   		
	   		if (correctWordle.matches()) {
	   			filter(correctWordle.group(1), correctWordle.group(2), playerName);
	   		}
	            	            
		} 
	 }

	
	// decides how to update each of the stats given a new games' been played
	private static void filter (String gameNumber, String score, String playerName) {
		System.out.println(score);
		// have to add myself into database before games start
		String[] statsArr = playerInfo.get(playerName);
		 
		// again, update hash then database for runtime sake
		
		// games played
		Integer gamesPlayed = Integer.parseInt(statsArr[0]) + 1;
		statsArr[0] = gamesPlayed.toString();
		
		// win % & wins
		Double currWinPercentage = Double.parseDouble(statsArr[1]);
		Integer wins = Integer.parseInt(statsArr[9]) + 1;
		Double winPercentage = (double) (wins / gamesPlayed) * 100.0;
		
		if (!score.equals("X")) {
			statsArr[9] = wins.toString();
		}
		
		// player actually won wordle
		if ((!score.equals("X") && winPercentage > currWinPercentage) 
				|| (!score.equals("X") && currWinPercentage == 101.1)) {
			statsArr[1] = winPercentage.toString();
		}
	
		printHash();
//		
//		// current streak
//		Integer currentStreak = Integer.parseInt(statsArr[2]) + 1;
//		statsArr[0] = gamesPlayed.toString();
//		
//		// max_streak
//		Integer gamesPlayed = Integer.parseInt(statsArr[0]) + 1;
//		statsArr[0] = gamesPlayed.toString();
//		
//		// last fourteen guesses
//		
//		
//		// best score
//		Integer gamesPlayed = Integer.parseInt(statsArr[0]) + 1;
//		statsArr[0] = gamesPlayed.toString();
//		
//		// median
//		Integer gamesPlayed = Integer.parseInt(statsArr[0]) + 1;
//		statsArr[0] = gamesPlayed.toString();
//		
//		// mode
//		Integer gamesPlayed = Integer.parseInt(statsArr[0]) + 1;
//		statsArr[0] = gamesPlayed.toString();
//		
//		// standard deviation
//		Double winPercentage = Double.parseDouble(statsArr[1]);
//		
				
				//updateDatabaseGamesPlayed(String gamesPlayed, String playerName)
	}
	 
	 private boolean playerExists (String playerName) { return playerInfo.containsKey(playerName); }
	 
	 
	 private void clearHash () { playerInfo.clear(); }
	 

	 
//TODO 1. auto assign permissions on join
//	 	2. create separate method for each statistics one by one, add more indices for other stats, get inspiration from no longer existing wordle stats bot
//      3. test adding other members and reading multiple lines for hash initialization

	
}

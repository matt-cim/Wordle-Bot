package wordleBot;

import net.dv8tion.jda.api.EmbedBuilder;
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

import java.awt.Color;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.mysql.cj.xdevapi.Statement;
//https://zetcode.com/java/opencsv/
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;


//**************************************************
//	https://github.com/DV8FromTheWorld/JDA/wiki - wiki section for examples

//	add roles on join

//	actual javadoc for JDA

// hash map- key is userID value is all stats boom

// games played, win percentage, current streak, max streak, guess distribution-> ones present on wordle
//**************************************************

// TODO
// 1. create other commands like scoreboard etc.
// 2. hints scraper that sends dm's
// 3. style the embed builder and round values
// 4. add funny quips to the i have recieved your wordle-- seee desktop screenshot
// 5. test test and test, correct values and multiple users
// 6. add comments and beautify
// 7. release



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
				String lastWordle = String.valueOf(resultSet.getInt("last_wordle"));
				String average = String.valueOf(resultSet.getDouble("average"));
				
				
				String[] stats = {gamesPlayed, winPercentage, currentStreak, maxStreak, 
									lastFourteen, bestScore, median, mode, standardDev, wins, lastWordle, average};
				
				
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
        System.out.println("Player " + playerName + " has role updated");

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
		
   		String[] defaultStats = {"0", "0.0", "0", "0", "NULL", "0", "0", "0", "0.0", "0", "0", "0.0"};
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
	        String playerName = event.getMember().getUser().getAsTag();  
	            
	        // match standard Wordle format
	   		Pattern wordleRegex = Pattern.compile("[W|w]ordle (\\d+) (1|2|3|4|5|6|X)\\/6\\*?(\\n.*){2,}");
	   		Matcher correctWordle = wordleRegex.matcher(text);
	   		
	   		if (correctWordle.matches()) {
	   			channel.sendMessage("I have recieved your Wordle").queue();
	   			try {
					filter(correctWordle.group(1), correctWordle.group(2), playerName);
				} catch (SQLException e) {
					e.printStackTrace();
				}
	   		}
	   		else if (text.equalsIgnoreCase("!mystats")) {
	   			String[] statsArr = playerInfo.get(playerName);
	   			//begin embed stuff
	   			EmbedBuilder builder = new EmbedBuilder();
	   			builder.setTitle("Here are all your Wordle statistics");
	   			builder.setThumbnail("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSWhLZqeHfe_8OzK50hCU-ES4yJ-CEwaraB1A&usqp=CAU");
	   		    builder.addField("Games Played", statsArr[0], true);
	   		    builder.addField("Win Percentage", statsArr[1], true);
	   		    builder.addField("Current Streak", statsArr[2], true);
	   		    builder.addField("Max Streak", statsArr[3], true);
	   			builder.addField("History", statsArr[4], true);
	   			builder.addField("Best Score", statsArr[5], true);
	   			builder.addField("Median", statsArr[6], true);
	   			builder.addField("Mode", statsArr[7], true);
	   			builder.addField("Standard Deviation", statsArr[8], true);
	   			builder.addField("Wins", statsArr[9], true);
	   			builder.addField("Last Wordle", statsArr[10], true);
	   			builder.addField("Average", statsArr[11], true);
//	   		    builder.addBlankField(false);
//	   		    builder.setFooter("Text");
	   			channel.sendMessageEmbeds(builder.build()).queue();
	   		}
	   		else if (text.equalsIgnoreCase("!scoreboard")) {
	   			List<String> averageScores = new ArrayList<>();			
	   			EmbedBuilder builder = new EmbedBuilder();
	   			builder.setTitle("SCOREBOARD");
	   			builder.setThumbnail("https://images.unsplash.com/photo-1652451764453-eff80b50f736?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8d29yZGxlfGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=400&q=60");
	   			builder.setColor(Color.CYAN);

	   			
	   			for (String name: playerInfo.keySet()) {
	   			    String[] fullArr = playerInfo.get(name);
	   			    // get rid of id after name
	   			    averageScores.add(fullArr[11] + " -> " + name.substring(0, name.length() - 5));
	   			}
	   			averageScores.add("4.1 -> joe mama");
	   			
	   			averageScores.add("7.5 -> foo_bomb");
	   			averageScores.add("1 -> oscar");
	   			Collections.sort(averageScores);
	   			
	   			for (Integer i = 1; i <= averageScores.size(); i ++) {
	   				builder.addField(i.toString() + ". ", averageScores.get(i - 1), true);
	   				builder.addBlankField(false);
	   			}
		
	   			channel.sendMessageEmbeds(builder.build()).queue();
	   		}
	   		else if (text.equalsIgnoreCase("!joke")) {
	   			// https://jokes.one/api/joke/#java
	   			// gets knock knock joke of day from that ^ API, using free service
	   			// To maintain our service level we ratelimit the number of API calls.
	   			// For public API calls this is 60 API calls a day with distribution of 5 calls an hour.
	   			// For paid plans this limit is increased according to the service level described in the plan.
	   			
	   		// personalize and add own comments
	   			
	   	        URL url = null;
	   	        String findJokeFromJSON = new String();
	   	        EmbedBuilder builder = new EmbedBuilder();
	   	        String sanitized = new String();
	   	        
	   	        
				try {
					url = new URL("https://api.jokes.one/jod?category=knock-knock");
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}

	   	        try {
	   	            //make connection
	   	            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
	   	            urlc.setRequestMethod("GET");
	   	            // set the content type
	   	            urlc.setRequestProperty("Content-Type", "application/json");
	   	            urlc.setRequestProperty("X-JokesOne-Api-Secret", "YOUR API KEY HERE");
	   	            urlc.setAllowUserInteraction(false);
	   	            urlc.connect();

	   	            //get result
	   	            BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
	   	            String l = null;
	   	            while ((l=br.readLine())!=null) {
	   	                //System.out.println(l);
	   	                findJokeFromJSON = l;
	   	            }
	   	            br.close();
	   	        } catch (Exception e){
	   	            System.out.println("Error occured");
	   	            System.out.println(e.toString());
	   	        }
	   	        
	   	        int start = findJokeFromJSON.lastIndexOf("text") + 7, finish = findJokeFromJSON.indexOf("copyright") - 6;
	   	        sanitized = findJokeFromJSON.substring(start, finish);
	   	        sanitized = sanitized.replaceAll("\r", "").replaceAll("\n", "");
	   	        builder.setDescription(sanitized);
	   	        channel.sendMessageEmbeds(builder.build()).queue();
	   		}
	   		else if (text.equalsIgnoreCase("!hint")) {
	   			//event.getAuthor().openPrivateChannel().complete().sendMessage("hello there").queue();
	   		    // personalize and add own comments
	   		    
	   		    
	   		    //Instantiating the URL class
	   			URL url = null;
	   			try {
	   				url = new URL("https://gamerjournalist.com/wordle-answers/");
	   			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	   			}
	   			//Retrieving the contents of the specified page
	   			Scanner sc = null;
	   			try {
	   				sc = new Scanner(url.openStream());
	   			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	   			}
	   			//Instantiating the StringBuffer class to hold the result
	   			StringBuffer sb = new StringBuffer();
	   			while(sc.hasNext()) {
	   				sb.append(sc.next());
	   	         //System.out.println(sc.next());
	   			}
	   			//Retrieving the String from the String Buffer object
	   			String result = sb.toString();
	   			//Removing the HTML tags
	   			result = result.replaceAll("<[^>]*>", "");
	   			boolean postedAnswer = result.contains("Merriam-Webster");
	   			String definition = result.substring(result.indexOf("as:") + 3, result.indexOf("Yesterday"));
	   			event.getAuthor().openPrivateChannel().complete().sendMessage(definition).queue();
	   		}
	            	            
		} 
	 }

	
	// decides how to update each of the stats given a new games' been played
	private static void filter (String gameNumber, String score, String playerName) throws SQLException {

				
		String[] statsArr = playerInfo.get(playerName);
		boolean wonGame = !score.equals("X");
		 
		// again, update hash then database for runtime sake
		
		// games played
		Integer gamesPlayed = Integer.parseInt(statsArr[0]) + 1;
		statsArr[0] = gamesPlayed.toString();
		
		// win % & wins
		Double winPercentage;
		Integer wins;
		double doublePlayed = gamesPlayed;
		double doubleWins;
		
		if (wonGame) {
			wins = Integer.parseInt(statsArr[9]) + 1;
			statsArr[9] = wins.toString();
			doubleWins = wins;
			winPercentage = (doubleWins / doublePlayed) * 100.0;
			statsArr[1] = winPercentage.toString();
		}
		else {
			wins = Integer.parseInt(statsArr[9]);
			doubleWins = wins;
			winPercentage = (doubleWins / doublePlayed) * 100.0;
			statsArr[1] = winPercentage.toString();
		}
		
				
		// current streak & max streak, needed to add another column- last wordle
		Integer lastWordle = Integer.parseInt(statsArr[10]);
		Integer thisWordle = Integer.parseInt(gameNumber);
		statsArr[10] = gameNumber;
		Integer streak = Integer.parseInt(statsArr[2]) + 1;
		
		if ((thisWordle - lastWordle == 1) ||  lastWordle == 0 || statsArr[2].equals("0")) {
			statsArr[2] = streak.toString();
			Integer currMaxStreak = Integer.parseInt(statsArr[3]);
			
			if (currMaxStreak < streak) {
				statsArr[3] = streak.toString();
			}
		}
		else {
			statsArr[2] = "0";
		}
		
		
		// best score
		Integer currBestScore = Integer.parseInt(statsArr[5]);
		Integer currScore;
		if (currBestScore == 0 && wonGame) {
			currScore = Integer.parseInt(score);
			statsArr[5] = currScore.toString();;
		}
		else if (wonGame) {
			currScore = Integer.parseInt(score);
			if (currScore < currBestScore) {
				statsArr[5] = currScore.toString();
			}
		}

	
		dataSetComputation(statsArr, score);	
		
		
        // update database now that local hash has been updated
  		MySQLConnection task = new MySQLConnection();
   		task.updateDatabase(playerName, statsArr);
   		task.closeConnection();	
				
		printHash();
	}
	
	
	// performs computation that require the last <= 14 Wordle submissions of a user
	private static void dataSetComputation (String[] statsArr, String score) {
		// last fourteen guesses
		String lastFourteen = statsArr[4];
		
		if (lastFourteen.equals("NULL")) {
			statsArr[4] = score + "-";
		}
		else {
			String currStats = statsArr[4] + score + "-";
			statsArr[4] = currStats;
		}
		
		String noNullScores = new String();
		noNullScores = statsArr[4].replaceAll("X","");
		
		String[] splitScores = noNullScores.split("-");
		
		Integer singleScore;
		int sum = 0;
		double doubleSum = 0;
		Double averageScore;
		List<Integer> noEmptyStrings = new ArrayList<>();
		
		for (int i = 0; i < splitScores.length; i ++) {
			if (!splitScores[i].equals("X") && !splitScores[i].equals("")) {
				singleScore = Integer.parseInt(splitScores[i]);
				noEmptyStrings.add(singleScore);
				sum += singleScore;
			}
		}
		
		// average
		doubleSum = sum;
		averageScore = doubleSum / noEmptyStrings.size();
		statsArr[11] = averageScore.toString();
		
		
		// only do these stats for wins meaning dont include the X shit
		// need to add an average score column
		Collections.sort(noEmptyStrings);
		// median
		if (noEmptyStrings.size() > 0) {
			int medianValue = 0; 
			int middle = noEmptyStrings.size() / 2;
			if (noEmptyStrings.size() % 2 == 1) {
			    medianValue = noEmptyStrings.get(middle);
			}
			else {
			   medianValue = (noEmptyStrings.get(middle - 1) + noEmptyStrings.get(middle)) / 2;
			}
			Integer median = medianValue;
			statsArr[6] = median.toString();
			
			
			int count = 0, countCheck = 0;
			Integer mode = 0;
			
			for (int j = 1; j <= 6; j ++) {
				
				count = Collections.frequency(noEmptyStrings, j);
				
				if (count > countCheck) {
					countCheck = count;
					mode = j;
				}
				
			}
			statsArr[7] = mode.toString();
		}
		

		// standard deviation
        double varianceHelp = 0.0;
        
        for (Integer value : noEmptyStrings) {
        	varianceHelp += Math.pow(value - averageScore, 2);
        }

        // standard deviation is the square root of variance
        Double standardDeviation = Math.sqrt(varianceHelp / noEmptyStrings.size());
        statsArr[8] = standardDeviation.toString();
	}
	 
	private boolean playerExists (String playerName) { return playerInfo.containsKey(playerName); }
	 
	 
	private void clearHash () { playerInfo.clear(); }
	 
	
}

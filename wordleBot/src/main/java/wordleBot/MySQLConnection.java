package wordleBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnection {
	
	private static Connection connection = null;
	
	// default constructor, establishes connection
	public MySQLConnection () throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String dbURL = "jdbc:mysql://customer_338853_wordendb:Ovechkin8$$@na02-sql.pebblehost.com/customer_338853_wordendb";
			String username = "customer_338853_wordendb";
			String password = "Ovechkin8$$";
			connection = DriverManager.getConnection(dbURL, username, password);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
		
	}

	public static void updateDatabaseGamesPlayed(String gamesPlayed, String playerName) throws SQLException {
		// note have to find the games played first via the hash
		String sql = "UPDATE nameOfTable " + "SET gamesPlayed = “ + gamesPlayed + “WHERE     name = " + playerName;
			
		// prolly need a try catch for this
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	}
	
	
	// would execute this if player DNE in hash and joins channel, sets default stats
	// note want this when a new player JOINS, NOT sends first message
	public static void addPlayerToDatabase() throws SQLException {
		String sql = "INSERT INTO nameOfTable VALUES (‘name’, 'defaultStat1', 'defaultStat2', 69)";
	         

		Statement statement  = connection.createStatement();
		statement.executeUpdate(sql);
		statement.close();

		System.out.println("New player added to the database");
	}

	public static void closeConnection() throws SQLException {
		connection.close();
	}



}

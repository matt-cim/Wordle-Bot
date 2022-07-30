package wordleBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
	
	
	// would execute this if player DNE in hash and joins channel, sets default stats
	// note want this when a new player JOINS, NOT sends first message
	public void addPlayerToDatabase(String playerName) throws SQLException {
		String sql = "INSERT INTO info_catalog (name, games_played, win_percentage, current_streak, max_streak, last_fourteen, best_score, median, mode, standard_deviation, wins, last_wordle) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		//String sql = "INSERT INTO info_catalog VALUES (" + playerName + ", '0', '101.1', '0', '0', 'NULL', '0', '0', '0', '1001')";
		
		// using a prepared statement to avoid SQL injection attack
		PreparedStatement prepStatement = connection.prepareStatement(sql);
		prepStatement.setString(1, playerName);
		prepStatement.setInt(2, 0);
		prepStatement.setDouble(3, 101.1);
		prepStatement.setInt(4, 0);
		prepStatement.setInt(5, 0);
		prepStatement.setString(6, "NULL");
		prepStatement.setInt(7, 0);
		prepStatement.setInt(8, 0);
		prepStatement.setInt(9, 0);
		prepStatement.setInt(10, 1001);
		prepStatement.setInt(11, 0);
		prepStatement.setInt(12, 0);
		

		prepStatement.executeUpdate();
		prepStatement.close();

		System.out.println("New player added to the database");
	}

	public void updateDatabaseGamesPlayed(String gamesPlayed, String playerName) throws SQLException {
		// note have to find the games played first via the hash
		// this sql is def wrong lmao, follow similar method as add player to databasse
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
	

	public void closeConnection() throws SQLException {
		connection.close();
	}



}

package wordleBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLConnection {
	
	private static Connection connection = null;
	
	// default constructor, establishes connection
	public MySQLConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String dbURL = "jdbc:mysql://REST_OF_URL";
			String username = "USERNAME";
			String password = "PASSWORD";
			connection = DriverManager.getConnection(dbURL, username, password);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
		
	}
	
	
	// executed if the player DNE in hash and joins channel, sets default statistics
	// note want this when a new player JOINS, NOT sends first message
	public void addPlayerToDatabase(String playerName) throws SQLException {
		String sql = "INSERT INTO info_catalog (name, games_played, win_percentage, current_streak, max_streak, last_fourteen, best_score, median, mode, standard_deviation, wins, last_wordle, average) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		// using a prepared statement to avoid SQL injection attack
		PreparedStatement prepStatement = connection.prepareStatement(sql);
		prepStatement.setString(1, playerName);
		prepStatement.setInt(2, 0);
		prepStatement.setDouble(3, 0.0);
		prepStatement.setInt(4, 0);
		prepStatement.setInt(5, 0);
		prepStatement.setString(6, "NULL");
		prepStatement.setInt(7, 0);
		prepStatement.setInt(8, 0);
		prepStatement.setInt(9, 0);
		prepStatement.setDouble(10, 0.0);
		prepStatement.setInt(11, 0);
		prepStatement.setInt(12, 0);
		prepStatement.setDouble(13, 0);
		
		prepStatement.executeUpdate();
		prepStatement.close();

		System.out.println("New player added to the database");
	}

	public void updateDatabase(String playerName, String[] statsArr) throws SQLException {
		// this SQL query only affects the row of the specified player name 
		String sql = "UPDATE info_catalog SET games_played = " + statsArr[0] + ", win_percentage = " + statsArr[1] + 
				", current_streak = " + statsArr[2] + ", max_streak = " + statsArr[3] + 
				", last_fourteen = '" + statsArr[4] + "', best_score = " + statsArr[5] + ", median = " + statsArr[6] + 
				", mode = " + statsArr[7] + ", standard_deviation = " + statsArr[8] + ", wins = " + statsArr[9] +
				", last_wordle = " + statsArr[10] + ", average = " + statsArr[11] + "WHERE name = '" + playerName + "'";
			
		PreparedStatement prepStatement = connection.prepareStatement(sql);
		try {
			prepStatement.executeUpdate(sql);
			prepStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	
	}
	
	// closes connection to database
	public void closeConnection() throws SQLException {
		connection.close();
	}


}

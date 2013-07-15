package net.prawny.ChestShopDB.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Database interface. (Sounds more awesome than it actually is)
 * @author Prawny
 *
 */
public interface Database {
	
	/**
	 * Opens the database connection.
	 * @return the database Connection
	 */
	public Connection open();
	
	/**
	 * Closes the database connection.
	 */
	public void close();
	
	/**
	 * Send a query to the database.<br/>
	 * <i>Remember to call resultSet.close() after use!</i>
	 * @param sql SQL string
	 * @return ResultSet result of query
	 */
	public ResultSet query(PreparedStatement sql);
	
	/**
	 * Performs an update on the database.
	 * @param sql SQL string
	 * @return The row count for SQL Data Manipulation Language (DML) statements<br/>
	 * &nbsp;&nbsp;<i>or</i><br/>
	 * 0 for SQL statements that return nothing 
	 */
	public int update(PreparedStatement sql);
	
	/**
	 * Checks if a table exists.
	 * @return true if table exists
	 */
	public boolean checkTable(String table);
	
	/**
	 * Check if the connection is still active.
	 * @return true if the connection is still active
	 */
	public boolean checkConnection();
	
	/**
	 * Gets the database connection object.
	 * @return Connection to the database
	 */
	public Connection getConnection();
}

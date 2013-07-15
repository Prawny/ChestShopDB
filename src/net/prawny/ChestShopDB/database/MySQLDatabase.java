package net.prawny.ChestShopDB.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.prawny.ChestShopDB.ChestShopDB;
import net.prawny.ChestShopDB.Config;


/**
 * Creates and handles a MySQL database.
 * @author Prawny
 *
 */
public class MySQLDatabase implements Database {
	
	private final String host;
	private final String port;
	private final String database;
	private final String username;
	private final String password;
	private final String url;
	
	private Connection connection;
	
	/**
	 * MySQL database constructor. All needed information is loaded from config.yml
	 */
	public MySQLDatabase() {
		this.host = Config.loadValue("host");
		this.port = Config.loadValue("port");
		this.database = Config.loadValue("database");
		this.username = Config.loadValue("username");
		this.password = Config.loadValue("password");
		this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
	}
	
	@Override
	public Connection open() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection(url, username, password);
			return connection;
		} catch (SQLException e) {
			ChestShopDB.getPlugin().getLogger().severe("An error has occured with the MySQL server. Reason: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			ChestShopDB.getPlugin().getLogger().severe("JDBC driver not found!");
		}
		return connection;
	}
	
	@Override
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ResultSet query(PreparedStatement sql) {
		ResultSet result;
		
		try {
			result = sql.executeQuery();
			return result;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public int update(PreparedStatement sql) {
		int result;
		
		try {
			result = sql.executeUpdate();
			return result;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean checkTable(String table) {
		DatabaseMetaData md = null;
		
		try {
			md = connection.getMetaData();
			ResultSet tables = md.getTables(null, null, table, null);
			
			if (tables.next()) {
				tables.close();
				return true;
			} else {
				tables.close();
				return false;
			}
		} catch (SQLException e) {
			ChestShopDB.getPlugin().getLogger().warning("Could not check if table \"" + table + "exists. Reason: \"" + e.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean checkConnection() {
		if (this.connection != null) {
			return true;
		}
		return false;
	}
	
	@Override
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * Gets the database host.
	 * @return database host
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Gets the database port.
	 * @return database port
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * Gets the database username.
	 * @return database username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Gets the database password.
	 * @return database password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Returns the database name.
	 * @return database name
	 */
	public String getDatabase() {
		return database;
	}
	
	/**
	 * Gets the database connection url.
	 * @return connection url String
	 */
	public String getUrl() {
		return url;
	}
}

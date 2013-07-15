//https://github.com/PatPeter/SQLibrary/blob/master/lib/PatPeter/SQLibrary/SQLite.java
package net.prawny.ChestShopDB.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.prawny.ChestShopDB.ChestShopDB;
import net.prawny.ChestShopDB.Config;

/**
 * Creates and handles an SQLite database.
 * @author Prawny
 *
 */
public class SQLiteDatabase implements Database {
	
	private final String separator = System.getProperty("file.separator");
	
	private Connection connection;
	
	private String datafolder;
	private File dbFile;
	
	private final String name;
	
	/**
	 * SQLite database constructor.
	 */
	public SQLiteDatabase() {
		this.name = Config.loadValue("database");
		
		datafolder = ChestShopDB.getPlugin().getDataFolder().getAbsolutePath();
		dbFile = new File(datafolder + separator + name + ".db");
		
		//Create database if it doesn't exist
		if (!dbFile.exists()) {
			try {
				dbFile.createNewFile();
			} catch (IOException e) {
				ChestShopDB.getPlugin().getLogger().severe("Could not create a new SQLite database! Reason: " + e.getMessage());
			}
		}
	}
	
	@Override
	public Connection open() {
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
			return connection;
		} catch (SQLException e) {
			ChestShopDB.getPlugin().getLogger().severe("Could not establish an SQLite connection. Reason: " + e.getMessage());
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
}

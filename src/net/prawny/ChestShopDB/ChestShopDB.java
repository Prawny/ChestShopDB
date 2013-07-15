package net.prawny.ChestShopDB;

import java.sql.PreparedStatement;
import java.util.logging.Logger;

import net.prawny.ChestShopDB.database.Database;
import net.prawny.ChestShopDB.database.MySQLDatabase;
import net.prawny.ChestShopDB.database.SQLiteDatabase;
import net.prawny.ChestShopDB.listeners.ShopCreatedListener;
import net.prawny.ChestShopDB.listeners.ShopDestroyedListener;
import net.prawny.ChestShopDB.listeners.TransactionListener;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Main plugin class.
 * @author Prawny
 *
 */
public class ChestShopDB extends JavaPlugin {
	
	private static ChestShopDB plugin;
	private static Server server;
	private static Logger logger;
	
	private static Database database;
	private static String dbSystem;
	
	private final String table = 
		"CREATE TABLE shops (" +
		"owner varchar(16) NOT NULL, " +
		"adminShop boolean, " +
		"itemId int, " +
		"stock int, " +
		"buyPrice double, " +
		"sellPrice double, " +
		"quantity int, " +
		"world varchar(50), " +
		"x double, " +
		"y double, " +
		"z double);";
	
	@Override
	public void onDisable() {
		try {
			database.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void onEnable() {
		plugin = this;
		server = getServer();
		logger = getLogger();
		
		PluginManager pluginManager = Bukkit.getPluginManager();
		
		//Check that ChestShop is enabled
		if (pluginManager.getPlugin("ChestShop") != null) {
			logger.info("ChestShop has been detected! This is a good thing.");
		} else {
			logger.severe("ChestShop has not been detected. This is a bad thing.");
			pluginManager.disablePlugin(this);
			return;
		}
		
		//Load config
		Config.createConfigFile();
		Config.loadConfig();
		
		dbSystem = Config.loadValue("dbsystem");
		
		//Create or load database
		if (dbSystem.equalsIgnoreCase("mysql")) {
			database = new MySQLDatabase();
			database.open();
		} else if (dbSystem.equalsIgnoreCase("sqlite")) {
			database = new SQLiteDatabase();
			database.open();
		} else {
			logger.info("No database system loaded - disabling plugin. (Check config.yml!)");
			plugin.getPluginLoader().disablePlugin(this);
		}
		
		//Check that a connection has been established, else an NPE would be thrown
		if (!database.checkConnection()) {
			logger.severe("Database connection failed, disabling plugin...");
			pluginManager.disablePlugin(this);
			return;
		}
		
		//If "shops" table does not exist, create it
		if (!database.checkTable("shops")) {
			try {
				//Create the "shops" table
				PreparedStatement ps = database.getConnection().prepareStatement(table);
				database.update(ps);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		//If both shop logging options are false, disable the plugin
		if (!Config.canLogNewShops() && !Config.canLogOldShops()) {
			logger.info("All database logging options are set to false. Check config.yml!");
			pluginManager.disablePlugin(this);
			return;
		}
		
		//New shops should be logged, register listener
		if (Config.canLogNewShops()) {
			pluginManager.registerEvents(new ShopCreatedListener(), plugin);
		}
		
		//Old shops should be logged, register listener
		if (Config.canLogOldShops()) {
			pluginManager.registerEvents(new TransactionListener(), plugin);
		}
		
		//Register ShopDestroyEvent listener
		pluginManager.registerEvents(new ShopDestroyedListener(), plugin);
		
	}
	
	/**
	 * Returns the ChestShopDB instance.
	 * @return ChestShopDB instance
	 */
	public static ChestShopDB getPlugin() {
		return plugin;
	}
	
	/**
	 * Returns the server.
	 * @return Server object
	 */
	public static Server getBukkitServer() {
		return server;
	}
	
	/**
	 * Returns the database instance.
	 * @return Database object
	 */
	public static Database getPluginDatabase() {
		return database;
	}
}

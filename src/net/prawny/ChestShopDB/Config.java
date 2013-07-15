package net.prawny.ChestShopDB;

/**
 * Handles all config.yml operations.
 * @author Prawny
 *
 */
public class Config {
	
	private static ChestShopDB plugin = ChestShopDB.getPlugin();
	
	private static boolean newShops;
	private static boolean oldShops;
	private static String databaseSystem;
	
	/**
	 * Load config values from config.yml
	 */
	public static void loadConfig() {	
		//Default values in case of bad config.yml
		newShops = true;
		oldShops = true;
		databaseSystem = "sqlite";
		
		//Load config values
		newShops = plugin.getConfig().getBoolean("newshops");
		oldShops = plugin.getConfig().getBoolean("oldshops");
		databaseSystem = plugin.getConfig().getString("dbsystem");
	}
	
	/**
	 * Save a copy of config.yml to ChestShopDB data folder.
	 */
	public static void createConfigFile() {
		plugin.saveDefaultConfig();
	}
	
	/**
	 * Returns whether shops are logged on creation.
	 * @return boolean value specified in config.yml
	 */
	public static boolean canLogNewShops() {
		return newShops;
	}
	
	/**
	 * Returns true if shops are logged after a transaction.
	 * @return boolean value specified in config.yml
	 */
	public static boolean canLogOldShops() {
		return oldShops;
	}
	
	/**
	 * Returns the database management system specified in config.
	 * <br/><i>Defaults to SQLite.</i>
	 * @return String value of either MySQL or SQLite, specified in config.yml
	 */
	public static String getDatabaseSystem() {
		return databaseSystem;
	}
	
	/**
	 * Returns the specified config value.
	 * @param path - Path of the String to get
	 * @return Requested String
	 */
	public static String loadValue(String path) {
		return plugin.getConfig().getString(path);
	}
}

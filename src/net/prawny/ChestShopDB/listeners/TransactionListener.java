package net.prawny.ChestShopDB.listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.prawny.ChestShopDB.ChestShopDB;
import net.prawny.ChestShopDB.database.Database;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.ChestShop.Events.TransactionEvent;

public class TransactionListener implements Listener {
	
	private static Database database = ChestShopDB.getPluginDatabase();
	private static String adminShop = Bukkit.getPluginManager().getPlugin("ChestShop").getConfig().getString("ADMIN_SHOP_NAME");
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public static void onTransaction(TransactionEvent event) {
		Sign sign = event.getSign();
		boolean isAdminShop = false;
		
		//Check if shop is an admin shop
		if (sign.getLine(1).equalsIgnoreCase(adminShop)) {
			isAdminShop = true;
		}
		
		//Get item Id
		String itemString = sign.getLine(3);
		Material item = Material.matchMaterial(itemString);
		
		//Get remaining stock
		int stock = 0;
		ItemStack[] inventory = event.getStock();
		
		for (ItemStack stack : inventory) {
			if (stack.getType() == item) {
				stock += stack.getAmount();
			}
		}
		
		//Get buy & sell prices
		String formatted[] = event.getSign().getLine(2).toLowerCase().split(":");
		double buyPrice = 0;
		double sellPrice = 0;
		
		for (String s : formatted) {
			if (s.contains("free")) {
				buyPrice = 0;
				sellPrice = 0;
				break;
			}
			
			if (s.contains("b")) {
				buyPrice = Integer.parseInt(s.replace("[b\\s]", ""));
			}
			
			if (s.contains("s")) {
				sellPrice = Integer.parseInt(s.replace("[s\\s]", ""));
			}
		}
		
		//Search for existing record
		Connection connection = database.getConnection();
		
		try {
			PreparedStatement existing = connection.prepareStatement("SELECT world, x, y, z FROM shops WHERE world = ? AND x = ? AND y = ? AND z = ?;");
			
			existing.setString(1, sign.getWorld().getName());
			existing.setDouble(2, sign.getX());
			existing.setDouble(3, sign.getY());
			existing.setDouble(4, sign.getZ());
			
			ResultSet result = database.query(existing);
		
			if (result.wasNull()) { //Add new record
				result.close();			
				
				PreparedStatement newShop = connection.prepareStatement("INSERT INTO shops VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				
				newShop.setString(1, event.getOwner().getName());
				newShop.setBoolean(2, isAdminShop);
				newShop.setInt(3, item.getId());
				newShop.setInt(4, stock);
				newShop.setDouble(5, buyPrice);
				newShop.setDouble(6, sellPrice);
				newShop.setInt(7, Integer.parseInt(event.getSign().getLine(1)));
				newShop.setString(8, sign.getWorld().getName());
				newShop.setDouble(9, sign.getX());
				newShop.setDouble(10, sign.getY());
				newShop.setDouble(11, sign.getZ());
				
				database.query(newShop);

			} else if (!isAdminShop){ //Update shop stock
				result.close();
				
				PreparedStatement admin = connection.prepareStatement("UPDATE shops SET stock = ? WHERE world = ? AND x = ? AND y = ? AND z = ?;");
				
				admin.setInt(1, stock);
				admin.setString(2, sign.getWorld().getName());
				admin.setDouble(3, sign.getX());
				admin.setDouble(4, sign.getY());
				admin.setDouble(5, sign.getZ());
				
				database.update(admin);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

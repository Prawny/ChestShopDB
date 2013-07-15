package net.prawny.ChestShopDB.listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.prawny.ChestShopDB.ChestShopDB;
import net.prawny.ChestShopDB.database.Database;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.ChestShop.Events.ShopCreatedEvent;

public class ShopCreatedListener implements Listener {
	
	private static Database database = ChestShopDB.getPluginDatabase();
	private static String adminShop = Bukkit.getPluginManager().getPlugin("ChestShop").getConfig().getString("ADMIN_SHOP_NAME");
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public static void onShopCreate(ShopCreatedEvent event) {
		Sign sign = event.getSign();
		Chest chest = event.getChest();
		boolean isAdminShop = false;
		
		//Check if shop is an admin shop
		if (event.getSignLine((short) 0).equalsIgnoreCase(adminShop)) {
			isAdminShop = true;
		}
		
		//Get item Id
		String itemString = event.getSignLine((short) 3);
		Material item = Material.matchMaterial(itemString);
		
		//Get stock
		Inventory chestInv = chest.getInventory();
		int stock = 0;
		
		for (ItemStack stack : chestInv) {
			if (stack == null) {
				continue;
			}
			
			if (stack.getType() == item) {
				stock += stack.getAmount();
			}
		}
		
		//Get buy & sell prices
		String formatted[] = event.getSignLine((short) 2).toLowerCase().split(":");
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
		
		//Add new record
		Connection connection = database.getConnection();
		
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO shops VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
			
			ps.setString(1, event.getPlayer().getName());
			ps.setBoolean(2, isAdminShop);
			ps.setInt(3, item.getId());
			ps.setInt(4, stock);
			ps.setDouble(5, buyPrice);
			ps.setDouble(6, sellPrice);
			ps.setInt(7, Integer.parseInt(event.getSignLine((short) 1)));
			ps.setString(8, sign.getWorld().getName());
			ps.setDouble(9, sign.getX());
			ps.setDouble(10, sign.getY());
			ps.setDouble(11, sign.getZ());

			database.update(ps);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
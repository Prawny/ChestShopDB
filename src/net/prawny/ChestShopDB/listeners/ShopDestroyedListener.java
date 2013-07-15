package net.prawny.ChestShopDB.listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.prawny.ChestShopDB.ChestShopDB;
import net.prawny.ChestShopDB.database.Database;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;

public class ShopDestroyedListener implements Listener {
	
	private static Database database = ChestShopDB.getPluginDatabase();
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public static void onSignBreak(ShopDestroyedEvent event) {
		Sign sign = event.getSign();
		Connection connection = database.getConnection();
		
		try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM shops WHERE world = ? AND x = ? AND y = ? AND z = ?;");
			
			ps.setString(1, sign.getWorld().getName());
			ps.setDouble(2, sign.getX());
			ps.setDouble(3, sign.getY());
			ps.setDouble(4, sign.getZ());
			
			database.update(ps);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

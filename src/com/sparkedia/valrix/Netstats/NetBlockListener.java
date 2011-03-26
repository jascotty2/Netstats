package com.sparkedia.valrix.Netstats;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class NetBlockListener extends BlockListener {
	protected Netstats plugin;
	private HashMap<String, Property> users;
	private HashMap<String, Integer> actions;
	private String pFolder;
	protected LinkedHashMap<String, Object> config;
	protected String host;
	protected String database;
	protected String username;
	protected String password;
	protected Database db;
	private int updateRate;
	
	public NetBlockListener(Netstats plugin) {
		this.plugin = plugin;
		this.pFolder = plugin.pFolder+"/players/";
		this.config = plugin.config;
		this.users = plugin.users;
		this.actions = plugin.actions;
		this.updateRate = (Integer)config.get("updateRate");
		this.db = plugin.db;
	}

	public void onBlockBreak(BlockBreakEvent e) {
		// If the event wasn't canceled by another plugin
		if (!e.isCancelled()) {
			String name = e.getPlayer().getName();
			if (!users.containsKey(name)) {
				// They reloaded the plugins, time to re-set the player property files
				users.put(name, new Property(pFolder+name+".stats", plugin));
				actions.put(name, (updateRate/2));
			}
			Property prop = users.get(name);
			int count = actions.get(name)+1;
			prop.inc("broken"); // Add 1 to broken
			if (count == updateRate) {
				long now = System.currentTimeMillis();
				String sql = "";
				sql += "broken="+prop.getInt("broken")+", ";
				sql += (prop.getInt("placed") > 0) ? "placed="+prop.getInt("placed")+", " : "";
				sql += (prop.getInt("deaths") > 0) ? "deaths="+prop.getInt("deaths")+", " : "";
				sql += (prop.getInt("mobsKilled") > 0) ? "mobskilled=mobskilled+"+prop.getInt("mobsKilled")+", " : "";
				sql += (prop.getInt("playersKilled") > 0) ? "playerskilled=playerskilled+"+prop.getInt("playersKilled")+", " : "";
				sql += (prop.getDouble("distance") > 0) ? "distance=distance+"+prop.getDouble("distance")+", " : "";
				sql += "seen="+prop.getLong("seen")+", ";
				sql += "total="+(prop.getLong("total")+(now-prop.getLong("seen")))+" WHERE player='"+name+"';";
				db.update(sql);
				// Reset data data back to nothing except enter and total
				prop.setInt("broken", 0);
				prop.setInt("placed", 0);
				prop.setInt("deaths", 0);
				prop.setInt("mobsKilled", 0);
				prop.setInt("playersKilled", 0);
				prop.setDouble("distance", 0);
				prop.setLong("seen", now);
				prop.save();
				// Reset watched actions back to 0 (zero)
				actions.put(name, 0);
			} else {
				// Update timestamp
				long now = System.currentTimeMillis();
				prop.setLong("total", prop.getLong("total")+(now-prop.getLong("seen")));
				prop.setLong("seen", now);
				prop.save();
				actions.put(name, count);
			}
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent e) {
		// If the event wasn't canceled by another plugin
		if (!e.isCancelled()) {
			String name = e.getPlayer().getName();
			if (!users.containsKey(name)) {
				// Plugin is reset, make sure to re-set the property files
				users.put(name, new Property(pFolder+name+".stats", plugin));
				actions.put(name, (updateRate/2));
			}
			Property prop = users.get(name);
			int count = actions.get(name)+1;
			prop.inc("placed");
			if (count == updateRate) {
				long now = System.currentTimeMillis();
				String sql = "";
				sql += "placed="+prop.getInt("placed")+", ";
				sql += (prop.getInt("broken") > 0) ? "broken="+prop.getInt("broken")+", " : "";
				sql += (prop.getInt("deaths") > 0) ? "deaths="+prop.getInt("deaths")+", " : "";
				sql += (prop.getInt("mobsKilled") > 0) ? "mobskilled=mobskilled+"+prop.getInt("mobsKilled")+", " : "";
				sql += (prop.getInt("playersKilled") > 0) ? "playerskilled=playerskilled+"+prop.getInt("playersKilled")+", " : "";
				sql += (prop.getDouble("distance") > 0) ? "distance=distance+"+prop.getDouble("distance")+", " : "";
				sql += "seen="+prop.getLong("seen")+", ";
				sql += "total="+(prop.getLong("total")+(now-prop.getLong("seen")))+" WHERE player='"+name+"';";
				db.update(sql);
				// Reset data data back to nothing except enter and total
				prop.setInt("broken", 0);
				prop.setInt("placed", 0);
				prop.setInt("deaths", 0);
				prop.setInt("mobsKilled", 0);
				prop.setInt("playersKilled", 0);
				prop.setDouble("distance", 0);
				prop.setLong("seen", now);
				prop.save();
				// Reset watched actions back to 0 (zero)
				actions.put(name, 0);
			} else {
				// Update timestamp
				long now = System.currentTimeMillis();
				prop.setLong("total", prop.getLong("total")+(now-prop.getLong("seen")));
				prop.setLong("seen", now);
				prop.save();
				actions.put(name, count);
			}
		}
	}
}

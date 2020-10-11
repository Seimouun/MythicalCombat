package aut.smn.mythicalcombat.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import aut.smn.mythicalcombat.commands.DebugCommand;
import aut.smn.mythicalcombat.listeners.SwordListener;

public class Main extends JavaPlugin {
	
	private static Main plugin;

	@Override
	public void onEnable() {
		plugin = this;
		getCommand("debug").setExecutor(new DebugCommand());
		PluginManager pl = Bukkit.getPluginManager();
		pl.registerEvents(new SwordListener(), this);
		SwordListener.update();
	}
	@Override
	public void onDisable() {
		super.onDisable();
	}
	public static Main getPlugin() {
		return plugin;
	}
}

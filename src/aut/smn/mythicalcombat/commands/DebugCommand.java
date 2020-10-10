package aut.smn.mythicalcombat.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import aut.smn.mythicalcombat.util.Prefix;
import aut.smn.mythicalcombat.util.Util;

public class DebugCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player)sender;
		if(player.isOp()) {
			player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aFull Counter"));
		}else {
			player.sendMessage(Prefix.getPrefix() + "§cYou are not allowed to use that command");
		}
		return false;
	}
	
}

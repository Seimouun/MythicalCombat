package aut.smn.mythicalcombat.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import aut.smn.mythicalcombat.util.Prefix;
import aut.smn.mythicalcombat.util.SoundEffects;
import aut.smn.mythicalcombat.util.Util;

public class DebugCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player)sender;
		if(player.isOp()) {
			if(args[0].equals("1")) {
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aFull Counter"));
			}else if(args[0].equals("2")) {
				SoundEffects.playFullCounterCounterSound(player);
			}else if(args[0].equals("3")) {
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aPerfect Execution"));
			}else if(args[0].equals("4")) {
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aHammer Shock"));
			}
		}else {
			player.sendMessage(Prefix.getPrefix() + "Â§cYou are not allowed to use that command");
		}
		return false;
	}
	
}

package aut.smn.mythicalcombat.commands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import aut.smn.mythicalcombat.util.Prefix;
import aut.smn.mythicalcombat.util.SoundEffects;
import aut.smn.mythicalcombat.util.Util;

public class DebugCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player)sender;
		if(player.isOp()) {
			if(args[0].equals("1")) {
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aFull Counter", new String[] {"§8Counter every §7Attack §8or §7Spell", "§8and repell the spell with §b150% §8of the damage"}));
			}else if(args[0].equals("2")) {
				SoundEffects.playFullCounterCounterSound(player);
			}else if(args[0].equals("3")) {
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aPerfect Execution", new String[] {"§8Select any entity by hovering them,", "§8activate the item and slash through all of the entities", "§8dealing §b10™¥ §8damage"}));
			}else if(args[0].equals("4")) {
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aHammer Shock", new String[] {"§7This item has 3 activations", "§81. Launch yourself up in the air", "§82. Stop in the air for a brief second to aim", "§81. Launch yourself in a direction, ", "  §8slamming the ground causing", "  §8lightning to strike and §5knock upping", "  §8everyone surrounding you and", "  §8dealing §c3™¥ §8damage"}, 100));
			}else if(args[0].equals("5")) {
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aAwakening Souls", new String[] {"§8Passive: Collect souls of dead entities near you", "§8Active: Consume all souls around you", "§8and charge a powerful attack"}, 100));
			}else if(args[0].equals("6")) {
				ItemStack item = Util.getItemStackInInventoryStartsWithName("§aAwakening Souls", player);
				ItemMeta im = item.getItemMeta();
				List<String> lore = im.getLore();
				if(lore.size() <= 3) {
					lore.add("§eSouls collected: §6100");
				}else {
					lore.set(3, "§eSouls collected: §6100");
				}
				im.setLore(lore);
				item.setItemMeta(im);
			}else if(args[0].equals("7")) {
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aAzrael's Scythe", new String[] {"§8Passive: Collect souls of dead entities near you", "§8Active: Consume all souls around you", "§8and charge a powerful attack"}, 100));
			}else if(args[0].equals("8")) {
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aKulgron's Scythe", new String[] {"§8Passive: Collect souls of dead entities near you", "§8Active: Consume all souls around you", "§8and charge a powerful attack"}, 100));
			}else if(args[0].equals("9")) {
				//TODO
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aChainlink", new String[] {"§8Passive: Collect souls of dead entities near you", "§8Active: Consume all souls around you", "§8and charge a powerful attack"}, 100));
			}else if(args[0].equals("10")) {
				//TODO
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aPerfect Teleportation", new String[] {"§8Passive: Collect souls of dead entities near you", "§8Active: Consume all souls around you", "§8and charge a powerful attack"}, 100));
			}else if(args[0].equals("11")) {
				//TODO
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aLast Breath", new String[] {"§8Passive: Collect souls of dead entities near you", "§8Active: Consume all souls around you", "§8and charge a powerful attack"}, 100));
			}else if(args[0].equals("12")) {
				//TODO riven ult in direction
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aDaromok's Scythe", new String[] {"§8Passive: Collect souls of dead entities near you", "§8Active: Consume all souls around you", "§8and charge a powerful attack"}, 100));
			}else if(args[0].equals("11")) {
				//TODO
				player.getInventory().addItem(Util.createItem(Material.NETHERITE_SWORD, "§aAria of Perseverance", new String[] {"§8Passive: Collect souls of dead entities near you", "§8Active: Consume all souls around you", "§8and charge a powerful attack"}, 100));
			}
		}else {
			player.sendMessage(Prefix.getPrefix() + "§cYou are not allowed to use that command");
		}
		return false;
	}
	
}

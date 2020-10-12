package aut.smn.mythicalcombat.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundEffects {

	public static void playFullCounterCounterSound(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VEX_HURT, 1, 2);
		player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.3f, 2);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FOX_BITE, 1, 2);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_DEATH, 0.3f, (float)(1.7 + Math.random() * 0.3));
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1.5f);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 2f);
	}
	public static void playPerfectExecutionSound(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1.8f);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1, 1.3f);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.7f, 1.5f);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 2f);
	}
	public static void playPerfectExecutionExecuteSound(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.1f, 1.4f);
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.2f, 0.8f);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.8f, 1.1f);
	}
	public static void playHammerShockLaunchInAir(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 0.8f);
	}
	public static void playHammerShockLaunchFromAir(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_AMBIENT, 1f, 1.4f);
	}
	public static void playHammerShockIdle(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f);
	}
	public static void playHammerShockLand(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.7f);
	}
}

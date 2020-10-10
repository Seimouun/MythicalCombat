package aut.smn.mythicalcombat.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import aut.smn.mythicalcombat.main.Main;
import aut.smn.mythicalcombat.util.Util;

public class SwordListener implements Listener {

//	private static int eggs = 0;
//	private static int rounds = 1;
//	private static boolean fun = true;
	private static ArrayList<String> fullCounterList = new ArrayList<String>();
	private static ArrayList<Integer> fullCounterExplosionDamage = new ArrayList<Integer>();
	
	@EventHandler
	public void onSwordInterract(PlayerInteractEvent event) {
		if(event.getItem() != null && event.getItem().hasItemMeta()) {
			String itemName = event.getItem().getItemMeta().getDisplayName();
			Player player = event.getPlayer();
			if(itemName.equals("§aFull Counter")) {
				fullCounterList.add(player.getName());
				new BukkitRunnable() {
					int timer = 0;
					@Override
					public void run() {
						player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(Util.getRightHandDireciton(player)), 10, 0.05, 0.05, 0.05, 0.01);
						if(timer >= 10) {
							fullCounterList.remove(player.getName());
							cancel();
						}
						timer++;
					}
					
				}.runTaskTimer(Main.getPlugin(), 0, 1);
			}
		}
	}
//	@EventHandler
//	public void projectileLandDuplicate(ProjectileHitEvent event) {
//		if(eggs < 1000) {
//			Egg e1 = (Egg)event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.EGG);
//			e1.setVelocity(new Vector((fun)? 0.1 : 0,0.3,(fun)? 0 : 0.1));
//			Egg e2 = (Egg)event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.EGG);
//			e2.setVelocity(new Vector((fun)? -0.1 : 0,0.3,(fun)? 0 : -0.1));
//			eggs++;
//		}
//		if(eggs % Math.pow(2, rounds) - 1 == 0) {
//			fun = !fun;
//			rounds++;
//		}
//	}
//	@EventHandler
//	public void chickenSpawn(CreatureSpawnEvent event) {
//		if(event.getEntityType() == EntityType.CHICKEN) {
//			event.setCancelled(true);
//		}
//		
//	}
	@EventHandler
	public void onTakeDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile)event.getDamager();
			if("counter_projectile".equals(proj.getCustomName())) {
				event.setDamage(1 + event.getDamage() * 3);
			}
		}
		if(event.getEntity() instanceof Player) {
			if(fullCounterList.contains(event.getEntity().getName())) {
				Player player = (Player)event.getEntity();
				player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 70, 0.5, 1, 0.5);
				//explosion counter
				if(event.getCause() == DamageCause.ENTITY_EXPLOSION) {
					fullCounterExplosionDamage.add(event.getDamager().getEntityId());
					player.getWorld().createExplosion(player.getLocation(), 5);
				}else if(event.getCause() == DamageCause.ENTITY_ATTACK) {
					event.getDamager().setVelocity(event.getDamager().getLocation().subtract(player.getLocation()).toVector().normalize().setY(0));
					((LivingEntity)event.getDamager()).damage(event.getDamage() * 1.5);
					fullCounterList.remove(player.getName());
				}else if(event.getCause() == DamageCause.PROJECTILE) {
					Projectile proj = (Projectile) player.getWorld().spawn(player.getEyeLocation(), event.getDamager().getClass());
					event.getDamager().remove();
					proj.setCustomName("counter_projectile");
					proj.setVelocity(player.getLocation().getDirection().multiply(2));
				}
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void takeDamage(EntityDamageByBlockEvent event) {
		if(event.getEntity() instanceof Player) {
			if(fullCounterList.contains(event.getEntity().getName())) {
				Player player = (Player)event.getEntity();
				event.setCancelled(true);
				fullCounterList.remove(player.getName());
			}
		}
	}
	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		if(fullCounterExplosionDamage.contains(event.getEntity().getEntityId())) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onPotionHit(PotionSplashEvent event) {
		event.getAffectedEntities().forEach(x -> {
			if(fullCounterList.contains(x.getName())) {
				if(event.getEntity().getShooter() instanceof LivingEntity) {
					Bukkit.broadcastMessage(event.getEntity().getMetadata("EffectData") + "");
					event.getPotion().getEffects().forEach(e -> {x.removePotionEffect(e.getType());});
				}
				event.setCancelled(true);
			}
		});
	}	
}

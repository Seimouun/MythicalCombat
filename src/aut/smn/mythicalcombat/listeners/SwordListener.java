package aut.smn.mythicalcombat.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import aut.smn.mythicalcombat.effects.CrowdControl;
import aut.smn.mythicalcombat.effects.CrowdControlType;
import aut.smn.mythicalcombat.main.Main;
import aut.smn.mythicalcombat.util.SoundEffects;
import aut.smn.mythicalcombat.util.Util;
import net.minecraft.server.v1_16_R1.PacketPlayOutAnimation;

public class SwordListener implements Listener {

//	private static int eggs = 0;
//	private static int rounds = 1;
//	private static boolean fun = true;
	private static ArrayList<String> fullCounterList = new ArrayList<String>();
	private static ArrayList<Integer> fullCounterExplosionDamage = new ArrayList<Integer>();

	private static HashMap<String, Set<UUID>> perfectExecutionSelection = new HashMap<String, Set<UUID>>();

	private static HashMap<String, Integer> hammerShockState = new HashMap<String, Integer>();

	@EventHandler
	public void onSwordInterract(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getItem().hasItemMeta()) {
			String itemName = event.getItem().getItemMeta().getDisplayName();
			Player player = event.getPlayer();
			if (itemName.equals("§aFull Counter")) {
				fullCounterList.add(player.getName());
				new BukkitRunnable() {
					int timer = 0;

					@Override
					public void run() {
						player.getWorld().spawnParticle(Particle.FLAME,
								player.getLocation().add(Util.getRightHandDireciton(player)), 10, 0.05, 0.05, 0.05,
								0.01);
						if (timer >= 80) {
							fullCounterList.remove(player.getName());
							cancel();
						}
						timer++;
					}

				}.runTaskTimer(Main.getPlugin(), 0, 1);
			} else if (itemName.equals("§aPerfect Execution")) {
				for (UUID uuid : perfectExecutionSelection.get(player.getName())) {
					Entity e = Bukkit.getEntity(uuid);
					if (e != null) {
						if (e instanceof LivingEntity) {
							player.setGameMode(GameMode.SPECTATOR);
							Vector vec = Util.genVec(player.getLocation(), e.getLocation()).normalize();
							player.setVelocity(vec.multiply(4).add(new Vector(0, 0.3, 0)));
							player.setFallDistance(0);
							SoundEffects.playPerfectExecutionSound(player);
							new BukkitRunnable() {
								int timer = 0;
								boolean damaged = false;

								@Override
								public void run() {
									boolean canContinue = !damaged || player.getEyeLocation()
											.add(player.getEyeLocation().getDirection().multiply(2)).getBlock()
											.getType().equals(Material.AIR);
									if (timer < 4 && canContinue) {
										player.getWorld().spawnParticle(Particle.SQUID_INK,
												player.getLocation().add(0, 1, 0), 10, 0.2, 1, 0.2, 0.01);
										if (player.getLocation().distance(e.getLocation()) < 2) {
											player.setGameMode(GameMode.SURVIVAL);
											EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(
													player, e, DamageCause.ENTITY_ATTACK, 20);
											Bukkit.getPluginManager().callEvent(damageEvent);
											if (!damageEvent.isCancelled()) {
												((LivingEntity) e).damage(20);
												SoundEffects.playPerfectExecutionExecuteSound(player);
												damaged = true;
											}
										}
										timer++;
									} else {
										player.setGameMode(GameMode.SURVIVAL);
										player.setVelocity(vec.multiply(0.2));
										cancel();
									}
								}
							}.runTaskTimer(Main.getPlugin(), 0, 1);
						}
					}
				}
				Set<UUID> list = perfectExecutionSelection.getOrDefault(player.getName(), new HashSet<UUID>());
				list.clear();
				perfectExecutionSelection.put(player.getName(), list);
			} else if (itemName.equals("§aHammer Shock")) {
				int state = hammerShockState.getOrDefault(player.getName(), 0);
				if (state == 0) {
					Location playerLoc = player.getLocation();
					player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation(), 200, 0.1, 0.5, 0.1, 0,
							playerLoc.clone().subtract(0, 1, 0).getBlock().getBlockData());
					playerLoc.setPitch(0);
					Vector direction = playerLoc.getDirection();
					player.setVelocity(direction.setY(1.5));
					hammerShockState.put(player.getName(), 1);
					SoundEffects.playHammerShockLaunchInAir(player);
				} else if (state == 1) {
					player.setVelocity(new Vector(0, player.getVelocity().getY() / 2, 0));
					player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 0, true));
					hammerShockState.put(player.getName(), 2);
					SoundEffects.playHammerShockIdle(player);
				} else if (state == 2) {
					player.removePotionEffect(PotionEffectType.LEVITATION);
					player.setVelocity(player.getLocation().getDirection().multiply(3));
					hammerShockState.put(player.getName(), 4);
					SoundEffects.playHammerShockLaunchFromAir(player);
				}
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
	public static void update() {
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory() .getItemInMainHand().getItemMeta().getDisplayName().equals("§aPerfect Execution")) {
						glowEntitesAndReturnThem(player);
					} else {
						int state = hammerShockState.getOrDefault(player.getName(), 0);
						if (state == 4 && player.isOnGround()) {
							player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation(), 1000, 3, 0.2, 3, 0, player.getLocation().subtract(0, 1, 0).getBlock().getBlockData());
							player.setVelocity(player.getVelocity().multiply(0.2));
							hammerShockState.put(player.getName(), 0);
							SoundEffects.playHammerShockLand(player);
							player.getWorld().getNearbyEntities(player.getLocation(), 3, 2, 3).forEach(e -> {
								if (!e.equals(player) && e instanceof LivingEntity) {
									EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, e, DamageCause.ENTITY_ATTACK, 3);
									Bukkit.getPluginManager().callEvent(damageEvent);
									if (!damageEvent.isCancelled()) {
										((LivingEntity) e).damage(6);
										new CrowdControl() {
											
											@Override
											public CrowdControl effect(Entity entity) {
												entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1f, 1f);
												entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
												entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1f, 1f);
												entity.getWorld().strikeLightningEffect(entity.getLocation());
												entity.getWorld().spawnParticle(Particle.BLOCK_CRACK, entity.getLocation(), 50, 0.02, 0.2, 0.02, 0, entity.getLocation().subtract(0, 1, 0).getBlock().getBlockData());
												return this;
											}
										}.control(e, CrowdControlType.AIRBORNE.setIntensity(1.3));
									} else {
										player.damage(6);
										new CrowdControl() {
											
											@Override
											public CrowdControl effect(Entity entity) {
												entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1f, 1f);
												entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
												entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1f, 1f);
												entity.getWorld().strikeLightningEffect(entity.getLocation());
												entity.getWorld().spawnParticle(Particle.BLOCK_CRACK, entity.getLocation(), 50, 0.02, 0.2, 0.02, 0, entity.getLocation().subtract(0, 1, 0).getBlock().getBlockData());
												return this;
											}
										}.control(player, CrowdControlType.AIRBORNE.setIntensity(1.3));
										
									}
								}
							});
						}
					}
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 1);
	}

	public static void glowEntitesAndReturnThem(Player player) {
		for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), 12, 12, 12)) {
			if (e != player && !(e instanceof Item)) {
				try {
					if (e.getLocation().distance(player.getLocation()) < 13) {
						Vector direction = player.getLocation().getDirection().normalize();
						Vector directionToEntity = player.getLocation().add(0, 1, 0)
								.subtract(e.getLocation().add(0, e.getHeight() / 2, 0)).toVector().normalize();
						double distance = direction.distance(directionToEntity);

						if (distance > 1.993) {
							Set<UUID> list = perfectExecutionSelection.getOrDefault(player.getName(),
									new HashSet<UUID>());
							list.add(e.getUniqueId());
							perfectExecutionSelection.put(player.getName(), list);

							Util.setEntityGlowing(e, player, true);
						} else {
							Set<UUID> list = perfectExecutionSelection.getOrDefault(player.getName(),
									new HashSet<UUID>());
							list.remove(e.getUniqueId());
							perfectExecutionSelection.put(player.getName(), list);

							Util.setEntityGlowing(e, player, false);
						}
					} else {
						Set<UUID> list = perfectExecutionSelection.getOrDefault(player.getName(), new HashSet<UUID>());
						list.remove(e.getUniqueId());
						perfectExecutionSelection.put(player.getName(), list);

						Util.setEntityGlowing(e, player, false);
					}
				} catch (Exception exc) {
				}
			}
		}
	}

	@EventHandler
	public void onTakeDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if (proj.getCustomName().startsWith("counter_projectile_")) {
				int damage = Integer.parseInt(proj.getCustomName().split("_")[2]);
				event.setDamage(1 + damage * 3);
			}
		}
		if (event.getEntity() instanceof Player) {
			if (fullCounterList.contains(event.getEntity().getName())) {
				Player player = (Player) event.getEntity();
				player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 70, 0.5, 1, 0.5);
				player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 20, 0.5, 1, 0.5);
				PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
				Util.setValue(packet, "a", player.getEntityId());
				Util.setValue(packet, "b", (byte) 0);
				Bukkit.getOnlinePlayers().forEach(p -> {
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
				});
				SoundEffects.playFullCounterCounterSound(player);
				// explosion counter
				if (event.getCause() == DamageCause.ENTITY_EXPLOSION) {
					fullCounterExplosionDamage.add(event.getDamager().getEntityId());
					player.getWorld().createExplosion(player.getLocation(), 5);
				} else if (event.getCause() == DamageCause.ENTITY_ATTACK) {
					event.getDamager().setVelocity(event.getDamager().getLocation().subtract(player.getLocation())
							.toVector().normalize().setY(0));
					((LivingEntity) event.getDamager()).damage(event.getDamage() * 1.5);
					fullCounterList.remove(player.getName());
				} else if (event.getCause() == DamageCause.PROJECTILE) {
					Projectile proj = (Projectile) player.getWorld().spawn(player.getEyeLocation(),
							event.getDamager().getClass());
					event.getDamager().remove();
					proj.setCustomName("counter_projectile_" + event.getDamage());
					proj.setVelocity(player.getLocation().getDirection().multiply(2));
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void takeDamage(EntityDamageByBlockEvent event) {
		if (event.getEntity() instanceof Player) {
			if (fullCounterList.contains(event.getEntity().getName())) {
				Player player = (Player) event.getEntity();
				event.setCancelled(true);
				fullCounterList.remove(player.getName());
			}
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		if (fullCounterExplosionDamage.contains(event.getEntity().getEntityId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPotionHit(PotionSplashEvent event) {
		event.getAffectedEntities().forEach(x -> {
			if (fullCounterList.contains(x.getName())) {
				if (event.getEntity().getShooter() instanceof LivingEntity) {

				}
				event.getPotion().getEffects().forEach(e -> {
					x.removePotionEffect(e.getType());
				});
				Player player = (Player) event.getEntity();
				player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 70, 0.5, 1, 0.5);
				player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 20, 0.5, 1, 0.5);
				PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
				Util.setValue(packet, "a", player.getEntityId());
				Util.setValue(packet, "b", (byte) 0);
				Bukkit.getOnlinePlayers().forEach(p -> {
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
				});
				SoundEffects.playFullCounterCounterSound(player);
				event.setCancelled(true);
			}
		});
	}
}

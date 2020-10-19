package aut.smn.mythicalcombat.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import aut.smn.mythicalcombat.effects.CrowdControl;
import aut.smn.mythicalcombat.effects.CrowdControlType;
import aut.smn.mythicalcombat.main.Main;
import aut.smn.mythicalcombat.util.Damage;
import aut.smn.mythicalcombat.util.Damage.DamageType;
import aut.smn.mythicalcombat.util.SoundEffects;
import aut.smn.mythicalcombat.util.Util;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R1.PacketPlayOutAnimation;

public class SwordListener implements Listener {

//	private static int eggs = 0;
//	private static int rounds = 1;
//	private static boolean fun = true;
	private static ArrayList<String> fullCounterList = new ArrayList<String>();
	private static ArrayList<Integer> fullCounterExplosionDamage = new ArrayList<Integer>();

	private static HashMap<String, UUID> perfectExecutionSelection = new HashMap<String, UUID>();
	
	private static HashMap<String, UUID> perfectTeleportationSelection = new HashMap<String, UUID>();

	private static HashMap<String, Integer> hammerShockState = new HashMap<String, Integer>();
	
	private static ArrayList<Location> awakeningSoulsLocList = new ArrayList<Location>();
	private static Set<String> kulgronsScytheList = new HashSet<String>();

	@EventHandler
	public void onSwordInterract(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getItem().hasItemMeta() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
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
				Entity e = Bukkit.getEntity(perfectExecutionSelection.get(player.getName()));
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
								boolean canContinue = !damaged || player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(2)).getBlock().getType().equals(Material.AIR);
								if (timer < 4 && canContinue) {
									player.getWorld().spawnParticle(Particle.SQUID_INK, player.getLocation().add(0, 1, 0), 10, 0.2, 1, 0.2, 0.01);
									if (player.getLocation().distance(e.getLocation()) < 2) {
										player.setGameMode(GameMode.SURVIVAL);
										EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent( player, e, DamageCause.ENTITY_ATTACK, 20);
										Bukkit.getPluginManager().callEvent(damageEvent);
										if (!damageEvent.isCancelled()) {
											Damage.damageEntity(((LivingEntity) e), DamageType.MAGICAL, 20);
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
			} else if (itemName.equals("§aHammer Shock")) {
				int state = hammerShockState.getOrDefault(player.getName(), 0);
				if (state == 0) {
					Location playerLoc = player.getLocation();
					player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation(), 200, 0.1, 0.5, 0.1, 0, playerLoc.clone().subtract(0, 1, 0).getBlock().getBlockData());
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
			}else if (itemName.startsWith("§aAwakening Souls")) {
				if(awakeningSoulsLocList.size() > 0) {
					for(Location loc : awakeningSoulsLocList) {
						SoundEffects.playAwakeningSoulsCall(loc);
						new BukkitRunnable() {
							double i = 0;
							@Override
							public void run() {
								if(i < player.getLocation().distance(loc)) {
									Vector dirToPlayer = Util.genVec(loc, player.getLocation());
									i+=1;
									player.getWorld().spawnParticle(Particle.SOUL, loc.clone().add(0,0.5,0).add(dirToPlayer.clone().multiply(i)), 2, 0.05, 0.05, 0.05, 0);
								}else {
									ItemStack item = Util.getItemStackInInventoryStartsWithName("§aAwakening Souls", player);
									ItemMeta im = item.getItemMeta();
									List<String> lore = im.getLore();
									if(lore.size() <= 3) {
										lore.add("§eSouls collected: §61");
									}else {
										int soulsCollected = (Integer.parseInt(ChatColor.stripColor(lore.get(3)).split(" ")[2]));
										String name = im.getDisplayName().split(" ")[0] + " " + im.getDisplayName().split(" ")[1];
										if(soulsCollected >= 5 && soulsCollected < 10) {
											im.setDisplayName(name + " 1");
										}else if(soulsCollected >= 10 && soulsCollected < 15) {
											im.setDisplayName(name + " 2");
										}else if (soulsCollected > 15) {
											im.setDisplayName(name + " 3");
										}
										lore.set(3, "§eSouls collected: §6" + (soulsCollected + 1));
									}
									im.setLore(lore);
									item.setItemMeta(im);
									awakeningSoulsLocList.remove(loc);
									player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 10, 0.5, 0.5, 0.5, 0);
									SoundEffects.playAwakeningSoulsRecieve(player);
									cancel();
								}
							}
						}.runTaskTimerAsynchronously(Main.getPlugin(), 0, 1);
					}
				}
			}else if (itemName.startsWith("§aAzrael's Scythe")) {
				Location loc = player.getLocation().add(0,0.1,0);
				loc.setPitch(0);
				Vector direction = loc.getDirection();
				Vector normalDirection = new Vector(-direction.getZ(), direction.getY(), direction.getX());
				PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
				Util.setValue(packet, "a", player.getEntityId());
				Util.setValue(packet, "b", (byte) 0);
				Bukkit.getOnlinePlayers().forEach(p -> {
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
				});
				new BukkitRunnable() {

					int i = 0;
					
					@Override
					public void run() {
						if(i < 10) {
							player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc.clone().add(direction.clone().multiply(i).add(normalDirection)), 10, 0.05, 0.05, 0.05, 0);
							player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc.clone().add(direction.clone().multiply(i).subtract(normalDirection)), 10, 0.05, 0.05, 0.05, 0);
							SoundEffects.playAzraelsScytheWindupSound(loc);
							i++;
						}else if(i == 10) {
							for(double x = -1; x < 0; x+=0.1) {
								player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc.clone().add(direction.clone().multiply(i).subtract(normalDirection.multiply(x))), 2, 0.05, 0.05, 0.05, 0);
							}
							SoundEffects.playAzraelsScytheWindupSound(loc);
							i++;
						}else {
							SoundEffects.playAzraelsScytheKnockUp(player.getLocation());
							for(int x = 0; x < 10; x++) {
								Location currLoc = loc.clone().add(direction.clone().multiply(x));
								player.getWorld().spawnParticle(Particle.BLOCK_CRACK, currLoc, 20, 0.7, 0.2, 0.7, 0, currLoc.clone().subtract(0, 1, 0).getBlock().getBlockData());
								for(Entity e : player.getWorld().getNearbyEntities(currLoc, 1,0.5,1)){
									CrowdControlType type = CrowdControl.controlList.getOrDefault(e.getUniqueId(), null);
									if((type == null || type != CrowdControlType.AIRBORNE) && e instanceof LivingEntity && !e.equals(player)) {
										new CrowdControl() {
											
											@Override
											public CrowdControl effect(Entity entity) {
												entity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, entity.getLocation().add(0,0.2,0), 1, 0, 0, 0, 0);
												Damage.damageEntity((LivingEntity)e, DamageType.MAGICAL, 10);
												return null;
											}
										}.control(player, e, CrowdControlType.AIRBORNE.setIntensity(1));
									}
								}
							}
							cancel();
						}
					}
					
				}.runTaskTimer(Main.getPlugin(), 0, 1);
			}else if (itemName.startsWith("§aChainlink")) {
				
			}else if (itemName.startsWith("§aPerfect Teleportation")) {
				Entity e = Bukkit.getEntity(perfectExecutionSelection.get(player.getName()));
				player.teleport(e.getLocation().subtract(e.getLocation().getDirection()));
				perfectTeleportationSelection.put(player.getName(), e.getUniqueId());
				new BukkitRunnable() {
					
					@Override
					public void run() {
						perfectTeleportationSelection.remove(player.getName());
					}
				}.runTaskLater(Main.getPlugin(), 60);
			}
		}
	}

	@EventHandler
	public void entityInterractEvent(PlayerInteractAtEntityEvent event) {
		if (event.getPlayer().getInventory().getItemInMainHand() != null && event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
			String itemName = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName();
			Player player = event.getPlayer();
			if (itemName.startsWith("§aKulgron's Scythe")) {
				SoundEffects.playKulgronsScytheInto(player.getLocation());
				Vector vec = Util.genVec(player.getLocation(), event.getRightClicked().getLocation());
				for(double i = 0; i < player.getLocation().distance(event.getRightClicked().getLocation()); i+=0.5) {
					player.getWorld().spawnParticle(Particle.SQUID_INK, player.getLocation().add(vec.clone().multiply(i)), 5, 0.1, 0.1, 0.1, 0);
				}
				player.setGameMode(GameMode.SPECTATOR);
				player.setSpectatorTarget(event.getRightClicked());
				kulgronsScytheList.add(player.getName());
			}
		}
	}
	@EventHandler
	public void playerSneakEvent(PlayerToggleSneakEvent event) {
		if(kulgronsScytheList.contains(event.getPlayer().getName())&& !event.getPlayer().isSneaking()) {
			Player player = event.getPlayer();
			Entity e = player.getSpectatorTarget();
			Damage.damageEntity((LivingEntity)e, Damage.DamageType.TRUEDAMAGE, 15);
			e.getWorld().spawnParticle(Particle.SWEEP_ATTACK, e.getLocation().add(0,e.getHeight()/2,0), 1, 0.1, 0.1, 0.1, 0);
			e.getWorld().spawnParticle(Particle.CRIT_MAGIC, e.getLocation().add(0,e.getHeight()/2,0), 100, 0.1, 0.1, 0.1, 0.5);
			player.setGameMode(GameMode.SURVIVAL);
			player.setVelocity(player.getLocation().getDirection());
			kulgronsScytheList.remove(player.getName());
			SoundEffects.playKulgronsScytheExit(player.getLocation());
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
			
			UUID currEntity = null;
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					String itemName = (player.getInventory().getItemInMainHand().hasItemMeta()) ? player.getInventory() .getItemInMainHand().getItemMeta().getDisplayName() : "";
					if (itemName.equals("§aPerfect Execution") || itemName.equals("§aPerfect Teleportation")) {
						if(perfectExecutionSelection.containsKey(player.getName()) && !perfectExecutionSelection.getOrDefault(player.getName(), null).equals(currEntity)) {
							Util.setEntityGlowing(Bukkit.getEntity(perfectExecutionSelection.get(player.getName())), player, false);
							perfectExecutionSelection.remove(player.getName());
						}
						currEntity = null;
						RayTraceResult result = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 20D, 0.4D, (e) -> {return !(e.equals(player) || e instanceof Item || e.isDead());});
						if(result != null) {
							Entity hitEntity = result.getHitEntity();
							
							Util.setEntityGlowing(hitEntity, player, true);
							
							if(!hitEntity.getUniqueId().equals(perfectExecutionSelection.get(player.getName())) && perfectExecutionSelection.get(player.getName()) != null) {
								Util.setEntityGlowing(Bukkit.getEntity(perfectExecutionSelection.get(player.getName())), player, false);
							}
							perfectExecutionSelection.put(player.getName(), hitEntity.getUniqueId());
							
							currEntity = hitEntity.getUniqueId();
						}
						
					}else if (itemName.startsWith("§aAwakening Souls")) {
						player.spawnParticle(Particle.SOUL, player.getLocation().add(0,0.5,0), 1, 0.4, 0.6, 0.4, 0.05);
						for(Location loc : awakeningSoulsLocList) {
							player.spawnParticle(Particle.SOUL, loc, 3, 0.1, 1, 0.1, 0.01);
							player.spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 2, 0.1, 0.3, 0.1, 0.01);
						}
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
										Damage.damageEntity(((LivingEntity) e), Damage.DamageType.PHYSICAL, 6);
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
										}.control(player, e, CrowdControlType.AIRBORNE.setIntensity(1.3));
									} else {
										
										Damage.damageEntity(player, Damage.DamageType.PHYSICAL, 6);
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
										}.control(player, player, CrowdControlType.AIRBORNE.setIntensity(1.3));
									}
								}
							});
						}
					}
				}
				for(String u : kulgronsScytheList) {
					Player player = Bukkit.getPlayer(u);
					Entity target = player.getSpectatorTarget();
					if(target != null) {
						target.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, target.getLocation().add(0,0.5,0), 3, 0.3, 0.5, 0.3, 0.05);
						target.getWorld().spawnParticle(Particle.REVERSE_PORTAL, target.getLocation().add(0,0.5,0), 3, 0.3, 0.5, 0.3, 0.05);
					}else {
						player.setGameMode(GameMode.SURVIVAL);
						kulgronsScytheList.remove(player.getName());
					}
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 1);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		awakeningSoulsLocList.add(event.getEntity().getLocation());
	}
	
//	public static void glowEntitesAndReturnThem(Player player) {
//		for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), 12, 12, 12)) {
//			if (e != player && !(e instanceof Item)) {
//				try {
//					if (e.getLocation().distance(player.getLocation()) < 13) {
//						Vector direction = player.getLocation().getDirection().normalize();
//						Vector directionToEntity = player.getLocation().add(0, 1, 0).subtract(e.getLocation().add(0, e.getHeight() / 2, 0)).toVector().normalize();
//						double distance = direction.distance(directionToEntity);
//
//						if (distance > 1.993) {
//							Set<UUID> list = perfectExecutionSelection.getOrDefault(player.getName(), new HashSet<UUID>());
//							list.add(e.getUniqueId());
//							perfectExecutionSelection.put(player.getName(), list);
//
//							Util.setEntityGlowing(e, player, true);
//						} else {
//							Set<UUID> list = perfectExecutionSelection.getOrDefault(player.getName(), new HashSet<UUID>());
//							list.remove(e.getUniqueId());
//							perfectExecutionSelection.put(player.getName(), list);
//
//							Util.setEntityGlowing(e, player, false);
//						}
//					} else {
//						Set<UUID> list = perfectExecutionSelection.getOrDefault(player.getName(), new HashSet<UUID>());
//						list.remove(e.getUniqueId());
//						perfectExecutionSelection.put(player.getName(), list);
//
//						Util.setEntityGlowing(e, player, false);
//					}
//				} catch (Exception exc) {
//				}
//			}
//		}
//	}

	@EventHandler
	public void onTakeDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if (proj.getCustomName().startsWith("counter_projectile_")) {
				int damage = Integer.parseInt(proj.getCustomName().split("_")[2]);
				event.setDamage(1 + damage * 3);
			}
		}else if(event.getDamager() instanceof Player) {
			Player player = (Player)event.getDamager();
			if (player.getInventory().getItemInMainHand().hasItemMeta() && player.getInventory() .getItemInMainHand().getItemMeta().getDisplayName().startsWith("§aAwakening Souls")) {
				ItemStack item = Util.getItemStackInInventoryStartsWithName("§aAwakening Souls", player);
				ItemMeta im = item.getItemMeta();
				int soulsCount = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(3)).split(" ")[2]);
				
				new CrowdControl() {
					@Override
					public CrowdControl effect(Entity entity) {
						int actualSouls = 0;
						if(soulsCount > 15) {
							actualSouls = (int)(soulsCount - 15) / 3 + 15;
						}else {
							actualSouls = soulsCount;
						}
						event.getEntity().getWorld().spawnParticle(Particle.SOUL, event.getEntity().getLocation(), 10 * actualSouls, 0.1, 0.1, 0.1, 0.5);
						event.getEntity().getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, event.getEntity().getLocation(), 4 * actualSouls, 0.1, 0.1, 0.1, 0.2);
						event.getEntity().getWorld().spawnParticle(Particle.FLASH, event.getEntity().getLocation(), (int)(0.5 * actualSouls), 0.1, 0.1, 0.1, 0.2);
						event.getEntity().getWorld().spawnParticle(Particle.CLOUD, event.getEntity().getLocation(), (int)(0.5 * actualSouls), 0.5, 0.5, 0.5, 0.03);
						
						Damage.damageEntity((LivingEntity)event.getEntity(), Damage.DamageType.PHYSICAL, actualSouls);
						return this;
					}
				}.control(player, event.getEntity(), CrowdControlType.KNOCKBACK.setIntensity(1.4));
				new CrowdControl() {
					@Override
					public CrowdControl effect(Entity entity) {
						return this;
					}
				}.control(player, player, CrowdControlType.KNOCKBACK.setIntensity(-1));
				int actualSouls = 0;
				if(soulsCount > 15) {
					actualSouls = (int)(soulsCount - 15) / 3 + 15;
				}else {
					actualSouls = soulsCount;
				}
				SoundEffects.playAwakeningSoulsHit(player,  1.2f - actualSouls / 15);
				String name = im.getDisplayName().split(" ")[0] + " " + im.getDisplayName().split(" ")[1];
				List<String> lore = im.getLore();
				lore.remove(3);
				im.setLore(lore);
				im.setDisplayName(name);
				item.setItemMeta(im);
				event.setCancelled(true);
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
					event.getDamager().setVelocity(event.getDamager().getLocation().subtract(player.getLocation()).toVector().normalize().setY(0));
					Damage.damageEntity(((LivingEntity) event.getDamager()), Damage.DamageType.MAGICAL, (int)(event.getDamage() * 1.5));
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
	public void itemSwitchEvent(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		if(player.getInventory().getItem(event.getPreviousSlot()).hasItemMeta() && player.getInventory().getItem(event.getPreviousSlot()).getItemMeta().getDisplayName().equals("§aPerfect Execution")) {
			for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), 13, 13, 13)) {
				Util.setEntityGlowing(e, player, false);
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

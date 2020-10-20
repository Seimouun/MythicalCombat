package aut.smn.mythicalcombat.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import aut.smn.mythicalcombat.main.Main;
import net.minecraft.server.v1_16_R1.DataWatcher;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import net.minecraft.server.v1_16_R1.NBTTagInt;
import net.minecraft.server.v1_16_R1.NBTTagList;
import net.minecraft.server.v1_16_R1.NBTTagString;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityMetadata;

public class Util {

	public static ItemStack createItem(Material mat, String itemName) {
		ItemStack iStack = new ItemStack(mat);
		ItemMeta iMeta = iStack.getItemMeta();
		iMeta.setDisplayName(itemName);
		iStack.setItemMeta(iMeta);
		return iStack;
	}
	public static ItemStack createItem(Material mat, String itemName, String[] lore) {
		ItemStack iStack = new ItemStack(mat);
		ItemMeta iMeta = iStack.getItemMeta();
		iMeta.setDisplayName(itemName);
		iMeta.setLore(Arrays.asList(lore));
		iStack.setItemMeta(iMeta);
		return iStack;
	}
	public static ItemStack createItem(Material mat, String itemName, String[] lore, int attackSpeed) {
		ItemStack iStack = new ItemStack(mat);
		net.minecraft.server.v1_16_R1.ItemStack item = CraftItemStack.asNMSCopy(iStack);
        NBTTagCompound nbt = (item.hasTag() ? item.getTag() : new NBTTagCompound());
        NBTTagList modifiers = new NBTTagList();
        NBTTagCompound aS = new NBTTagCompound();
        aS.set("AttributeName", NBTTagString.a("genertic.attackSpeed"));
        aS.set("Name", NBTTagString.a("genertic.attackSpeed"));
        aS.set("Amount", NBTTagInt.a(attackSpeed));
        aS.set("Operation", NBTTagInt.a(0));
        aS.set("UUIDLeast", NBTTagInt.a(894654));
        aS.set("UUIDMost", NBTTagInt.a(2872));
        modifiers.add(aS);
        nbt.set("AttributeModifiers", modifiers);
        item.setTag(nbt);
        iStack = CraftItemStack.asBukkitCopy(item);
        ItemMeta iMeta = iStack.getItemMeta();
		iMeta.setDisplayName(itemName);
		iMeta.setLore(Arrays.asList(lore));
		iStack.setItemMeta(iMeta);
		return iStack;
	}
	public static ItemStack createItem(Material mat, String itemName, String[] lore, int attackSpeed, double attackDamage) {
		ItemStack iStack = new ItemStack(mat);
		ItemMeta iMeta = iStack.getItemMeta();
		iMeta.setDisplayName(itemName);
		iMeta.setLore(Arrays.asList(lore));
		iStack.setItemMeta(iMeta);
		return iStack;
	}
	public static ItemStack createItem(Material mat, String itemName, boolean hideAttributes) {
		ItemStack iStack = new ItemStack(mat);
		ItemMeta iMeta = iStack.getItemMeta();
		iMeta.setDisplayName(itemName);
		if (hideAttributes)
			iMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		iStack.setItemMeta(iMeta);
		return iStack;
	}

	public static void createExplosion(Player player, Location loc, float size, float damage) {
		loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc, (int) (10 * size), size, size, size);
		loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		for (Entity e : loc.getWorld().getNearbyEntities(loc, size, size, size)) {
			if (!e.getName().equals(player.getName()) && e instanceof LivingEntity) {
				((LivingEntity) e).damage(damage);
			}
		}
	}

	public static boolean containsEntity(String entityName, Location loc, int radius) {
		for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
			if (e.getName().equals(entityName)) {
				return true;
			}
		}
		return false;
	}

	public static Vector getRightHeadDirection(Player player) {
		Vector direction = player.getLocation().getDirection().normalize();
		return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
	}

	public static Vector getLeftHeadDirection(Player player) {
		Vector direction = player.getLocation().getDirection().normalize();
		return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
	}
	public static Vector getRightHandDireciton(Player player) {
		Vector direction = player.getLocation().getDirection().normalize();
		return new Vector(-direction.getZ(), 1, direction.getX()).normalize().multiply(new Vector(0.5,1,0.5));
	}

	public static Vector getLeftHandDireciton(Player player) {
		Vector direction = player.getLocation().getDirection().normalize();
		return new Vector(direction.getZ(), 1, -direction.getX()).normalize();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public static void setEntityGlowing(Entity glowingEntity, Player reciever, boolean glow) {
        try {
            net.minecraft.server.v1_16_R1.Entity entityPlayer = ((CraftEntity) glowingEntity).getHandle();

            DataWatcher dataWatcher = entityPlayer.getDataWatcher();

            entityPlayer.glowing = glow; // For the update method in EntityPlayer to prevent switching back.

            // The map that stores the DataWatcherItems is private within the DataWatcher Object.
            // We need to use Reflection to access it from Apache Commons and change it.
            Map<Integer, DataWatcher.Item<?>> map = (Map<Integer, DataWatcher.Item<?>>) FieldUtils.readDeclaredField(dataWatcher, "d", true);

            // Get the 0th index for the BitMask value. http://wiki.vg/Entities#Entity
            DataWatcher.Item item = map.get(0);
            byte initialBitMask = (Byte) item.b(); // Gets the initial bitmask/byte value so we don't overwrite anything.
            byte bitMaskIndex = (byte) 0x40; // The index as specified in wiki.vg/Entities
            if (glow) {
                item.a((byte) (initialBitMask));
            } else {
                item.a((byte) (initialBitMask & ~(1 << bitMaskIndex))); // Inverts the specified bit from the index.
            }
            PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(glowingEntity.getEntityId(), dataWatcher, true);

            ((CraftPlayer) reciever).getHandle().playerConnection.sendPacket(metadataPacket);
        } catch (IllegalAccessException e) { // Catch statement necessary for FieldUtils.readDeclaredField()
            e.printStackTrace();
        } catch (Exception e) {
        }
    }
	public static void setValue(Object obj, String name, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		}catch(Exception e) {}
	}
	public static Entity getFirstEntityInDirection(Player player, int range) {
		for(int i = 0; i < 5; i++) {
			for(Entity e : player.getWorld().getNearbyEntities(player.getLocation().add(player.getLocation().getDirection().multiply(i)), 1, 1, 1)){
				if(e != player) {
					if(player.getLocation().add(player.getLocation().getDirection()).distance(e.getLocation()) < 1.6) {
						return e;
					}
				}
			}
		}
		return null;
	}
	public static Entity getFirstEntityInDirectionOutside(Player player, int range) {
		for(int i = 0; i < 5; i++) {
			for(Entity e : player.getWorld().getNearbyEntities(player.getLocation().add(player.getLocation().getDirection().multiply(i)), 1.5, 1.5, 1.5)){
				if(e != player) {
					if(player.getLocation().add(player.getLocation().getDirection()).distance(e.getLocation()) > 1.6) {
						return e;
					}
				}
			}
		}
		return null;
	}
	public static Vector genVec(Location a, Location b) {
        double dX = a.getX() - b.getX();
        double dY = a.getY() - b.getY();
        double dZ = a.getZ() - b.getZ();
        double yaw = Math.atan2(dZ, dX);
        double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        Vector vector = new Vector(x, z, y);
        //If you want to: vector = vector.normalize();

        return vector;
    }
	public static ItemStack getItemStackInInventoryWithName(String name, Player player) {
		for(ItemStack i : player.getInventory().getContents()) {
			if(i.hasItemMeta() && i.getItemMeta().getDisplayName().equals(name)) {
				return i;
			}
		}
		return null;
	}
	public static ItemStack getItemStackInInventoryStartsWithName(String name, Player player) {
		for(ItemStack i : player.getInventory().getContents()) {
			if(i.hasItemMeta() && i.getItemMeta().getDisplayName().startsWith(name)) {
				return i;
			}
		}
		return null;
	}
	@SuppressWarnings("deprecation")
	public static void setItemCooldown(ItemStack iStack, int ticks) {
		new BukkitRunnable() {
			int tick = 0;
			@Override
			public void run() {
				short maxDur = iStack.getType().getMaxDurability();
				if(tick <= ticks) {
					iStack.setDurability((short)(maxDur - maxDur * tick / ticks - 1));
					tick++;
				}else {
					cancel();
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 1);
	}
}

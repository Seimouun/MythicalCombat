package aut.smn.mythicalcombat.effects;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import aut.smn.mythicalcombat.main.Main;
import aut.smn.mythicalcombat.util.Util;

public interface CrowdControl {
	
	public static HashMap<UUID, CrowdControlType> controlList = new HashMap<UUID, CrowdControlType>();
	
	public default void control(Player player, Entity entity, CrowdControlType type) {
		if(type.equals(CrowdControlType.AIRBORNE)) {
			entity.setVelocity(new Vector(0, type.getIntensity(), 0));
		}else if(type.equals(CrowdControlType.KNOCKBACK)) {
			entity.setVelocity(Util.genVec(player.getLocation(), entity.getLocation()).multiply(type.getIntensity()).setY(0.4));
		}
		effect(entity);
		controlList.put(entity.getUniqueId(), type);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				controlList.remove(entity.getUniqueId());
			}
		}.runTaskLater(Main.getPlugin(), (int)((type.getIntensity() != 0) ? type.getIntensity() * 10 : type.getDuration() * 10));
	}
	public default List<UUID> getAllEntitiesOfType(CrowdControlType type){
		return controlList.entrySet().stream().filter(ent -> ent.getValue().equals(type)).map(ent -> ent.getKey()).collect(Collectors.toList());
	}
	public default CrowdControlType getControlTypeOf(Entity entity) {
		return controlList.getOrDefault(entity.getUniqueId(), null);
	}
	public CrowdControl effect(Entity entity);
}

package aut.smn.mythicalcombat.effects;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface CrowdControl {
	
	public static HashMap<UUID, CrowdControlType> controlList = new HashMap<UUID, CrowdControlType>();
	
	public default void control(Entity entity, CrowdControlType type) {
		if(type.equals(CrowdControlType.AIRBORNE)) {
			entity.setVelocity(new Vector(0, type.setIntensity(1.5).getIntensity(), 0));
		}
		effect(entity);
		controlList.put(entity.getUniqueId(), type);
	}
	public CrowdControl effect(Entity entity);
}

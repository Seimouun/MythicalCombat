package aut.smn.mythicalcombat.util;

import org.bukkit.entity.LivingEntity;

public class Damage {

	public static void damageEntity(LivingEntity e, DamageType type, int damage) {
		switch (type) {
		case TRUEDAMAGE:
			e.damage(damage);
			break;
		case MAGICAL:
			e.damage(damage);
			break;
		case PHYSICAL:
			e.damage(damage);
			break;
		}
	}
	public static double getDamageReduction(DamageType type) {
		return 0;
	}
	
	public enum DamageType {
		PHYSICAL, MAGICAL, TRUEDAMAGE;
	}
	
}

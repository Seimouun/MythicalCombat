package aut.smn.mythicalcombat.effects;

public enum CrowdControlType {

	AIRBORNE, ROOT, SLOW, STUN, KNOCKBACK;
	
	private int duration;
	private double intensity;
	
	public CrowdControlType setDuration(int duration) {
		this.duration = duration;
		return this;
	}
	public CrowdControlType setIntensity(double d) {
		this.intensity = d;
		return this;
	}
	
	public int getDuration() {
		return duration;
	}
	public double getIntensity() {
		return intensity;
	}
	
}

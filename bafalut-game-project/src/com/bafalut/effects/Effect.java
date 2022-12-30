package com.bafalut.effects;

class Effect {
	private int effectXPosition = 0;
	private int effectYPosition = 0;
	private final int effectWidth = 48;
	private final int effectHeight = 48;
	private final int scale = 2;
	
	// setters
	public final void setEffectXPosition(int x) {
		this.effectXPosition = x;
	}
	public final void setEffectYPosition(int y) {
		this.effectYPosition = y;
	}
	
	// getters
	public final int getEffectXPosition() {
		return this.effectXPosition;
	}
	public final int getEffectYPosition() {
		return this.effectYPosition;
	}
	public final int getEffectWidth() {
		return this.effectWidth;
	}
	public final int getEffectHeight() {
		return this.effectHeight;
	}
	public final int scale() {
		return this.scale;
	}
}

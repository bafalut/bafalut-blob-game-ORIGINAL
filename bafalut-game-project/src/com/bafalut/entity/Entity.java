package com.bafalut.entity;

import java.awt.Rectangle;

class Entity {
	private int maxHealth;
	private int health;
	private int speed;
	private int xPosition, yPosition = 0;
	private boolean alive = true;
	private final int xSize = 48;
	private final int ySize = 48;
	
	
	private Rectangle hitbox = new Rectangle();
	
	final void setHitbox() {
		hitbox.x = getXPosition();
		hitbox.y = getYPosition();
		
		hitbox.width = getXSize();
		hitbox.height = getYSize();
	}
	
	// Setters
	protected final void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	
	public final void setHealth(int health) {
		this.health = health;
	}
	
	public final void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public final void setXPosition(int xPosition) {
		this.xPosition = xPosition;
	}
	
	public final void setYPosition(int yPosition) {
		this.yPosition = yPosition;
	}
	
	public final void setXHitboxPosition(int x) {
		this.hitbox.x = x;
	}
	
	public final void setYHitboxPosition(int y) {
		this.hitbox.y = y;
	}
	
	public final void setHitboxWidth(int x) {
		this.hitbox.width = x;
	}
	
	public final void setHitboxHeight(int y) {
		this.hitbox.height = y;
	}
	
	final void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	// Getters
	public final int getMaxHealth() {
		return this.maxHealth;
	}
	
	public final int getHealth() {
		return this.health;
	}
	
	public final int getSpeed() {
		return this.speed;
	}
	
	public final int getXPosition() {
		return this.xPosition;
	}
	
	public final int getYPosition() {
		return this.yPosition;
	}
	
	public final int getXSize() {
		return this.xSize;
	}
	
	public final int getYSize() {
		return this.ySize;
	}
	
	public final int getXHitboxPosition() {
		return this.hitbox.x;
	}
	
	public final int getYHitboxPosition() {
		return this.hitbox.y;
	}
	
	public final int getHitboxWidth() {
		return this.hitbox.width;
	}
	
	public final int getHitboxHeight() {
		return this.hitbox.height;
	}
	
	public final Rectangle getHitbox() {
		return this.hitbox;
	}
	
	public final boolean getAlive() {
		return this.alive;
	}
}

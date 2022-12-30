package com.bafalut.item;

import java.awt.Rectangle;

class Item {
	private Rectangle hitbox = new Rectangle();
	
	private int itemXPosition = 0;
	private int itemYPosition = 0;
	private final int itemXSize = 48;
	private final int itemYSize = 48;
	private int scale = 3;
	
	// setters
	public final void setItemXPosition(int x) {
		this.itemXPosition = x;
	}
	
	public final void setItemYPosition(int y) {
		this.itemYPosition = y;
	}
	
	public final void setScale(int i) {
		this.scale = i;
	}
	
	public final void setItemYHitboxPosition(int y) {
		this.hitbox.y = y;
	}
	
	public final void setItemHitboxWidth(int width) {
		this.hitbox.width = width;
	}
	
	public final void setItemHitboxHeight(int height) {
		this.hitbox.height = height;
	}
	
	
	// getters
	
	public final int getItemXPosition() {
		return this.itemXPosition;
	}
	
	public final int getItemYPosition() {
		return this.itemYPosition;
	}
	
	public final int getItemWidth() {
		return this.itemXSize;
	}
	
	public final int getItemHeight() {
		return this.itemYSize;
	}
	
	public final int getScale() {
		return this.scale;
	}
	
	public final Rectangle getHitbox() {
		return this.hitbox;
	}
}

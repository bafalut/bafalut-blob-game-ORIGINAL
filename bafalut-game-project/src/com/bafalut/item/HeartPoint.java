package com.bafalut.item;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.bafalut.entity.Player;
import com.bafalut.main.GamePanel;

public class HeartPoint extends Item {
	private Player player;
	private Random rng = new Random();
	private HeartSounds heartSounds = new HeartSounds();
	private static BufferedImage image;
	
	private boolean isPickedUp = false;
	private boolean canBeRemoved = false;
	
	private final int minimumYRange = 225;
	private final int maximumYRange = 325;
	
	private final int minimumXRange = 0;
	private final int maximumXRange = 512 - (this.getItemWidth() * this.getScale());
	
	private final int heartPoint = 2;
	
	public HeartPoint(Player player) {
		this.player = player;
		this.setScale(1);
		this.setItemYPosition(rng.nextInt(maximumYRange - minimumYRange) + minimumYRange);
		this.setItemXPosition(rng.nextInt(maximumXRange - minimumXRange) + minimumXRange);
		
		this.getImage();
	}
	
	private final void getImage() {
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/items/Item-HeartPoint/Heart-Point.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private double delta = 0;
	public final void heartUpdate() {
		// sets the coin hitbox
		if(this.isPickedUp == false) {
			this.getHitbox().x = this.getItemXPosition();
			this.getHitbox().y = this.getItemYPosition();
			this.getHitbox().width = this.getItemWidth();
			this.getHitbox().height = this.getItemHeight();
		}
		
		// checks for collisions
		if(this.isPickedUp == false) {
			if(this.getHitbox().intersects(player.getHitbox())) {
				this.isPickedUp = true;
				heartSounds.startSound();
				player.setHealth(player.getHealth() + heartPoint);
			}
		}
		
		if(this.isPickedUp == true) {
			this.getHitbox().x = -1;
			this.getHitbox().y = -1;
			this.getHitbox().width = 0;
			this.getHitbox().height = 0;
			
			if(GamePanel.getDeltaTime() >= 1) {
				this.delta += 1;
			}
			
			if(this.delta >= 30) {
				this.canBeRemoved = true;
			}
		}
	}
	
	public final void draw(Graphics2D g2) {
		if(this.isPickedUp == false) {
			g2.drawImage(image, this.getItemXPosition(), this.getItemYPosition(), this.getItemWidth() * this.getScale(), this.getItemHeight() * this.getScale(), null);
		}
	}
	
	// getters
	public final boolean isPickedUp() {
		return this.isPickedUp;
	}
	
	public final boolean getCanBeRemoved() {
		return this.canBeRemoved;
	}
	
	public static final BufferedImage getCoinImage() {
		return image;
	}
	
	private final class HeartSounds {
		Clip clip;
		
		HeartSounds() {
			try {
				AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResource("/com/bafalut/sounds/Item-Sounds/Heart-Point-PickUp.wav"));
				clip = AudioSystem.getClip();
				clip.open(ais);
				
			} catch(Exception e) {
				System.err.println("Something went wrong!");
			}
		}
		
		final void startSound() {
			clip.start();
		}
	}
}

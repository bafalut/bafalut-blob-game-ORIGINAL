package com.bafalut.effects;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.bafalut.main.GamePanel;

public final class ExplosionEffect extends Effect {
	private BufferedImage explosion1, explosion2, explosion3, explosion4, explosion5, explosion6, explosion7, explosion8, explosion9, explosion10;  
	private BufferedImage[] frames = new BufferedImage[10];
	
	private double deltaTime = 0;
	private boolean running = false;
	private int currentFrame = 0;
	
	private ExplosionSound sound = new ExplosionSound();
	private Rectangle hitbox;
	
	private final int animationSpeed = 5; // In frames (60 frames = 1 second)
	
	public ExplosionEffect(Rectangle hitbox) {
		this.hitbox = hitbox;
		
		getImages();
		running = true;
		
		frames[0] = explosion1;
		frames[1] = explosion2;
		frames[2] = explosion3;
		frames[3] = explosion4;
		frames[4] = explosion5;
		frames[5] = explosion6;
		frames[6] = explosion7;
		frames[7] = explosion8;
		frames[8] = explosion9;
		frames[9] = explosion10;
		
	}
	
	public final void effectUpdate() {
		
		if(running == true) {
			boolean finished = false;
			
			if(finished == false) {
				this.sound.startSound();
				finished = true;
			}
			
			if(currentFrame > frames.length) {
				running = false;
			}
			
			if(GamePanel.getDeltaTime() >= 1) {
				deltaTime += 1;
			}
			
			if(deltaTime >= this.animationSpeed) {
				this.currentFrame += 1;
				deltaTime = 0;
			}
		}
	}
	
	public final void draw(Graphics2D g2) {
		if(running == true && currentFrame < frames.length) {
			this.setEffectXPosition((int) hitbox.getX());
			this.setEffectYPosition((int) hitbox.getY());
			
			//g2.setColor(Color.RED);
			//g2.drawRect(this.getEffectXPosition(), this.getEffectYPosition(), this.getEffectWidth() * this.scale(), this.getEffectHeight() * this.scale());
			
			g2.drawImage(frames[currentFrame], this.getEffectXPosition(), this.getEffectYPosition(), this.getEffectWidth() * this.scale(), this.getEffectHeight() * this.scale(), null);
		}
	}
	
	private final void getImages() {
		try {
			this.explosion1 = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/effects/Explosion-Effect/Effect-Explosion-1.png"));
			this.explosion2 = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/effects/Explosion-Effect/Effect-Explosion-2.png"));
			this.explosion3 = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/effects/Explosion-Effect/Effect-Explosion-3.png"));
			this.explosion4 = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/effects/Explosion-Effect/Effect-Explosion-4.png"));
			this.explosion5 = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/effects/Explosion-Effect/Effect-Explosion-5.png"));
			this.explosion6 = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/effects/Explosion-Effect/Effect-Explosion-6.png"));
			this.explosion7 = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/effects/Explosion-Effect/Effect-Explosion-7.png"));
			this.explosion8 = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/effects/Explosion-Effect/Effect-Explosion-8.png"));
			this.explosion9 = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/effects/Explosion-Effect/Effect-Explosion-9.png"));
			this.explosion10 = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/effects/Explosion-Effect/Effect-Explosion-10.png"));
			
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private final class ExplosionSound {
		Clip clip;
		
		ExplosionSound() {
			try {
				AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResource("/com/bafalut/sounds/Explosion-Sound-Effect.wav"));
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

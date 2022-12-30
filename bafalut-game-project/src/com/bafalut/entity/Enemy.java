package com.bafalut.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.bafalut.effects.ExplosionEffect;
import com.bafalut.main.GamePanel;

public final class Enemy extends Entity {
	private Player player;
	private static BufferedImage character;
	private ExplosionEffect explosionEffect = new ExplosionEffect(this.getHitbox());
	private Random rng = new Random();
	
	private boolean canDamage = true;
	private boolean fromLeft;
	private boolean canBeRemoved = false;
	
	private int minimumSpeed = 3;
	private int maximumSpeed = 7;
	
	private final int yPositionMinimumRange = 100;
	private final int yPositionMaximumRange = 300;
	
	private static final int DAMAGE = 1;
	
	private final int scale = 3;
	
	public Enemy(Player player) {
		this.setMaxHealth(1);
		this.setHealth(this.getMaxHealth());

		this.fromLeft = rng.nextBoolean();
		if(fromLeft == true) {
			this.setXPosition(0 - (this.getXSize() * this.scale));
		} else if(fromLeft == false) {
			this.setXPosition(512);
		}
		this.setYPosition(this.rng.nextInt(this.yPositionMaximumRange - this.yPositionMinimumRange) + this.yPositionMinimumRange);
		int tempSpeed = this.getYPosition() / 32;
		if(tempSpeed > this.maximumSpeed) {
			tempSpeed = maximumSpeed;
		} else if(tempSpeed < this.minimumSpeed) {
			tempSpeed = minimumSpeed;
		}
		
		this.setSpeed(tempSpeed);
		//System.out.println(getYPosition() + " " + this.fromLeft);
		
		this.player = player;
		getImage();
	}
	
	private final void getImage() {
		try {
			character = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/enemycharacter/Enemy-Character.png"));
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private double delta = 0;
	public final void enemyUpdate() {
		if(this.fromLeft == true) {
			if(this.getXPosition() > 512) {
				this.canBeRemoved = true;
			} else {
				this.canBeRemoved = false;
			}
		} else if(this.fromLeft == false) {
			if(this.getXPosition() < 0 - (this.getXSize() * this.scale)) {
				this.canBeRemoved = true;
			} else {
				this.canBeRemoved = false;
			}
		}
		
		if(this.getAlive() == true) {
			// sets up the character hit box
			this.setXHitboxPosition(this.getXPosition() + (8 * this.scale));
			this.setYHitboxPosition(this.getYPosition() + (18 * this.scale));
			this.setHitboxWidth(33 * this.scale);
			this.setHitboxHeight(11 * this.scale);
			
			// checks health
			if(this.getHealth() <= 0) {
				this.setAlive(false);
			}
			
			// moves the enemy
			if(this.fromLeft == true) {
				this.setXPosition(this.getXPosition() + this.getSpeed());
			} else if(this.fromLeft == false) {
				this.setXPosition(this.getXPosition() - this.getSpeed());
			}
			
			// checks for collisions
			
			// checks if enemy touches the player beam
			if(this.getHitbox().intersects(player.getPlayerBeamHitbox())) {
				if(this.canDamage == true) {
					this.setHealth(this.getHealth() - Enemy.DAMAGE);
					GamePanel.setKillScore(GamePanel.getKillScore() + 1);
					//System.out.println(this.getHealth());
					this.canDamage = false;
				}
			}
			
			// checks if enemy touches player
			if(this.getHitbox().intersects(player.getHitbox())) {
				if(player.getPlayerCanBeDamaged() == true) {
					player.setHealth(player.getHealth() - Enemy.DAMAGE);
					
					player.setPlayerCanBeDamaged(false);
					
				}
			}
			
			
		} else if(this.getAlive() == false) {
			explosionEffect.effectUpdate();
			
			if(GamePanel.getDeltaTime() >= 1) {
				this.delta += 1;	
			}
			
			if(this.delta >= 60) {
				this.canBeRemoved = true;
				delta = 0;
			}
			
		}
	}
	
	public final void draw(Graphics2D g2) {
		if(this.getAlive() == true) {
			g2.drawImage(character, this.getXPosition(), this.getYPosition(), this.getXSize() * this.scale, this.getYSize() * this.scale, null);
			
			//g2.setColor(Color.RED);
			//g2.drawRect(this.getXHitboxPosition(), this.getYHitboxPosition(), this.getHitboxWidth(), this.getHitboxHeight());
			
		} else if(this.getAlive() == false) {
			explosionEffect.draw(g2);
			
		} else if(this.canBeRemoved == true) {
			
		}
	}
	
	
	// getters
	public static final BufferedImage getEnemyImage() {
		return character;
	}
	
	public final boolean getCanBeRemoved() {
		return this.canBeRemoved;
	}
}

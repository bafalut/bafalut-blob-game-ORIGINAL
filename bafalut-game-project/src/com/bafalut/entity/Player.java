package com.bafalut.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.bafalut.main.GamePanel;

public final class Player extends Entity implements KeyListener {
	private BufferedImage playerModel, playerModelRight, playerModelLeft , playerBeam, playerBeamEnd;
	private Rectangle playerBeamHitbox = new Rectangle();
	private PlayerSounds playerSounds = new PlayerSounds();
	
	private boolean canMoveUp, canMoveDown, canMoveRight, canMoveLeft = true;
	private boolean pressingUp, pressingDown, pressingRight, pressingLeft = false;
	private boolean pressingQ = false;
	private boolean canBeDamaged = true;
	private boolean canShoot = true;
	private boolean shooting = false;
	
	// In frames (60 frames = 1 second) (1 frame = 0.16)
	private final int cooldown = 60;
	private final int shootingDuration = 15;
	private final int canBeDamagedCooldown = 60;
	private final int scale = 3;
	
	private int oldHealth;
	public Player() {
		this.setXPosition(256 - (this.getXSize() * 2));
		this.setYPosition(50);
		this.setSpeed(4);
		
		this.setMaxHealth(6);
		this.setHealth(this.getMaxHealth());
		oldHealth = this.getHealth();
		
		this.getPlayerImage();
		
		this.canMoveRight = true;
	}
	
	//private final void shoot() {
		
	//}
	
	private final void getPlayerImage() {
		try {
			this.playerModel = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/playermodel/Player-Character.png"));
			this.playerModelRight = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/playermodel/Player-Character-Right.png"));
			this.playerModelLeft = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/playermodel/Player-Character-Left.png"));
			this.playerBeam = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/playermodel/Player-Character-Beam.png"));
			this.playerBeamEnd = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/playermodel/Player-Character-Beam-End.png"));
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private double delta = 0;
	private double delta2 = 0;
	private double temp = 0;
	public final void playerUpdate() {
		// checks for input if the player presses the up,down,right,left arrow keys
		if(pressingUp == true) {
			if(canMoveUp == true) {
				this.setYPosition(this.getYPosition() - this.getSpeed());
			}
		}
		if(pressingDown == true) {
			if(canMoveDown == true) {
				this.setYPosition(this.getYPosition() + this.getSpeed());
			}
		}
		if(pressingRight == true) {
			if(canMoveRight == true) {
				this.setXPosition(this.getXPosition() + this.getSpeed());
			}
		}
		if(pressingLeft == true) {
			if(canMoveLeft == true) {
				this.setXPosition(this.getXPosition() - this.getSpeed());
			}
		}
		if(pressingQ == true) {
			if(this.canShoot == true) {
				this.canShoot = false;
				this.shooting = true;
				playerSounds.playSound(PlayerSounds.PLAYER_SHOOT);
			}
		}
		
		// checks if health is over max health
		if(this.getHealth() > this.getMaxHealth()) {
			this.setHealth(this.getMaxHealth());
		}
		
		// handles the player can be damaged boolean
		if(this.canBeDamaged == false) {
			if(GamePanel.getDeltaTime() >= 1) {
				temp += 1;
			}
			
			if(temp >= this.canBeDamagedCooldown) {
				this.canBeDamaged = true;
				temp = 0;
			}
		}
		
		// checks if player is damaged
		if(this.oldHealth > this.getHealth()) {
			playerSounds.playSound(PlayerSounds.PLAYER_HURT);
			
			this.oldHealth = this.getHealth();
		}
		
		// Sets up the player hit box
		this.setXHitboxPosition(this.getXPosition() + (4 * this.scale));
		this.setYHitboxPosition(this.getYPosition() + (10 * this.scale));
		this.setHitboxWidth(40 * this.scale);
		this.setHitboxHeight(21 * this.scale);
		//System.out.println("X: " + this.getXHitboxPosition() + ", Y: " + this.getYHitboxPosition() + ", Width: " + this.getHitboxWidthPosition() + ", Height: " + this.getHitboxHeightPosition());
		
		// Checks if the hit box of the player reaches the top or bottom of the screen
		if(this.getYHitboxPosition() >= 384 - this.getHitboxHeight()) {
			// TODO: If the player is at the bottom
			this.canMoveDown = false;
			
		} else if(this.getYHitboxPosition() < 384 - this.getHitboxHeight()) {
			this.canMoveDown = true;
			this.setYPosition(this.getYPosition() + 1);
		} 
		
		if(this.getYHitboxPosition() <= 0) {
			// TODO: If the player is at the top
			this.canMoveUp = false;
			
		} else if(this.getYHitboxPosition() > 0) {
			this.canMoveUp = true;
			
		}
		
		if(this.getXHitboxPosition() <= 0) {
			// TODO: If the player is at the left
			this.canMoveLeft = false;
			
		} else if(this.getXHitboxPosition() > 0) {
			this.canMoveLeft = true;
			
		}
		
		if(this.getXHitboxPosition() >= 512 - this.getHitboxWidth()) {
			// TODO: If the player is at the right
			this.canMoveRight = false;
			
		} else if(this.getXHitboxPosition() < 512 - this.getHitboxWidth()) {
			this.canMoveRight = true;
			
		}
		
		if(this.canShoot == false) {
			if(GamePanel.getDeltaTime() >= 1) {
				this.delta += 1;
				this.delta2 += 1;
			}
			
			if(this.delta >= this.shootingDuration) {
				this.shooting = false;
				this.delta = 0;
			}
			
			if(this.delta2 >= this.cooldown) {
				this.canShoot = true;
				this.delta2 = 0;
			}
		}
	}
	
	public final void draw(Graphics2D g2) {
		if(shooting == true) {
			//System.out.println("X: " + this.getXPosition() + ", Y: " + this.getYPosition());
			this.playerBeamHitbox.x = this.getXPosition() + 48;
			this.playerBeamHitbox.y = this.getYPosition() + 90;
			this.playerBeamHitbox.width = this.playerBeam.getWidth();
			
			int playerBeamHeight = 480;
			int temp = 480 - this.getYPosition();
			playerBeamHeight = temp;
			
			playerBeamHeight -= 64;
			//System.out.println(fuel);
			
			g2.drawImage(playerBeam, this.getXPosition(), this.getYPosition() + 90, this.playerBeam.getWidth() * this.scale, playerBeamHeight, null);
			g2.drawImage(playerBeamEnd, this.getXPosition(), 483, this.playerBeamEnd.getWidth() * this.scale, this.playerBeamEnd.getHeight() * this.scale, null);
			this.playerBeamHitbox.height = playerBeamHeight;

		} else if(shooting == false) {
			this.playerBeamHitbox.x = this.getXPosition() + 48;
			this.playerBeamHitbox.y = this.getYPosition() + 90;
			this.playerBeamHitbox.width = this.playerBeam.getWidth();
			this.playerBeamHitbox.height = 0;
		}/*else if(pressingQ == false) {
			this.playerBeamHitbox.x = this.getXPosition() + 48;
			this.playerBeamHitbox.y = this.getYPosition() + 90;
			this.playerBeamHitbox.height = 0;
			
			g2.setColor(this.fuelBarColor);
			g2.fillRect(this.getXHitboxPosition(), this.getYHitboxPosition() - 20, 0 + (this.fuel), 15);
		}*/
		
		if(pressingRight == true) {
			g2.drawImage(this.playerModelRight, this.getXPosition(), this.getYPosition(), this.getXSize() * this.scale, this.getYSize() * this.scale, null);
		} else if(pressingLeft == true) {
			g2.drawImage(this.playerModelLeft, this.getXPosition(), this.getYPosition(), this.getXSize() * this.scale, this.getYSize() * this.scale, null);
		} else {
			g2.drawImage(this.playerModel, this.getXPosition(), this.getYPosition(), this.getXSize() * this.scale, this.getYSize() * this.scale, null);
		}
		g2.setColor(new Color(255, 0, 0));
		g2.fillRect(this.getXHitboxPosition(), this.getYHitboxPosition() - 20, this.getHitboxWidth(), 15);
		
		if(this.canBeDamaged == true) {
			g2.setColor(new Color(114, 225, 111));
			g2.fillRect(this.getXHitboxPosition(), this.getYHitboxPosition() - 20, 0 + (this.getHealth() * 21), 15);
		} else if(this.canBeDamaged == false) {
			g2.setColor(new Color(114 - 50, 225 - 50, 111 - 50));
			g2.fillRect(this.getXHitboxPosition(), this.getYHitboxPosition() - 20, 0 + (this.getHealth() * 21), 15);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// NOTHING
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			this.pressingUp = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			this.pressingDown = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.pressingRight = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			this.pressingLeft = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_Q) {
			this.pressingQ = true;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			this.pressingUp = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			this.pressingDown = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.pressingRight = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			this.pressingLeft = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_Q) {
			this.pressingQ = false;
		}
	}
	
	// setters
	
	public final void setPlayerCanBeDamaged(boolean canBeDamaged) {
		this.canBeDamaged = canBeDamaged;
	}
	
	// getters
	
	public final Rectangle getPlayerBeamHitbox() {
		return this.playerBeamHitbox;
	}
	
	public final boolean getPlayerCanBeDamaged() {
		return this.canBeDamaged;
	}
	
	// sound class
	private final class PlayerSounds {
		private URL[] soundURL = new URL[10];
		private Clip clip;
		
		static final int PLAYER_HURT = 0;
		static final int PLAYER_SHOOT = 1;
		
		PlayerSounds() {
			soundURL[0] = getClass().getResource("/com/bafalut/sounds/Player-Sounds/Player-Hurt.wav");
			soundURL[1] = getClass().getResource("/com/bafalut/sounds/Player-Sounds/Player-Shoot.wav");
		}
		
		final void playSound(int i) {
			try {
				AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
				clip = AudioSystem.getClip();
				clip.open(ais);
				clip.start();
				
			} catch(Exception e) {
				System.err.println("Something went wrong in PlayerSounds");
			}
		}
		
	}
}

package com.bafalut.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;

import com.bafalut.entity.Enemy;
import com.bafalut.entity.Player;
import com.bafalut.item.Coin;
import com.bafalut.item.HeartPoint;

@SuppressWarnings("serial")
public final class GamePanel extends JPanel implements Runnable {
	private Thread gameThread;
	
	private final File statsFolder = new File("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\AlienBlobGameStats");
	@SuppressWarnings("unused")
	private Formatter killScoreTextFile;
	@SuppressWarnings("unused")
	private Formatter coinScoreTextFile;
	private File killScoreFile = new File("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\AlienBlobGameStats\\KillScore.txt");
	private File coinScoreFile = new File("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\AlienBlobGameStats\\CoinScore.txt");
	
	private static Player mainPlayer = new Player();
	private KeyHandler keyHandler = new KeyHandler();
	private Sounds sounds = new Sounds();
	
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private ArrayList<Coin> coins = new ArrayList<Coin>();
	private ArrayList<HeartPoint> heartPoints = new ArrayList<HeartPoint>();
	
	private static String playerSelected = "Play";
	private static boolean onCredits = false, onStats = false, onHelp = false;
	private static int coinScore = 0;
	private static int killScore = 0;
	
	private final int coinLimit = 4;
	private final int heartLimit = 1;
	
	private final int coinSpawnCooldown = 60 * 3; // in frames
	private final int heartSpawnCooldown = 60 * 30; // in frames
	
	private static double deltaTime;
	
	private static final int FPS = 60;
	private static boolean running = false;
	
	protected GamePanel() {
		this.setPreferredSize(new Dimension(512, 512));
		this.setBackground(Color.BLACK);
		this.setDoubleBuffered(true);
		this.addKeyListener(GamePanel.mainPlayer);
		this.addKeyListener(keyHandler);
		this.setFocusable(true);
		
		this.setLayout(null);
		
		
		if(!statsFolder.exists()) {
			if(statsFolder.mkdir()) {
				System.out.println("Made a stats folder.");
			}
		} else {
			System.out.println("There is already a stats folder.");
		}
		
		try {
			if(statsFolder.exists()) {
				if(!this.killScoreFile.exists()) {
					killScoreTextFile = new Formatter("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\AlienBlobGameStats\\KillScore.txt");
					killScoreFile = new File("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\AlienBlobGameStats\\KillScore.txt");
					System.out.println("Successfully created a kill score file...");
				} else if(this.killScoreFile.exists()) {
					System.out.println("There is already a kill score file...");
				}
				
				if(!this.coinScoreFile.exists()) {
					coinScoreTextFile = new Formatter("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\AlienBlobGameStats\\CoinScore.txt");
					coinScoreFile = new File("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\AlienBlobGameStats\\CoinScore.txt");
					System.out.println("Successfully created a coin score file...");
				} else if(this.killScoreFile.exists()) {
					System.out.println("There is already a coin score file...");
				}
			}
		} catch(Exception e) {
			System.err.println("You got an error!");
		}
		
		int oldKillScore = 0;
		int oldCoinScore = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(killScoreFile));
			try {
				System.out.println("Successfully loaded kill score data...");
				oldKillScore = Integer.parseInt(reader.readLine());
				
			} catch(NumberFormatException e) {
				System.out.println("Creating new kill score data...");
				oldKillScore = 0;
			}
			
			reader.close();
			reader = new BufferedReader(new FileReader(coinScoreFile));
			try {
				System.out.println("Successfully loaded coin score data...");
				oldCoinScore = Integer.parseInt(reader.readLine());
				
			} catch(NumberFormatException e) {
				System.out.println("Creating new coin score data...");
				oldCoinScore = 0;
			}
			
			reader.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(killScoreFile));
			writer.append(String.valueOf(oldKillScore));
			System.out.println("Successfully loaded kill score data...");
			
			writer.close();
			
			writer = new BufferedWriter(new FileWriter(coinScoreFile));
			writer.append(String.valueOf(oldCoinScore));
			System.out.println("Successfully loaded coin score data...");
			
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	final void startThread() {
		System.err.println("Creating new thread...");
		
		this.gameThread = new Thread(this);
		this.gameThread.start();
	}
	
	private int temp = 0;
	private int temp1 = 0;
	private int temp2 = 0;
	private boolean gameOver = false;
	private final void update() {
		if(running == true) {
			GamePanel.mainPlayer.playerUpdate();
			
			if(GamePanel.mainPlayer.getHealth() <= 0) {
				try {
					BufferedReader reader = new BufferedReader(new FileReader(killScoreFile));
					
					int oldKillScore = Integer.parseInt(reader.readLine());
					reader.close();
					
					reader = new BufferedReader(new FileReader(coinScoreFile));
					int oldCoinScore = Integer.parseInt(reader.readLine());
					
					reader.close();
					BufferedWriter writer = new BufferedWriter(new FileWriter(killScoreFile));
					writer.write(String.valueOf(oldKillScore + GamePanel.killScore));
					writer.close();
					
					writer = new BufferedWriter(new FileWriter(coinScoreFile));
					writer.write(String.valueOf(oldCoinScore + GamePanel.coinScore));
					writer.close();
					GamePanel.killScore = 0;
					GamePanel.coinScore = 0;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				temp = 0;
				temp1 = 0;
				temp2 = 0;
				this.gameOver = true;
				this.enemies.clear();
				this.coins.clear();
				this.heartPoints.clear();
				GamePanel.mainPlayer.setXPosition(256 - (GamePanel.mainPlayer.getXSize() * 2));
				GamePanel.mainPlayer.setYPosition(50);
				GamePanel.running = false;
			}
			
			if(GamePanel.getDeltaTime() >= 1) {
				temp += 1;
				if(this.heartPoints.size() < this.heartLimit) {
					temp2 += 1;
				}
				
				if(this.coins.size() < this.coinLimit) {
					temp1 += 1;
				}
			}
			
			// checks if coins can be removed
			for(int i = 0; i < enemies.size(); i++) {
				if(enemies.get(i).getCanBeRemoved() == true) {
					enemies.remove(i);
					//System.out.println("removed an enemy");
				}
			}
			
			for(int i = 0; i < coins.size(); i++) {
				if(coins.get(i).getCanBeRemoved() == true) {
					coins.remove(i);
				}
			}
			
			for(int i = 0; i < heartPoints.size(); i++) {
				if(heartPoints.get(i).getCanBeRemoved() == true) {
					heartPoints.remove(i);
				}
			}
			// generates coins and enemies
			if(temp >= 60) {
				enemies.add(new Enemy(GamePanel.mainPlayer));
				
				temp = 0;
			}
			
			// generates coins
			if(temp1 >= coinSpawnCooldown) {
				if(this.coinLimit > this.coins.size()) {
					coins.add(new Coin(GamePanel.mainPlayer));
				}
				temp1 = 0;
			}
			
			// generates a new heart point every time the heart cool down is reached
			if(temp2 >= heartSpawnCooldown) {
				if(this.heartLimit > this.heartPoints.size()) {
					heartPoints.add(new HeartPoint(GamePanel.mainPlayer));
				}
				temp2 = 0;
			}
			
			// updates coins and enemies
			for(int i = 0; i < this.enemies.size(); i++) {
				enemies.get(i).enemyUpdate();
			}
			
			for(int i = 0; i < this.coins.size(); i++) {
				coins.get(i).coinUpdate();
			}
			
			for(int i = 0; i < this.heartPoints.size(); i++) {
				heartPoints.get(i).heartUpdate();
			}
		}
	}
	
	@Override	
	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		// Drawing background
		BufferedImage background = null;
		BufferedImage coinImage = null, enemyImage = null, playerImage = null;
		try {
			background = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/background/planet.png"));
			coinImage = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/items/Item-Coin/Item-Coin.png"));
			enemyImage = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/enemycharacter/Enemy-Character.png"));
			playerImage = ImageIO.read(getClass().getResourceAsStream("/com/bafalut/playermodel/Player-Character.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(running == true) {
			
			
			g2.drawImage(background, 0, 0, null);
			
			// drawing the enemies
			for(int i = 0; i < this.enemies.size(); i++) {
				enemies.get(i).draw(g2);
			}
			
			// drawing the coins
			for(int i = 0; i < this.coins.size(); i++) {
				coins.get(i).draw(g2);
			}
			
			// drawing the heart points
			for(int i = 0; i < this.heartPoints.size(); i++) {
				heartPoints.get(i).draw(g2);
			}
			
			// Drawing the player character
			GamePanel.mainPlayer.draw(g2);
			
			
			// draws the hitboxes for testing
			g2.setColor(Color.RED);
			//g2.drawRect(coin.getHitbox().x, coin.getHitbox().y, coin.getHitbox().width, coin.getHitbox().height);
			//g2.drawRect(GamePanel.mainPlayer.getXHitboxPosition(), GamePanel.mainPlayer.getYHitboxPosition(), GamePanel.mainPlayer.getHitboxWidth(), GamePanel.mainPlayer.getHitboxHeight());
			//g2.drawRect((int) GamePanel.mainPlayer.getPlayerBeamHitbox().getX(), (int) GamePanel.mainPlayer.getPlayerBeamHitbox().getY(), (int) GamePanel.mainPlayer.getPlayerBeamHitbox().getWidth(), (int) GamePanel.mainPlayer.getPlayerBeamHitbox().getHeight());
			
			
			g2.setColor(new Color(213, 182, 44));
			g2.fillRoundRect(0, 0, 100, 50, 50, 25);
			
			g2.setColor(new Color(194, 162, 44));
			g2.drawRoundRect(0, 0, 100, 50, 50, 25);
			
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("Consolas", Font.PLAIN, 25));
			g2.drawString(String.valueOf(coinScore), 10, 32);
			
			g2.drawImage(coinImage, 44, 0, 48, 48, null);
			
			g2.setColor(new Color(167, 215, 242));
			g2.fillRoundRect(125, 0, 100, 50, 50, 25);
			
			g2.setColor(new Color(124, 160, 179));
			g2.drawRoundRect(125, 0, 100, 50, 50, 25);
			
			g2.setColor(Color.BLACK);
			g2.drawString(String.valueOf(killScore), 135, 32);
			g2.drawImage(enemyImage, 125 + 50, 5, 48, 48, null);
			
			g2.dispose();
		} else if(GamePanel.running == false) {
			g2.drawImage(background, 0, 0, null);
			g2.drawImage(playerImage, 185, 25, 48 * 3, 48 * 3, null);
			
			g2.setFont(new Font("Consolas", Font.PLAIN, 25));
			g2.setColor(Color.GREEN);
			g2.drawString("bafalut's blob game", 125, 30);
			
			g2.setFont(new Font("Consolas", Font.PLAIN, 20));
			g2.setColor(Color.BLACK);
			g2.fillRect(15, 225, 100, 200);
			
			g2.setColor(Color.GREEN);
			g2.drawRoundRect(15, 225, 100, 200, 25, 25);
			
			g2.setColor(Color.BLACK);
			g2.fillRect(15, 125, 512 - (15 * 2), 75);
			
			g2.setColor(Color.GREEN);
			g2.drawRoundRect(15, 125, 512 - (15 * 2), 75, 25, 25);
			
			g2.setFont(new Font("Consolas", Font.PLAIN, 30));
			
			if(this.gameOver == true) {
				g2.drawString("Main Menu", 50, 170);
				g2.drawString("-  Game Over!", 245, 170);
			} else {
				g2.drawString("Main Menu", 175, 170);
			}
			
			// play button
			g2.setFont(new Font("Consolas", Font.PLAIN, 20));
			g2.setColor(Color.GREEN);
			if(GamePanel.playerSelected == "Play") {
				g2.setColor(Color.GREEN);
				g2.drawString(">Play", 25, 275);
			} else if(GamePanel.playerSelected != "Play") {
				g2.setColor(Color.GREEN);
				g2.drawString("Play", 25, 275);
			}
			
			// credits button
			if(GamePanel.playerSelected == "Credit") {
				if(GamePanel.onCredits == true) {
					g2.setColor(new Color(0, 205, 0));
					g2.drawString(">Credit", 25, 300);
				} else if(GamePanel.onCredits == false) {
					g2.setColor(Color.GREEN);
					g2.drawString(">Credit", 25, 300);
				}
			} else if(GamePanel.playerSelected != "Credit") {
				g2.setColor(Color.GREEN);
				g2.drawString("Credit", 25, 300);
			}
			
			// stats button
			if(GamePanel.playerSelected == "Stat") {
				if(GamePanel.onStats == true) {
					g2.setColor(new Color(0, 205, 0));
					g2.drawString(">Stat", 25, 325);
				} else if(GamePanel.onCredits == false) {
					g2.setColor(Color.GREEN);
					g2.drawString(">Stat", 25, 325);
				}
			} else if(GamePanel.playerSelected != "Stat") {
				g2.setColor(Color.GREEN);
				g2.drawString("Stat", 25, 325);
			}
			
			// help button
			if(GamePanel.playerSelected == "Help") {
				if(GamePanel.onHelp == true) {
					g2.setColor(new Color(0, 205, 0));
					g2.drawString(">Help", 25, 350);
				} else if(GamePanel.onHelp == false) {
					g2.setColor(Color.GREEN);
					g2.drawString(">Help", 25, 350);
				}
			} else if(GamePanel.playerSelected != "Help") {
				g2.setColor(Color.GREEN);
				g2.drawString("Help", 25, 350);
			}
			
			// exit button
			if(GamePanel.playerSelected == "Exit") {
				g2.setColor(Color.GREEN);
				g2.drawString(">Exit", 25, 375);
			} else if(GamePanel.playerSelected != "Exit") {
				g2.setColor(Color.GREEN);
				g2.drawString("Exit", 25, 375);
			}
			
			if(GamePanel.onCredits == true) {
				g2.setColor(Color.BLACK);
				g2.fillRect(150, 225, 300, 200);
				
				g2.setColor(Color.GREEN);
				g2.drawRoundRect(150, 225, 300, 200, 50, 50);
				
				g2.drawString("Credits:", 175, 245);
				g2.drawString("Creator: bafalut", 175, 295);
				g2.drawString("That's it! :D", 175, 345);
			}
			
			if(GamePanel.onStats == true) {
				int tempKillScore = 0;
				int tempCoinScore = 0;
				
				try {
					BufferedReader reader = new BufferedReader(new FileReader(killScoreFile));
					
					tempKillScore = Integer.parseInt(reader.readLine());
					reader.close();
				} catch (IOException | NumberFormatException e) {
					e.printStackTrace();
				}
				
				try {
					BufferedReader reader = new BufferedReader(new FileReader(coinScoreFile));
					
					tempCoinScore = Integer.parseInt(reader.readLine());
					reader.close();
				} catch (IOException | NumberFormatException e) {
					e.printStackTrace();
				}
				
				g2.setColor(Color.BLACK);
				g2.fillRect(150, 225, 300, 200);
				
				g2.setColor(Color.GREEN);
				g2.drawRoundRect(150, 225, 300, 200, 50, 50);
				
				g2.drawString("Stats:", 175, 245);
				
				g2.setFont(new Font("Consolas", Font.PLAIN, 15));
				g2.drawString("Total Coins Collected: " + tempCoinScore, 175, 275);
				g2.drawString("Total Enemies Destroyed: " + tempKillScore, 175, 300);
				g2.drawString("Total Score: " + ((tempCoinScore * 2) + tempKillScore), 175, 325);
			}
			
			if(GamePanel.onHelp == true) {
				g2.setColor(Color.BLACK);
				g2.fillRect(150, 225, 300, 200);
				
				g2.setColor(Color.GREEN);
				g2.drawRoundRect(150, 225, 300, 200, 50, 50);
				
				g2.drawString("Help:", 175, 245);
				g2.setFont(new Font("Consolas", Font.PLAIN, 15));
				
				g2.drawString("Coins give you 2 total score points", 155, 260);
				g2.drawString("if you collect 1, destroying enemies", 155, 280);
				g2.drawString("only give you 1 point, there are", 155, 300);
				g2.drawString("also heart points that heal 2 points", 155, 320);
				g2.drawString("of health, speaking of health points,", 155, 340);
				g2.drawString("you only have 6 max heart points,", 155, 360);
				g2.drawString("press [Q] to shoot, and [W,A,S,D] to", 155, 380);
				g2.drawString("move, enemy speeds depend on their", 155, 400);
				g2.drawString("height (the lower the faster)", 175, 420);
			}
		}
		
	}
	
	@Override
	public void run() {
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;
		int drawCount = 0;
		
		while(this.gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			GamePanel.deltaTime = delta;
			timer += (currentTime - lastTime);
			lastTime = currentTime;
			
			if (delta >= 1) {
				
				// update
				update();
				
				// draw
				repaint();
					
				
				delta--;
				drawCount++;
			}
			
			if (timer >= 1000000000) {
				System.out.println("UPS: " + drawCount);
				drawCount = 0;
				timer = 0;
			}
		}
	}
	
	// setters
	
	public static final void setCoinScore(int i) {
		GamePanel.coinScore = i;
	}
	
	public static final void setKillScore(int i) {
		GamePanel.killScore = i;
	}
	
	// getters
	
	public static final double getDeltaTime() {
		return GamePanel.deltaTime;
	}
	
	public static final boolean isRunning() {
		return GamePanel.running;
	}
	
	public static final int getCoinScore() {
		return GamePanel.coinScore;
	}
	
	public static final int getKillScore() {
		return GamePanel.killScore;
	}
	
	// inner classes
	private final class KeyHandler implements KeyListener {
		
		KeyHandler() {
			
		}
		
		@Override
		public final void keyTyped(KeyEvent e) {
			// NOTHING
		}

		@Override
		public final void keyPressed(KeyEvent e) {
			if(running == false) {
				switch(e.getKeyCode()) {
				
				case(KeyEvent.VK_UP):
					switch(GamePanel.playerSelected) {
					case("Play"):
						GamePanel.playerSelected = "Exit";
						sounds.playSound(Sounds.SELECT);
						break;
						
					case("Credit"):
						if(GamePanel.onCredits == false) {
							GamePanel.playerSelected = "Play";
							sounds.playSound(Sounds.SELECT);
						}
						break;
						
					case("Stat"):
						if(GamePanel.onStats == false) {
							GamePanel.playerSelected = "Credit";
							sounds.playSound(Sounds.SELECT);
						}
						break;
						
					case("Help"):
						if(GamePanel.onHelp == false) {
							GamePanel.playerSelected = "Stat";
							sounds.playSound(Sounds.SELECT);
						}
						break;
						
					case("Exit"):
						GamePanel.playerSelected = "Help";
						sounds.playSound(Sounds.SELECT);
						break;
					}
					break;
					
				case(KeyEvent.VK_DOWN):
					switch(GamePanel.playerSelected) {
					case("Play"):
						GamePanel.playerSelected = "Credit";
						sounds.playSound(Sounds.SELECT);
						break;
						
					case("Credit"):
						if(GamePanel.onCredits == false) {
							GamePanel.playerSelected = "Stat";
							sounds.playSound(Sounds.SELECT);
						}
						break;
						
					case("Stat"):
						if(GamePanel.onStats == false) {
							GamePanel.playerSelected = "Help";
							sounds.playSound(Sounds.SELECT);
						}
						break;
						
					case("Help"):
						if(GamePanel.onHelp == false) {
							GamePanel.playerSelected = "Exit";
							sounds.playSound(Sounds.SELECT);
						}
						break;
						
					case("Exit"):
						GamePanel.playerSelected = "Play";
						sounds.playSound(Sounds.SELECT);
						break;
					}
					break;
					
				case(KeyEvent.VK_ENTER):
					switch(GamePanel.playerSelected) {
					case("Play"):
						sounds.playSound(Sounds.ENTER);
						GamePanel.mainPlayer.setHealth(GamePanel.mainPlayer.getMaxHealth());
						GamePanel.running = true;
						break;
						
					case("Credit"):
						sounds.playSound(Sounds.ENTER);
						if(GamePanel.onCredits == false) {
							GamePanel.onCredits = true;
						} else if(GamePanel.onCredits == true) {
							GamePanel.onCredits = false;
						}
						break;
						
					case("Stat"):
						sounds.playSound(Sounds.ENTER);
						if(GamePanel.onStats == false) {
							GamePanel.onStats = true;
						} else if(GamePanel.onStats == true) {
							GamePanel.onStats = false;
						}
						break;
						
					case("Help"):
						sounds.playSound(Sounds.ENTER);
						if(GamePanel.onHelp == false) {
							GamePanel.onHelp = true;
						} else if(GamePanel.onHelp == true) {
							GamePanel.onHelp = false;
						}
						break;
						
					case("Exit"):
						sounds.playSound(Sounds.ENTER);
						System.exit(0);
						break;
					}
				}
			}
		}

		@Override
		public final void keyReleased(KeyEvent e) {
			
		}
	}
	
	private final class Sounds {
		private URL[] soundURL = new URL[10];
		private Clip clip;
		
		static final int SELECT = 0;
		static final int ENTER = 1;
		
		Sounds() {
			soundURL[0] = getClass().getResource("/com/bafalut/sounds/Panel-Sounds/Select.wav");
			soundURL[1] = getClass().getResource("/com/bafalut/sounds/Panel-Sounds/Enter.wav");
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

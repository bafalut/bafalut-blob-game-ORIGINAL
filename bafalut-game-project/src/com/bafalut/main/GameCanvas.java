package com.bafalut.main;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

@SuppressWarnings("serial")
class GameCanvas extends JFrame {
	private GamePanel gamePanel;
	private ImageIcon canvasIcon;
	
	protected GameCanvas() {
		this.canvasIcon = new ImageIcon(GameCanvas.class.getResource("/Game-Icon.png"));
		
		this.setTitle("alien blob game");
		this.setIconImage(this.canvasIcon.getImage());
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.gamePanel = new GamePanel();
		this.add(this.gamePanel);
		this.pack();
		
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		this.gamePanel.startThread();
	}
	
}

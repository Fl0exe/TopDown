package binaries;

import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import game.buffs.*;
import entities.*;
import game.behaviours.*;

/**
 *
 * @author Florian Pabler
 * @version 1.0 (12.10.2022)
 *
 */

public class Kontrolle implements MouseListener, KeyListener, FocusListener {

	private ZeichenFlaeche15 zeichenflaeche = new ZeichenFlaeche15(); // ZeichenFlaeche wird erstellt

	private boolean spielAmLaufen = true; // Wenn "false", wird das Spiel beendet.
	private boolean spielIstPausiert = false; // Wenn "true" wird das spiel Pausiert
	
	// Lukas Meeder (20.10.2022)
	private Player player1 = new Player(25, 25, 50, 50, zeichenflaeche, this);

	// Lukas Meeder (20.10.2022)
	// Checks
	private long lastDamageTimestamp = 0;
	
	public void starteSpiel() { // Hier Gehts Los!

		zeichenflaeche.macheZeichenFlaecheSichtbar(true); // Init ZeichenFlaeche!
		zeichenflaeche.requestFocus();
		zeichenflaeche.stopAutomatischesZeichnen();
		zeichenflaeche.addMouseListener(this);
		zeichenflaeche.addKeyListener(this);
		zeichenflaeche.addFocusListener(this);
		
		// Lukas Meeder (20.10.2022)
		// Hintergrundbild erstmal als Template
		zeichenflaeche.setzeBild(-1, Kontrolle.loadPicture("Assets/Images/UI/dungeon-background.png"), 0, 0, zeichenflaeche.getWidth(), zeichenflaeche.getHeight());

		// Lukas Meeder (20.10.2022)
		LittleDragon lildragon = new LittleDragon(50, 50, 25, 25, zeichenflaeche, this);

		// Lukas Meeder (20.10.2022)
		// Beispiels Buff!
		// Buff der für 5 Sekunden hält und jede 0.15 Sekunden dem Spieler 1 Schaden hinzufuegt!
		new Buff(1, "Aura of enemy Dragon", "You are a enemy of Dragons!", (player1) -> player1.damage(1), true, 150, 5000, player1);
					
		
		// Lukas Meeder (20.10.2022) (Bedinung und Inhalt der Schleife generell überarbeitet
		while (spielAmLaufen && player1.isAlive()) { // Game Loop

			while (spielIstPausiert) { // Sperrt den code ein

			}
			
			
			// Move each entity!
			for(Entity entity : Entity.getAllEntities())
			{
				entity.move();
			}
			
			// Enemy Damage check
			if(checkForCollision(player1)) {
				if((System.currentTimeMillis() - lastDamageTimestamp) >= 250)
				{
					if(player1.isGod() == false)
					{
						player1.damage(10);
						lastDamageTimestamp = System.currentTimeMillis();
					}
				}
			}
			
			//System.out.println("X: " + player1.getX() + " | Y: " + player1.getY());
			System.out.println(player1.getHp());
			
			// Execute Buff Functions
			doBuffStuff();

			zeichenflaeche.manualPaint();
			
			try {
				Thread.sleep(15); // Pause im GameLoop!
			} catch (InterruptedException event) {
			}

		}

	}
	
	/*
	 * 
	 * Author: Lukas Meeder
	 * 20.10.2022 19:48 Uhr
	 * NOTE: Picture Loader und weitere funktionen einfach in die Kontrolle rein
	 */
	
	public static Image loadPicture(String pictureURL)
	{
		// Load Entity Picture
		URL urlPicture = ClassLoader.getSystemResource(pictureURL);
		ImageIcon icon = new ImageIcon(urlPicture);
		return icon.getImage();
	}
	
	public static ArrayList<Image> loadPictures(String[] pictureURLs)
	{
		ArrayList<Image> pictures = new ArrayList<Image>();
		
		for(String url : pictureURLs)
		{
			// Load Entity Picture
			URL urlPicture = ClassLoader.getSystemResource(url);
			ImageIcon icon = new ImageIcon(urlPicture);
			pictures.add(icon.getImage());
		}
		
		return pictures;
	}
	
	public void assignNewTarget(Entity entityThatNeedsANewTarget)
	{
		// First of all check if the Entity is even Enemy Flagged
		if(entityThatNeedsANewTarget.getBehaviour() instanceof Enemy) {
			((Enemy) entityThatNeedsANewTarget.getBehaviour()).setTargetPlayer(player1);
		} else 
		{
			System.err.println("ERROR: " + this + " should've gotten a new target assigned but isn't flagged as an Enemy!");
		}
	}
	
	public boolean checkForCollision(Entity player)
	{
		for(Entity enemy : Entity.getAllEntities()) {
			if(enemy instanceof LittleDragon) {
				if(player.getX() < enemy.getX() + enemy.getBreite() && player.getX() + player.getBreite() > enemy.getX() && player.getY() < enemy.getY() + enemy.getHoehe() && player.getHoehe() + player.getY() > enemy.getY()) {
		 	 	 	 return true;
		 	 	 }
			}
		}
		return false;
	}
	
	public boolean checkForCollision(Entity entity, Entity entity2)
	{
		if(entity.getX() < entity2.getX() + entity2.getBreite() && entity.getX() + entity.getBreite() > entity2.getX() && entity.getY() < entity2.getY() + entity2.getHoehe() && entity.getHoehe() + entity.getY() > entity2.getY()) {
 	 	 	 return true;
 	 	 }
		
		return false;
	}
	
	public void doBuffStuff()
	{
		for(Buff buff : Buff.getAllBuffs())
		{
			buff.executeFunktion();
		}
		Buff.getAllBuffs().removeAll(Buff.getBuffsToBeRemoved());
		Buff.getBuffsToBeRemoved().clear(); 
	}
	
	// ENDE DER FUNKTIONEN VOM 20.10.
	

	public void mouseClicked(MouseEvent event) { // Unused
	}

	public void mouseEntered(MouseEvent event) { // Unused
	}

	public void mouseExited(MouseEvent event) { // Unused
	}

	public void keyTyped(KeyEvent event) { // Unused
	}

	public void mousePressed(MouseEvent event) {
		// TODO mache sie mal

	}

	public void mouseReleased(MouseEvent event) {
		// TODO mache sie mal

	}

	public void keyPressed(KeyEvent event) {
		// TODO mache sie mal
		// Lukas Meeder (20.10.2022)

		int taste = event.getKeyCode();
		
		// Player Input
		if(taste == KeyEvent.VK_W) // Up
		{
			player1.setUp(true);
		}
		if(taste == KeyEvent.VK_S) // Down
		{
			player1.setDown(true);
		}
		if(taste == KeyEvent.VK_A) // Left
		{
			player1.setLeft(true);
		}
		if(taste == KeyEvent.VK_D) // Right
		{
			player1.setRight(true);
		}
		if(taste == KeyEvent.VK_SHIFT)
		{
			player1.setSprint(true);
		}
		
		
	}

	public void keyReleased(KeyEvent event) {
		// TODO mache sie mal
		// Lukas Meeder (20.10.2022)
		
		int taste = event.getKeyCode();
		
		// Player Input
		if(taste == KeyEvent.VK_W) // Up
		{
			player1.setUp(false);
		}
		if(taste == KeyEvent.VK_S) // Down
		{
			player1.setDown(false);
		}
		if(taste == KeyEvent.VK_A) // Left
		{
			player1.setLeft(false);
		}
		if(taste == KeyEvent.VK_D) // Right
		{
			player1.setRight(false);
		}
		if(taste == KeyEvent.VK_SHIFT)
		{
			player1.setSprint(false);
		}

	}

	public void focusGained(FocusEvent event) {
		spielIstPausiert = false; // Resume das Spiel
	}

	public void focusLost(FocusEvent event) {
		spielIstPausiert = true; // Pausiere das Spiel
	}

}

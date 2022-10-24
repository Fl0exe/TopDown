package entities;

// TODO create subclass NichtSpieler

//Imports
import java.awt.Image;
import java.util.ArrayList;

import binaries.Kontrolle;
import binaries.ZeichenFlaeche15;
import game.behaviours.Behaviour;
import game.behaviours.Enemy;
import game.behaviours.Friendly;
import game.behaviours.Neutral;
import game.behaviours.availableBehaviours;

/**
 *
 * @author Lukas Meeder
 * @version 1.0 (20.10.2022)
 *
 */

public class Entity {

// Constructor
	public Entity(int x, int y, int hoehe, int breite, String[] pictureURLs, availableBehaviours behaviour,
			int healthpoints, ZeichenFlaeche15 renderEngine, Kontrolle createdInGame) {
		// Set Attributes
		this.id = entityCount;
		this.x = x;
		this.y = y;

		this.hoehe = hoehe;
		this.breite = breite;

		// Properties
		if ((healthpoints != 0 || healthpoints != 1) && healthpoints > 0) // Checking if the HP is a valid value
		{
			this.hp = healthpoints;
			this.maxHp = this.hp;
		}

		// Game
		this.zeichenFlaeche = renderEngine;
		this.game = createdInGame;

		// Setting the Behaviour of the Entity
		if (behaviour == availableBehaviours.BEHAVE_NEUTRAL) {
			Neutral neutralBehaviour = new Neutral();
			this.behaviour = neutralBehaviour;
		} else if (behaviour == availableBehaviours.BEHAVE_ENEMY) {
			Enemy enemyBehaviour = new Enemy();
			this.behaviour = enemyBehaviour;
		} else if (behaviour == availableBehaviours.BEHAVE_FRIENDLY) {
			Friendly friendlyBehaviour = new Friendly();
			this.behaviour = friendlyBehaviour;
		} else {
			System.err.println("Error on Entity: " + this + ", couldn't set the Behaviour type! Will be null");
			this.behaviour = null;
		}

		this.defaultPicture = Kontrolle.loadPicture(pictureURLs[0]);

		// Count entityCount up
		entityCount++;

		// Print into console
		System.out.println("New Entity with id " + id + " created!");
		allEntities.add(this);

		// Time for movement
		lastTimestamp = System.currentTimeMillis();

		// Load pictures
		pictures = Kontrolle.loadPictures(pictureURLs);
	}

	// Constructor for UI Elements
	public Entity(double x, double y, ZeichenFlaeche15 renderEngine) {
		// Set Attributes
		this.id = entityCount;
		this.x = x;
		this.y = y;

		// Count entityCount up
		entityCount++;

		// Print into console
		System.out.println("New Entity with id " + id + " created!");

		this.zeichenFlaeche = renderEngine;
	}

// Attributes
	private static ArrayList<Entity> allEntities = new ArrayList<>();

	private static int entityCount = 0; // Start at 1 because Background is id 0
	protected int id;
	private int hp = 1; // Healthpoints
	private int maxHp = hp;
	private boolean isAlive = true;
	private boolean isGod = false; // activates Godmode!

	// TODO Buffs and Debuffs
	// Add new class for that - not an entity but an Debuff and Buff class
	// Maybe different behaviours (nature, fire ...)
	// Maybe make it able to be seen on the UI - but it could be different due to
	// the id thingy
	// Make that you can receive damage through the debuffs or heal through the
	// buffs
	private double x;
	private double y;
	protected int hoehe = 50;
	protected int breite = 50;
	protected double speed = 0.5;
	protected Image currentPicture;
	protected Image defaultPicture;
	protected long lastTimestamp;
	protected ArrayList<Image> pictures;
	protected ZeichenFlaeche15 zeichenFlaeche;
	protected Behaviour behaviour;
	protected Kontrolle game;

// Methods
	public void move() {
		// "Move" the Entity
		zeichenFlaeche.loeschen(this.id);
		if (currentPicture != null) {
			zeichenFlaeche.setzeBild(id, currentPicture, x, y, hoehe, breite);
		} else {
			zeichenFlaeche.setzeBild(id, defaultPicture, x, y, hoehe, breite);
		}

		lastTimestamp = System.currentTimeMillis();
	}

	protected Image getPicture() {
		return this.defaultPicture;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getX() {
		return x;
	}

	public boolean isGod() {
		return isGod;
	}

	public void setGod(boolean value) {
		isGod = value;
	}

	public int getBreite() {
		return breite;
	}

	public int getHoehe() {
		return hoehe;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public static ArrayList<Entity> getAllEntities() {
		return allEntities;
	}

	public static void setAllEntities(ArrayList<Entity> allEntities) {
		Entity.allEntities = allEntities;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public static int getEntityCount() {
		return entityCount;
	}

	public static void setEntityCount(int entityCount) {
		Entity.entityCount = entityCount;
	}

	public Image getCurrentPicture() {
		return currentPicture;
	}

	public void setCurrentPicture(Image currentPicture) {
		this.currentPicture = currentPicture;
	}

	public Image getDefaultPicture() {
		return defaultPicture;
	}

	public void setDefaultPicture(Image defaultPicture) {
		this.defaultPicture = defaultPicture;
	}

	public long getLastTimestamp() {
		return lastTimestamp;
	}

	public void setLastTimestamp(long lastTimestamp) {
		this.lastTimestamp = lastTimestamp;
	}

	public ZeichenFlaeche15 getRenderEngine() {
		return zeichenFlaeche;
	}

	public void setRenderEngine(ZeichenFlaeche15 renderEngine) {
		this.zeichenFlaeche = renderEngine;
	}

	public Behaviour getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(Behaviour behaviour) {
		this.behaviour = behaviour;
	}

	public double getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double amount) {
		this.speed = amount;
	}

	public void addSpeed(double amount) {
		this.speed = this.speed + amount;
	}

	public void removeSpeed(double amount) {
		this.speed = this.speed - amount;
	}

	public void damage() {
		this.hp--;
		if (getHp() <= 0) {
			this.isAlive = false;
		}
	}

	public void damage(int amount) {
		this.hp = this.hp - amount;
		if (getHp() <= 0) {
			this.isAlive = false;
		}
	}

	public void heal() {
		if ((getHp() + 1) <= getMaxHp()) {
			this.hp++;
		}
		if (getHp() >= 1) {
			this.isAlive = true;
		}
	}

	public void heal(int amount) {
		if ((getHp() + amount) <= getMaxHp()) {
			this.hp = this.hp + amount;
		}
		if (getHp() >= 1) {
			this.isAlive = true;
		}
	}

}

package entities;

import binaries.Kontrolle;
import binaries.ZeichenFlaeche15;
import game.behaviours.availableBehaviours;

/**
 *
 * @author Lukas Meeder
 * @version 1.0 (20.10.2022)
 *
 */

public class Spieler extends Entity {
// Constructor
	public Spieler(int x, int y, int hoehe, int breite, ZeichenFlaeche15 renderEngine, Kontrolle game) {
		super(x, y, hoehe, breite,
				new String[] { "Assets/Images/Player/amg1_bk1.png", "Assets/Images/Player/amg1_lf1.png",
						"Assets/Images/Player/amg1_rt1.png", "Assets/Images/Player/amg1_fr1.png" },
				availableBehaviours.BEHAVE_NEUTRAL, 100, renderEngine, game);
	}

// Attributes
	private boolean up = false;
	private boolean down = false;
	private boolean left = false;
	private boolean right = false;

	// private final int WALK_SPEED = 1;
	private int bonusSpeed = 0;

	private boolean sprint = false;

// Methods
	@Override
	public void move() // Overloading Entity Move
	{
		long thisTime = System.currentTimeMillis();
		double deltaTime = (thisTime - this.lastTimestamp);

		// Player movement method
		if (this.isUp()) {
			this.setY(this.getY() - calcSpeed() * deltaTime);
			this.setCurrentPicture(pictures.get(0));
		}
		if (this.isDown()) {
			this.setY(this.getY() + calcSpeed() * deltaTime);
			this.setCurrentPicture(pictures.get(3));
		}
		if (this.isLeft()) {
			this.setX(this.getX() - calcSpeed() * deltaTime);
			this.setCurrentPicture(pictures.get(1));
		}
		if (this.isRight()) {
			this.setX(this.getX() + calcSpeed() * deltaTime);
			this.setCurrentPicture(pictures.get(2));
		}

		// "Move" the Entity

		zeichenFlaeche.loeschen(this.id);
		if (currentPicture != null) {
			zeichenFlaeche.setzeBild(id, currentPicture, (zeichenFlaeche.getWidth() / 2) - (breite / 2),
					(zeichenFlaeche.getHeight() / 2) - (hoehe / 2), hoehe, breite);
		} else {
			zeichenFlaeche.setzeBild(id, defaultPicture, (zeichenFlaeche.getWidth() / 2) - (breite / 2),
					(zeichenFlaeche.getHeight() / 2) - (hoehe / 2), hoehe, breite);
		}

	}

	private double calcSpeed() {
		// To calculate the speed on every move
		if (isSprint()) {
			return (getSpeed() * 2) + bonusSpeed;
		} else {
			return getSpeed() + bonusSpeed;
		}
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isSprint() {
		return sprint;
	}

	public void setSprint(boolean sprint) {
		this.sprint = sprint;
	}

}

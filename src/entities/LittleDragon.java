package entities;

import binaries.Kontrolle;
import binaries.ZeichenFlaeche15;
import game.behaviours.Enemy;
import game.behaviours.Friendly;
import game.behaviours.availableBehaviours;

/**
 *
 * @author Lukas Meeder
 * @version 1.0 (20.10.2022)
 *
 */

public class LittleDragon extends Entity {

	public LittleDragon(int x, int y, int hoehe, int breite, ZeichenFlaeche15 renderEngine, Kontrolle game) {
		super(x, y, hoehe, breite,
				new String[] { "Assets/Images/Enemies/Dragon/dvl1_fr1.png",
						"Assets/Images/Enemies/Dragon/dvl1_bk1.png" },
				availableBehaviours.BEHAVE_ENEMY, 1, renderEngine, game);
	}

	@Override
	public void move() {
		long thisTime = System.currentTimeMillis();
		double deltaTime = (thisTime - this.lastTimestamp);

		// If Dragon is defined as an enemy
		if (behaviour instanceof Enemy) {

			// Check if Dragon has a target
			if (((Enemy) this.behaviour).getTargetPlayer() != null) {

				Spieler targetPlayer = ((Enemy) this.behaviour).getTargetPlayer();

				// Try to follow the player
				if (targetPlayer.getX() > this.getX()) {
					this.setX(this.getX() + (this.speed / 2) * deltaTime);
					this.setCurrentPicture(pictures.get(0));
				}

				if (targetPlayer.getX() < this.getX()) {
					this.setX(this.getX() - (this.speed / 2) * deltaTime);
					this.setCurrentPicture(pictures.get(1));
				}

				if (targetPlayer.getY() > this.getY()) {
					this.setY(this.getY() + (this.speed / 2) * deltaTime);
					this.setCurrentPicture(pictures.get(0));
				}

				if (targetPlayer.getY() < this.getY()) {
					this.setY(this.getY() - (this.speed / 2) * deltaTime);
					this.setCurrentPicture(pictures.get(0));
				}

			} else {
				// If has no target yet, try to get a target
				this.game.assignNewTarget(this);

			}
		} else if (behaviour instanceof Friendly) {

			// Just walk up and down
			if (this.getY() > 60) {
				setX(getX() - 5);
			}

		}
		super.move();
	}

}

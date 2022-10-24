package game.behaviours;

import entities.Spieler;

/**
 *
 * @author Lukas Meeder
 * @version 1.0 (20.10.2022)
 *
 */

public class Enemy extends Behaviour {

	public Spieler targetPlayer;

	public Spieler getTargetPlayer() {
		return targetPlayer;
	}

	public void setTargetPlayer(Spieler targetPlayer) {
		this.targetPlayer = targetPlayer;
		System.out.println("A new target has been assigned to entity " + this);
	}

	@Override
	public void scream() {
		// Print a text if behaviour is null
		System.out.println("Behaviour is Enemy!");
	}
}

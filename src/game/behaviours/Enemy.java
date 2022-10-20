package game.behaviours;
import entities.Player;

/**
*
* @author Lukas Meeder
* @version 1.0 (20.10.2022)
*
*/

public class Enemy extends Behaviour {

	public Player targetPlayer;

	public Player getTargetPlayer() {
		return targetPlayer;
	}

	public void setTargetPlayer(Player targetPlayer) {
		this.targetPlayer = targetPlayer;
		System.out.println("A new target has been assigned to entity " + this);
	}

	public void scream()
	{
		// Print a text if behaviour is null
		System.out.println("Behaviour is Enemy!");
	}
}

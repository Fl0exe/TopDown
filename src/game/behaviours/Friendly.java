package game.behaviours;

/**
 *
 * @author Lukas Meeder
 * @version 1.0 (20.10.2022)
 *
 */

public class Friendly extends Behaviour {

	@Override
	public void scream() {
		// Print a text if behaviour is null
		System.out.println("Behaviour is Friendly!");
	}
}

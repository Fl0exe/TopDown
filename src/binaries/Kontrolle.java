package binaries;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

	public static int idZaehler = 0; // Der universal funktionierende IDZaehler

	public void starteSpiel() { // Hier Gehts Los!

		zeichenflaeche.macheZeichenFlaecheSichtbar(true); // Init ZeichenFlaeche!
		zeichenflaeche.requestFocus();
		zeichenflaeche.stopAutomatischesZeichnen();
		zeichenflaeche.addMouseListener(this);
		zeichenflaeche.addKeyListener(this);
		zeichenflaeche.addFocusListener(this);

		while (spielAmLaufen) { // Game Loop

			while (spielIstPausiert) { // Sperrt den code ein

			}

			try {
				Thread.sleep(15); // Pause im GameLoop!
			} catch (InterruptedException event) {
			}

		}

	}

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

	}

	public void keyReleased(KeyEvent event) {
		// TODO mache sie mal

	}

	public void focusGained(FocusEvent event) {
		spielIstPausiert = false; // Resume das Spiel
	}

	public void focusLost(FocusEvent event) {
		spielIstPausiert = true; // Pausiere das Spiel
	}

}

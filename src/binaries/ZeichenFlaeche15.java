package binaries;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 *
 * @author Florian Pabler
 * @version 15.0 (28.09.2022)
 *
 */

public class ZeichenFlaeche15 extends JPanel {

	private static final long serialVersionUID = 1L;

	private JFrame fenster;

	private volatile ArrayList<ZuZeichnendesObjekt> zuZeichnendeObjekteListe = new ArrayList<>();

	private final String[] zulassigeFarbenString = { "BLACK", "BLUE", "CYAN", "DARK_GRAY", "GRAY", "GREEN",
			"LIGHT_GRAY", "MAGENTA", "ORANGE", "PINK", "RED", "WHITE", "YELLOW" };
	private Color[] zulassigeFarben;
	private final Color defaultFarbe = Color.BLACK;
	private Color aktuelleFarbe = defaultFarbe;
	private final Color defaultHintergrundfarbe = Color.WHITE;
	private Color aktuelleHintergrundfarbe = defaultHintergrundfarbe;

	static long gesamtRenderzeit = 0;
	static int anzahlRenderings = 0;

	private Renderer renderEngine = null;
	private ThreadPoolExecutor renderThread = null;

	private ScheduledThreadPoolExecutor timer = null;

	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	/**
	 * <p>
	 * Diese Methode muss aufgerufen werden, um die Zeichenflaeche auf dem
	 * Bildschirm sichtbar zu machen. Nach dem Aufruf dieser Methode ist die
	 * Zeichenflaeche leer.
	 * </p>
	 */
	public void macheZeichenFlaecheSichtbar(boolean fullscreen) {

		initializeColors(); // Call own method to create an array of possible colors.

		fenster = new JFrame();
		fenster.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		fenster.setTitle("ZeichenflÃ¤che");
		fenster.setBackground(aktuelleHintergrundfarbe);

		/*
		 * Lukas Meeder 14.10.2022 00:36 Uhr
		 *
		 * NOTE: Scheinbar muss das setUndecorated(true) passieren, bevor das Fenster
		 * sichtbar gemacht wird, daher dann der Fehler "Window is Displayable.
		 *
		 * Habe das jetzt einfach mal als Parameter gemacht.
		 */
		if (fullscreen) {
			fenster.setExtendedState(Frame.MAXIMIZED_BOTH);
			fenster.setResizable(false);
			fenster.setUndecorated(true);
		}

		/*
		 * Lukas Meeder 14.10.2022 00:44 Uhr
		 *
		 * NOTE: Habe die PreferredSize mal so gemacht, dass Vollbild ist. Weiß nicht
		 * wofür das vorherige war.
		 */
		this.setPreferredSize(new Dimension(screenSize.width, screenSize.height));
		this.setBackground(aktuelleHintergrundfarbe);
		this.setOpaque(true);
		fenster.setContentPane(this);

		fenster.pack();
		fenster.setVisible(true);

		this.requestFocusInWindow(); // Gets the key typing focus to this window so that KeyListener will get events.

		renderEngine = new Renderer(this);

		renderThread = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
				new ThreadPoolExecutor.DiscardPolicy());
		renderThread.prestartAllCoreThreads();

		// Die Timer-Funktion fï¿½r das zeichnen (auch wenn es nicht so aussieht...).
		timer = new ScheduledThreadPoolExecutor(1);
		timer.scheduleAtFixedRate(renderEngine, 50, 16, TimeUnit.MILLISECONDS);
	}

	public void setzeTitel(String titel) {
		fenster.setTitle(titel);
	}

	public void setzeIcon(String pfadZumBild) {

		try {

			URL imgURL = ClassLoader.getSystemResource(pfadZumBild);
			ImageIcon icon = new ImageIcon(imgURL);
			Image img = icon.getImage();
			fenster.setIconImage(img);

		} catch (Exception e) {

			System.err.println("Pfad zum Bild ungültig");

		}
	}

	private void initializeColors() {
		zulassigeFarben = new Color[13];
		zulassigeFarben[0] = Color.BLACK;
		zulassigeFarben[1] = Color.BLUE;
		zulassigeFarben[2] = Color.CYAN;
		zulassigeFarben[3] = Color.DARK_GRAY;
		zulassigeFarben[4] = Color.GRAY;
		zulassigeFarben[5] = Color.GREEN;
		zulassigeFarben[6] = Color.LIGHT_GRAY;
		zulassigeFarben[7] = Color.MAGENTA;
		zulassigeFarben[8] = Color.ORANGE;
		zulassigeFarben[9] = Color.PINK;
		zulassigeFarben[10] = Color.RED;
		zulassigeFarben[11] = Color.WHITE;
		zulassigeFarben[12] = Color.YELLOW;
	}

	/**
	 * <p>
	 * Setzen der Zeichenfarbe fï¿½r die Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Alles, was im folgenden auf der Zeichenflï¿½che gezeichnet wird, wird in der
	 * hier gesetzten Farbe gezeichnet.
	 * </p>
	 * <p>
	 * Um auf die ursprï¿½ngliche Farbe (meist "BLACK") zurï¿½ckzustellen, muss die
	 * resetFarbe() Methode aufgerufen werden.
	 * </p>
	 * <p>
	 * Zulï¿½ssige Farbennamen kï¿½nnen in der Java-API in der Klasse "Color"
	 * nachgesehen werden.
	 * </p>
	 *
	 * @param farbeString Der Farbenname als String (z.B. "YELLOW", "RED", usw.)
	 */
	// The use of this color system is NOT thread safe!
	// For thread safety use setzeXXXX Methods with method header including id and
	// color!
	public void setzeFarbe(String farbeString) {

		for (int i = 0; i < zulassigeFarbenString.length; i++) {
			if (farbeString.equals(zulassigeFarbenString[i])) {
				aktuelleFarbe = zulassigeFarben[i];
			}
		}
	}

	/**
	 * <p>
	 * Zurï¿½cksetzen der Zeichenfarbe fï¿½r die Zeichenflï¿½che auf den
	 * Standardwert.
	 * </p>
	 * <p>
	 * (Meist "BLACK")
	 * </p>
	 */
	public void resetFarbe() {

		aktuelleFarbe = defaultFarbe;
	}

	/**
	 * <p>
	 * Setzen eines Punktes auf der Zeichenflï¿½che.
	 * </p>
	 *
	 * @param id                  Die ID des Punktes. Anhand der ID kann der Punkt
	 *                            spï¿½ter gelï¿½scht werden.
	 * @param gegebenexKoordinate Die X-Koordinate, auf die der Punkt auf der
	 *                            Zeichenflï¿½che gesetzt werden soll.
	 * @param gegebeneyKoordinate Die Y-Koordinate, auf die der Punkt auf der
	 *                            Zeichenflï¿½che gesetzt werden soll.
	 */
	public void setzePunkt(int id, double gegebenexKoordinate, double gegebeneyKoordinate) {

		Punkt neuerPunkt = new Punkt(id, gegebenexKoordinate, gegebeneyKoordinate, aktuelleFarbe);

		renderEngine.hinzufuegen(neuerPunkt);
	}

	/**
	 * <p>
	 * Setzen eines Punktes auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Der Punkt ist wieder lï¿½schbar. Lï¿½schen mit der loeschen(int id) Methode.
	 * </p>
	 *
	 * @param id                  Die ID des Punktes. Anhand der ID kann der Punkt
	 *                            spï¿½ter gelï¿½scht werden.
	 * @param gegebenexKoordinate Die X-Koordinate, auf die der Punkt auf der
	 *                            Zeichenflï¿½che gesetzt werden soll.
	 * @param gegebeneyKoordinate Die Y-Koordinate, auf die der Punkt auf der
	 *                            Zeichenflï¿½che gesetzt werden soll.
	 * @param farbe               Die Farbe, mit der der Punkt auf der
	 *                            Zeichenflï¿½che gesetzt werden soll (z.B.
	 *                            Color.BLACK oder Color.RED).
	 */
	public void setzePunkt(int id, double gegebenexKoordinate, double gegebeneyKoordinate, Color farbe) {

		Punkt neuerPunkt = new Punkt(id, gegebenexKoordinate, gegebeneyKoordinate, farbe);

		renderEngine.hinzufuegen(neuerPunkt);
	}

	/**
	 * <p>
	 * Setzen einer Linie auf der Zeichenflï¿½che.
	 * </p>
	 *
	 * @param x1 Die X-Koordinate des Ausgangspunktes.
	 * @param y1 Die Y-Koordinate des Ausgangspunktes.
	 * @param x2 Die X-Koordinate des Zielpunktes.
	 * @param y2 Die Y-Koordinate des Zielpunktes.
	 */
	public void setzeLinie(int id, double x1, double y1, double x2, double y2) {

		Linie neueLinie = new Linie(id, x1, y1, x2, y2, aktuelleFarbe);

		renderEngine.hinzufuegen(neueLinie);
	}

	/**
	 * <p>
	 * Setzen einer Linie auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Die Linie ist wieder lï¿½schbar. Lï¿½schen mit der loeschen(int id) Methode.
	 * </p>
	 *
	 * @param id    Die ID der Linie. Anhand der ID kann die Linie spï¿½ter
	 *              gelï¿½scht werden.
	 * @param x1    Die X-Koordinate des Ausgangspunktes.
	 * @param y1    Die Y-Koordinate des Ausgangspunktes.
	 * @param x2    Die X-Koordinate des Zielpunktes.
	 * @param y2    Die Y-Koordinate des Zielpunktes.
	 * @param farbe Die Farbe, mit der die Linie auf der Zeichenflï¿½che gesetzt
	 *              werden soll (z.B. Color.BLACK oder Color.RED).
	 */
	public void setzeLinie(int id, double x1, double y1, double x2, double y2, Color farbe) {

		Linie neueLinie = new Linie(id, x1, y1, x2, y2, farbe);

		renderEngine.hinzufuegen(neueLinie);
	}

	/**
	 * <p>
	 * Zeichnen einer Elipse auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Es wird eine Elipse gezeichnet, der genau in das Rechteck paï¿½t, welches
	 * durch die Koordinaten definiert wird. Die Koordinaten geben die linke obere
	 * Ecke und die Breite und Hï¿½he des Rechtecks an.
	 * </p>
	 * <p>
	 * Werden die Koordinaten so gesetzt, dass das Rechteck ein Quadrat ist, so wird
	 * ein Kreis gezeichnet.
	 * </p>
	 *
	 * @param x      Die X-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param y      Die Y-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param breite Die Breite des Rechtecks.
	 * @param hoehe  Die Hï¿½he des Rechtecks.
	 */
	public void setzeElipse(int id, double x, double y, double breite, double hoehe) {

		Elipse neueElipse = new Elipse(id, x, y, breite, hoehe, aktuelleFarbe);

		renderEngine.hinzufuegen(neueElipse);
	}

	/**
	 * <p>
	 * Zeichnen einer Elipse auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Es wird eine Elipse gezeichnet, der genau in das Rechteck paï¿½t, welches
	 * durch die Koordinaten definiert wird. Die Koordinaten geben die linke obere
	 * Ecke und die Breite und Hï¿½he des Rechtecks an.
	 * </p>
	 * <p>
	 * Werden die Koordinaten so gesetzt, dass das Rechteck ein Quadrat ist, so wird
	 * ein Kreis gezeichnet.
	 * </p>
	 * <p>
	 * Die Elipse ist wieder lï¿½schbar. Lï¿½schen mit der loeschen(int id) Methode.
	 * </p>
	 *
	 * @param id     Die ID der Elipse. Anhand der ID kann die Elipse spï¿½ter
	 *               gelï¿½scht werden.
	 * @param x      Die X-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param y      Die Y-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param breite Die Breite des Rechtecks.
	 * @param hoehe  Die Hï¿½he des Rechtecks.
	 * @param farbe  Die Farbe, mit der die Linie auf der Zeichenflï¿½che gesetzt
	 *               werden soll (z.B. Color.BLACK oder Color.RED).
	 */
	public void setzeElipse(int id, double x, double y, double breite, double hoehe, Color farbe) {

		Elipse neueElipse = new Elipse(id, x, y, breite, hoehe, farbe);

		renderEngine.hinzufuegen(neueElipse);
	}

	/**
	 * <p>
	 * Zeichnen einer ausgefï¿½llten Elipse auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Es wird eine Elipse gezeichnet, der genau in das Rechteck paï¿½t, welches
	 * durch die Koordinaten definiert wird. Die Koordinaten geben die linke obere
	 * Ecke und die Breite und Hï¿½he des Rechtecks an.
	 * </p>
	 * <p>
	 * Werden die Koordinaten so gesetzt, dass das Rechteck ein Quadrat ist, so wird
	 * ein Kreis gezeichnet.
	 * </p>
	 *
	 * @param x      Die X-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param y      Die Y-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param breite Die Breite des Rechtecks.
	 * @param hoehe  Die Hï¿½he des Rechtecks.
	 */
	public void setzeGefuellteElipse(int id, double x, double y, double breite, double hoehe) {

		GefuellteElipse neueGefuellteElipse = new GefuellteElipse(id, x, y, breite, hoehe, aktuelleFarbe);

		renderEngine.hinzufuegen(neueGefuellteElipse);
	}

	/**
	 * <p>
	 * Zeichnen einer ausgefï¿½llten Elipse auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Es wird eine Elipse gezeichnet, der genau in das Rechteck paï¿½t, welches
	 * durch die Koordinaten definiert wird. Die Koordinaten geben die linke obere
	 * Ecke und die Breite und Hï¿½he des Rechtecks an.
	 * </p>
	 * <p>
	 * Werden die Koordinaten so gesetzt, dass das Rechteck ein Quadrat ist, so wird
	 * ein Kreis gezeichnet.
	 * </p>
	 * <p>
	 * Die Elipse ist wieder lï¿½schbar. Lï¿½schen mit der loeschen(int id) Methode.
	 * </p>
	 *
	 * @param id     Die ID der Elipse. Anhand der ID kann die Elipse spï¿½ter
	 *               gelï¿½scht werden.
	 * @param x      Die X-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param y      Die Y-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param breite Die Breite des Rechtecks.
	 * @param hoehe  Die Hï¿½he des Rechtecks.
	 * @param farbe  Die Farbe, mit der die Elipse auf der Zeichenflï¿½che gesetzt
	 *               werden soll (z.B. Color.BLACK oder Color.RED).
	 */
	public void setzeGefuellteElipse(int id, double x, double y, double breite, double hoehe, Color farbe) {

		GefuellteElipse neueGefuellteElipse = new GefuellteElipse(id, x, y, breite, hoehe, farbe);

		renderEngine.hinzufuegen(neueGefuellteElipse);
	}

	/**
	 * <p>
	 * Zeichnen eines Rechtecks auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Es wird ein Rechteck gezeichnet. Die Koordinaten geben die linke obere Ecke
	 * des Rechtecks an.
	 * </p>
	 *
	 * @param x      Die X-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param y      Die Y-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param breite Die Breite des Rechtecks.
	 * @param hoehe  Die Hï¿½he des Rechtecks.
	 */
	public void setzeRechteck(int id, double x, double y, double breite, double hoehe) {

		Rechteck neuesRechteck = new Rechteck(id, x, y, breite, hoehe, aktuelleFarbe);

		renderEngine.hinzufuegen(neuesRechteck);
	}

	/**
	 * <p>
	 * Zeichnen eines Rechtecks auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Es wird ein Rechteck gezeichnet. Die Koordinaten geben die linke obere Ecke
	 * des Rechtecks an.
	 * </p>
	 * <p>
	 * Das Rechteck ist wieder lï¿½schbar. Lï¿½schen mit der loeschen(int id)
	 * Methode.
	 * </p>
	 *
	 * @param id     Die ID des Rechtecks. Anhand der ID kann das Rechteck spï¿½ter
	 *               gelï¿½scht werden.
	 * @param x      Die X-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param y      Die Y-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param breite Die Breite des Rechtecks.
	 * @param hoehe  Die Hï¿½he des Rechtecks.
	 * @param farbe  Die Farbe, mit der das Rechteck auf der Zeichenflï¿½che gesetzt
	 *               werden soll (z.B. Color.BLACK oder Color.RED).
	 */
	public void setzeRechteck(int id, double x, double y, double breite, double hoehe, Color farbe) {

		Rechteck neuesRechteck = new Rechteck(id, x, y, breite, hoehe, farbe);

		renderEngine.hinzufuegen(neuesRechteck);
	}

	/**
	 * <p>
	 * Zeichnen eines ausgefuellten Rechtecks auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Es wird ein Rechteck gezeichnet. Die Koordinaten geben die linke obere Ecke
	 * des Rechtecks an.
	 * </p>
	 *
	 * @param x      Die X-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param y      Die Y-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param breite Die Breite des Rechtecks.
	 * @param hoehe  Die Hï¿½he des Rechtecks.
	 */
	public void setzeGefuelltesRechteck(int id, double x, double y, double breite, double hoehe) {

		GefuelltesRechteck neuesGefuelltesRechteck = new GefuelltesRechteck(id, x, y, breite, hoehe, aktuelleFarbe);

		renderEngine.hinzufuegen(neuesGefuelltesRechteck);
	}

	/**
	 * <p>
	 * Zeichnen eines ausgefuellten Rechtecks auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Es wird ein Rechteck gezeichnet. Die Koordinaten geben die linke obere Ecke
	 * des Rechtecks an.
	 * </p>
	 * <p>
	 * Das Rechteck ist wieder lï¿½schbar. Lï¿½schen mit der loeschen(int id)
	 * Methode.
	 * </p>
	 *
	 * @param id     Die ID des Rechtecks. Anhand der ID kann das Rechteck spï¿½ter
	 *               gelï¿½scht werden.
	 * @param x      Die X-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param y      Die Y-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param breite Die Breite des Rechtecks.
	 * @param hoehe  Die Hï¿½he des Rechtecks.
	 * @param farbe  Die Farbe, mit der das Rechteck auf der Zeichenflï¿½che gesetzt
	 *               werden soll (z.B. Color.BLACK oder Color.RED).
	 */
	public void setzeGefuelltesRechteck(int id, double x, double y, double breite, double hoehe, Color farbe) {

		GefuelltesRechteck neuesGefuelltesRechteck = new GefuelltesRechteck(id, x, y, breite, hoehe, farbe);

		renderEngine.hinzufuegen(neuesGefuelltesRechteck);
	}

	/**
	 * <p>
	 * Zeichnen von Text auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Der Text ist wieder lï¿½schbar. Lï¿½schen mit der loeschen(int id) Methode.
	 * </p>
	 *
	 * @param id      Die ID des Textes. Anhand der ID kann der Text spï¿½ter
	 *                gelï¿½scht werden.
	 * @param x       Die X-Koordinate der linken Seite des Textes.
	 * @param y       Die Y-Koordinate der Grundlinie des Textes.
	 * @param groesse Die Grï¿½ï¿½e der Buchstaben.
	 */
	public void setzeText(int id, String text, double x, double y, int groesse) {

		Text neuerText = new Text(id, text, x, y, groesse, aktuelleFarbe);

		renderEngine.hinzufuegen(neuerText);
	}

	/**
	 * <p>
	 * Zeichnen von Text auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Der Text ist wieder lï¿½schbar. Lï¿½schen mit der loeschen(int id) Methode.
	 * </p>
	 *
	 * @param id      Die ID des Textes. Anhand der ID kann der Text spï¿½ter
	 *                gelï¿½scht werden.
	 * @param x       Die X-Koordinate der linken Seite des Textes.
	 * @param y       Die Y-Koordinate der Grundlinie des Textes.
	 * @param groesse Die Grï¿½ï¿½e der Buchstaben.
	 * @param farbe   Die Farbe, mit der der Text auf der Zeichenflï¿½che gesetzt
	 *                werden soll (z.B. Color.BLACK oder Color.RED).
	 */
	public void setzeText(int id, String text, double x, double y, double groesse, Color farbe) {

		Text neuerText = new Text(id, text, x, y, groesse, farbe);

		renderEngine.hinzufuegen(neuerText);
	}

	/**
	 * <p>
	 * Zeichnen eines Bildes auf der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Es wird ein Bild gezeichnet. Die Koordinaten geben die linke obere Ecke des
	 * Bildes an.
	 * </p>
	 * <p>
	 * Das Bild wird so skaliert, dass es in die Vorgegebene Breite und Hï¿½he
	 * passt.
	 * </p>
	 * <p>
	 * Das Bild ist wieder lï¿½schbar. Lï¿½schen mit der loeschen(int id) Methode.
	 * </p>
	 *
	 * @param id     Die ID des Rechtecks. Anhand der ID kann das Rechteck spï¿½ter
	 *               gelï¿½scht werden.
	 * @param bild   Das Bild, dass gezeichnet werden soll.
	 * @param x      Die X-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param y      Die Y-Koordinate der linken oberen Ecke des Rechtecks.
	 * @param breite Die Breite des Rechtecks.
	 * @param hoehe  Die Hï¿½he des Rechtecks.
	 */
	public void setzeBild(int id, Image bild, double x, double y, double breite, double hoehe) {

		Bild neuesBild = new Bild(id, bild, x, y, breite, hoehe);

		renderEngine.hinzufuegen(neuesBild);
	}

	/**
	 * <p>
	 * ï¿½ndert die Position aller Objekte mit einer bestimmten ID auf der
	 * Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Werden mehreren Objekten die gleiche ID zugewiesen, so ist es mï¿½glich diese
	 * Objekte als Gruppe auf einmal zu bewegen.
	 * </p>
	 *
	 * @param id     Die ID des zu lï¿½schenden Objektes.
	 * @param deltaX Anzahl Pixel um die das Objekt verschoben wird.
	 * @param deltaY Anzahl Pixel um die das Objekt verschoben wird.
	 */
	public void verschieben(int id, double deltaX, double deltaY) {

		renderEngine.verschieben(id, deltaX, deltaY);
	}

	/**
	 * <p>
	 * Lï¿½schen aller Objekte mit einer bestimmten ID von der Zeichenflï¿½che.
	 * </p>
	 * <p>
	 * Werden mehreren Objekten die gleiche ID zugewiesen, so ist es mï¿½glich diese
	 * Objekte als Gruppe auf einmal zu lï¿½schen.
	 * </p>
	 *
	 * @param id Die ID des zu lï¿½schenden Objektes.
	 */
	public void loeschen(int id) {

		renderEngine.loeschen(id);
	}

	public void manualPaint() {

		if (renderThread != null && renderEngine != null) {

			renderThread.execute(renderEngine);

		}
	}

	@Override
	public void paintComponent(Graphics graphic) {

		if (renderThread != null && renderEngine != null) {

			renderThread.execute(renderEngine);

		}
	}

// Innere Klassen.

	class Renderer implements Runnable {

		// Attribute

		// Zeigt an, ob ein Rendering notwendig ist. Das ist z.B. der Fall, wenn neue
		// Objekte zum Zeichnen zugefï¿½hrt oder gelï¿½scht
		// wurden.
		private boolean renderingNotwendig = true;

		private ZeichenFlaeche15 zeichenFlaechenPanel = null;

		public volatile BufferedImage bild = null;

		// Konstruktoren

		public Renderer(ZeichenFlaeche15 zeichenFlaeche15) {

			this.zeichenFlaechenPanel = zeichenFlaeche15;
		}

		// Methoden
		@Override
		public void run() {

			// Nur zeichnen, wenn zeichenFlaechenPanel existiert und eine Grï¿½ï¿½e hat.
			if (zeichenFlaechenPanel != null
					&& zeichenFlaechenPanel.getWidth() * zeichenFlaechenPanel.getHeight() != 0) {

				if (renderingNotwendig) {

					render();
				}

				Graphics grafik = zeichenFlaechenPanel.getGraphics();

				if (grafik != null) {

					synchronized (this) {

						grafik.drawImage(renderEngine.bild, 0, 0, null);
					}
					java.awt.Toolkit.getDefaultToolkit().sync();
				}
			}
		}

		@SuppressWarnings("unchecked")
		private void render() {

			// Hier wird erst eine Kopie der Objektliste gemacht. Im Weiteren wird die Kopie
			// zum Zeichnen verwendet
			// und die setzexxxx() Methoden kï¿½nnen mit der Originalliste gleichzeitig
			// weiterarbeiten.
			ArrayList<ZuZeichnendesObjekt> tempZuZeichnendeObjekte = null;
			synchronized (this) {
				tempZuZeichnendeObjekte = (ArrayList<ZuZeichnendesObjekt>) zuZeichnendeObjekteListe.clone();
			}

			int anzahlObjekte = tempZuZeichnendeObjekte.size();

			// Neue graphics erstellen um in ihr zu zeichnen.
			BufferedImage neuesBild = new BufferedImage(zeichenFlaechenPanel.getWidth(),
					zeichenFlaechenPanel.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D neueGraphics = neuesBild.createGraphics();

			neueGraphics.setColor(aktuelleHintergrundfarbe);
			neueGraphics.fillRect(0, 0, neuesBild.getWidth(), neuesBild.getHeight());

			// Zeichne alle Objekte in der Objekteliste.
			for (int objekteZahler = 0; objekteZahler < anzahlObjekte; objekteZahler = objekteZahler + 1) {
				ZuZeichnendesObjekt aktuellesObjekt = tempZuZeichnendeObjekte.get(objekteZahler);
				aktuellesObjekt.zeichne(neueGraphics);
			}

			// Nach der Arbeit das alte Bild mit dem neuen Bild ï¿½berschreiben.
			synchronized (this) {

				bild = neuesBild;
			}

			renderingNotwendig = false;
		}

		public void setRendernIstNotwendig() {
			renderingNotwendig = true;
		}

		public void loeschen(int id) {

			ZuZeichnendesObjekt objekt;
			int objektId = 0;
			int objektIndex = 0;

			synchronized (this) {

				objektIndex = sucheErstenIndexInListe(id);

				// Wenn die suchen-Methode -1 zurï¿½ckgegeben hat, gibt es die gewï¿½nschte id
				// nicht
				// in der Liste.
				if (objektIndex == -1)
					return;

				while (objektIndex < zuZeichnendeObjekteListe.size()) {
					objekt = zuZeichnendeObjekteListe.get(objektIndex);
					objektId = objekt.getId();

					// Beim Lï¿½schen ist zu beachten:
					// Wenn ein Objekt gelï¿½scht wird, dann "rutschen" alle Objekte in der
					// ArrayList
					// einen Platz nach "oben".
					// Daher muss objektIndex auf dem selben Wert BLEIBEN.
					// Nur wenn KEIN Objekt gelï¿½scht wird, dann muss objektIndex einen Schritt
					// weiter gezï¿½hlt werden.
					if (objektId == id) {
						zuZeichnendeObjekteListe.remove(objektIndex);
					} else {
						objektIndex = objektIndex + 1;
					}
				}
			}

			renderingNotwendig = true;
		}

		public void verschieben(int id, double deltaX, double deltaY) {

			synchronized (this) {

				int objektIndex = sucheErstenIndexInListe(id);

				// Wenn die suchen-Methode -1 zurï¿½ckgegeben hat, gibt es die gewï¿½nschte id
				// nicht
				// in der Liste.
				if (objektIndex == -1)
					return;

				// Sonst verschieben.
				zuZeichnendeObjekteListe.get(objektIndex).verschieben(deltaX, deltaY);
			}

			renderingNotwendig = true;
		}

		// Ein Objekt wird an der richtigen Stelle in die ArrayList eingefï¿½gt.
		private synchronized void hinzufuegen(ZuZeichnendesObjekt zuZeichnendesObjekt) {

			int objektID = zuZeichnendesObjekt.getId();

			int tokenPosition = 0;
			int untereGrenze = 0;
			int obereGrenze = zuZeichnendeObjekteListe.size() - 1;

			int linkesObjektID = 0;
			int rechtesObjektID = 0;

			boolean hinzugefuegt = false;

			// Falls Liste noch leer, dann einfach einfï¿½gen.
			if (obereGrenze < 0) {
				zuZeichnendeObjekteListe.add(zuZeichnendesObjekt);
				hinzugefuegt = true;
			}

			// Falls nur ein Objekt in der Liste ist, dann einfaches vorher/nachher
			// einfï¿½gen.
			if (!hinzugefuegt) {
				if (obereGrenze == 0) {
					if (objektID >= zuZeichnendeObjekteListe.get(0).getId()) {

						zuZeichnendeObjekteListe.add(zuZeichnendesObjekt);

					} else {

						zuZeichnendeObjekteListe.add(0, zuZeichnendesObjekt);
					}
					hinzugefuegt = true;
				}
			}

			// Falls die Liste nicht leer ist, mit Teilmengenbildung (immer die Hï¿½lfte)
			// einfï¿½gen.
			while (!hinzugefuegt && (untereGrenze < obereGrenze)) {

				tokenPosition = ((obereGrenze - untereGrenze) / 2) + untereGrenze;

				linkesObjektID = zuZeichnendeObjekteListe.get(tokenPosition).getId();
				rechtesObjektID = zuZeichnendeObjekteListe.get(tokenPosition + 1).getId();

				if (linkesObjektID <= objektID) {

					if (objektID < rechtesObjektID) {

						zuZeichnendeObjekteListe.add(tokenPosition + 1, zuZeichnendesObjekt);
						hinzugefuegt = true;

					} else {
						untereGrenze = tokenPosition + 1;
					}

				} else {

					obereGrenze = tokenPosition;
				}
			}

			// Wenn in der obigen Schleife nichts gefunden wurde, sind hier obere und untere
			// Grenze gleich.
			// Jetzt gibt es nur noch zwei Mï¿½glchkeiten, rechts oder links der Grenze.
			if (!hinzugefuegt) {
				if (objektID >= zuZeichnendeObjekteListe.get(obereGrenze).getId()) {

					zuZeichnendeObjekteListe.add(obereGrenze + 1, zuZeichnendesObjekt);

				} else {

					zuZeichnendeObjekteListe.add(obereGrenze, zuZeichnendesObjekt);

				}
			}

			// Rendern ist notwendig, wenn hinzugefï¿½gt wurde.
			renderingNotwendig = true;
		}

		// Gibt die Position des ERSTEN Objekts mit der angegebenen id zurï¿½ck.
		// Gibt -1 zurï¿½ck, wenn es kein Objekt mit der id in der Liste gibt.
		private int sucheErstenIndexInListe(int id) {

			int objektId = 0;
			int objektIndex = 0;

			int anzahlObjekte = zuZeichnendeObjekteListe.size();

			// Wenn die Liste relativ kurz ist, dann linear von vorne suchen.
			if (anzahlObjekte < 20) {
				while (objektIndex < anzahlObjekte) {

					objektId = zuZeichnendeObjekteListe.get(objektIndex).getId();

					// Da die ArrayList sortiert ist, kann nach dem ersten Objekt mit einer
					// grï¿½ï¿½eren objektID abgebrochen werden. Die id kann noch nicht in der Liste
					// sein.
					if (objektId > id) {
						return -1;
					}

					if (objektId == id) {
						return objektIndex;
					}

					objektIndex = objektIndex + 1;
				}

				return -1;

			}
			// Wenn die Liste lï¿½nger ist, dann lieber mit dem Teilungsverfahren
			// irgendeinen
			// Eintrag finden
			// und danach von dort aus den linkesten Eintrag finden.
			else {

				objektIndex = sucheIrgendeinenIndexInListe(id);

				// Wenn bereits die vorherie Suche nichts gefunden hat, brauchen wir nicht
				// weiter suchen.
				if (objektIndex == -1) {
					return -1;
				}

				// Wenn id prinzipiell vorhanden ist, nach links gehen und weiter suchen.
				// Wenn wir ganz links angekommen sind, war unsere id der erste Eintrag.
				while (objektIndex > 0) {

					// Links von uns prï¿½fen.
					objektId = zuZeichnendeObjekteListe.get(objektIndex - 1).getId();

					// Wenn links von uns nicht die gleiche id hat, dann sind wir der erste Eintrag
					// da die Liste ja als sortiert vorausgesetzt wird.
					if (objektId != id) {
						return objektIndex;
					}

					objektIndex = objektIndex - 1;
				}

				return 0;
			}
		}

		// Sucht nach der angegebenen id in der Liste. Gibt die Position des ersten
		// Objekts zurï¿½ck, das
		// gefunden wird. Die Position des Objekts in einer Gruppe von Objekten mit der
		// gleichen id
		// wird nicht beachtet. Irgendein (zufï¿½llig) gefundenes Objekt mit der
		// gegebenen
		// id wird genommen.
		private int sucheIrgendeinenIndexInListe(int id) {

			int tokenPosition = 0;
			int untereGrenze = 0;
			int obereGrenze = zuZeichnendeObjekteListe.size() - 1;

			int linkesObjektID = 0;
			int rechtesObjektID = 0;

			// Falls Liste leer ist, dann -1 returnieren.
			if (obereGrenze < 0) {
				return -1;
			}
			// Falls in der Liste nur 1 Objekt ist, dann prï¿½fen ob es die selbe id hat,
			// wenn ja, fertig, wenn nein, dann gibt es die id nicht in der Liste.
			if (obereGrenze == 0) {
				if (id == zuZeichnendeObjekteListe.get(0).getId()) {
					return 0;
				} else {
					return -1;
				}
			}

			// Falls die Liste nicht leer ist, mit Teilmengenbildung (immer die Hï¿½lfte)
			// suchen.
			while (untereGrenze != obereGrenze) {

				tokenPosition = ((obereGrenze - untereGrenze) / 2) + untereGrenze;

				linkesObjektID = zuZeichnendeObjekteListe.get(tokenPosition).getId();
				rechtesObjektID = zuZeichnendeObjekteListe.get(tokenPosition + 1).getId();

				// Falls die id zur Linken oder zur Rechten stimmt, dann diese Position
				// zurï¿½ckgeben.
				if (id == linkesObjektID)
					return tokenPosition;
				if (id == rechtesObjektID)
					return (tokenPosition + 1);

				if (linkesObjektID < id) {

					// Falls die objektID zwischen der Linken und der Rechten ist, dann kann sie
					// nicht in
					// der Liste sein. Also -1 zurï¿½ckgeben.
					if (id < rechtesObjektID) {

						return -1;

					} else {

						untereGrenze = tokenPosition + 1;
					}

				} else {

					obereGrenze = tokenPosition;
				}
			}

			return -1;
		}
	}

	abstract class ZuZeichnendesObjekt {
		abstract void zeichne(Graphics g);

		abstract void verschieben(double deltaX, double deltaY);

		abstract int getId();
	}

	class Punkt extends ZuZeichnendesObjekt {

		// Instanzvariablen.
		int id = 0;
		double x = 0;
		double y = 0;
		Color farbe = null;

		// Konstruktoren.
		// Wenn die id -1 ist, dann ist das Objekt nicht lï¿½schbar.
		Punkt(int id, double x, double y, Color farbe) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.farbe = farbe;
		}

		// Zeichnet sich selbst.
		@Override
		void zeichne(Graphics zeichenFlaechenGraphics) {
			zeichenFlaechenGraphics.setColor(farbe);
			zeichenFlaechenGraphics.fillRect((int) x, (int) y, 1, 1);
		}

		// ï¿½ndert seine Position.
		@Override
		void verschieben(double deltaX, double deltaY) {

			x = x + deltaX;
			y = x + deltaY;
		}

		// get-Methoden.
		@Override
		int getId() {
			return this.id;
		}
	}

	class Linie extends ZuZeichnendesObjekt {

		// Instanzvariablen.
		int id = 0;
		double x1 = 0;
		double y1 = 0;
		double x2 = 0;
		double y2 = 0;
		Color farbe = null;

		// Konstruktoren.
		// Wenn die id -1 ist, dann ist das Objekt nicht lï¿½schbar.
		Linie(int id, double x1, double y1, double x2, double y2, Color farbe) {
			this.id = id;
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.farbe = farbe;
		}

		// Zeichnet sich selbst.
		@Override
		void zeichne(Graphics zeichenFlaechenGraphics) {
			zeichenFlaechenGraphics.setColor(farbe);
			zeichenFlaechenGraphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
		}

		// ï¿½ndert seine Position.
		@Override
		void verschieben(double deltaX, double deltaY) {

			x1 = x1 + deltaX;
			y1 = y1 + deltaY;
			x2 = x2 + deltaX;
			y2 = y2 + deltaY;
		}

		// get-Methoden.
		@Override
		int getId() {
			return this.id;
		}
	}

	class Elipse extends ZuZeichnendesObjekt {

		// Instanzvariablen.
		int id = 0;
		double x = 0;
		double y = 0;
		double breite = 0;
		double hoehe = 0;
		Color farbe = null;

		// Konstruktoren.
		// Wenn die id -1 ist, dann ist das Objekt nicht lï¿½schbar.
		Elipse(int id, double x, double y, double breite, double hoehe, Color farbe) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.breite = breite;
			this.hoehe = hoehe;
			this.farbe = farbe;
		}

		// ï¿½ndert seine Position.
		@Override
		void verschieben(double deltaX, double deltaY) {

			x = x + deltaX;
			y = y + deltaY;
		}

		// Zeichnet sich selbst.
		@Override
		void zeichne(Graphics zeichenFlaechenGraphics) {
			zeichenFlaechenGraphics.setColor(farbe);
			zeichenFlaechenGraphics.drawOval((int) x, (int) y, (int) breite, (int) hoehe);
		}

		// get-Methoden.
		@Override
		int getId() {
			return this.id;
		}
	}

	class GefuellteElipse extends ZuZeichnendesObjekt {

		// Instanzvariablen.
		int id = 0;
		double x = 0;
		double y = 0;
		double breite = 0;
		double hoehe = 0;
		Color farbe = null;

		// Konstruktoren.
		// Wenn die id -1 ist, dann ist das Objekt nicht lï¿½schbar.
		GefuellteElipse(int id, double x, double y, double breite, double hoehe, Color farbe) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.breite = breite;
			this.hoehe = hoehe;
			this.farbe = farbe;
		}

		// ï¿½ndert seine Position.
		@Override
		void verschieben(double deltaX, double deltaY) {

			x = x + deltaX;
			y = y + deltaY;
		}

		// Zeichnet sich selbst.
		@Override
		void zeichne(Graphics zeichenFlaechenGraphics) {
			zeichenFlaechenGraphics.setColor(farbe);
			zeichenFlaechenGraphics.fillOval((int) x, (int) y, (int) breite, (int) hoehe);
		}

		// get-Methoden.
		@Override
		int getId() {
			return this.id;
		}
	}

	class Rechteck extends ZuZeichnendesObjekt {

		// Instanzvariablen.
		int id = 0;
		double x = 0;
		double y = 0;
		double breite = 0;
		double hoehe = 0;
		Color farbe = null;

		// Konstruktoren.
		// Wenn die id -1 ist, dann ist das Objekt nicht lï¿½schbar.
		Rechteck(int id, double x, double y, double breite, double hoehe, Color farbe) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.breite = breite;
			this.hoehe = hoehe;
			this.farbe = farbe;
		}

		// ï¿½ndert seine Position.
		@Override
		void verschieben(double deltaX, double deltaY) {

			x = x + deltaX;
			y = y + deltaY;
		}

		// Zeichnet sich selbst.
		@Override
		void zeichne(Graphics zeichenFlaechenGraphics) {
			zeichenFlaechenGraphics.setColor(farbe);
			zeichenFlaechenGraphics.drawRect((int) x, (int) y, (int) breite, (int) hoehe);
		}

		// get-Methoden.
		@Override
		int getId() {
			return this.id;
		}
	}

	class GefuelltesRechteck extends ZuZeichnendesObjekt {

		// Instanzvariablen.
		int id = 0;
		double x = 0;
		double y = 0;
		double breite = 0;
		double hoehe = 0;
		Color farbe = null;

		// Konstruktoren.
		// Wenn die id -1 ist, dann ist das Objekt nicht lï¿½schbar.
		GefuelltesRechteck(int id, double x, double y, double breite, double hoehe, Color farbe) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.breite = breite;
			this.hoehe = hoehe;
			this.farbe = farbe;
		}

		// ï¿½ndert seine Position.
		@Override
		void verschieben(double deltaX, double deltaY) {

			x = x + deltaX;
			y = y + deltaY;
		}

		// Zeichnet sich selbst.
		@Override
		void zeichne(Graphics zeichenFlaechenGraphics) {
			zeichenFlaechenGraphics.setColor(farbe);
			zeichenFlaechenGraphics.fillRect((int) x, (int) y, (int) breite, (int) hoehe);
		}

		// get-Methoden.
		@Override
		int getId() {
			return this.id;
		}
	}

	class Text extends ZuZeichnendesObjekt {

		// Instanzvariablen.
		int id = 0;
		String text = null;
		double x = 0;
		double y = 0;
		double groesse = 0;
		Color farbe = null;

		// Konstruktoren.
		// Wenn die id -1 ist, dann ist das Objekt nicht lï¿½schbar.
		Text(int id, String text, double x, double y, double groesse, Color farbe) {
			this.id = id;
			this.text = text;
			this.x = x;
			this.y = y;
			this.groesse = groesse;
			this.farbe = farbe;
		}

		// ï¿½ndert seine Position.
		@Override
		void verschieben(double deltaX, double deltaY) {

			x = x + deltaX;
			y = y + deltaY;
		}

		// Zeichnet sich selbst.
		@Override
		void zeichne(Graphics zeichenFlaechenGraphics) {
			zeichenFlaechenGraphics.setColor(farbe);

			Font altFont = zeichenFlaechenGraphics.getFont();
			Font font = new Font(altFont.getName(), altFont.getStyle(), (int) groesse);

			zeichenFlaechenGraphics.setFont(font);
			zeichenFlaechenGraphics.setColor(farbe);
			zeichenFlaechenGraphics.drawString(text, (int) x, (int) y);
		}

		// get-Methoden.
		@Override
		int getId() {
			return this.id;
		}
	}

	class Bild extends ZuZeichnendesObjekt {

		// Instanzvariablen.
		int id = 0;
		double x = 0;
		double y = 0;
		Image bild = null;
		double breite = 0;
		double hoehe = 0;

		// Konstruktoren.
		// Wenn die id -1 ist, dann ist das Objekt nicht lï¿½schbar.
		Bild(int id, Image bild, double x, double y, double breite, double hoehe) {
			this.id = id;
			this.bild = bild;
			this.x = x;
			this.y = y;
			this.breite = breite;
			this.hoehe = hoehe;
		}

		// Zeichnet sich selbst.
		@Override
		void zeichne(Graphics zeichenFlaechenGraphics) {
			zeichenFlaechenGraphics.drawImage(bild, (int) x, (int) y, (int) breite, (int) hoehe, null);
		}

		// ï¿½ndert seine Position.
		@Override
		void verschieben(double deltaX, double deltaY) {

			x = x + deltaX;
			y = y + deltaY;
		}

		// get-Methoden.
		@Override
		int getId() {
			return this.id;
		}
	}

	/**
	 * <p>
	 * Einschalten des automatischen Zeichnens.
	 * </p>
	 * <p>
	 * Mit dieser Methode wird ausserdem das Zeichenintervall geï¿½ndert, falls das
	 * automatische Zeichnen bereits eingeschaltet war.
	 * </p>
	 * <p>
	 * </p>
	 *
	 * @param intervallInNanosekunden Zeit, nach der spï¿½testens ein repaint()
	 *                                erfolgt in Nanosekunden. Hier kann man die
	 *                                minimale Frames pro Sekunde einstellen.
	 */

	public void startAutomatischesZeichnen(int intervallInNanosekunden) {

		// Die Timer-Funktion fï¿½r das zeichnen (auch wenn es nicht so aussieht...).
		timer = new ScheduledThreadPoolExecutor(1);
		timer.scheduleAtFixedRate(renderEngine, 0, 16, TimeUnit.MILLISECONDS);
	}

	/**
	 * <p>
	 * Ausschalten des automatischen Zeichnens.
	 * </p>
	 * <p>
	 * Ist das automatische Zeichnen ausgeschaltet, muss das Zeichnen "manuell"
	 * durch aufrufen der Methode repaint() erfolgen.
	 * </p>
	 * <p>
	 * </p>
	 */
	public void stopAutomatischesZeichnen() {
		timer.shutdown();
	}
}

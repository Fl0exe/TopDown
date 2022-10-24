package binaries;

import entities.Spieler;

public class Kamera {

	private Spieler fokussierterSpieler;

	private static double entityOffsetX = 0;
	private static double entityOffsetY = 0;

	private static double kameraOffsetX = 0;
	private static double kameraOffsetY = 0;

	public Kamera(Spieler fokussierterSpieler) {

		this.fokussierterSpieler = fokussierterSpieler;

		entityOffsetX = fokussierterSpieler.getX();
		entityOffsetY = fokussierterSpieler.getY();

	}

	public static double getEntityOffsetX() {
		return entityOffsetX;
	}

	public static void setEntityOffsetX(long entityOffsetX) {
		Kamera.entityOffsetX = entityOffsetX;
	}

	public static double getEntityOffsetY() {
		return entityOffsetY;
	}

	public static void setEntityOffsetY(long entityOffsetY) {
		Kamera.entityOffsetY = entityOffsetY;
	}

	public static double getKameraOffsetX() {
		return kameraOffsetX;
	}

	public static void setKameraOffsetX(int kameraOffsetX) {
		Kamera.kameraOffsetX = kameraOffsetX;
	}

	public static double getKameraOffsetY() {
		return kameraOffsetY;
	}

	public static void setKameraOffsetY(int kameraOffsetY) {
		Kamera.kameraOffsetY = kameraOffsetY;
	}

}

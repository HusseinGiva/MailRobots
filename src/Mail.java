package src;

import java.awt.Color;
import java.awt.Point;

public class Mail extends Entity {

	private static Long ID = 0L;

	private final Long id;
	private Point dest;
	private Point source;

	public Mail(Point source, Point dest, Color color) {
		super(source, color);
		this.id = Mail.ID++;
		this.dest = dest;
		this.source = source;
	}

	/*****************************
	 ***** AUXILIARY METHODS ***** 
	 *****************************/

	public void grabMail(Point newpoint) {
		Board.removeEntity(point);
		point = newpoint;
	}
	
	public void dropMail(Point newpoint) {
		Board.insertEntity(this, newpoint);
		point = newpoint;
	}

	public void moveMail(Point newpoint) {
		point = newpoint;
	}

	public double distanceToDest() {
		return this.dest.distance(this.point);
	}

	public Long getMailId() {
		return id;
	}

	public Point getMailDest() {
		return dest;
	}
}

package src;

import java.awt.Color;
import java.awt.Point;

public class Mail extends Entity {

	private static Long ID = 0L;

	private final Long id;

	public Mail(Point point, Color color) {
		super(point, color);
		this.id = Mail.ID++;
	}

	/*****************************
	 ***** AUXILIARY METHODS ***** 
	 *****************************/

	public void grabMail(Point newpoint) {
		Board.removeEntity(point);
		point = newpoint;
	}
	
	public void dropMail(Point newpoint) {
		Board.insertEntity(this,newpoint);
		point = newpoint;
	}

	public void moveMail(Point newpoint) {
		point = newpoint;
	}

	public double distanceTo(Point destination) {
		return destination.distance(this.point);
	}

	public Long getMailId() {
		return id;
	}
}

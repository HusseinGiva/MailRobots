package src;

import java.awt.Color;
import java.awt.Point;

public class Mail extends Entity {

	public Mail(Point point, Color color) {
		super(point, color);
	}
	
	/*****************************
	 ***** AUXILIARY METHODS ***** 
	 *****************************/

	public void grabBox(Point newpoint) {
		Board.removeEntity(point);
		point = newpoint;
	}
	
	public void dropBox(Point newpoint) {
		Board.insertEntity(this,newpoint);
		point = newpoint;
	}

	public void moveBox(Point newpoint) {
		point = newpoint;
	}
}

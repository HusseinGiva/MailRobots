package src;

import java.awt.Point;

public class Mail extends Thread {

	private static Long ID = 0L;

	private final Long id;
	private Point dest;
	private Point source;

	public Mail(Point source, Point dest) {
		this.id = Mail.ID++;
		this.dest = dest;
		this.source = source;
	}

	/*****************************
	 ***** AUXILIARY METHODS ***** 
	 *****************************/

	public Long getMailId() {
		return id;
	}

	public Point getMailDest() {
		return dest;
	}

	public Point getMailSource() {
		return source;
	}
}

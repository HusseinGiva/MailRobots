package src;

import java.awt.EventQueue;

/**
 * Multi-agent system creation
 * @author Rui Henriques
 */
public class Main {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				GUI frame = new GUI();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}

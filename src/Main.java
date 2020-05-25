package src;

import java.awt.EventQueue;
import java.util.Arrays;

/**
 * Multi-agent system creation
 */

public class Main {

	public static void main(String[] args) {
		int w = Integer.parseInt(args[0]);
		int m = Integer.parseInt(args[1]);
		int a = Integer.parseInt(args[2]);
		int h = Integer.parseInt(args[3]);
		try {
			GUI frame = new GUI(w,m,a,h);
			frame.setVisible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public static void main(String[] args) {
		int w = 5;
		int m = 100;
		int a = 5;
		int h = 10;
		for (int i = 1; i < 10; i++) {
			test(i, m ,a, h);
		}
	}

	private static void test(int i, int m, int a, int h) {
		EventQueue.invokeLater(() -> {
			try {
				GUI frame = new GUI(i,m,a,h);
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}*/


}

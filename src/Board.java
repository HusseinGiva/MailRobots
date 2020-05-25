package src;

import java.awt.Color;
import java.awt.Point;
import java.util.*;

import src.Agent.Action;
import src.Block.Shape;

/**
 * Environment
 */

public class Board {

	/** The environment */

	public static int nX = 15, nY = 10, WAREHOUSES, INIT_MAILS, N_ROBOTS, HOUSES;
	private static Block[][] board;
	private static Entity[][] objects;
	private static List<Agent> robots;
	private static List<Warehouse> warehouses;
	private static List<House> houses;

	/****************************
	 ***** A: SETTING BOARD *****
	 ****************************/
	public static void initialize() {
		Board.initialize(WAREHOUSES, INIT_MAILS, N_ROBOTS, HOUSES);
	}
	public static void initialize(int WAREHOUSES, int INIT_MAILS, int N_ROBOTS,int HOUSES) {
		Board.WAREHOUSES = WAREHOUSES;
		Board.INIT_MAILS = INIT_MAILS;
		Board.N_ROBOTS = N_ROBOTS;
		Board.HOUSES = HOUSES;
		/** A: create board */
		board = new Block[Board.nX][Board.nY];
		for (int i = 0; i < Board.nX; i++) {
			for(int j = 0; j < Board.nY; j++) {
				board[i][j] = new Block(Shape.free, Color.lightGray);
			}
		}
		Random rd = new Random();

		/** B: create destinations */
		houses = new ArrayList<>();
		List<Point> housePoints = generatePoints(HOUSES, null);
		for (int i = 0; i < HOUSES; i++) {
			Point point = housePoints.get(i);
			House house = new House(Shape.house, point, Color.green);
			houses.add(house);
			board[point.x][point.y] = house;
		}

		/** C: create warehouses */
		warehouses = new ArrayList<>();
		List<Point> warehousePoints = generatePoints(WAREHOUSES, new HashSet<>(housePoints));
		for (int i = 0; i < WAREHOUSES; i++) {
			Point point = warehousePoints.get(i);
			Warehouse warehouse = new Warehouse(Shape.warehouse, point, Color.red, INIT_MAILS);
			warehouses.add(warehouse);
			board[point.x][point.y] = warehouse;
		}

		/** D: create agents randomly dispersed */
		robots = new ArrayList<>();
		Set<Point> set = new HashSet<>(housePoints); set.addAll(warehousePoints);
		List<Point> agentPoints = generatePoints(N_ROBOTS, set);
		for(int j = 0; j < N_ROBOTS; j++) {
			Point point = agentPoints.get(j);
			List<Mail> mailList = new ArrayList<>();
			for (Warehouse w : warehouses) {
				mailList.addAll(w.getMailList());
			}
			robots.add(new Agent(point, Color.pink, mailList));
		}

		objects = new Entity[Board.nX][Board.nY];
		for (Agent agent : robots) {
			objects[agent.point.x][agent.point.y] = agent;
		}
	}

	private static List<Point> generatePoints(int size, Set<Point> avoid) {
		Random rd = new Random();
		Set<Point> resp = new HashSet<>();
		if (avoid == null) avoid = new HashSet<>();
		Point point;
		while (resp.size() < size) {
			point = new Point(rd.nextInt(nX), rd.nextInt(nY));
			if(!avoid.contains(point))
				resp.add(point);
		}
		return new ArrayList<>(resp);
	}

	/****************************
	 ***** B: BOARD METHODS *****
	 ****************************/
	
	public static Entity getEntity(Point point) {
		return objects[point.x][point.y];
	}
	public static List<Agent> getAgents() {
		return robots;
	}
	public static List<House> getHouses() {
		return houses;
	}
	public static Block getBlock(Point point) {
		return board[point.x][point.y];
	}
	public static void updateEntityPosition(Point point, Point newpoint) {
		objects[newpoint.x][newpoint.y] = objects[point.x][point.y];
		objects[point.x][point.y] = null;
	}

	/***********************************
	 ***** C: ELICIT AGENT ACTIONS *****
	 ***********************************/
	
	private static RunThread runThread;
	private static GUI GUI;

	public static void start() {
		run(0);
	}

	public static class RunThread extends Thread {
		
		int time;
		
		public RunThread(int time){
			this.time = time * time;
		}
		
	    public void run() {
			String stop;
			boolean end = false;
			long startTime = System.nanoTime();
	    	while (!end) {
	    		stop = step();
				if(stop.equals("STOP")) {
					end = true;
				}
				try {
					sleep(time);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
			System.out.print(System.nanoTime() - startTime);
	    	System.exit(1);
	    }
	}
	
	public static void run(int time) {
		Board.runThread = new RunThread(time);
		Board.runThread.start();
	}

	public static void reset() {
		removeObjects();
		initialize();
		GUI.displayBoard();
		displayObjects();	
		GUI.update();
	}

	public static void sendMessage(Action action, Mail ml) {
		for (Agent a : robots) {
			a.receiveMessage(action, ml);
		}
	}

	public static String step() {
		removeObjects();
		String stop = "";
		for (Agent a : robots) {
			stop = a.agentDecision();
		}
		displayObjects();
		GUI.update();
		return stop;
	}

	public static void stop() {
		runThread.interrupt();
		runThread.stop();
	}

	public static void displayObjects(){
		for (Agent agent : robots) {
			GUI.displayObject(agent);
		}
	}

	public static void removeObjects(){
		for (Agent agent : robots) {
			GUI.removeObject(agent);
		}
	}

	public static void associateGUI(GUI graphicalInterface) {
		GUI = graphicalInterface;
	}
}

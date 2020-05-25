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

	public static final int nX = 15, nY = 15, WAREHOUSES = 5, MAX_MAILS = 20, N_ROBOTS = 5, HOUSES = 15;
	private static Block[][] board;
	private static Entity[][] objects;
	private static List<Agent> robots;
	private static List<Warehouse> warehouses;
	private static List<House> houses;
	private static List<Point> housePoints;
	private static List<Point> wareHousePoints;
	private static List<Point> agentPoints;
	private static List<Point> avoid;

	/****************************
	 ***** A: SETTING BOARD *****
	 ****************************/
	
	public static void initialize() {

		/** A: create board */
		board = new Block[Board.nX][Board.nY];
		for (int i = 0; i < Board.nX; i++) {
			for(int j = 0; j < Board.nY; j++) {
				board[i][j] = new Block(Shape.free, Color.lightGray);
			}
		}



		/** B: create destinations */
		houses = new ArrayList<>();
		avoid = new ArrayList<>();
		housePoints = generatePoints(HOUSES, avoid);
		avoid.addAll(housePoints);
		for (int i = 0; i < HOUSES; i++) {
			Point point = housePoints.get(i);
			House house = new House(Shape.house, point, Color.green);
			houses.add(house);
			board[point.x][point.y] = house;
		}

		Random rd = new Random();

		/** C: create warehouses */
		warehouses = new ArrayList<>();
		wareHousePoints = generatePoints(WAREHOUSES, avoid);
		avoid.addAll(wareHousePoints);
		for (int i = 0; i < WAREHOUSES; i++) {
			Point point = wareHousePoints.get(i);
			int initMails = rd.nextInt(MAX_MAILS); // If we want an equal distribution of number of packages for each warehouse just use MAX_MAILS as argument in the next line
			System.out.println("Warehouse " + point + " has " + initMails + " packages to be delivered.");
			Warehouse warehouse = new Warehouse(Shape.warehouse, point, Color.red, initMails);
			warehouses.add(warehouse);
			board[point.x][point.y] = warehouse;
		}

		/** D: create agents randomly dispersed */
		robots = new ArrayList<>();
		agentPoints = generatePoints(N_ROBOTS, avoid);
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

	private static List<Point> generatePoints(int size, List<Point> avoid) {
		Random rd = new Random();
		List<Point> resp = new ArrayList<>();
		Point point;
		while (resp.size() < size) {
			point = new Point(rd.nextInt(nX), rd.nextInt(nY));
			if (!avoid.contains(point)) {
				resp.add(point);
				avoid.add(point);
			}
		}
		return resp;
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

	public static class RunThread extends Thread {
		
		int time;
		
		public RunThread(int time){
			this.time = time * time;
		}
		
	    public void run() {
	    	while (true) {
	    		step();
				try {
					sleep(time);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	public static void run(int time) {
		Board.runThread = new RunThread(time);
		Board.runThread.start();
	}

	public static void sendMessage(Action action, Mail ml) {
		for (Agent a : robots) {
			a.receiveMessage(action, ml);
		}
	}

	public static void step() {
		removeObjects();
		for (Agent a : robots) {
			a.agentDecision();
		}
		displayObjects();
		GUI.update();
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

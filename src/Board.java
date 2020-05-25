package src;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import src.Agent.Action;
import src.Block.Shape;


import java.util.Random;


/**
 * Environment
 * @author Rui Henriques
 */
public class Board {

	/** The environment */

	public static final int nX = 10, nY = 10, WAREHOUSES = 2, INIT_MAILS = 10, N_ROBOTS = 3, HOUSES = 5;
	private static Block[][] board;
	private static Entity[][] objects;
	private static List<Agent> robots;
	private static List<Warehouse> warehouses;
	private static List<House> houses;
	private static List<Mail> packages;


	/****************************
	 ***** A: SETTING BOARD *****
	 ****************************/
	
	public static void initialize() {

		/** A: create board */
		board = new Block[Board.nX][Board.nY];
		for(int i = 0; i< Board.nX; i++)
			for(int j = 0; j< Board.nY; j++)
				board[i][j] = new Block(Shape.free, Color.lightGray);

		Random rd = new Random();

		/** B: create destinations */
		houses = new ArrayList<>();
		for (int i = 0; i < HOUSES; i++) {
			Point point = new Point(rd.nextInt(nX), rd.nextInt(nY));
			// avoid the warehouse point
			boolean different = false;
			boolean exit = true;
			while (!different) {
				for (House h : houses) {
					if (h.point.equals(point)) {
						point = new Point(rd.nextInt(nX), rd.nextInt(nY));
						exit = false;
						break;
					}
				}
				if (exit == true) {
					different = true;
				}
			}
			House house = new House(Shape.house, point, Color.green);
			houses.add(house);
			board[point.x][point.y] = house;
		}

		/** C: create warehouses */
		warehouses = new ArrayList<>();
		for (int i = 0; i < WAREHOUSES; i++) {
			Point point = new Point(rd.nextInt(nX), rd.nextInt(nY));
			boolean different = false;
			boolean exit = true;
			while (!different) {
				for (Warehouse w : warehouses) {
					if (w.point.equals(point)) {
						point = new Point(rd.nextInt(nX), rd.nextInt(nY));
						exit = false;
						break;
					}
				}
				for (House h: houses) {
					if (h.point.equals(point)) {
						point = new Point(rd.nextInt(nX), rd.nextInt(nY));
						exit = false;
						break;
					}
				}
				if (exit == true) {
					different = true;
				}
			}
			//System.out.println(point);
			Warehouse warehouse = new Warehouse(Shape.warehouse, point, Color.red, INIT_MAILS, nX, nY, warehouses);
			warehouses.add(warehouse);
			board[point.x][point.y] = warehouse;
		}

		/** D: create agents randomly dispersed */
		robots = new ArrayList<>();
		for(int j = 0; j< N_ROBOTS; j++) {
			Point point = new Point(rd.nextInt(nX), rd.nextInt(nY));
			// avoid the warehouse point
			boolean different = false;
			boolean exit = true;
			while (!different) {
				for (Warehouse w : warehouses) {
					if (w.point.equals(point)) {
						point = new Point(rd.nextInt(nX), rd.nextInt(nY));
						exit = false;
						break;
					}
				}
				for (House h : houses) {
					if (h.point.equals(point)) {
						point = new Point(rd.nextInt(nX), rd.nextInt(nY));
						exit = false;
						break;
					}
				}
				if (exit == true) {
					different = true;
				}
			}
			List<Mail> mailList = new ArrayList<Mail>();
			for (Warehouse w : warehouses) {
				mailList.addAll(w.getMailList());
			}
			robots.add(new Agent(point, Color.pink, mailList));
		}
		objects = new Entity[Board.nX][Board.nY];
		for(Agent agent : robots) objects[agent.point.x][agent.point.y]=agent;
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
	public static void removeEntity(Point point) {
		objects[point.x][point.y] = null;
	}
	public static void insertEntity(Entity entity, Point point) {
		objects[point.x][point.y] = entity;
	}

	/***********************************
	 ***** C: ELICIT AGENT ACTIONS *****
	 ***********************************/
	
	private static RunThread runThread;
	private static GUI GUI;

	public static class RunThread extends Thread {
		
		int time;
		
		public RunThread(int time){
			this.time = time*time;
		}
		
	    public void run() {
	    	while(true){
	    		step();
				try {
					sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
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
		for(Agent a : robots) a.receiveMessage(action, ml);
	}

	public static void step() {
		removeObjects();
		for(Agent a : robots) a.agentDecision();
		displayObjects();
		GUI.update();
	}

	public static void stop() {
		runThread.interrupt();
		runThread.stop();
	}

	public static void displayObjects(){
		for(Agent agent : robots) GUI.displayObject(agent);
	}

	public static void removeObjects(){
		for(Agent agent : robots) GUI.removeObject(agent);
	}

	public static void associateGUI(GUI graphicalInterface) {
		GUI = graphicalInterface;
	}
}

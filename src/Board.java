package src;

import src.Block.Shape;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Environment
 * @author Rui Henriques
 */
public class Board {

	/** The environment */

	public static final int N_X = 10, N_Y = 10, WAREHOUSES = 2, INIT_MAILS = 1000, N_ROBOTS = 3;;
	private static Block[][] board;
	private static Entity[][] objects;
	private static List<Agent> robots;
	private static List<Warehouse> warehouses;
	
	
	/****************************
	 ***** A: SETTING BOARD *****
	 ****************************/
	
	public static void initialize() {

		/** A: create board */
		board = new Block[Board.N_X][Board.N_Y];
		for(int i = 0; i< Board.N_X; i++)
			for(int j = 0; j< Board.N_Y; j++)
				board[i][j] = new Block(Shape.free, Color.lightGray);

		Random rd = new Random();
		/** B: create warehouses */
		warehouses = new ArrayList<>();
		for (int i = 0; i < WAREHOUSES; i++) {
			Point point = new Point(rd.nextInt(N_X), rd.nextInt(N_Y));
			Warehouse warehouse = new Warehouse(Shape.warehouse, point, Color.red, INIT_MAILS, N_X, N_Y);
			warehouses.add(warehouse);
			board[point.x][point.y] = warehouse;
		}

		/** C: create agents randomly dispersed */
		robots = new ArrayList<>();
		for(int j = 0; j< N_ROBOTS; j++) {
			Point point = new Point(rd.nextInt(N_X), rd.nextInt(N_Y));
			// avoid the warehouse point
			boolean different = false;
			while (!different) {
				for (Warehouse w: warehouses) {
					if (w.point.equals(point)){
						point = new Point(rd.nextInt(N_X), rd.nextInt(N_Y));
						break;
					}
				}
				different = true;
			}
			robots.add(new Agent(point, Color.pink));
		}
		
		objects = new Entity[Board.N_X][Board.N_Y];
		for(Agent agent : robots) objects[agent.point.x][agent.point.y]=agent;
	}
	
	/****************************
	 ***** B: BOARD METHODS *****
	 ****************************/
	
	public static Entity getEntity(Point point) {
		return objects[point.x][point.y];
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

	public static void broadcastBeliefs(Object object) {
		for(Agent a : robots) a.receiveMessage(object);		
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

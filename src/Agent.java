package src;

import java.awt.Color;
import java.awt.Point;
import java.util.*;

import src.Block.Shape;

/**
 * Agent behavior
 */

public class Agent extends Entity {

	public enum Desire {grab, drop, finalPosition}
	public enum Action {moveAhead, grab, drop, rotateRight, rotateLeft, stay}

	public int direction = 90;
	public int mailLeft, mailDelivered, totalMail;

	public Mail mail;
	private Point ahead;

	public List<Desire> desires;
	public List<Mail> mailList;
	public AbstractMap.SimpleEntry<Desire,Point> intention;
	public Queue<Action> plan;

	public Agent(Point point, Color color, List<Mail> mailLst) {
		super(point, color);
		mail = null;
		mailLeft = mailLst.size();
		totalMail = mailLst.size();
		mailDelivered = 0;
		mailList = mailLst;
		plan = new LinkedList<>();
	}

	/**********************
	 **** A: decision ***** 
	 **********************/

	public void agentDecision() {
		if (mailDelivered < totalMail) {
			updateBeliefs();
			if ((!plan.isEmpty()) && (!succeededIntention()) && (!impossibleIntention())) {
				Action action = plan.remove();
				if (isPlanSound(action)) {
					execute(action);
				}
				else {
					rebuildPlan();
				}
				if (reconsider()) {
					deliberate();
				}
			}
			else {
				deliberate();
				buildPlan();
				if (plan.isEmpty()) {
					agentReactiveDecision();
				}
			}
		}
	}

	private void deliberate() {
		desires = new ArrayList<>();
		if (mail()) {
			desires.add(Desire.drop);
		}
		if (mailLeft > 0) {
			desires.add(Desire.grab);
		}
		if (mailLeft == 0) {
			desires.add(Desire.finalPosition);
		}
		intention = new AbstractMap.SimpleEntry<>(desires.get(0), null);
		switch (intention.getKey()) { //high-priority desire
			case grab :
				intention.setValue(getBestPackage().getMailSource());
				break;
			case drop :
				intention.setValue(mailDest());
				break;
			case finalPosition:
				intention.setValue(this.point);
		}
	}

	private void buildPlan() {
		plan = new LinkedList<>();
		if ((intention.getValue()) == null) {
			return;
		}
		switch (intention.getKey()) {
			case grab :
				plan = buildPathPlan(point,intention.getValue());
				plan.add(Action.grab);
				break;
			case drop :
				plan = buildPathPlan(point,intention.getValue());
				plan.add(Action.drop);
				break;
			case finalPosition :
				plan = buildPathPlan(point,intention.getValue());
				plan.add(Action.stay);
		}
	}

	private void rebuildPlan() {
		plan = new LinkedList<>();
		for (int i = 0; i < 4; i++) {
			agentReactiveDecision(); //attempt to come out of a conflict with full plan
		}
	}

	private boolean isPlanSound(Action action) {
		switch (action) {
			case moveAhead : return isFreeCell();
			case grab : return isWarehouseNotEmpty();
			case drop : return ((isHouse()) && (ahead.equals(mail.getMailDest())));
			default : return true;
		}
	}

	private void execute(Action action) {
		switch (action) {
			case moveAhead : moveAhead(); return;
			case rotateRight : rotateRight(); return;
			case rotateLeft : rotateLeft(); return;
			case grab : grabMail(); return;
			case drop : dropMail();
		}
	}

	private boolean impossibleIntention() {
		if (intention.getKey().equals(Desire.grab)) {
			return (mailLeft == 0);
		}
		else {
			return false;
		}
	}

	private boolean succeededIntention() {
		switch (intention.getKey()) {
			case grab : return mail();
			case drop : return !mail();
		}
		return false;
	}

	private boolean reconsider() {
		return false;
	}

	/*******************************/
	/**** B: reactive behavior ****/
	/*******************************/

	public void agentReactiveDecision() {
		ahead = aheadPosition();
		if (isWall()) {
			rotateRandomly();
		}
		else if ((isWarehouse()) && (isWarehouseNotEmpty()) && (!mail())) {
			grabMail();
		}
		else if (canDropMail()) {
			dropMail();
		}
		else if (!isFreeCell()) {
			rotateRandomly();
		}
		else if (random.nextInt(5) == 0) {
			rotateRandomly();
		}
		else {
			moveAhead();
		}
	}

	/**************************/
	/**** C: communication ****/
	/**************************/

	private void updateBeliefs() {
		ahead = aheadPosition();
	}

	public void receiveMessage(Action action, Mail ml) {
		if (action.equals(Action.drop)) {
			mailDelivered++;
		}
		else if(action.equals(Action.grab)) {
			mailLeft--;
			mailList.removeIf(m -> m.getMailId().equals(ml.getMailId()));
		}
	}

	/*******************************/
	/**** D: planning auxiliary ****/
	/*******************************/

	private Queue<Action> buildPathPlan(Point p1, Point p2) {
		Stack<Point> path = new Stack<>();
		Node node = shortestPath(p1,p2);
		path.add(node.point);
		while (node.parent!=null) {
			node = node.parent;
			path.push(node.point);
		}
		Queue<Action> result = new LinkedList<>();
		p1 = path.pop();
		int auxdirection = direction;
		while (!path.isEmpty()) {
			p2 = path.pop();
			result.add(Action.moveAhead);
			result.addAll(rotations(p1,p2));
			p1 = p2;
		}
		direction = auxdirection;
		result.remove();
		return result;
	}

	private List<Action> rotations(Point p1, Point p2) {
		List<Action> result = new ArrayList<>();
		while (!p2.equals(aheadPosition())) {
			Action action = rotate(p1,p2);
			if (action == null) {
				break;
			}
			execute(action);
			result.add(action);
		}
		return result;
	}

	private Action rotate(Point p1, Point p2) {
		boolean vertical = Math.abs(p1.x - p2.x) < Math.abs(p1.y - p2.y);
		boolean upright = vertical ? p1.y < p2.y : p1.x < p2.x;
		if (vertical) {
			if (upright) { //move up
				if (direction != 0) {
					return (direction == 90 ? Action.rotateLeft : Action.rotateRight);
				}
			}
			else if (direction != 180) {
				return (direction == 90 ? Action.rotateRight : Action.rotateLeft);
			}
		}
		else {
			if (upright) { //move right
				if (direction != 90) {
					return (direction == 180 ? Action.rotateLeft : Action.rotateRight);
				}
			}
			else if (direction != 270) {
				return (direction == 180 ? Action.rotateRight : Action.rotateLeft);
			}
		}
		return null;
	}

	/********************/
	/**** E: sensors ****/
	/********************/

	/* Check if agent is carrying a package */
	public boolean mail() {
		return (mail != null);
	}

	/* Check if the cell ahead is free (which means not a wall, not a house nor a warehouse) and there are any robot there */
	public boolean isFreeCell() {
		return ((isRoad()) && (Board.getEntity(ahead) == null));
	}

	public boolean isRoad() {
		return Board.getBlock(ahead).shape.equals(Shape.free);
	}

	/* Check if the cell ahead is a house */
	public boolean isHouse() {
		Block block = Board.getBlock(ahead);
		return block.shape.equals(Shape.house);
	}

	/* Check if the cell ahead is a warehouse */
	public boolean isWarehouse(){
		Block block = Board.getBlock(ahead);
		return block.shape.equals(Shape.warehouse);
	}

	/* Check if the cell ahead is a wall (meaning it is a limiter of the grid of the city) */
	private boolean isWall() {
		return ((ahead.x < 0) || (ahead.y < 0) || (ahead.x >= Board.nX) || (ahead.y >= Board.nY));
	}

	/* Check if the cell ahead is a wall (meaning it is a limiter of the grid of the city) */
	private boolean isWall(int x, int y) {
		return ((x < 0) || (y < 0) || (x >= Board.nX) || (y >= Board.nY));
	}

	/* Check if we can drop the package in the house ahead (check if it is the correct house) */
	private boolean canDropMail() {
		return ((isHouse()) && (mail()) && (ahead.equals(mail.getMailDest())));
	}

	/**********************/
	/**** F: actuators ****/
	/**********************/

	/* Rotate agent to right */
	public void rotateRandomly() {
		if (random.nextBoolean()) {
			rotateLeft();
		}
		else {
			rotateRight();
		}
	}

	/* Rotate agent to right */
	public void rotateRight() {
		direction = (direction + 90) % 360;
	}

	/* Rotate agent to left */
	public void rotateLeft() {
		direction = (direction - 90 + 360) % 360;
	}

	/* Move agent forward */
	public void moveAhead() {
		Board.updateEntityPosition(point,ahead);
		point = ahead;
	}

	/* Grab package */
	public void grabMail() {
		mail = getBestPackageWarehouse(ahead);
		System.out.println("pickup " + mail.getMailSource() + " " + mail.getMailDest());
		Board.sendMessage(Action.grab, mail);
	}

	/* Drop package */
	public void dropMail() {
		System.out.println("dropoff " + mail.getMailSource() + " " + mail.getMailDest());
		Board.sendMessage(Action.drop, mail);
		mail = null;
	}

	/**********************/
	/**** G: auxiliary ****/
	/**********************/

	/* Return the destination address of the package */
	public Point mailDest() {
		return mail.getMailDest();
	}

	/* Check if the warehouse ahead is not empty */
	public boolean isWarehouseNotEmpty(){
		boolean notEmpty = false;
		for (Mail m: mailList) {
			if (m.getMailSource().equals(ahead)) {
				notEmpty = true;
				break;
			}
		}
		return notEmpty;
	}

	private Mail getBestPackage() {
		Mail minimum = mailList.get(0);
		double minimumDist = point.distance(minimum.getMailSource()) + minimum.getMailSource().distance(minimum.getMailDest());
		for (Mail m: mailList) {
			if ((point.distance(m.getMailSource()) + m.getMailSource().distance(m.getMailDest())) < minimumDist) {
				minimum = m;
				minimumDist = point.distance(m.getMailSource()) + m.getMailSource().distance(m.getMailDest());
			}
		}
		// Agent with common optimality in its interest
		/*minimumDist = point.distance(mailList.get(0).getMailSource()) + mailList.get(0).getMailSource().distance(mailList.get(0).getMailDest());
		for (Mail m: mailList) {
			if ((point.distance(m.getMailSource()) + m.getMailSource().distance(m.getMailDest())) < minimumDist) {
				for (Agent a: Board.getAgents()) {
					if ((!a.equals(this)) && (((a.mail()) && (a.mail.getMailDest().distance(m.getMailSource()) + m.getMailSource().distance(m.getMailDest()) < point.distance(m.getMailSource()) + m.getMailSource().distance(m.getMailDest()))) || (!a.mail()) && (a.point.distance(m.getMailSource()) + m.getMailSource().distance(m.getMailDest()) < point.distance(m.getMailSource()) + m.getMailSource().distance(m.getMailDest())))) {
						minimum = m;
						minimumDist = point.distance(m.getMailSource()) + m.getMailSource().distance(m.getMailDest());
					}
				}
			}
		}*/
		return minimum;
	}

	private Mail getBestPackageWarehouse(Point ahead) {
		List<Mail> warehouseMailList = new ArrayList<>();
		for (Mail m: mailList) {
			if (m.getMailSource().equals(ahead)) {
				warehouseMailList.add(m);
			}
		}
		Mail minimum = warehouseMailList.get(0);
		double minimumDist = ahead.distance(minimum.getMailDest());
		for (Mail m: warehouseMailList) {
			if (ahead.distance(m.getMailDest()) < minimumDist) {
				minimum = m;
				minimumDist = ahead.distance(m.getMailDest());
			}
		}
		return minimum;
	}

	/* Position ahead */
	private Point aheadPosition() {
		Point newpoint = new Point(point.x,point.y);
		switch (direction) {
			case 0: newpoint.y++; break;
			case 90: newpoint.x++; break;
			case 180: newpoint.y--; break;
			default: newpoint.x--;
		}
		return newpoint;
	}

	//For queue used in BFS
	public class Node {

		Point point;
		Node parent; //cell's distance to source

		public Node(Point point, Node parent) {
			this.point = point;
			this.parent = parent;
		}
		public String toString() {
			return ("(" + point.x + "," + point.y + ")");
		}
	}

	public Node shortestPath(Point src, Point dest) {
		boolean[][] visited = new boolean[100][100];
		visited[src.x][src.y] = true;
		Queue<Node> q = new LinkedList<>();
		q.add(new Node(src,null)); //enqueue source cell

		//access the 4 neighbours of a given cell
		int[] row = {-1, 0, 0, 1};
		int[] col = {0, -1, 1, 0};

		while (!q.isEmpty()) {//do a BFS
			Node curr = q.remove(); //dequeue the front cell and enqueue its adjacent cells
			Point pt = curr.point;
			//System.out.println(">"+pt);
			for (int i = 0; i < 4; i++) {
				int x = pt.x + row[i], y = pt.y + col[i];
				if ((x == dest.x) && (y == dest.y)) {
					return new Node(dest,curr);
				}
				if ((!isWall(x,y)) && (Board.getBlock(new Point(x,y)).shape.equals(Shape.free)) && (!visited[x][y])) {
					visited[x][y] = true;
					q.add(new Node(new Point(x,y), curr));
				}
			}
		}
		return null; //destination not reached
	}
}
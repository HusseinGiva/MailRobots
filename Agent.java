package mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.util.Pair;
import java.util.ArrayList;

public class Agent{
  //public Float speed; //Did we set out to implement different speeds on different agents?
  public Pair position;
  public boolean available = true;
  public ArrayList<Warehouse> warehouses = new ArrayList<Warehouse>();
  public Agent(String[] options){

  }
  ///////////////////
  //Main Actions/////
  ///////////////////
  public void pickUp(){ //When at a warehouse, picks up an order and becomes unavaible, setting course to the destination
    this.available = false;
  }

  public void drop(){ //Marks the order as delivered, becomes available for pickUp, calls upon calcBestOrder function to know next move
    this.available = true;
  }

  public void calcBestOrder(){  //Accesses the warehouses' order list and picks the best given the distance to each warehouse and the orders destination
    float minDistance = -1.0;
    float currDistance = 0.0;
    for(Warehouse w: warehouses){
      for(Order o: w.orders){
        //Euclidean distance?
        //Calculate minimum sum of distance to warehouse + from warehouse to destination to all orders. Choose the best one.
      }
    }
  }
}

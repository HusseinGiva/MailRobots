package mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.util.Pair;
import java.util.ArrayList;

public class Warehouse{
  public Pair position;
  public static ArrayList<Order> orders = new ArrayList<Order>();
  public static ArrayList<Agent> agents = new ArrayList<Agent>();

  public Warehouse(String[] options){

  }
}

public static void main(String[] args) throws IOException { //Suggestion for first input: *Number of warehouses*: *Positions* | *Number of Agents*: *position + speed(?)*
  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  String line = br.readLine();
    //Warehouse warehouse = new Warehouse();
}

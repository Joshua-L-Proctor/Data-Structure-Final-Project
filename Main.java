import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

class Player {
  private String name;
  private String team;
  private int points;
  private double price;
  private int pos; 

  public Player(String _name, String _team, int _points, double _price, int _pos) {
    this.name = _name;
    this.team = _team;
    this.points = _points;
    this.price = _price;
    this.pos = _pos;
  }

  public String getName() { return this.name; }
  public String getTeam() { return this.team; }
  public int getPoints() { return this.points; }
  public double getPrice() { return this.price; }
  public int getPosition() { return this.pos; }

  @Override
  public String toString() {
    return "Player {" + 
           "name: '" + this.name + '\'' +
           ", team: " + this.team +
           ", points: " + this.points +
           ", price: " + this.price + 
           ", position: " + this.pos + '}';
  }

  public boolean alphabeticalOrder(Player p) { return this.name.compareTo(p.getName()) <= 0; }
  public boolean isOnTheSameTeam(Player p) { return this.team.equals(p.getTeam()); }
  public boolean hasMorePointsThan(Player p) { return this.points >= p.getPoints(); }
  public boolean isMoreExpensiveThan(Player p) { return this.price >= p.getPrice(); }
}

public class Main {
  private static void addPlayers(ArrayList<Player> playerList, String filename, int pos) {
    try {
      File myObj = new File(filename);
      System.out.println(myObj.getAbsolutePath());
      Scanner myReader = new Scanner(myObj, "UTF-8");

      while(myReader.hasNextLine()) {
        String data = myReader.nextLine();
        // System.out.println(data);
        Scanner lineScan = new Scanner(data.trim());

        while(lineScan.hasNext()) {
          lineScan.useDelimiter("#");

          // Create Player Object From Data
          String name = lineScan.next();
          String team = lineScan.next();
          int score = lineScan.nextInt();
          double price = lineScan.nextDouble();

          Player player = new Player(name, team, score, price, pos);

          // Load Player into Player List
          playerList.add(player);
        }

      }
      myReader.close();
    } catch(FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  // Helper Functions for the Quicksort Algorithms
  private static int partitionByPoints(ArrayList<Player> list, int low, int hi) {
    int pivot = hi;
    int i = low - 1;

    for(int j = low; j < hi; j++) {
      if(list.get(pivot).hasMorePointsThan(list.get(j))) {
        i++;
        Player temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp); 
      }
    }

    Player temp = list.get(i+1);
    list.set(i+1, list.get(hi));
    list.set(hi, temp);

    return i+1;
  }

  private static int partitionByPrice(ArrayList<Player> list, int low, int hi) {
    int pivot = hi;
    int i = low - 1;

    for(int j = low; j < hi; j++) {
      if(list.get(pivot).isMoreExpensiveThan(list.get(j))) {
        i++;
        Player temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
      }
    }

    Player temp = list.get(i+1);
    list.set(i+1, list.get(hi));
    list.set(hi, temp);

    return i+1;
  }

  // --- Quick Sort Algorithms that works on Player Objects ---
  private static void sortListByPoints(ArrayList<Player> list, int low, int hi) { // Low and Hi are the ends of the part of the array you want to sort
    if(low < hi) {
      int pi = partitionByPoints(list, low, hi); // Finds the partition index, the value is correctly placed
      sortListByPoints(list, low, pi-1); // Sort the list before the partition
      sortListByPoints(list, pi+1, hi); // Sort the list after the partitiion
    }
  }

  private static void sortListByPrice(ArrayList<Player> list, int low, int hi) { // Low and Hi are the ends of the part of the array you want to sort
    if(low < hi) {
      int pi = partitionByPrice(list, low, hi); // Finds the partition index, the value is correctly placed
      sortListByPoints(list, low, pi-1); // Sort the list before the partition
      sortListByPoints(list, pi+1, hi); // Sort the list after the partitiion
    }
  }

  private static ArrayList<Player> getTeamByPoints(ArrayList<Player> _list) {
    ArrayList<Player> list = (ArrayList<Player>)_list.clone();

    // Sort the list by points
    sortListByPoints(list, 0, list.size()-1);
    Collections.reverse(list);
    System.out.println("Fuck"); 
    // Create list to store selected players    
    ArrayList<Player> team = new ArrayList<Player>();

    // Keep track of the number of players per position
    int positions[] = new int[4];
    int limits[] = new int[] {2, 5, 5, 3};

    // Keep track of the number of players per team
    HashMap<String, Integer> teams = new HashMap<String, Integer>();

    // Keep track of the cost of the team
    double cost = 0.0;

    int i = 0;
    boolean flag = false;
    while(list.size() > 0 && team.size() < 15) {
      System.out.println(list);
      System.out.println("");
      flag = false;
      if(positions[list.get(i).getPosition()-1] < limits[list.get(i).getPosition()-1]) {
        if(teams.get(list.get(i).getTeam()) == null || teams.get(list.get(i).getTeam()) < 3) {
          if(cost + list.get(i).getPrice() < 100.0) {
            positions[list.get(i).getPosition() - 1] = positions[list.get(i).getPosition() - 1] + 1;
            teams.put(list.get(i).getTeam(), (teams.get(list.get(i).getTeam()) == null ? 1 : teams.get(list.get(i).getTeam()) + 1));
            cost = cost + list.get(i).getPrice();

            team.add(list.get(i));
            list.remove(i);
            flag = true;
          }
        } else {
          list.removeIf(element -> (element.getTeam().equals(list.get(i).getTeam())));
        }
      } else {
        list.removeIf(element -> (element.getPosition() == list.get(i).getPosition()));
      }

      if(!flag) {
        list.remove(i);
      }
    }

    return team;
  }

  public static void main(String[] args) {
    ArrayList<Player> playerList = new ArrayList<Player>();
    
    // GK - 1, D - 2, MF - 3, F - 4
    addPlayers(playerList, "goalkeepers.txt", 1);
    addPlayers(playerList, "defenders.txt", 2);
    addPlayers(playerList, "midfielders.txt", 3);
    addPlayers(playerList, "forwards.txt", 4);
  
    ArrayList<Player> teamByPoints = getTeamByPoints(playerList);
    for(int i = 0; i < teamByPoints.size(); i++) {
      System.out.println(teamByPoints.get(i));
    }
  }
}

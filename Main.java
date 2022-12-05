import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Stack;

class Player {
  private String name;
  private String team;
  private int points;
  private double price;
  private int pos; 

  //Constructor
  public Player(String _name, String _team, int _points, double _price, int _pos) {
    this.name = _name;
    this.team = _team;
    this.points = _points;
    this.price = _price;
    this.pos = _pos;
  }

  //Getters
  public String getName() { return this.name; }
  public String getTeam() { return this.team; }
  public int getPoints() { return this.points; }
  public double getPrice() { return this.price; }
  public int getPosition() { return this.pos; }

  @Override
  public String toString() {
    return this.name;
  }

  public String OldtoString() {
    return "Player {" + 
           "name: '" + this.name + '\'' +
           ", team: " + this.team +
           ", points: " + this.points +
           ", price: " + this.price + 
           ", position: " + this.pos + '}';
  }

  //Conditionals to Compare Players in List by All Attributes
  public boolean alphabeticalOrder(Player p) { return this.name.compareTo(p.getName()) <= 0; }
  public boolean isOnTheSameTeam(Player p) { return this.team.equals(p.getTeam()); }
  public boolean hasMorePointsThan(Player p) { return this.points >= p.getPoints(); }
  public boolean isMoreExpensiveThan(Player p) { return this.price - p.getPrice() > 0.000001d; }
  public boolean hasSamePositionAs(Player p) { return this.pos == p.getPosition(); }
}

public class Main {
  private static void addPlayers(ArrayList<Player> playerList, String filename, int pos) {
    //Reads in the Player Files
    try {
      File myObj = new File(filename);
      System.out.println(myObj.getAbsolutePath());
      Scanner myReader = new Scanner(myObj, "UTF-8");

      //Checks for next line to read in for files
      while(myReader.hasNextLine()) {
        String data = myReader.nextLine();
        // System.out.println(data);
        Scanner lineScan = new Scanner(data.trim());

        while(lineScan.hasNext()) {
          // useDelimiter separates the information in files by searching #'s in between lines
          lineScan.useDelimiter("#");

          // Create Player Object From Data
          String name = lineScan.next();
          String team = lineScan.next();
          int score = lineScan.nextInt();
          double price = lineScan.nextDouble();

          //Creates Instance of Player
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

  // Following functions are used for a Quicksort, O(nlogn)
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
  //Recursive Function Call that runs until low==high (One element is in the array)
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

  //Under Construction: Greedy Algorithms Auto Builds Team based on Most Points
  private static Stack<Player> getTeamByPoints(ArrayList<Player> _list) {
    //List is cloned to be able to make specific changes without changing
    ArrayList<Player> list = (ArrayList<Player>)_list.clone();

    // Sort the list by points
    sortListByPoints(list, 0, list.size()-1);
    //List is sorted low to high, must be flipped
    Collections.reverse(list);
    // Create list to store selected players    
    Stack<Player> team = new Stack<Player>();
    Stack<Integer> index = new Stack<Integer>();
    Stack<ArrayList<Player>> lists = new Stack<ArrayList<Player>>();

    // Keep track of the number of players per position
    int positions[] = new int[4];
    int limits[] = new int[] {2, 5, 5, 3};

    // Keep track of the number of players per team
    HashMap<String, Integer> teams = new HashMap<String, Integer>();

    // Keep track of the cost of the team
    double cost = 0.0;

    int i = 0;
    boolean flag = false;
    while(i < list.size() && team.size() < 15) {
      // System.out.println(list);
      // System.out.println("");
      flag = true;
      //Starts by getting player from list at index "i"
      //This line makes sure that there are not too many players in one positions.
      if(positions[list.get(i).getPosition()-1] < limits[list.get(i).getPosition()-1]) {
        //This line checks to make sure we arent adding more than 3 players from one team
        if(teams.containsKey(list.get(i).getTeam()) == false || teams.get(list.get(i).getTeam()) < 3) {
          //This line checks to make sure we don't go over max cost
          if(cost + list.get(i).getPrice() < 100.0) {

            positions[list.get(i).getPosition() - 1] = positions[list.get(i).getPosition() - 1] + 1;
            int val = teams.get(list.get(i).getTeam()) == null ? 0 : teams.get(list.get(i).getTeam());
            teams.put(list.get(i).getTeam(), val + 1);
            cost = cost + list.get(i).getPrice();

            team.push(list.get(i));
            index.push(i);
            lists.push(list);

            flag = false;
          } else {
            list.remove(i);
            i += 1;
          }

        } else {
          System.out.println("Fuck 2");
          Player temp = list.get(i);
          ArrayList<Player> check = (ArrayList<Player>) list.clone();
          check.removeIf(element -> (element.getTeam().equals(temp.getTeam())));
          if((15 - team.size()) > check.size() && !team.empty()) {
            Player p = team.pop();
            positions[p.getPosition() - 1] = positions[p.getPosition() - 1] - 1;
            teams.put(p.getTeam(), teams.get(p.getTeam()) - 1);
            cost= cost - p.getPrice();
            list = lists.pop();
            i = index.pop() + 1;
          } else {
            list = check;
            i = 1;
          }
        }

      } else {
        System.out.println("Fuck 3");
        Player temp = list.get(i);
        ArrayList<Player> check = (ArrayList<Player>) list.clone();
        check.removeIf(element -> (element.getPosition() == temp.getPosition()));
        if((15 - team.size()) > check.size() && !team.empty()) {
          Player p = team.pop();
          positions[p.getPosition() - 1] = positions[p.getPosition() - 1] - 1;
          teams.put(p.getTeam(), teams.get(p.getTeam()) - 1);
          cost= cost - p.getPrice();
          list = lists.pop();
          i = index.pop() + 1;
        } else {
          list = check;
          i = 1;
        }
      }

      //Prevents infinite auto loop
      if(!flag) {
        i++;
      }

      if(i == list.size() && team.size() < 15) {
        System.out.println("Fuck 4");
        Player p = team.pop();
        positions[p.getPosition() - 1] = positions[p.getPosition() - 1] - 1;
        teams.put(p.getTeam(), teams.get(p.getTeam()) - 1);
        cost= cost - p.getPrice();
        list = lists.pop();
        i = 1;
      }

      System.out.println(team);
    }

    System.out.println(cost);
    return team;
  }



  public static void main(String[] args) {
    ArrayList<Player> playerList = new ArrayList<Player>();
    
    // GK - 1, D - 2, MF - 3, F - 4
    addPlayers(playerList, "goalkeepers.txt", 1);
    addPlayers(playerList, "defenders.txt", 2);
    addPlayers(playerList, "midfielders.txt", 3);
    addPlayers(playerList, "forwards.txt", 4);
  
    Stack<Player> teamByPoints = getTeamByPoints(playerList);
    for(int i = 0; i < teamByPoints.size(); i++) {
      System.out.println(teamByPoints.get(i));
    }
  }
}

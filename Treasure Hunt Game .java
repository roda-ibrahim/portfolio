//Roda Ibrahim, ribr221, descprition:  The main program that initializes the Treasure Hunt game, takes user inputs, runs the gameplay, and shows final results.
import java.util.*;
public class A1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Treasure Hunt Game!");
        System.out.println("Enter the initial score (1-100):");
        int initialScore = scanner.nextInt();
        System.out.println("Enter the number of Treasures:");
        int numTreasures = scanner.nextInt();
        TreasureMap map = new TreasureMap(initialScore, numTreasures, 50, 50, 30);
        System.out.println("Enter the number of hunters (1-5):");
        int numHunters = scanner.nextInt();
        scanner.nextLine(); // consume remaining newline
        for (int i = 1; i <= numHunters; i++) {
            System.out.println("Enter the name of the " + i + " hunter:");
            String hunterName = scanner.nextLine();
            map.addHunter(hunterName);
            
        }
        map.start();
        map.announce();
        scanner.close();
        
    }
}


//classes: 
class Treasure{
    public int x=0;
    public int y=0;
    protected int value=20;
    public Treasure(){}
    public Treasure(int value){
        this.value=value;
    }
    public Treasure(int x,int y,int value){
        this.x=x;
        this.y=y;
        this.value=value;
    }
    public int getValue(){
        return this.value;
    }
    public int distance(int hunter_x,int hunter_y){
        return (int)Math.round(Math.sqrt(Math.pow((hunter_x-this.x),2)+Math.pow(hunter_y-this.y,2)));
    }
    public String toString(){
        return String.format("Treasure at (%d, %d) worth %d points",this.x,this.y,this.value );
    }
}
class DoubleBonusTreasure extends Treasure{
    public DoubleBonusTreasure(){
        super.value=value*2;
    }
    public DoubleBonusTreasure(int value){
        super(value*2);
    }
    public DoubleBonusTreasure(int x,int y,int value){
        super(x,y,value*2);
    }
    
}
class TrapTreasure extends Treasure{
    private int penalty=50;
    public TrapTreasure(){
        super();
    }
    public TrapTreasure(int penalty){
        this.penalty=penalty;
    }
    public TrapTreasure(int value,int penalty){
        super(value);
        this.penalty=penalty;
    }
    public TrapTreasure(int x,int y,int value,int penalty){
        super(x,y,value);
        this.penalty=penalty;
    }
    public int getPenalty(){
        return this.penalty;
    }
    public int getValue(){
        return super.value-this.penalty;
    }
    public String toString(){
        return String.format("Treasure at (%d, %d) worth %d points but has a penalty of %d",super.x,super.y,super.value,this.penalty);    }
}
class Hunter {
    private String name;
    private int x;
    private int y;
    private int score;
    private ArrayList<Treasure> collected;
    
    public Hunter() {
        this.name = "Unknown";
        this.x = 0;
        this.y = 0;
        this.score = 20;
        this.collected = new ArrayList<>();
    }
    
    public Hunter(String name, int initialScore) {
        this.name = name;
        this.score = initialScore;
        this.x = 0;
        this.y = 0;
        this.collected = new ArrayList<>();
    }
    
    public Hunter(int x, int y, String name, int initialScore) {
        this.name = name;
        this.score = initialScore;
        this.x = x;
        this.y = y;
        this.collected = new ArrayList<>();
    }
    
    public String toString() {
        return "Hunter " + name + ": " + score + " points. Treasures collected: " + collected.size();
    }
    
    public Treasure findClosest(ArrayList<Treasure> treasures) {
        if (treasures == null || treasures.isEmpty()) {
            return null;
        }
        Treasure closest = treasures.get(0);
        int minDistance = closest.distance(x, y);
        for (Treasure t : treasures) {
            int currentDistance = t.distance(x, y);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                closest = t;
            }
        }
        return closest;
    }
    
    public boolean collect(ArrayList<Treasure> treasures) {
        Treasure closest = findClosest(treasures);
        if (closest == null) {
            System.out.println("No treasures available to collect.");
            return false;
        }
        int distance = closest.distance(x, y);
        if (score >= distance) {
            int startX = x;
            int startY = y;
            score -= distance;
            score += closest.getValue();
            collected.add(closest);
            treasures.remove(closest);
            x = closest.x;
            y = closest.y;
            System.out.println(name + " started at (" + startX + ", " + startY + "), spent " + distance 
                + " points, and collected " + closest.toString() + ". New score: " + score + ".");
            return true;
        } else {
        System.out.println(name + " started at (" + x + ", " + y + "), but does not have enough points to reach the treasure.");
            return false;
        }
    }
}
class TreasureMap {
    private ArrayList<Hunter> hunters;
    private ArrayList<Treasure> treasures;
    private int initialScore = 20;

    public TreasureMap(int initialScore, int numberOfTreasures, int maxX, int maxY, int maxValue) {
        this.initialScore = initialScore;
        this.hunters = new ArrayList<>();
        this.treasures = new ArrayList<>();

        // Fixed seed random generator
        Random rand = new Random(30);

        // Generate normal treasures
        for (int i = 0; i < numberOfTreasures; i++) {
            int x = rand.nextInt(maxX);
            int y = rand.nextInt(maxY);
            int value = rand.nextInt(maxValue);
            treasures.add(new Treasure(x, y, value));
        }

        // Add one DoubleBonusTreasure
        int dbX = rand.nextInt(maxX);
        int dbY = rand.nextInt(maxY);
        int dbValue = rand.nextInt(maxValue);
        treasures.add(new DoubleBonusTreasure(dbX, dbY, dbValue));

        // Add one TrapTreasure
        int trapX = rand.nextInt(maxX);
        int trapY = rand.nextInt(maxY);
        int trapValue = rand.nextInt(maxValue);
        int penalty = rand.nextInt(maxValue); // 0–10 inclusive
        treasures.add(new TrapTreasure(trapX, trapY, trapValue, penalty));

        // Print treasure details
        for (Treasure t : treasures) {
            System.out.println(t);
        }
    }

    public void addHunter(String name) {
        hunters.add(new Hunter(name, initialScore));
    }

    public void start() {
        boolean allCanCollect = true;
        while (allCanCollect && !treasures.isEmpty()) {
            for (Hunter h : hunters) {
                boolean success = h.collect(treasures);
                if (!success) {
                    allCanCollect = false;
                    break;
                }
            }
        }
    }

    public void announce() {
        System.out.println("\n--- Final Scores ---");
        for (Hunter h : hunters) {
            System.out.println(h);
        }
    }
}

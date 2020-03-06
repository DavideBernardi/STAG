import java.util.ArrayList;

public class GameState {

    private ArrayList<Location> locations;
    private ArrayList<Action> actions;
    private Location unplaced;

    public GameState() {
        locations = new ArrayList<>();
        actions = new ArrayList<>();
    }

    public void addLocation(Location loc) {
        locations.add(loc);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public Location getUnplaced() {
        return unplaced;
    }

    public void setUnplaced(Location unplaced) {
        this.unplaced = unplaced;
    }
}

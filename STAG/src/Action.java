import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.*;

public class Action {

    ArrayList<String> triggers;
    ArrayList<String> subjects;
    ArrayList<String> consumed;
    ArrayList<String> produced;
    String narration;

    public Action() {
        triggers = new ArrayList<>();
        subjects = new ArrayList<>();
        consumed = new ArrayList<>();
        produced = new ArrayList<>();

    }

    public Action(ArrayList<String> triggers, ArrayList<String> subjects, ArrayList<String> consumed,
                         ArrayList<String> produced, String narration) {
        this.triggers = new ArrayList<String>();
        this.subjects = new ArrayList<String>();
        this.consumed = new ArrayList<String>();
        this.produced = new ArrayList<String>();
        this.triggers.addAll(triggers);
        this.subjects.addAll(subjects);
        this.consumed.addAll(consumed);
        this.produced.addAll(produced);
        this.narration = narration;
    }

    public void buildFromJSON(JSONObject action) {
        triggers = new ArrayList<String>((JSONArray) action.get("triggers")); //(Collection<? extends String>) action.get("triggers")
        subjects = new ArrayList<String>((JSONArray) action.get("subjects"));
        consumed = new ArrayList<String>((JSONArray) action.get("consumed"));
        produced = new ArrayList<String>((JSONArray) action.get("produced"));
        narration = (String) action.get("narration");
    }

    public ArrayList<String> getTriggers() {
        return triggers;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public ArrayList<String> getConsumed() {
        return consumed;
    }

    public ArrayList<String> getProduced() {
        return produced;
    }

    public String getNarration() {
        return narration;
    }

    public void describe() {
        System.out.print("ACTION: ");
        System.out.println(narration);
        System.out.print("\tTriggers: ");
        System.out.println(triggers);
        System.out.print("\tSubjects: ");
        System.out.println(subjects);
        System.out.print("\tConsumed ");
        System.out.println(consumed);
        System.out.print("\tProduced: ");
        System.out.println(produced);
    }

    public String execute(Location loc, Player player, Location unplaced) throws EntityTypeException, EntityNotFoundException {
        if(!subjectsPresent(loc, player)) {
            return player.getName() + " cannot do this at the moment.\n";
        } else {
            removeConsumed(loc,player);
            addProduced(loc, player, unplaced);
            return  player.getName() + ": " + narration;}
    }

    private boolean subjectsPresent(Location loc, Player player) {
        for (String subject : subjects) {
            if (loc.getEntities().stream().noneMatch(entity -> entity.getName().equals(subject)) &&
                    player.getArtefacts().stream().noneMatch(entity -> entity.getName().equals(subject))) {
                return false;
            }
        }
        return true;
    }

    private void removeConsumed(Location loc, Player player) throws EntityTypeException {
        for (String cons : consumed) {
            removeThis(loc, player, cons);
        }
    }

    private void removeThis(Location loc, Player player, String cons) throws EntityTypeException {
        if (cons.equalsIgnoreCase("health")) {
            player.setHealth(player.getHealth()-1);
            return;
        }
        for (Entity ent : loc.getEntities()) {
            if (ent.getName().equalsIgnoreCase(cons)) {
                loc.remove(ent);
                return;
            }
        }
        for (Artefact art : player.getArtefacts()) {
            if (art.getName().equalsIgnoreCase(cons)) {
                player.removeArtefact(art);
                return;
            }
        }
    }

    private void addProduced(Location loc, Player player, Location unplaced) throws EntityNotFoundException, EntityTypeException {

        for (String prod : produced) {
            addThis(loc,player,unplaced, prod);
        }
    }

    private void addThis(Location loc, Player player, Location unplaced, String prod) throws EntityNotFoundException, EntityTypeException {
        if (prod.equalsIgnoreCase("health")) {
            player.setHealth(player.getHealth()+1);
            return;
        }
        for (Entity ent : unplaced.getEntities()) {
            if (ent.getName().equalsIgnoreCase(prod)) {
                loc.add(ent);
                return;
            }
        }
        throw new EntityNotFoundException(prod);
    }
}

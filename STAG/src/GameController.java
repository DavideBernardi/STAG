import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class GameController {

    private GameState model;
    /*Here I store the currentPlayer and currentLocation because they change (possibly) every new command, so they
    * are part of the logic.
    * Any other piece of data that needs storing (I.E. special location "unplaced" is stored in the game model and then picked up from there*/
    private Player currPlayer;
    private Location currLocation;

    public GameController(GameState model) {
        this.model = model;
    }

    public void describeGameState() {
        for (Location loc : model.getLocations()){
            System.out.println(loc.describe());
        }
        System.out.print("\n\n\n");
        for (Action act : model.getActions()) {
            act.describe();
        }
        System.out.println("_____________________________________________________________________________________");
    }

    /*Set "unplaced" as a special location, add every possible path to it so paths can be added as any other entity
     */
    public void setUnplaced() throws EntityTypeException {
        for (Location loc : model.getLocations()) {
            if (loc.getName().equalsIgnoreCase("unplaced")) {
                model.setUnplaced(loc);
                for (Location path : model.getLocations()) {
                    model.getUnplaced().add(path);
                }
                return;
            }
        }
    }

    public void processNextCommand(BufferedReader in, BufferedWriter out) throws IOException {
        Command command = new Command(in.readLine());
        command.parse();

        setSetting(command.getOwner());
        try{
            String message;
            message = tryDefaultActions(command.getWords());
            if (message == null) {
                Action action = pickAction(command.getWords().get(0));
                message = action.execute(currLocation,currPlayer,model.getUnplaced());
            }
            message = message.concat(killPlayersIfDead());
            out.write(message);
        }catch(IOException | EntityTypeException | EntityNotFoundException e){
            out.write(e.toString());
        }
    }

    private void setSetting(String playerName) {
        if (newPlayer(playerName)) addPlayer(playerName);
        for (Location loc : model.getLocations()) {
            for (StagChar character : loc.getCharacters()) {
                if (character.getName().equals(playerName) && character.getClass().getName().equals("Player")) {
                        currPlayer = (Player) character;
                        currLocation = loc;
                        return;
                }
            }
        }
    }

    private String tryDefaultActions(ArrayList<String> commandWords) throws EntityTypeException {
        if (commandWords.get(0).equalsIgnoreCase("inventory") ||
                commandWords.get(0).equalsIgnoreCase("inv")) return actionInv();

        if (commandWords.get(0).equalsIgnoreCase("get")) return actionGet(commandWords);

        if (commandWords.get(0).equalsIgnoreCase("drop")) return actionDrop(commandWords);

        if (commandWords.get(0).equalsIgnoreCase("goto")) return actionGoto(commandWords);

        if (commandWords.get(0).equalsIgnoreCase("look")) return currLocation.describe();

        if (commandWords.get(0).equalsIgnoreCase("health")) return actionHealth();

        return null;
    }

    private String actionInv() {
        String message = currPlayer.getName() + " has: \n";
        for (Artefact art : currPlayer.getArtefacts()) {
            message = message.concat("\t" + art.getName() + ": " + art.getDescription() + "\n");
        }
        return message;
    }

    private String actionGet(ArrayList<String> commandWords) throws EntityTypeException {
        for (Artefact art : currLocation.getArtefacts()) {
            if (art.getName().equalsIgnoreCase(commandWords.get(1))) {
                currPlayer.addArtefact(art);
                currLocation.remove(art);
                return currPlayer.getName() + " has picked up: " + commandWords.get(1);
            }
        }
        return "Artefact '" + commandWords.get(1) + "' is not in " + currLocation.getName();
    }

    private String actionDrop(ArrayList<String> commandWords) throws EntityTypeException {
        for (Artefact art : currPlayer.getArtefacts()) {
            if (art.getName().equalsIgnoreCase(commandWords.get(1))) {
                currLocation.add(art);
                currPlayer.removeArtefact(art);
                return currPlayer.getName() + " has dropped: " + commandWords.get(1);
            }
        }
        return "Artefact '" + commandWords.get(1) + "' is not in " + currPlayer.getName() + "'s inventory";
    }

    private String actionGoto(ArrayList<String> commandWords) throws EntityTypeException {
        for (Location loc : currLocation.getPaths()) {
            if (loc.getName().equalsIgnoreCase(commandWords.get(1))) {
                currLocation.remove(currPlayer);
                loc.add(currPlayer);
                return currPlayer.getName() + " has moved to " + loc.getName() + "\nIt is " + loc.getDescription();
            }
        }
        return "No path for " + currPlayer.getName() + " from " + currLocation.getName() + " to " + commandWords.get(1);
    }

    private String actionHealth() {
        return currPlayer.getName() + "'s Health Level is " + currPlayer.getHealth();
    }

    private Action pickAction(String firstWord) throws IOException {
        for (Action action : model.getActions()) {
            for (String trigger : action.getTriggers()) {
                if (firstWord.equalsIgnoreCase(trigger)) return action;
            }
        }
        throw new IOException("Trigger word: '" + firstWord + "' not recognized, ");
    }

    private String killPlayersIfDead() {
        String message = "\n";
        for (Player player : getPlayers()) {
            if (player.getHealth()<=0) {
                for (Location loc : model.getLocations()) {
                    if (loc.getCharacters().contains(player)) {
                        loc.getArtefacts().addAll(player.getArtefacts());
                        player.getArtefacts().removeAll(player.getArtefacts());
                        loc.removeCharacter(player);
                        break;
                    }
                }
                model.getLocations().get(0).addCharacter(player);
                player.setHealth(3);
                message = message.concat(player.getName() + " has died . . .\n");
            }
        }
        return message;
    }

    private boolean newPlayer(String playerName) {
        for (Player p : getPlayers()) {
            if (p.getName().equals(playerName)) return false; //Add break? Faster but looks ugly
        }
        return true;
    }

    private void addPlayer(String name) {
        model.getLocations().get(0).addCharacter(new Player(name, "A Playing Character"));
    }

    /*I could store ArrayList players; in the GameState but since they are scattered across the locations it would
    not be of much use, instead if I need to get a list of players I search through every location.
    This has complexity O(n^2) so for bigger games it might be worth having an ArrayList of playerNames so it's faster
    to check if a new player is playing.     */
    private ArrayList<Player> getPlayers() {
        ArrayList<Player> Players = new ArrayList<>();
        for (Location loc : model.getLocations()) {
            for (StagChar character : loc.getCharacters()) {
                if (character.getClass().getName().equals("Player")) Players.add((Player) character);
            }
        }
        return Players;
    }

}

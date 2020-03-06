import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.objects.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class StagParser {
    String entityFilename;
    String actionFilename;
    GameState game;

    public StagParser(String entityFilename, String actionFilename) {
        this.entityFilename = entityFilename;
        this.actionFilename = actionFilename;
        game = new GameState();
    }

    public GameState parseGame() {
        parseEntities();
        parseActions();
        return game;
    }

    private void parseEntities() {
        FileReader fileIn;
        File f = new File(entityFilename);

        try {
            fileIn = new FileReader(f);
            Parser parser = new Parser();
            parser.parse(fileIn);
            ArrayList<Graph> graphs = parser.getGraphs();
            ArrayList<Graph> mainSubgraph = graphs.get(0).getSubgraphs(); //Split into locationGraph and pathGraph
            /*This loop is unnecessary since given our .dot syntax I could just use .get(0) for the locationGraph and
            .get(1) for the pathGraph
             */
            for(Graph locsOrPaths : mainSubgraph){
                buildLocations(locsOrPaths);
                buildPaths(locsOrPaths);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (com.alexmerz.graphviz.ParseException e) { //Need full name bc both libs have ParseException
            System.out.println("Parser Error");
        }
    }

    private void buildLocations(Graph locationsGraph) {
        ArrayList<Graph> LocationGraphs = locationsGraph.getSubgraphs(); //Get subgraphs of graph g
        for (Graph currLocGraph : LocationGraphs){
            ArrayList<Node> nodesInLoc = currLocGraph.getNodes(false);
            Node nLoc = nodesInLoc.get(0); //nLoc is the node describing the location
            Location loc = new Location(nLoc.getId().getId(),nLoc.getAttribute("description"));
            ArrayList<Graph> entitiesGraphs = currLocGraph.getSubgraphs();
            for (Graph currEntityType : entitiesGraphs) {
                ArrayList<Node> nodesEnt = currEntityType.getNodes(false);
                for (Node entityNode : nodesEnt) {
                    switch (currEntityType.getId().getId()) {
                        case "artefacts":
                            loc.addArtefact(new Artefact(entityNode.getId().getId(), entityNode.getAttribute("description")));
                            break;
                        case "furniture":
                            loc.addFurniture(new Furniture(entityNode.getId().getId(), entityNode.getAttribute("description")));
                            break;
                        case "characters":
                            loc.addCharacter(new StagChar(entityNode.getId().getId(), entityNode.getAttribute("description")));
                            break;
                        default:
                            System.err.println("Invalid entity type:" + currEntityType.getId().getId());
                    }
                }
            }
            game.addLocation(loc);
        }
    }

    private void buildPaths(Graph pathsGraph) {
        /*This has complexity O(n^3) since for every edge, it goes through every starting location and for every
        * starting location it goes through every ending location.
        * Don't know how to make it better :( */
        ArrayList<Edge> edges = pathsGraph.getEdges();
        for (Edge e : edges){   //For every edge
            for (Location startLoc : game.getLocations()){ //Test every starting location
                for (Location endLoc : game.getLocations()){ //against every ending location
                    //If the current edge describes the path from the starting location to the ending location
                    if (startLoc.getName().equals(e.getSource().getNode().getId().getId()) &&
                            endLoc.getName().equals(e.getTarget().getNode().getId().getId())){
                        startLoc.addPathTowards(endLoc); //Add that path
                    }
                }
            }
        }
    }

    private void parseActions() {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(actionFilename)) {

            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray actions = (JSONArray) jsonObject.get("actions");

            for (Object actionObj : actions) {
                Action action = new Action();
                action.buildFromJSON((JSONObject) actionObj);
                game.addAction(action);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

}

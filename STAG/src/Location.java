import java.util.*;

public class Location extends artefactHolder {

    ArrayList<Location> paths;
    ArrayList<StagChar> characters;
    ArrayList<Furniture> furniture;

    public Location(String name, String description) {
        super(name, description);

        paths = new ArrayList<>();
        characters = new ArrayList<>();
        furniture = new ArrayList<>();
    }

    void remove(Entity ent) throws EntityTypeException {
        switch(ent.getClass().getName()) {
            case "Artefact":
                artefacts.remove((Artefact) ent);
                break;
            case "Furniture":
                furniture.remove((Furniture) ent);
                break;
            case "Location":
                paths.remove((Location) ent);
                break;
            case "StagChar":
            case "Player":
                characters.remove((StagChar) ent);
                break;
            default: throw new EntityTypeException(ent.getClass().getName());
        }
    }

    void add(Entity ent) throws EntityTypeException {
        switch(ent.getClass().getName()) {
            case "Artefact":
                artefacts.add((Artefact) ent);
                break;
            case "Furniture":
                furniture.add((Furniture) ent);
                break;
            case "Location":
                paths.add((Location) ent);
                break;
            case "StagChar":
            case "Player":
                characters.add((StagChar) ent);
                break;
            default: throw new EntityTypeException(ent.getClass().getName());
        }
    }

    void addPathTowards(Location destination) {
        paths.add(destination);
    }

    void removePathTowards(Location destination) {
        paths.remove(destination);
    }

    ArrayList<Location> getPaths() {
        return paths;
    }

    void addFurniture(Furniture pieceOfFurniture) {
        furniture.add(pieceOfFurniture);
    }

    void removeFurniture(Furniture pieceOfFurniture) {
        furniture.remove(pieceOfFurniture);
    }

    ArrayList<Furniture> getFurniture() {
        return furniture;
    }

    void addCharacter(StagChar character) {
        characters.add(character);
    }

    void removeCharacter(StagChar character) {
        characters.remove(character);
    }

    ArrayList<StagChar> getCharacters() {
        return characters;
    }

    ArrayList<Entity> getEntities() {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        entities.addAll(paths);
        entities.addAll(characters);
        entities.addAll(furniture);
        entities.addAll(artefacts);
        return entities;
    }

    String describe() {
        String message = (name + ", " + description + "\n");
        message = message.concat(describeEntities(furniture, "contains", "Furniture"));
        message = message.concat(describeEntities(artefacts, "contains", "Artefact(s)"));
        message = message.concat(describeEntities(characters, "is inhabited by", "Character(s)"));
        message = message.concat(describeEntities(paths, "leads to", "Location(s)"));
        return message;
    }

    private String describeEntities(ArrayList<? extends Entity> array, String verb, String entityName) {

        if (array.size()==0) return "";
        String message = ("\tIt " + verb + " the following " + entityName + ":\n");
        for (Entity entity : array) {
            message = message.concat("\t\t" + entity.getName() + ": \"" + entity.getDescription() + "\"\n");
        }
        return message;
    }
}

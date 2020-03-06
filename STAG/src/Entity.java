public abstract class Entity {

    String name;
    String description;

    public Entity(String name, String description) {
        setName(name);
        setDescription(description);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}

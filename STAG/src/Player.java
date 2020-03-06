public class Player extends StagChar{

    int health;

    public Player(String name, String description) {
        super(name, description);

        health = 3;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}

public class EntityTypeException extends Exception {

    String entName;

    public EntityTypeException(String entName) {
        this.entName = entName;
    }

    public String toString() {
        return this.getClass().getName() + ": Entity of Type '" + entName + "' does not exist.";
    }
}

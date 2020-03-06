public class EntityNotFoundException extends Exception {
    String entName;

    public EntityNotFoundException(String entName) {
        this.entName = entName;
    }

    public String toString() {
        return this.getClass().getName() + ": Entity named '" + entName + "' does not exist. (i.e. Is not in Location 'unplaced')\n";
    }
}

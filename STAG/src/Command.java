import java.util.ArrayList;
import java.util.Arrays;

public class Command {
    
    private String rawIn;
    private String owner;
    private ArrayList<String> words;
    
    public Command(String line) {
        rawIn = line;
        words = new ArrayList<>();
    }

    public String getOwner() {
        return owner;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void parse() {
        String[] ownerAndWords = rawIn.split(": ", 2);
        owner = ownerAndWords[0];
        System.out.println(ownerAndWords[1]);
        words.addAll(Arrays.asList(ownerAndWords[1].split(" ", 0)));
    }

}

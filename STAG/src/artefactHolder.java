import java.util.*;

public abstract class artefactHolder extends Entity {

    ArrayList<Artefact> artefacts;

    public artefactHolder(String name, String description)
    {
        super(name, description);

        artefacts = new ArrayList<>();
    }

    void addArtefact(Artefact artefact)
    {
        artefacts.add(artefact);
    }

    void removeArtefact(Artefact artefact)
    {
        artefacts.remove(artefact);
    }

    ArrayList<Artefact> getArtefacts()
    {
        return artefacts;
    }
}

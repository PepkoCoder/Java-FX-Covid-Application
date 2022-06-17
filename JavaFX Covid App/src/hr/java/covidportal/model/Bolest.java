package hr.java.covidportal.model;

import java.util.*;

/**
 * Predstavlja entitet bolest koji je definiran listom simptoma i nazivom.
 */
public class Bolest extends ImenovaniEntitet{
    private List<Simptom> simptomi = new ArrayList<>();

    /**
     * Inicijalizira podatak o nazivu bolesti i simptomima bolesti
     * @param naziv Naziv bolesti
     * @param simptomi Simptomi bolesti
     */
    public Bolest(long id, String naziv, List<Simptom> simptomi) {
        super(id, naziv);
        this.simptomi = simptomi;
    }

    public List<Simptom> getSimptomi() {
        return simptomi;
    }

    public void setSimptomi(List<Simptom> simptomi) {
        this.simptomi = simptomi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bolest)) return false;
        Bolest bolest = (Bolest) o;
        return getSimptomi().equals(bolest.getSimptomi());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSimptomi());
    }

    @Override
    public String toString() {
        return getNaziv();
    }
}

package hr.java.covidportal.model;

import hr.java.covidportal.enumeracije.VrijednostSimptoma;

/**
 * Predstvalja entitet simptom koji je definiran nazivom i vrijednosti.
 */
public class Simptom extends ImenovaniEntitet{

    private VrijednostSimptoma vrijednost;

    /**
     * Inicijalizira podatke o nazivu i vrijednosti simptoma.
     * @param naziv Naziv simptoma
     * @param vrijednost Vrijednost simptoma
     */
    public Simptom(long id, String naziv, String vrijednost) {
        super(id, naziv);
        this.vrijednost = VrijednostSimptoma.valueOf(vrijednost);
    }

    public VrijednostSimptoma getVrijednost() {
        return vrijednost;
    }

    public void setVrijednost(VrijednostSimptoma vrijednost) {
        this.vrijednost = vrijednost;
    }

    @Override
    public String toString() {
        return naziv;
    }
}

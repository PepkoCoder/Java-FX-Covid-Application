package hr.java.covidportal.model;

import java.util.List;
import java.util.Set;

/**
 * Predstavlja entitet virus koji nasljeđuje funkcionalnost klase Bolest i implementira sučelje Zarazno.
 * Virus je isto kao i bolest definiran nazivom i listi simptoma.
 */
public class Virus extends Bolest implements Zarazno{

    /**
     * Inicijalizira podatke o nazivu i simptomima virusa.
     * @param naziv Naziv virusa
     * @param simptomi Simptomi virusa
     */
    public Virus(long id, String naziv, List<Simptom> simptomi) {
        super(id, naziv, simptomi);

    }

    /**
     * Postavlja bolest odabrane osobe da bude ovaj virus.
     * @param osoba Odabrana osoba
     */
    @Override
    public void prelazakZarazeNaOsobu(Osoba osoba) {
        osoba.setZarazenBolescu(this);
    }
}

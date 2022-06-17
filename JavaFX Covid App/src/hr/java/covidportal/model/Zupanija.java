package hr.java.covidportal.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Predstavlja entitet županija koji je definiran nazivom i brojem stanovnika.
 */
public class Zupanija extends ImenovaniEntitet{

    private Integer brojStanovnika;
    private Integer zarazeneOsobe;
    private float postotakZarazenosti;

    /**
     * Incijalizira podatke o nazivu i broju stanovnika županije.
     * @param naziv Naziv županije
     * @param brojStanovnika Broj stanovnika županije
     */
    public Zupanija(long id, String naziv, int brojStanovnika, int zarazeneOsobe) {
        super(id, naziv);
        this.brojStanovnika = brojStanovnika;
        this.zarazeneOsobe = zarazeneOsobe;

        postotakZarazenosti = (zarazeneOsobe / (float)brojStanovnika) * 100;
    }

    public Integer getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(Integer brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public Integer getZarazeneOsobe() {
        return zarazeneOsobe;
    }

    public void setZarazeneOsobe(Integer zarazeneOsobe) {
        this.zarazeneOsobe = zarazeneOsobe;
    }

    public float getPostotakZarazenosti() {
        return postotakZarazenosti;
    }

    public void setPostotakZarazenosti(float postotakZarazenosti) {
        this.postotakZarazenosti = postotakZarazenosti;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zupanija)) return false;
        Zupanija zupanija = (Zupanija) o;
        return getId() == zupanija.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return naziv;
    }
}

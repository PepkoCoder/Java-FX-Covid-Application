package hr.java.covidportal.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Predstavlja entitet osobe koja je definirana imenom, prezimenom, starosti, županiji u kojoj živi, bolest kojom je zaražena,
 * te listom kontaktiranih osoba.
 */
public class Osoba implements Serializable {

    /**
     * Builder klasa napravljena za lakše stvaranje novih objekata tipa osoba.
     */
    public static class Builder {
        private long id;
        private String ime, prezime;
        private LocalDate datum_rodenja;
        private Zupanija zupanija;
        private Bolest zarazenBolescu;
        private List<Osoba> kontaktiraneOsobe = new ArrayList<>();

        public Builder(long id) {
            this.id = id;
        }

        /**
         * Postavlja ime i prezime osobe
         * @param ime Ime osobe
         * @param prezime Prezime osobe
         */
        public Builder imena(String ime, String prezime){
            this.ime = ime;
            this.prezime = prezime;
            return this;
        }

        /**
         * Postavlja starost osobe
         * @return
         */
        public Builder godine(LocalDate datum_rodenja){
            this.datum_rodenja = datum_rodenja;
            return this;
        }

        /**
         * Postavlja županiju osobe
         * @param zupanija Županija osobe
         * @return
         */
        public Builder uZupaniji(Zupanija zupanija){
            this.zupanija = zupanija;
            return this;
        }

        /**
         * Postavlja bolest kojom je osoba zaražena.
         * @param bolest Bolest kojom je osoba zaražena
         * @return
         */
        public Builder zarazenSa(Bolest bolest){
            zarazenBolescu = bolest;

            return this;
        }

        /**
         * Postavlja listu kontaktiranih osoba
         * @param osobe Lista kontaktiranih osoba
         * @return
         */
        public Builder uKontaktuSa(List<Osoba> osobe){
            kontaktiraneOsobe = osobe;

            return this;
        }

        /**
         * Stvara novu osobu na temelju prije unesenih podataka
         * @return
         */
        public Osoba build(){
            Osoba osoba = new Osoba(this.id, this.ime, this.prezime, this.datum_rodenja, this.zupanija, this.zarazenBolescu, this.kontaktiraneOsobe);
            return osoba;
        }
    }

    private long id;
    private String ime, prezime;
    private Integer starost;
    private LocalDate datum_rodenja;
    private Zupanija zupanija;
    private Bolest zarazenBolescu;
    private List<Osoba> kontaktiraneOsobe = new ArrayList<>();

    /**
     * Inicijalizira podatke o imenu, prezimenu, starosti, županiji, bolesti i listi kontaktiranih osoba.
     * Ako je osoba zaražena virusom, sve kontaktirane osobe postavlja da su također zaražene tim virusom.
     * @param ime Ime osobe
     * @param prezime Prezime osobe
     * @param zupanija Županija osobe
     * @param zarazenBolescu Bolest osobe
     * @param kontaktiraneOsobe Lista kontaktiranih osoba
     */
    private Osoba(long id, String ime, String prezime, LocalDate datum_rodenja, Zupanija zupanija, Bolest zarazenBolescu, List<Osoba> kontaktiraneOsobe) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.datum_rodenja = datum_rodenja;
        this.zupanija = zupanija;
        this.zarazenBolescu = zarazenBolescu;
        this.kontaktiraneOsobe = kontaktiraneOsobe;

        if(zarazenBolescu instanceof Virus) {
            for (int i = 0; i < kontaktiraneOsobe.size(); i++) {
                ((Virus) zarazenBolescu).prelazakZarazeNaOsobu(kontaktiraneOsobe.get(i));
            }
        }

        LocalDate currentDate = LocalDate.now();

        starost = Period.between(datum_rodenja, currentDate).getYears();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public Integer getStarost() {
        return starost;
    }

    public void setDatum_rodenja(LocalDate datum_rodenja) {
        this.datum_rodenja = datum_rodenja;
    }

    public LocalDate getDatum_rodenja() {
        return datum_rodenja;
    }

    public Zupanija getZupanija() {
        return zupanija;
    }

    public void setZupanija(Zupanija zupanija) {
        this.zupanija = zupanija;
    }

    public Bolest getZarazenBolescu() {
        return zarazenBolescu;
    }

    public void setZarazenBolescu(Bolest zarazenBolescu) {
        this.zarazenBolescu = zarazenBolescu;
    }

    public List<Osoba> getKontaktiraneOsobe() {
        return kontaktiraneOsobe;
    }

    public void setKontaktiraneOsobe(List<Osoba> kontaktiraneOsobe) {
        this.kontaktiraneOsobe = kontaktiraneOsobe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Osoba)) return false;
        Osoba osoba = (Osoba) o;
        return getIme().equals(osoba.getIme()) &&
                getPrezime().equals(osoba.getPrezime()) &&
                getStarost().equals(osoba.getStarost()) &&
                getZupanija().equals(osoba.getZupanija()) &&
                getZarazenBolescu().equals(osoba.getZarazenBolescu()) &&
                getKontaktiraneOsobe().equals(osoba.getKontaktiraneOsobe());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIme(), getPrezime(), getStarost(), getZupanija(), getZarazenBolescu(), getKontaktiraneOsobe());
    }

    @Override
    public String toString() {
        return ime + " " + prezime;
    }
}

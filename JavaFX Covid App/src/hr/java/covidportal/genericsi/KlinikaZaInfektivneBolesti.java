package hr.java.covidportal.genericsi;

import hr.java.covidportal.model.Osoba;
import hr.java.covidportal.model.Virus;

import java.util.ArrayList;
import java.util.List;

public class KlinikaZaInfektivneBolesti <T extends Virus, S extends Osoba> {

    private List<T> listaVirusa = new ArrayList<>();
    private List<S> zarazeneOsobe = new ArrayList<>();

    public KlinikaZaInfektivneBolesti(List<T> listaVirusa, List<S> zarazeneOsobe) {
        this.listaVirusa = listaVirusa;
        this.zarazeneOsobe = zarazeneOsobe;
    }

    public List<T> getListaVirusa() {
        return listaVirusa;
    }

    public void setListaVirusa(List<T> listaVirusa) {
        this.listaVirusa = listaVirusa;
    }

    public void dodajVirus(T virus) {
        listaVirusa.add(virus);
    }

    public List<S> getZarazeneOsobe() {
        return zarazeneOsobe;
    }

    public void setZarazeneOsobe(List<S> zarazeneOsobe) {
        this.zarazeneOsobe = zarazeneOsobe;
    }

    public void dodajOsobu(S osoba) {
        zarazeneOsobe.add(osoba);
    }
}

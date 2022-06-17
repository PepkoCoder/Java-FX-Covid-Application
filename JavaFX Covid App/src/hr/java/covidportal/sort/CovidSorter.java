package hr.java.covidportal.sort;

import hr.java.covidportal.model.Zupanija;

import java.util.Comparator;

public class CovidSorter implements Comparator<Zupanija> {

    @Override
    public int compare(Zupanija o1, Zupanija o2) {
        if(o1.getPostotakZarazenosti() > o2.getPostotakZarazenosti()) return 1;
        else if(o1.getPostotakZarazenosti() < o2.getPostotakZarazenosti()) return -1;
        else return 0;
    }
}

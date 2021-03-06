package hr.java.covidportal.sort;

import hr.java.covidportal.model.Virus;

import java.util.Comparator;

public class VirusSorter implements Comparator<Virus> {

    @Override
    public int compare(Virus o1, Virus o2) {
        return o1.getNaziv().compareTo(o2.getNaziv());
    }
}

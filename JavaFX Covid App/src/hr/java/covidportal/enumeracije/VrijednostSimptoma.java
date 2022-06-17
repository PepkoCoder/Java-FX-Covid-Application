package hr.java.covidportal.enumeracije;

public enum VrijednostSimptoma {

    PRODUKTIVNI ("PRODUKTIVNI"),
    INTENZIVNO ("INTENZIVNO"),
    VISOKA ("VISOKA"),
    JAKA ("JAKA");

    private String vrijednost;

    private VrijednostSimptoma(String vrijednost) {
        this.vrijednost = vrijednost;
    }

    public String getVrijednost() {
        return vrijednost;
    }
}

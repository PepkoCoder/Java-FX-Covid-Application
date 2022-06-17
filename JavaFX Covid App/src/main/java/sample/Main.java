package main.java.sample;

import hr.java.covidportal.BazaPodataka;
import hr.java.covidportal.model.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main extends Application {

    private static final String ZUPANIJE_FILENAME = "dat/zupanije.txt";
    private static final String SIMPTOMI_FILENAME = "dat/simptomi.txt";
    private static final String BOLESTI_FILENAME = "dat/bolesti.txt";
    private static final String VIRUSI_FILENAME = "dat/virusi.txt";
    private static final String OSOBE_FILENAME = "dat/osobe.txt";

    private static List<Zupanija> zupanije = new ArrayList<>();
    private static List<Simptom> simptomi = new ArrayList<>();
    private static List<Bolest> bolesti = new ArrayList<>();
    private static List<Bolest> virusi = new ArrayList<>();
    private static List<Osoba> osobe = new ArrayList<>();

    private static ObservableList<Zupanija> observableListZupanije;
    private static ObservableList<Simptom> observableListSimptomi;
    private static ObservableList<Bolest> observableListBolesti;
    private static ObservableList<Bolest> observableListVirusi;
    private static ObservableList<Osoba> observableListOsobe;

    private static Stage mainStage;
    private static BazaPodataka database;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root =
                FXMLLoader.load(getClass().getClassLoader().getResource(
                        "pocetniEkran.fxml"));
        primaryStage.setTitle("Aplikacija za zarazne bolesti");
        primaryStage.setScene(new Scene(root, 450, 275));
        primaryStage.show();

        mainStage = primaryStage;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) throws SQLException, IOException {

        database = new BazaPodataka();
        ucitajSve();
        launch(args);
    }

    public static void ucitajSve() throws SQLException, IOException{
        ucitajZupanije();
        ucitajSimptome();
        ucitajBolesti();
        ucitajViruse();
        ucitajOsobe();
    }

    public static void ucitajZupanije() throws SQLException, IOException {
        zupanije = database.dohvatiSveZupanije();
        observableListZupanije = FXCollections.observableList(zupanije);
    }

    public static void ucitajSimptome() throws SQLException, IOException{
        simptomi = database.dohvatiSveSimptome();
        observableListSimptomi = FXCollections.observableList(simptomi);
    }

    public static void ucitajBolesti() throws SQLException, IOException{
        bolesti = database.dohvatiSveBolesti(false);
        observableListBolesti = FXCollections.observableList(bolesti);
    }

    public static void ucitajViruse() throws SQLException, IOException{
        virusi = database.dohvatiSveBolesti(true);
        observableListVirusi = FXCollections.observableList(virusi);
    }

    public static void ucitajOsobe() throws SQLException, IOException{
        osobe = database.dohvatiSveOsobe();
        observableListOsobe = FXCollections.observableList(osobe);
    }

    public static ObservableList<Zupanija> getObservableListZupanije() {
        return observableListZupanije;
    }

    public static ObservableList<Simptom> getObservableListSimptomi() {
        return observableListSimptomi;
    }

    public static ObservableList<Bolest> getObservableListBolesti() {
        return observableListBolesti;
    }

    public static ObservableList<Bolest> getObservableListVirusi() {
        return observableListVirusi;
    }

    public static ObservableList<Osoba> getObservableListOsobe() {
        return observableListOsobe;
    }

    public static BazaPodataka getDatabase() {
        return database;
    }

    /*static void UnosZupanija() {
        System.out.println("Učitavanje podataka o županijama...");

        Path datoteka = Path.of(ZUPANIJE_FILENAME);
        try {
            String tekst = Files.readString(datoteka);
            String[] linije = tekst.split("\\r?\\n");

            for(int i = 0; i < linije.length; i += 4){

                long id = 0;
                String naziv = "";
                int brojStanovnika = 0;
                int brojZarazenih = 0;

                id = Long.parseLong(linije[i]);
                naziv = linije[i+1];
                brojStanovnika = Integer.parseInt(linije[i+2]);
                brojZarazenih = Integer.parseInt(linije[i+3]);

                Zupanija zup = new Zupanija(id, naziv, brojStanovnika, brojZarazenih);
                zupanije.add(zup);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static void UnosSimptoma() {
        System.out.println("Učitavanje podataka o simptomima...");

        Path datoteka = Path.of(SIMPTOMI_FILENAME);
        try {
            String tekst = Files.readString(datoteka);
            String[] linije = tekst.split("\\r?\\n");

            for(int i = 0; i < linije.length; i += 3){

                long id = 0;
                String naziv = "";
                String vrijednost = "";

                id = Long.parseLong(linije[i]);
                naziv = linije[i+1];
                vrijednost = linije[i+2];

                simptomi.add(new Simptom(id, naziv, vrijednost));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void UnosBolesti() {
        System.out.println("Učitavanje podataka o bolestima...");

        Path datoteka = Path.of(BOLESTI_FILENAME);
        try {
            String tekst = Files.readString(datoteka);
            String[] linije = tekst.split("\\r?\\n");

            for(int i = 0; i < linije.length; i += 3){

                long id = 0;
                String naziv = "";
                String[] simptomIDs;

                id = Long.parseLong(linije[i]);
                naziv = linije[i+1];
                simptomIDs = linije[i+2].split(",");

                Set<Simptom> simptoms = new HashSet<>();

                for(String simpId : simptomIDs) {
                    long ID = Long.parseLong(simpId);

                    for(Simptom s : simptomi) {
                        if(s.getId() == ID) {
                            simptoms.add(s);
                            break;
                        }
                    }
                }

                bolesti.add(new Bolest(id, naziv, simptoms));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void UnosVirusa() {
        System.out.println("Učitavanje podataka o virusima...");

        Path datoteka = Path.of(VIRUSI_FILENAME);
        try {
            String tekst = Files.readString(datoteka);
            String[] linije = tekst.split("\\r?\\n");

            for(int i = 0; i < linije.length; i += 3){

                long id = 0;
                String naziv = "";
                String[] simptomIDs;

                id = Long.parseLong(linije[i]);
                naziv = linije[i+1];
                simptomIDs = linije[i+2].split(",");

                Set<Simptom> simptoms = new HashSet<>();

                for(String simpId : simptomIDs) {
                    long ID = Long.parseLong(simpId);

                    for(Simptom s : simptomi) {
                        if(s.getId() == ID) {
                            simptoms.add(s);
                            break;
                        }
                    }
                }

                virusi.add(new Virus(id, naziv, simptoms));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void UnosOsoba() {
        System.out.println("Učitavanje podataka o osobama...");

        Path datoteka = Path.of(OSOBE_FILENAME);
        try {
            String tekst = Files.readString(datoteka);
            String[] linije = tekst.split("\\r?\\n");

            for(int i = 0; i < linije.length; i += 7){

                long id = 0;
                String ime = "", prezime = "";
                int starost = 0;
                Zupanija zupanija = null;
                Bolest bolest = null;
                List<Osoba> kontakti = new ArrayList<>();

                id = Long.parseLong(linije[i]);
                ime = linije[i+1];
                prezime = linije[i+2];
                starost = Integer.parseInt(linije[i+3]);

                //Traženje županije
                long zupId = Long.parseLong(linije[i+4]);
                for(Zupanija z : zupanije) {
                    if(z.getId() == zupId) {
                        zupanija = z;
                        break;
                    }
                }

                //Traženje bolesti
                long bolId = Long.parseLong(linije[i+5]);
                for(Bolest b : bolesti) {
                    if(b.getId() == bolId) {
                        bolest = b;
                        break;
                    }
                }

                if(bolest == null){
                    for(Virus v : virusi) {
                        if(v.getId() == bolId) {
                            bolest = v;
                            break;
                        }
                    }
                }

                //Traženje kontakata
                String[] ids = linije[i+6].split(",");
                for(String ID : ids) {
                    long kontaktID = Long.parseLong(ID);

                    for(Osoba o : osobe) {
                        if(o.getId() == kontaktID) {
                            kontakti.add(o);
                            break;
                        }
                    }
                }

                Osoba osoba = new Osoba.Builder(id)
                        .imena(ime, prezime)
                        .godine(starost)
                        .uZupaniji(zupanija)
                        .zarazenSa(bolest)
                        .uKontaktuSa(kontakti)
                        .build();

                osobe.add(osoba);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
}
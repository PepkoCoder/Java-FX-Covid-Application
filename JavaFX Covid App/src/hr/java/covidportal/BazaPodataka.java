package hr.java.covidportal;

import hr.java.covidportal.enumeracije.VrijednostSimptoma;
import hr.java.covidportal.model.Bolest;
import hr.java.covidportal.model.Osoba;
import hr.java.covidportal.model.Simptom;
import hr.java.covidportal.model.Zupanija;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.sql.Date;


public class BazaPodataka {

    private static final String DATABASE_FILE = "database.properties";

    public static Connection connect() throws SQLException, IOException {

        Properties svojstva = new Properties();

        svojstva.load(new FileReader(DATABASE_FILE));

        String urlBazePodataka = svojstva.getProperty("bazaPodatakaUrl");
        String korisnickoIme = svojstva.getProperty("korisnickoIme");
        String lozinka = svojstva.getProperty("lozinka");

        Connection veza = DriverManager.getConnection(urlBazePodataka,
                korisnickoIme,lozinka);

        return veza;
    }

    public void disconnect(Connection veza) throws SQLException {
        veza.close();
    }

    public List<Simptom> dohvatiSveSimptome() throws SQLException, IOException {

        Connection veza = connect();

        List<Simptom> listaSimptoma = new ArrayList<>();

        Statement stmt = veza.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM SIMPTOM");

        while (rs.next()) {
            int id = rs.getInt("id");
            String naziv = rs.getString("naziv");
            String vrijednost = rs.getString("vrijednost").toUpperCase();

            Simptom simptom = new Simptom(id, naziv, vrijednost);

            listaSimptoma.add(simptom);
        }

        disconnect(veza);
        return listaSimptoma;
    }

    public Simptom dohvatiSimptom(int id) throws SQLException, IOException {

        Connection veza = connect();

        PreparedStatement findSimptom = veza.prepareStatement("SELECT * FROM SIMPTOM WHERE ID = ?");

        findSimptom.setInt(1, id);
        ResultSet rs = findSimptom.executeQuery();

        String naziv = rs.getString("naziv");
        String vrijednost = rs.getString("vrijednost").toUpperCase();

        Simptom simptom = new Simptom(id, naziv, vrijednost);

        disconnect(veza);
        return simptom;
    }

    public void spremiSimptom(Simptom s) throws SQLException, IOException {

        Connection veza = connect();
        PreparedStatement upit =
                veza.prepareStatement(
                        "INSERT INTO SIMPTOM(naziv, vrijednost) VALUES(?, ?)");

        upit.setString(1, s.getNaziv());
        upit.setString(2, s.getVrijednost().toString());
        upit.executeUpdate();

        disconnect(veza);
    }

    public List<Bolest> dohvatiSveBolesti(boolean virusi) throws SQLException, IOException {

        Connection veza = connect();

        List<Bolest> listaBolesti = new ArrayList<>();

        Statement stmt = veza.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM BOLEST");

        while (rs.next()) {
            int id = rs.getInt("id");
            String naziv = rs.getString("naziv");
            boolean virus = rs.getBoolean("virus");

            if(virus == virusi) {

                List<Simptom> simptomi = new ArrayList<>();
                List<Simptom> sviSimptomi = dohvatiSveSimptome();

                Connection veza2 = connect();
                Statement statement = veza2.createStatement();
                ResultSet res = statement.executeQuery("SELECT * FROM BOLEST_SIMPTOM");

                while (res.next()) {
                    int id_bolest = res.getInt("bolest_id");
                    int id_simptom = res.getInt("simptom_id");

                    if(id_bolest == id){
                        Optional<Simptom> sim = sviSimptomi.stream().filter(s -> s.getId() == id_simptom).findFirst();

                        try {
                            simptomi.add(sim.get());
                        } catch (NoSuchElementException e) {
                            e.printStackTrace();
                        }
                    }
                }

                disconnect(veza2);

                Bolest bolest = new Bolest(id, naziv, simptomi);
                listaBolesti.add(bolest);
            }
        }

        disconnect(veza);
        return listaBolesti;
    }

    public Bolest dohvatiBolest(int id) throws SQLException, IOException {

        Connection veza = connect();

        PreparedStatement findBolest = veza.prepareStatement("SELECT * FROM BOLEST WHERE ID = ?");

        findBolest.setInt(1, id);
        ResultSet rs = findBolest.executeQuery();

        String naziv = rs.getString("naziv");
        boolean virus = rs.getBoolean("virus");

        List<Simptom> simptomi = new ArrayList<>();
        List<Simptom> sviSimptomi = dohvatiSveSimptome();

        Connection veza2 = connect();
        Statement statement = veza2.createStatement();
        ResultSet res = statement.executeQuery("SELECT * FROM BOLEST_SIMPTOM");

        while (res.next()) {
            int id_bolest = res.getInt("bolest_id");
            int id_simptom = res.getInt("simptom_id");

            if(id_bolest == id){
                Optional<Simptom> sim = sviSimptomi.stream().filter(s -> s.getId() == id_simptom).findFirst();

                try {
                    simptomi.add(sim.get());
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                }
            }
        }

        disconnect(veza2);

        Bolest bolest = new Bolest(id, naziv, simptomi);

        disconnect(veza);

        return bolest;
    }

    public void spremiBolest(Bolest b, boolean v) throws SQLException, IOException {

        Connection veza = connect();
        PreparedStatement upit =
                veza.prepareStatement(
                        "INSERT INTO BOLEST(naziv, virus) VALUES(?, ?)");

        upit.setString(1, b.getNaziv());
        upit.setBoolean(2, v);
        upit.executeUpdate();

        for(Simptom s : b.getSimptomi()) {

            Connection veza2 = connect();
            PreparedStatement upit2 =
                    veza2.prepareStatement(
                            "INSERT INTO BOLEST_SIMPTOM(bolest_id, simptom_id) VALUES(?, ?)");

            upit2.setLong(1, b.getId());
            upit2.setLong(2, s.getId());
            upit2.executeUpdate();

            disconnect(veza2);
        }

        disconnect(veza);
    }

    public List<Zupanija> dohvatiSveZupanije() throws SQLException, IOException {

        Connection veza = connect();

        List<Zupanija> listaZupanija = new ArrayList<>();

        Statement stmt = veza.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM ZUPANIJA");

        while (rs.next()) {
            int id = rs.getInt("id");
            String naziv = rs.getString("naziv");
            int brojStanovnika = rs.getInt("broj_stanovnika");
            int brojZarazenih = rs.getInt("broj_zarazenih_stanovnika");

            Zupanija zup = new Zupanija(id, naziv, brojStanovnika, brojZarazenih);

            listaZupanija.add(zup);
        }

        disconnect(veza);
        return listaZupanija;
    }

    public Zupanija dohvatiZupaniju(int id) throws SQLException, IOException {

        Connection veza = connect();

        PreparedStatement findZupanija = veza.prepareStatement("SELECT * FROM BOLEST WHERE ID = ?");

        findZupanija.setInt(1, id);
        ResultSet rs = findZupanija.executeQuery();

        String naziv = rs.getString("naziv");
        int brojStanovnika = rs.getInt("broj_stanovnika");
        int brojZarazenih = rs.getInt("broj_zarazenih_stanovnika");

        Zupanija zup = new Zupanija(id, naziv, brojStanovnika, brojZarazenih);

        disconnect(veza);

        return zup;
    }

    public void spremiZupaniju(Zupanija z) throws SQLException, IOException {
        Connection veza = connect();
        PreparedStatement upit =
                veza.prepareStatement(
                        "INSERT INTO ZUPANIJA(naziv, broj_stanovnika, broj_zarazenih_stanovnika) VALUES(?, ?, ?)");

        upit.setString(1, z.getNaziv());
        upit.setInt(2, z.getBrojStanovnika());
        upit.setInt(3, z.getZarazeneOsobe());
        upit.executeUpdate();

        disconnect(veza);
    }

    public List<Osoba> dohvatiSveOsobe() throws SQLException, IOException {

        Connection veza = connect();

        List<Osoba> listaOsoba = new ArrayList<>();

        Statement stmt = veza.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM OSOBA");

        while (rs.next()) {
            int id = rs.getInt("id");
            String ime = rs.getString("ime");
            String prezime = rs.getString("prezime");

            Date date = (Date) rs.getDate("datum_rodjenja");
            Instant instant = Instant.ofEpochMilli(date.getTime());
            LocalDate localDate = LocalDateTime.ofInstant(
                    instant, ZoneId.systemDefault()).toLocalDate();

            int bolest_id = rs.getInt("bolest_id");
            int zupanija_id = rs.getInt("zupanija_id");

            //Traženje bolesti
            List<Bolest> bolesti = dohvatiSveBolesti(true);
            bolesti.addAll(dohvatiSveBolesti(false));

            Optional<Bolest> bo = bolesti.stream().filter(b -> b.getId() == bolest_id).findFirst();
            Bolest bol = bo.get();

            //Traženje županije
            List<Zupanija> zupanije = dohvatiSveZupanije();

            Optional<Zupanija> zu = zupanije.stream().filter(z -> z.getId() == zupanija_id).findFirst();
            Zupanija zup = zu.get();

            Osoba o = new Osoba.Builder(id).imena(ime, prezime).godine(localDate).uZupaniji(zup).zarazenSa(bol).build();
            listaOsoba.add(o);
        }

        disconnect(veza);

        for(Osoba o : listaOsoba) {

            //Traženje kontakata
            List<Osoba> kontakti = new ArrayList<>();

            Connection veza2 = connect();
            Statement stmt2 = veza2.createStatement();
            ResultSet rs2 = stmt2.executeQuery("SELECT * FROM KONTAKTIRANE_OSOBE");

            while(rs2.next()) {
                int osoba_id = rs2.getInt("osoba_id");
                int kontakt_id = rs2.getInt("kontaktirana_osoba_id");

                if(osoba_id == o.getId()) {
                    Optional<Osoba> osoba = listaOsoba.stream().filter(os -> os.getId() == kontakt_id).findFirst();
                    kontakti.add(osoba.get());
                }
            }

            disconnect(veza2);

            o.setKontaktiraneOsobe(kontakti);
        }

        return listaOsoba;
    }

    public Osoba dohvatiOsobu(int id) throws SQLException, IOException {
        Connection veza = connect();

        PreparedStatement findOsoba = veza.prepareStatement("SELECT * FROM BOLEST WHERE ID = ?");

        findOsoba.setInt(1, id);
        ResultSet rs = findOsoba.executeQuery();

        String ime = rs.getString("ime");
        String prezime = rs.getString("prezime");

        Date date = (Date) rs.getDate("datum_rodjenja");
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDate localDate = LocalDateTime.ofInstant(
                instant, ZoneId.systemDefault()).toLocalDate();

        int bolest_id = rs.getInt("bolest_id");
        int zupanija_id = rs.getInt("zupanija_id");

        //Traženje bolesti
        List<Bolest> bolesti = dohvatiSveBolesti(true);
        bolesti.addAll(dohvatiSveBolesti(false));

        Optional<Bolest> bo = bolesti.stream().filter(b -> b.getId() == bolest_id).findFirst();
        Bolest bol = bo.get();

        //Traženje županije
        List<Zupanija> zupanije = dohvatiSveZupanije();

        Optional<Zupanija> zu = zupanije.stream().filter(z -> z.getId() == zupanija_id).findFirst();
        Zupanija zup = zu.get();

        Osoba o = new Osoba.Builder(id).imena(ime, prezime).godine(localDate).uZupaniji(zup).zarazenSa(bol).build();

        //Traženje kontakata
        List<Osoba> kontakti = new ArrayList<>();
        List<Osoba> listaOsoba = dohvatiSveOsobe();

        Connection veza2 = connect();
        Statement stmt2 = veza2.createStatement();
        ResultSet rs2 = stmt2.executeQuery("SELECT * FROM KONTAKTIRANE_OSOBE");

        while(rs2.next()) {
            int osoba_id = rs2.getInt("osoba_id");
            int kontakt_id = rs2.getInt("kontaktirana_osoba_id");

            if(osoba_id == o.getId()) {
                Optional<Osoba> osoba = listaOsoba.stream().filter(os -> os.getId() == kontakt_id).findFirst();
                kontakti.add(osoba.get());
            }
        }

        disconnect(veza2);

        o.setKontaktiraneOsobe(kontakti);

        disconnect(veza);

        return o;
    }

    public void spremiOsobu(Osoba o) throws SQLException, IOException {

        Connection veza = connect();
        PreparedStatement upit =
                veza.prepareStatement(
                        "INSERT INTO OSOBA(ime, prezime, datum_rodjenja, zupanija_id, bolest_id) VALUES(?, ?, ?, ?, ?)");

        upit.setString(1, o.getIme());
        upit.setString(2, o.getPrezime());
        upit.setDate(3, Date.valueOf(o.getDatum_rodenja()));
        upit.setLong(4, o.getZupanija().getId());
        upit.setLong(5, o.getZarazenBolescu().getId());
        upit.executeUpdate();

        for(Osoba k : o.getKontaktiraneOsobe()) {
            Connection veza2 = connect();
            PreparedStatement upit2 =
                    veza2.prepareStatement(
                            "INSERT INTO KONTAKTIRANE_OSOBE(osoba_id, kontaktirana_osoba_id) VALUES(?, ?)");

            upit2.setLong(1, o.getId());
            upit2.setLong(2, k.getId());
            upit2.executeUpdate();

            disconnect(veza2);
        }


        disconnect(veza);
    }

}

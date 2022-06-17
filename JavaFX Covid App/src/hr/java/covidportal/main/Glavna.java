package hr.java.covidportal.main;

import hr.java.covidportal.genericsi.KlinikaZaInfektivneBolesti;
import hr.java.covidportal.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import hr.java.covidportal.sort.VirusSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Glavna {

    private static int kolicinaZupanija = 0;
    private static int kolicinaOsoba = 0;
    private static int kolicinaBolesti = 0;
    private static int kolicinaSimptoma = 0;

    private static final String ZUPANIJE_FILENAME = "dat/zupanije.txt";
    private static final String SIMPTOMI_FILENAME = "dat/simptomi.txt";
    private static final String BOLESTI_FILENAME = "dat/bolesti.txt";
    private static final String VIRUSI_FILENAME = "dat/virusi.txt";
    private static final String OSOBE_FILENAME = "dat/osobe.txt";

    private static List<Zupanija> zupanije = new ArrayList<>();
    private static List<Simptom> simptomi = new ArrayList<>();
    private static List<Bolest> bolesti = new ArrayList<>();
    private static List<Osoba> osobe = new ArrayList<>();

    private static Map<Bolest, List<Osoba>> osobePoBolestima = new HashMap<>();

    private static KlinikaZaInfektivneBolesti<Virus, Osoba> klinikaZaInfektivneBolesti;

    private static Scanner input;
    private static final Logger logger = LoggerFactory.getLogger(Glavna.class);

    public static final void main(String[] args){
        input = new Scanner(System.in);

        UnosZupanija();
        UnosSimptoma();
        UnosBolesti();
        UnosVirusa();
        UnosOsoba();

        System.out.println("\n\n Popis osoba: ");

        kolicinaOsoba = osobe.size();
        for(int i = 0; i < kolicinaOsoba; i++){
            System.out.println((i+1) + ". Osoba:");
            System.out.println("Ime i prezime: " + osobe.get(i).getIme() + " " + osobe.get(i).getPrezime());
            System.out.println("Starost: " + osobe.get(i).getStarost());
            System.out.println("Županija prebivališta: " + osobe.get(i).getZupanija().getNaziv());
            System.out.println("Zaražen bolešću: " + osobe.get(i).getZarazenBolescu().getNaziv());
            System.out.println("Kontaktirane osobe: ");

            int kOsobe = osobe.get(i).getKontaktiraneOsobe().size();

            if(kOsobe == 0) System.out.println("Nema kontaktiranih osoba!");
            else {
                for(int j = 0; j < kOsobe; j++){
                    Osoba o = osobe.get(i).getKontaktiraneOsobe().get(j);
                    System.out.println(o.getIme() + " " + o.getPrezime());
                }
            }

            System.out.println("\n");
        }

        //Setupiranje i ispis mape osoba po bolestima
        for(Bolest b : bolesti) {

            List<Osoba> zarazeneOsobe = new ArrayList<>();
            for(Osoba o : osobe) {
                if (o.getZarazenBolescu() == b) zarazeneOsobe.add(o);
            }

            osobePoBolestima.put(b, zarazeneOsobe);
        }

        for(Bolest b : osobePoBolestima.keySet()) {

            String tip = (b instanceof Virus) ? "Zarazeni virusom " : "Oboljeli od bolesti ";

            System.out.print(tip + b.getNaziv() + ": ");

            for(Osoba o : osobePoBolestima.get(b)) {
                System.out.print(o.getIme() + " " + o.getPrezime() + ", ");
            }

            System.out.println("\n");
        }

        Zupanija max = MaxPostotakZarazenosti();
        System.out.println("Županija sa najvećim postotkom zaraženosti je: " + max.getNaziv() + " sa " + max.getPostotakZarazenosti() + "% zaraženih");

        SortirajViruseIIspisi();

        FiltrirajOsobe();

        BrojSimptoma();

        SpremiZupanije();
    }

    private static void SpremiZupanije() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("dat/ZupanijeSaVelikimPostotkomZarazenosti.dat"));

            for(Zupanija z : zupanije) {
                if(z.getPostotakZarazenosti() > 2) {
                    out.writeObject(z);
                }
            }
            out.close();
        } catch (IOException exc) {
            System.out.println("Error");
        }
    }

    private static void FiltrirajOsobe() {

        System.out.print("Unesite string za pretragu po prezimenu: ");
        String str = input.nextLine();

        System.out.println("Osobe čija prezimena sadrže " + str + " su:");
        List<Osoba> filtriraneOsobe = osobe.stream().filter(o -> o.getPrezime().contains(str)).collect(Collectors.toList());

        if(filtriraneOsobe.isEmpty()){
            System.out.println("Osobe nisu nađene!");
        } else {
            filtriraneOsobe.forEach(System.out::println);
        }
    }

    static void BrojSimptoma() {
        bolesti.stream().map(b -> b.getNaziv() + " " + b.getSimptomi().size()).forEach(System.out::println);
    }

    static void SortirajViruseIIspisi() {
        List<Osoba> zarazeneOsobe = osobe.stream().filter(o -> o.getZarazenBolescu() instanceof Virus).collect(Collectors.toList());

        List<Virus> sviVirusi = new ArrayList<>();
        bolesti.stream().filter(b -> b instanceof Virus).forEach(b -> sviVirusi.add((Virus)b));

        klinikaZaInfektivneBolesti = new KlinikaZaInfektivneBolesti<>(sviVirusi, zarazeneOsobe);

        List<Virus> virusi = klinikaZaInfektivneBolesti.getListaVirusa();

        Instant start = Instant.now();
        virusi.stream().sorted(Comparator.comparing(Virus::getNaziv).reversed());
        Instant end = Instant.now();
        Duration lambda = Duration.between(start, end);

        Instant start2 = Instant.now();
        Collections.sort(virusi, new VirusSorter());
        Collections.reverse(virusi);
        Instant end2 = Instant.now();
        Duration lista = Duration.between(start2, end2);

        System.out.println("Sortiranje virusa bez lambdi traje " + lista + ", a s lambdama " + lambda + ".");

        System.out.println("Virusi sortirani po nazivu suprotno od poretka abecede:");
        virusi.stream().forEach(System.out::println);


    }

    static Zupanija MaxPostotakZarazenosti() {
        return zupanije.stream().max(Comparator.comparing(Zupanija::getPostotakZarazenosti)).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Obavlja unos određenog broja županije
     */
    /*static void UnosZupanija(){
        logger.info("Započet unos županija");

        do {
            System.out.print("Koliko županija želite unijeti: ");
            kolicinaZupanija = input.nextInt();
            input.nextLine();
        } while (kolicinaZupanija <= 0);

        System.out.println("Unesite podatke o " + kolicinaZupanija + " županije.");
        for(int i = 0; i < kolicinaZupanija; i++) {
            String naziv;
            Integer brojStanovnika = 0;
            Integer brojZarazenihStanovnika = 0;

            System.out.print("Unesite naziv " + (i + 1) + ". županije: ");
            naziv = input.nextLine();

            boolean loop = false;
            do {
                try {
                    System.out.print("Unesite broj stanovnika " + (i + 1) + ". županije: ");
                    brojStanovnika = input.nextInt();
                    input.nextLine();
                    loop = false;
                } catch (InputMismatchException ex1){
                    System.out.println("Unesite brojčane vrijednosti!");
                    logger.error("Nije upisana brojčana vrijednost za broj stanovnika " + (i+1) + ". županije");
                    input.nextLine();
                    loop = true;
                }
            } while(loop);

            do {
                System.out.print("Unesite broj zaraženih stanovnika " + (i + 1) + ". županije: ");
                brojZarazenihStanovnika = input.nextInt();
                input.nextLine();
            } while(brojZarazenihStanovnika < 0 || brojZarazenihStanovnika > brojStanovnika);

            Zupanija zup = new Zupanija(naziv, brojStanovnika, brojZarazenihStanovnika);
            zupanije.add(zup);
        }
    }*/
    static void UnosZupanija() {
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

    /**
     * Obavlja unos određenog broja simptoma
     */
    /*static void UnosSimptoma(){
        logger.info("Započet unos simptoma");

        do {
            System.out.print("Koliko simptoma želite unijeti: ");
            kolicinaSimptoma = input.nextInt();
            input.nextLine();
        } while (kolicinaSimptoma <= 0);

        System.out.println("Unesite podatke o " + kolicinaSimptoma + " simptoma.");
        for(int i = 0; i < kolicinaSimptoma; i++) {
            String naziv, vrijednost;

            System.out.print("Unesite naziv " + (i + 1) + ". simptoma: ");
            naziv = input.nextLine();

            do {
                System.out.print("Unesite vrijednost " + (i+1) + " simptoma (RIJETKO, SREDNJE ILI ČESTO): ");
                vrijednost = input.nextLine().toUpperCase();

                if(!vrijednost.equals("RIJETKO") && !vrijednost.equals("SREDNJE") && !vrijednost.equals("ČESTO")){
                    System.out.println("Vrijednost simptoma mora biti RIJETKO, SREDNJE ili ČESTO");
                    logger.error("Unesena kriva vrijednost " + (i+1)  + ". simptoma");
                }

            } while (!vrijednost.equals("RIJETKO") && !vrijednost.equals("SREDNJE") && !vrijednost.equals("ČESTO"));

            Simptom simp = new Simptom(naziv, vrijednost);
            simptomi.add(simp);
            logger.info("Kreiran je simptom");
        }
    }*/
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

    /**
     * Obavlja unos određenog broja bolesti
     */
    /*static void UnosBolesti(){
        logger.info("Započet unos bolesti");

        do {
            System.out.print("Koliko bolesti želite unijeti: ");
            kolicinaBolesti = input.nextInt();
            input.nextLine();
        } while (kolicinaBolesti <= 0);

        System.out.println("Unesite podatke o " + kolicinaBolesti + " bolesti ili virusa.");
        for(int i = 0; i < kolicinaBolesti; i++) {
            String naziv;
            int brojSimptoma = 0;
            Set<Simptom> simp = new HashSet<>();

            boolean virus = false;
            System.out.println("Unosite li bolest ili virus?");
            System.out.println("1) Bolest");
            System.out.println("2) Virus");
            int b = 0;

            boolean nastaviOdabir = false;
            do{
                try {
                    b = input.nextInt();
                    input.nextLine();
                    nastaviOdabir = false;
                } catch (InputMismatchException ex1){
                    System.out.println("Unesite brojčane vrijednosti!");
                    logger.error("Nije unesena brojčana vrijednost kod odabira " + (i+1) + ". virusa ili bolesti");
                    input.nextLine();
                    nastaviOdabir = true;
                }
            } while (nastaviOdabir);

            if(b == 2) virus = true;
            String tip = (virus) ? "virusa" : "bolesti";

            boolean ponovniUnos = false;
            do {
                System.out.print("Unesite naziv " + (i + 1) + ". " + tip + ": ");
                naziv = input.nextLine();

                nastaviOdabir = false;
                do {
                    try {
                        System.out.print("Unesite broj simptoma: ");
                        brojSimptoma = input.nextInt();
                        nastaviOdabir = false;
                    } catch (InputMismatchException ex1) {
                        System.out.println("Unesite brojčane vrijednosti!");
                        logger.error("Nije unesena brojčana vrijednost kod unosa broja simptoma " + (i+1) + ". bolesti");
                        input.nextLine();
                        nastaviOdabir = true;
                    }
                } while (brojSimptoma <= 0 || nastaviOdabir);

                //Unos Simptoma
                for(int j = 0; j < brojSimptoma; j++){
                    System.out.println("Odaberite " + (j+1) + ". simptom");

                    int izabraniSimptom = 0;
                    nastaviOdabir = false;
                    do {
                        int index = 0;
                        for(Simptom s : simptomi) {
                            index++;
                            System.out.println(index + ") "+ s.getNaziv() + " " + s.getVrijednost());
                        }

                        try {
                            System.out.print("Odabir: ");
                            izabraniSimptom = input.nextInt();
                            input.nextLine();
                            nastaviOdabir = false;
                        } catch (InputMismatchException ex1) {
                            System.out.println("Unesite brojčane vrijednosti!");
                            input.nextLine();
                            nastaviOdabir = true;
                        }

                        if(izabraniSimptom < 0 || izabraniSimptom > kolicinaSimptoma) {
                            System.out.println("Izaberite simptom između 1 i " + kolicinaSimptoma + "!");
                        }

                    } while (izabraniSimptom < 0 || izabraniSimptom > kolicinaSimptoma || nastaviOdabir);

                    int index = 0;
                    for(Simptom s : simptomi) {
                        if(index == izabraniSimptom-1) {
                            simp.add(s);
                        }
                        index++;
                    }
                }

                boolean unesenaIstaBolest = false;
                try {
                    unesenaIstaBolest = UnesenaIstaBolest((virus) ? new Virus(naziv, simp) : new Bolest(naziv, simp));
                    if(unesenaIstaBolest)
                        throw new BolestIstihSimptoma("Pogrešan unos, već ste unijeli bolest ili virus s istim simptomima. Molimo ponovite unos.");
                } catch (BolestIstihSimptoma ex1){
                    System.out.println(ex1.getMessage());
                    logger.error("Kod unosa " + (i+1) + " bolesti primjećeno je da je već upisana bolest sa istim simptomima");
                }
                ponovniUnos = unesenaIstaBolest;

            } while (ponovniUnos);

            if(virus) {
                Virus vir = new Virus(naziv, simp);
                bolesti.add(vir);
                logger.info("Kreiran je virus");
            }
            else {
                Bolest bol = new Bolest(naziv, simp);
                bolesti.add(bol);
                logger.info("Kreirana je bolest");
            }
        }
    }*/
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

                List<Simptom> simptoms = new ArrayList<>();

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

                List<Simptom> simptoms = new ArrayList<>();

                for(String simpId : simptomIDs) {
                    long ID = Long.parseLong(simpId);

                    for(Simptom s : simptomi) {
                        if(s.getId() == ID) {
                            simptoms.add(s);
                            break;
                        }
                    }
                }

                bolesti.add(new Virus(id, naziv, simptoms));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Obavlja unos određenog broja osoba
     */
    /*static void UnosOsobe() {
        logger.info("Započet je unos osoba");

        do {
            System.out.print("Koliko osoba želite unijeti: ");
            kolicinaOsoba = input.nextInt();
            input.nextLine();
        } while (kolicinaOsoba <= 0);

        System.out.println("Unesite podatke o " + kolicinaOsoba + " osobe.");
        for(int i = 0; i < kolicinaOsoba; i++) {
            String ime, prezime;
            Integer starost = 0;
            Zupanija odabranaZupanija = null;
            Bolest odabranaBolest = null;

            System.out.println("Unesite podatke " + (i + 1) + ". osobe: ");
            System.out.print("Ime: ");
            ime = input.nextLine();
            System.out.print("Prezime: ");
            prezime = input.nextLine();

            boolean nastaviOdabir = false;
            do {
                try {
                    System.out.print("Starost: ");
                    starost = input.nextInt();
                    input.nextLine();
                    nastaviOdabir = false;
                } catch (InputMismatchException ex1){
                    System.out.println("Unesite brojčane vrijednosti!");
                    logger.error("Nije unesena brojčana vrijednost kod upisa starosti " + (i+1) + ". osobe");
                    input.nextLine();
                    nastaviOdabir = true;
                }
            } while (nastaviOdabir);


            //Unos Zupanije
            int brojZupanije = 0;
            nastaviOdabir = false;
            do {
                System.out.println("Županija prebivališta: ");

                int index = 0;
                for(Zupanija zup : zupanije) {
                    index++;
                    System.out.println(index + ". " + zup.getNaziv());
                }

                try {
                    System.out.print("Odabir: ");
                    brojZupanije = input.nextInt();
                    input.nextLine();
                    nastaviOdabir = false;
                } catch (InputMismatchException ex1) {
                    System.out.println("Unesite brojčane vrijednosti!");
                    logger.error("Nije unesena brojčana vrijednost kod odabira županije " + (i+1) + ". osobe");
                    input.nextLine();
                    nastaviOdabir = true;
                }

                if(brojZupanije < 0 || brojZupanije > kolicinaZupanija){
                    System.out.println("Izaberite zupanije između brojeva 1 i " + kolicinaZupanija + "!");
                }

            } while (brojZupanije < 0 || brojZupanije > kolicinaZupanija || nastaviOdabir);

            int index = 0;
            for(Zupanija z : zupanije) {
                if(index == brojZupanije-1) {
                    odabranaZupanija = z;
                }
                index++;
            }
            //

            //Unos Bolesti
            int brojBolesti = 0;
            nastaviOdabir = false;
            do {
                System.out.println("Bolest osobe: ");

                index = 0;
                for(Bolest bol : bolesti) {
                    index++;
                    System.out.println(index + ". " + bol.getNaziv());
                }

                try {
                    System.out.print("Odabir: ");
                    brojBolesti = input.nextInt();
                    input.nextLine();
                    nastaviOdabir = false;
                } catch (InputMismatchException ex1) {
                    System.out.println("Unesite brojčane vrijednosti!");
                    logger.error("Nije unesena brojčana vrijednost kod odabira bolesti " + (i+1) + ". osobe");
                    input.nextLine();
                    nastaviOdabir = true;
                }

                if(brojBolesti < 0 || brojBolesti > kolicinaBolesti){
                    System.out.println("Izaberite bolest između brojeva 1 i " + kolicinaBolesti + "!");
                }

            } while (brojBolesti < 0 || brojBolesti > kolicinaBolesti || nastaviOdabir);

            index = 0;
            for(Bolest b : bolesti) {
                if(index == brojBolesti-1) {
                    odabranaBolest = b;
                }
                index++;
            }
            //

            //Unos Osoba
            int brojOsoba = osobe.size();

            if(brojOsoba == 0){
                Osoba osoba = new Osoba.Builder(ime, prezime)
                        .godine(starost)
                        .uZupaniji(odabranaZupanija)
                        .zarazenSa(odabranaBolest)
                        .uKontaktuSa(new ArrayList<Osoba>())
                        .build();
                osobe.add(osoba);
            }
            else {
                int osobeUKontaktu = 0;
                nastaviOdabir = false;
                do {

                    try {
                        System.out.println("Unesite broj osoba koje su bile u kontaktu s tom osobom: ");
                        osobeUKontaktu = input.nextInt();
                        input.nextLine();
                        nastaviOdabir = false;
                    } catch (InputMismatchException ex1) {
                        System.out.println("Unesite brojčane vrijednosti!");
                        logger.error("Nije unesena brojčana vrijednost kod upisa broja kontaktiranih osoba " + (i+1) + ". osobe");
                        input.nextLine();
                        nastaviOdabir = true;
                    }
                } while (osobeUKontaktu < 0 || osobeUKontaktu > brojOsoba || nastaviOdabir);

                List<Osoba> kontaktiraneOsobe = new ArrayList<>();

                for(int j = 0; j < osobeUKontaktu; j++){

                    int brojOdabraneOsobe = 0;
                    nastaviOdabir = false;
                    boolean odabranDuplikat = false;
                    do {
                        System.out.println("Odaberite " + (j+1) + " osobu: ");

                        for(int k = 0; k < brojOsoba; k++){
                            System.out.println((k+1) + ". " + osobe.get(k).getIme() + " " + osobe.get(k).getPrezime());
                        }

                        try {
                            System.out.print("Odabir: ");
                            brojOdabraneOsobe = input.nextInt();
                            input.nextLine();
                            nastaviOdabir = false;
                        } catch (InputMismatchException ex1) {
                            System.out.println("Unesite brojčane vrijednosti!");
                            logger.error("Nije unesena brojčana vrijednost kod odabira kontaktirane osobe " + (i+1) + ". osobe");
                            input.nextLine();
                            nastaviOdabir = true;
                        }

                        if(brojOdabraneOsobe < 0 || brojOdabraneOsobe > brojOsoba){
                            System.out.println("Izaberite osobu između brojeva 1 i " + brojOsoba + "!");
                        }

                        try {
                            odabranDuplikat = OdabranDuplikatKontaktiraneOsobe(osobe.get(brojOdabraneOsobe-1), kontaktiraneOsobe);
                            if(odabranDuplikat)
                                throw new DuplikatKontaktiraneOsobe("Odabrana osoba je već u listi kontaktiranih osoba!");
                        } catch (DuplikatKontaktiraneOsobe ex1) {
                            System.out.println(ex1.getMessage());
                            logger.error("Kod odabira kontaktiranih osoba odabrana je osoba koja je već u kontaktima " + (i+1) + ". osobe");
                        }

                    } while (brojOdabraneOsobe < 0 || brojOdabraneOsobe > brojOsoba || nastaviOdabir || odabranDuplikat);

                    kontaktiraneOsobe.add(osobe.get(brojOdabraneOsobe-1));

                }

                Osoba osoba = new Osoba.Builder(ime, prezime)
                        .godine(starost)
                        .uZupaniji(odabranaZupanija)
                        .zarazenSa(odabranaBolest)
                        .uKontaktuSa(kontaktiraneOsobe)
                        .build();
                osobe.add(osoba);
                logger.info("Kreirana je osoba");
            }
            //
        }
    }*/
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

                LocalDate date = LocalDate.now();

                Osoba osoba = new Osoba.Builder(id)
                        .imena(ime, prezime)
                        .godine(date)
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

    /**
     * Provjerava je li odabrana osoba koja se već nalazi u listi kontaktiranih osoba.
     * @param o Odabrana osoba
     * @param kontaktiraneOsobe Lista kontaktiranih osoba
     * @return Vraća true ako je odabrana osoba duplikat
     */
    static boolean OdabranDuplikatKontaktiraneOsobe(Osoba o, List<Osoba> kontaktiraneOsobe) {

        for (int i = 0; i < kontaktiraneOsobe.size(); i++){
            if(kontaktiraneOsobe.get(i) != null) {
                if(kontaktiraneOsobe.get(i) == o) return true;
            }
        }

        return false;
    }

    /**
     * Provjerava postoji li već bolest sa istim simptomima kao unesena bolest
     * @param b Unesena bolest.
     * @return Vraća true ako već postoji bolest sa istim simptomima
     */
    static boolean UnesenaIstaBolest(Bolest b) {
        int brojBolesti = bolesti.size();

        if(brojBolesti == 0) return false;

        System.out.println(b.getSimptomi().size());

        for(Bolest bol : bolesti){

            if(bol.getSimptomi().size() == b.getSimptomi().size()) {

                if(b instanceof Virus) {
                    if(!(bol instanceof Virus)) return false;
                }

                int istiSimptomi = 0;
                for(Simptom bs : b.getSimptomi()) {
                    for(Simptom s : bol.getSimptomi()) {

                        if(bs == s) {
                            istiSimptomi++;
                        }
                    }
                }

                if(istiSimptomi == b.getSimptomi().size()) return true;
            }
        }

        return false;
    }
}

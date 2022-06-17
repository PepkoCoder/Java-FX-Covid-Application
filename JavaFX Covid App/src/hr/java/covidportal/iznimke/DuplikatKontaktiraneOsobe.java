package hr.java.covidportal.iznimke;

/**
 * Iznimka koja se baca kada je odabrana osoba koja se već nalazi u listi kontaktiranih osoba
 */
public class DuplikatKontaktiraneOsobe extends Exception{

    /**
     * Kontruktor koji prima samo poruku o iznimci
     * @param message
     */
    public DuplikatKontaktiraneOsobe(String message) {
        super(message);
    }

    /**
     * Kontruktor koji prima i poruku o iznimci i Throwable cause
     * @param message
     * @param cause
     */
    public DuplikatKontaktiraneOsobe(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Kontruktor koji prima samo Throwable cause
     * @param cause
     */
    public DuplikatKontaktiraneOsobe(Throwable cause) {
        super(cause);
    }

}

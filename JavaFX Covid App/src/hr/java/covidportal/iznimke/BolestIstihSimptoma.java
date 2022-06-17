package hr.java.covidportal.iznimke;

/**
 * Iznimka koja se baca kada već postoji bolest sa istim simptomima kao unesena bolest.
 */
public class BolestIstihSimptoma extends RuntimeException{

    /**
     * Kontruktor koji prima samo poruku o iznimci
     * @param message
     */
    public BolestIstihSimptoma(String message) {
        super(message);
    }

    /**
     * Kontruktor koji prima i poruku o iznimci i Throwable cause
     * @param message
     * @param cause
     */
    public BolestIstihSimptoma(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Kontruktor koji prima samo Throwable cause
     * @param cause
     */
    public BolestIstihSimptoma(Throwable cause) {
        super(cause);
    }

}

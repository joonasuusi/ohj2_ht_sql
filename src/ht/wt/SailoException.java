package ht.wt;

/**
 * Poikkeusluokka tietorakenteessa aiheutuville poikkeuksille.
 * @author Joonas Uusi-Autti & Sini Lällä
 * @version 27.2.2020
 *
 */
public class SailoException extends Exception {
    private static final long serialVersionUID = 1L;
    
    /**
     * Poikkeuksen muodostaja jolle tuuaan poikkeuksen viesti
     * @param viesti Poikkeuksen viesti
     */
    public SailoException (String viesti) {
        super(viesti);
    }
}
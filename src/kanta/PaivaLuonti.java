package kanta;

/**
 * @author Joonas Uusi-Autti
 * @version 27.2.2020
 *
 */
public class PaivaLuonti {
    
    
    /**
     * Arvotaan satunnainen kokonaisluku välille [ala, ylä]
     * @param ala arvonnan alaraja
     * @param yla arvonnan yläraja
     * @return satunnainen luku väliltä [ala, ylä]
     */
    public static int rand(int ala, int yla) {
        double n = (yla-ala)*Math.random() + ala;
        return (int)Math.round(n);   
    }
    
    
    /**
     * Arvotaan satunnainen päivä
     * @return satunnainen päivä
     */
    public static String arvoPaiva() {
        String apuPaiva = String.format("%02d", rand(1,28)) + "." +
                          String.format("%02d", rand(1,12)) + "." +  
                          String.format("%04d", rand(1900,2020));
        return apuPaiva;
    }
}

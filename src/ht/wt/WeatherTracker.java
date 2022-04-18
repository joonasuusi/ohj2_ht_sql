/**
 * 
 */
package ht.wt;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**                                                
 * Huolehtii Paivat ja Saatilat luokkien välisestä  
 * yhteistyöstä ja välittää näitä tietoja pyydettäessä                                     
 * Lukee ja kirjoittaa WeatherTracker tiedostoon
 * Avustajat: Paivat, Saatilat, Paiva, Saatila      
 * @author Joonas Uusi-Autti & Sini Lällä
 * @version 1.3.2020
 *
 */
public class WeatherTracker {
    /*
     * Alustuksia ja puhdistuksia testiä varten
     * @example
     * <pre name="testJAVA">
     * #import java.io.*;
     * #import java.util.*;
     * 
     * private WeatherTracker weathertracker;
     * private String tiedNimi;
     * private File ftied;
     * 
     * @Before
     * public void alusta() throws SailoException { 
     *    weathertracker = new WeatherTracker();
     *    tiedNimi = "testiweathertracker";
     *    ftied = new File(tiedNimi+".db");
     *    ftied.delete();
     *    weathertracker.lueTiedostosta(tiedNimi);
     * }   
     *
     * @After
     * public void siivoa() {
     *    ftied.delete();
     * }   
     * </pre>
     */ 
    
    private Paivat paivat;
    private Saatilat saatilat;
 

    /**
     * @param args ei käytössä
     */
    public static void main(String[] args) {
    
        try {
            new File("kokeilu.db").delete();
            WeatherTracker weathertracker = new WeatherTracker();
            weathertracker.lueTiedostosta("kokeilu");

            Paiva pvm = new Paiva(), pvm1 = new Paiva();
            pvm.taytaPvmTiedoilla();
            pvm1.taytaPvmTiedoilla();
            
            weathertracker.lisaa(pvm);
            weathertracker.lisaa(pvm1);
            //int id1 = pvm.getSaatila();
            //int id2 = pvm1.getSaatila();
            
            System.out.println("=============== Päivän sää ===============");
            Collection<Paiva> paivat = weathertracker.etsi("", -1);
            int i = 0;
            for (Paiva paiva : paivat) {
                System.out.println("Päivän id: " + i);
                paiva.tulosta(System.out);
                i++;
            }
        } catch (SailoException e) {
            e.printStackTrace();
        }

        new File("kokeilu.db").delete();
    }
    
    
    /**
     * Lisätään uusi päivä
     * @param paiva lisättävä
     * @throws SailoException jos ei mahu
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * Paiva pvm1 = new Paiva(); pvm1.taytaPvmTiedoilla(); 
     * Paiva pvm2 = new Paiva(); pvm2.taytaPvmTiedoilla(); 
     * weathertracker.lisaa(pvm1);
     * Collection<Paiva> loytyneet = weathertracker.etsi("", -1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === pvm1;
     * weathertracker.lisaa(pvm2);
     * loytyneet = kerho.etsi("", -1);
     * loytyneet.size() === 2;
     * Iterator<Paiva> it = loytyneet.iterator(); 
     * it.next() === pvm1;
     * it.next() === pvm2;
     * </pre>
     */
    public void lisaa(Paiva paiva) throws SailoException {
       paivat.lisaa(paiva); 
    }
    
    
    /**
     * Lisätään säätila
     * @param saatila lisätään
     * @throws SailoException jos ei mahu
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * Paiva pvm = new Paiva(); pvm.taytaPvmTiedoilla(); 
     * weathertracker.lisaa(pvm);
     * Saatila saa = new Saatila();
     * saa.paivaSaa(pvm.getTunnusNro());
     * weathertracker.lisaa(saa);
     *  weathertracker.annaSaatilat(pvm).get(0) === saa;
     * </pre>
     */
    public void lisaa(Saatila saatila) throws SailoException {
        saatilat.lisaa(saatila); 
     }
    
    
    /**
     * Tallentaa molemmat tiedostot
     * @throws SailoException jos joku menee pieleen
     */
    public void tallenna() throws SailoException {
        return;
    }

    /**
     * Luo tietokannan jos annettu tiedosto on olemassa ja sisältää
     * tarvitut taulut, ei luoda mitään
     * @param nimi tietokannan nimi
     * @throws SailoException jos joku menee pieleen
     */
    public void lueTiedostosta(String nimi) throws SailoException {
        paivat = new Paivat(nimi);
        saatilat = new Saatilat(nimi);   
    }

    
    /**
     * Palauttaa päivät listassa
     * @param hakuehto hakuehto
     * @param k etsittävän kentän indeksi
     * @return tietorakenteen löytyvistä päivistä
     * @throws SailoException jos ei mahu
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * Paiva pvm1 = new Paiva(); pvm1.taytaPvmTiedoilla(); 
     * Paiva pvm2 = new Paiva(); pvm2.taytaPvmTiedoilla();  
     * paivat.lisaa(pvm1);
     * paivat.lisaa(pvm2);
     * paivat.lisaa(pvm2);  #THROWS SailoException  // ei saa lisätä sama id:tä uudelleen
     * Collection<Paiva> loytyneet = weathertracker.etsi(pvm1.getPvm(), 1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === pvm1;
     * loytyneet = weathertracker.etsi(pvm2.getPvm(), 1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === pvm2;
     * weathertracker.etsi("", 15); #THROWS SailoException
     * </pre>
     */
    public Collection<Paiva> etsi(String hakuehto, int k) throws SailoException {
        return paivat.etsi(hakuehto, k);
    }


    /**
     * Poistetaan halutun päivän tiedot
     * @param paiva joka halutaan poistaa
     * @return montako päivää poistettiin
     */
    public int poista(Paiva paiva) {
        return 0;

    }

    /**
     * Haetaan päivän säätila
     * @param paiva päivä jolle säätila haetaan
     * @return Tietorakenne jossa viitteet löydettyihin  säätiloihin
     * @throws SailoException jostain menee pieleen
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * Paiva pvm1 = new Paiva(); pvm1.taytaPvmTiedoilla();
     * weathertracker.lisaa(pvm1);
     * Saatila saa = new Saatila(); 
     * saa.paivanSaa(pvm1.getTunnusNro()); 
     * weathertracker.lisaa(saa);
     * weathertracker.annaSaatilat(pvm1).get(0) === saa;
     *
     * Paiva pvm2 = new Paiva(); pvm2.taytaPvmTiedoilla(); 
     * weathertracker.lisaa(pvm2);
     * weathertracker.annaSaatilat(pvm2).size() === 0;
     * </pre>
     */
    public List<Saatila> annaSaatilat(Paiva paiva) throws SailoException {
        return saatilat.annaSaatilat(paiva.getTunnusNro());
    }

}
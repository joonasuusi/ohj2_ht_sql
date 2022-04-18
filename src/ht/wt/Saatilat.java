/**
 * 
 */
package ht.wt;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ht.wt.Kanta.alustaKanta;

 /**
 * Pitää yllä varsinaista saatilarekisteria eli osaa lisätä ja poistaa säätilan               
 * Lukee ja kirjoittaa saatila tiedostoon        
 * Osaa etsiä ja lajitella
 * Avustaja: Saatila
 * @author Joonas Uusi-Autti ja Sini Lällä
 * @version 2.3.2020
 *
 */
public class Saatilat {
    /*
     * Alustuksia ja puhdistuksia testiä varten
     * @example
     * <pre name="testJAVA">
     * #import java.io.*;
     * #import java.util.*;
     * 
     * private Saatilat saatilat;
     * private String tiedNimi;
     * private File ftied;
     * 
     * @Before
     * public void alusta() throws SailoException { 
     *    tiedNimi = "testiweathertracker";
     *    ftied = new File(tiedNimi+".db");
     *    ftied.delete();
     *    saatilat = new Saatilat(tiedNimi);
     * }   
     *
     * @After
     * public void siivoa() {
     *    ftied.delete();
     * }   
     * </pre>
     */ 
    
    private static Saatila apuSaatila = new Saatila();
    private Kanta kanta;
    
    
    /**
     * Tarkistetaan että tietokannassa säätilojen tarvitsema taulu
     * @param nimi tietokannan nimi
     * @throws SailoException jos ei mahu
     */
    public Saatilat(String nimi) throws SailoException {
        kanta = alustaKanta(nimi);
        try (Connection con = kanta.annaKantayhteys()) {
            DatabaseMetaData meta = con.getMetaData();
            try (ResultSet taulu = meta.getTables(null, null, "Saatilat", null)) {
                if (!taulu.next()) {
                    try (PreparedStatement sql = con.prepareStatement(apuSaatila.annaLuontilauseke())) {
                        sql.execute();
                    }
                }
            }
        } catch(SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa: " +  e.getMessage());
        }
        
    }
    
   
    /**
     * Lisää uuden säätilan tietorakenteeseen
     * @param saa lisättävä säätila
     * @throws SailoException jos jotain menee pieleen
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * </pre>
     */
    public void lisaa(Saatila saa) throws SailoException {
        try ( Connection con = kanta.annaKantayhteys(); PreparedStatement sql = saa.annaLisayslauseke(con) ) {
            sql.executeUpdate();
            try ( ResultSet rs = sql.getGeneratedKeys() ) {
                saa.tarkistaId(rs);
             }   
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    

    /**
     * @param args ei käytössä
     */
    public static void main(String[] args) {;
    try {
        Saatilat saatilat = new Saatilat("kokeilu");
        Saatila saa = new Saatila();
        saa.paivanSaa(2);
        Saatila saa1 = new Saatila();
        saa1.paivanSaa(1);
        Saatila saa2 = new Saatila();
        saa2.paivanSaa(2);
        Saatila saa3 = new Saatila();
        saa3.paivanSaa(2);

        saatilat.lisaa(saa);
        saatilat.lisaa(saa1);
        saatilat.lisaa(saa2);
        saatilat.lisaa(saa3);
        saatilat.lisaa(saa2);
        
        System.out.println("============= Säätilat testi =================");

        List<Saatila> saatilat2;
        
        saatilat2 = saatilat.annaSaatilat(2);
        

        for (Saatila saat : saatilat2) {
            System.out.print(saat.getId() + " ");
            saat.tulosta(System.out);
        }
        
        new File("kokeilu.db").delete();
        } catch (SailoException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Haetaan säätilat
     * @param i Päivän tunnusnumero jolle haetaan
     * @return Tietorakenne jossa viitteet löydettyihin säätiloihin
     * @throws SailoException jos jotain menee pieleen
     * @example
     * <pre name="test">
     * #THROWS SailoException
     *  
     *  Saatila saa1 = new Saatila(1); saa1.paivanSaa(1); saatilat.lisaa(saa1);
     *  Saatila saa2 = new Saatila(2); saa2.paivanSaa(2); saatilat.lisaa(saa2);
     *  Saatila saa3 = new Saatila(3); saa3.paivanSaa(3); saatilat.lisaa(saa3);
     *  Saatila saa4 = new Saatila(4); saa4.paivanSaa(4); saatilat.lisaa(saa4);
     *  Saatila saa5 = new Saatila(5); saa5.paivanSaa(5); saatilat.lisaa(saa5);
     *  Saatila saa6 = new Saatila(6); saa6.paivanSaa(6); saatilat.lisaa(saa6);
     *  
     *  
     *  List<Saatila> loytyneet;
     *  loytyneet = saatilat.annaSaatilat(3);
     *  loytyneet.size() === 1; 
     *  loytyneet = saatlat.annaSaatilat(1);
     *  loytyneet.size() === 1; 
     *  
     *  loytyneet.get(0) === saa3;
     *  loytyneet.get(1) === saa2;
     *  
     *  loytyneet = saatilat.annaSaatilat(5);
     *  loytyneet.size() === 1; 
     *  loytyneet.get(0) === saa5;
     * </pre> 
     */
    public List<Saatila> annaSaatilat(int i) throws SailoException {
        List<Saatila> loydetyt = new ArrayList<Saatila>();
        
        try ( Connection con = kanta.annaKantayhteys();
              PreparedStatement sql = con.prepareStatement("SELECT * FROM Saatilat WHERE saaId = ?")
                ) {
            sql.setInt(1, i);
            try ( ResultSet tulokset = sql.executeQuery() )  {
                while ( tulokset.next() ) {
                    Saatila saa = new Saatila();
                    saa.parse(tulokset);
                    loydetyt.add(saa);
                }
            }
            
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
        return loydetyt;
    }   
    
}
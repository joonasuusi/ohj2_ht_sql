/**
 * 
 */
package ht.wt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import fi.jyu.mit.ohj2.WildChars;

import static ht.wt.Kanta.alustaKanta;

 /**
 * Pitää yllä varsinaista paivarekisteriä eli osaa lisätä ja poistaa päivän                         
 * Lukee ja kirjoittaa paivan tiedostoon            
 * Osaa etsiä ja lajitella
 * Avustaja: Paiva     
 * @author Joonas Uusi-Autti & Sini Lällä
 * @version 27.2.2020
 *
 */
public class Paivat { //implements Iterable<Paiva> {
    /*
     * Alustuksia ja puhdistuksia testiä varten
     * @example
     * <pre name="testJAVA">
     * #import java.io.*;
     * #import java.util.*;
     * 
     * private Paivat paivat;
     * private String tiedNimi;
     * private File ftied;
     * 
     * @Before
     * public void alusta() throws SailoException { 
     *    tiedNimi = "testiweathertracker";
     *    ftied = new File(tiedNimi+".db");
     *    ftied.delete();
     *    paivat = new Paivat(tiedNimi);
     * }   
     *
     * @After
     * public void siivoa() {
     *    ftied.delete();
     * }   
     * </pre>
     */ 
    
    private Kanta kanta;
    private static Paiva apuPaiva = new Paiva();


    /**
     * Tarkistetaan että kannassa päivien tarvitsema taulu
     * @param nimi tietokannan nimi
     * @throws SailoException jos jokin menee pieleen
     */
    public Paivat(String nimi) throws SailoException {
        kanta = alustaKanta(nimi);
        try ( Connection con = kanta.annaKantayhteys() ) {
            DatabaseMetaData meta = con.getMetaData();
            
            try ( ResultSet taulu = meta.getTables(null, null, "Paivat", null) ) {
                if ( !taulu.next() ) {
                    try ( PreparedStatement sql = con.prepareStatement(apuPaiva.annaLuontilauseke()) ) {
                        sql.execute();
                    }
                }
            }
            
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }
    
    /**
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        try {
            new File("kokeilu.db").delete();
            Paivat paivat = new Paivat("kokeilu");
            Paiva pvm = new Paiva(), pvm1 = new Paiva();

            pvm.taytaPvmTiedoilla();
            pvm1.taytaPvmTiedoilla();
            
            paivat.lisaa(pvm);
            paivat.lisaa(pvm1);
            pvm1.tulosta(System.out);
            
            System.out.println("=============== Päivän sää ===============");
            int i = 0;
            for (Paiva paiva : paivat.etsi("", -1)) {
                System.out.println("Päivämäärä: " + i++);
                paiva.tulosta(System.out);
            }
            new File("kokeilu.db").delete();
        } catch ( SailoException ex ) {
            System.out.println(ex.getMessage());
        }
           
    }

    
    /**
     * Lisää uuden päivämäärän tietorakenteeeseen.
     * Ottaa päivämäärän omistukseensa.
     * @param paiva Lisättävän päivämäärän viite. Huom. tietorakenne muuttuu omistajaksi.
     * @throws SailoException jos ei mahu
     * @example
     * <pre name="test">
     * #THROWS SailoException 
     * 
     * Collection<Paiva> loytyneet = paivat.etsi("", 1);
     * loytyneet.size() === 0;
     * 
     * Paiva pvm1 = new Paiva(), pvm2 = new Paiva();
     * paivat.lisaa(pvm1); 
     * paivat.lisaa(pvm2);
     *  
     * loytyneet = paivat.etsi("", 1);
     * loytyneet.size() === 2;
     * 
     * // Unique constraint ei hyväksy
     * paivat.lisaa(pvm1); #THROWS SailoException
     * Paiva pvm3 = new Paiva(); Paiva pvm4 = new Paiva(); Paiva pvm5 = new Paiva();
     * paivat.lisaa(pvm3); 
     * paivat.lisaa(pvm4); 
     * paivat.lisaa(pvm5); 

     * loytyneet = paivat.etsi("", 1);
     * loytyneet.size() === 5;
     * Iterator<Paiva> i = loytyneet.iterator();
     * i.next() === pvm1;
     * i.next() === pvm2;
     * i.next() === pvm3;
     * </pre>
     */
    public void lisaa(Paiva paiva) throws SailoException {
        try ( Connection con = kanta.annaKantayhteys(); PreparedStatement sql = paiva.annaLisayslauseke(con) ) {
            sql.executeUpdate();
            try ( ResultSet rs = sql.getGeneratedKeys() ) {
               paiva.tarkistaId(rs);
            }   
            
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }


    /**
     * palauttaa päivät listassa
     * @param hakuehto hakuehto
     * @param k etsittävän kentän indeksi
     * @return päivät listassa
     * @throws SailoException jos ei mahu
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * Paiva pvm1 = new Paiva(); pvm1.taytaPvmTiedoilla(); 
     * Paiva pvm2 = new Paiva(); pvm2.taytaPvmTiedoilla(); 
     * paivat.lisaa(pvm1);
     * paivat.lisaa(pvm2);
     * paivat.lisaa(pvm2);  #THROWS SailoException  // ei saa lisätä sama id:tä uudelleen
     * Collection<Paiva> loytyneet = paivat.etsi(pvm1.getPvm(), 1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === pvm1;
     * loytyneet = paivat.etsi(pvm2.getPvm(), 1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === pvm2;
     * loytyneet = paivat.etsi("", 15); #THROWS SailoException
     *
     * ftied.delete();
     * </pre>
     */
    public Collection<Paiva> etsi(String hakuehto, int k) throws SailoException {
        String ehto = hakuehto;
        String kysymys = apuPaiva.getKysymys(k);
        if ( k < 0 ) { kysymys = apuPaiva.getKysymys(0); ehto = ""; }
        try ( Connection con = kanta.annaKantayhteys();
              PreparedStatement sql = con.prepareStatement("SELECT * FROM Paivat WHERE " + kysymys + " LIKE ?") ) {
            ArrayList<Paiva> loytyneet = new ArrayList<Paiva>();
            
            sql.setString(1, "%" + ehto + "%");
            try ( ResultSet tulokset = sql.executeQuery() ) {
                while ( tulokset.next() ) {
                    Paiva p = new Paiva();
                    p.parse(tulokset);
                    loytyneet.add(p);
                }
            }
            return loytyneet;
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }

}
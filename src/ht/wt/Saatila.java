/**
 * 
 */
package ht.wt;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fi.jyu.mit.ohj2.Mjonot;

 /**
 * Tietää säätilojen kentät.                   
 * Osaa tarkistaa tietyn kentän oikeellisuuden (syntaksin)                                 
 * Osaa muuttaa 3|Puolipilvinen -merkkijonon   säätilan tiedoiksi.                         
 * Osaa antaa merkkijonona i:n kentän tiedot   
 * Osaa laittaa merkkijonon i:neksi kentäksi
 * @author Joonas Uusi-Autti & Sini Lällä
 * @version 2.3.2020
 *
 */
public class Saatila {
    private String saatila;
    private int id;
    private int paivanNro;
    private static int seuraavaId = 1;
    
    /**
     * Alustetaan sää
     */
    public Saatila() {
       // 
    }
    
    /**
     * Alustetaan tietyn päivän säätila
     * @param paivanNro päivän numero
     */
    public Saatila(int paivanNro) {
        this.paivanNro = paivanNro;
    }
    
    /**
     * Antaa säätilalle seuraaavan id-numeron
     * @return säätilan uusi id-numeron
     * @example
     * <pre name="test">
     * Paiva paiva1 = new Paiva();
     * paiva1.getTunnusNro() === 0;
     * paiva1.rekisteroi();
     * Paiva paiva2 = new Paiva();
     * paiva2.rekisteroi();
     * int n1 = paiva1.getTunnusNro();
     * int n2 = paiva2.getTunnusNro();
     * n1 === n2-1;
     * </pre>
     */
    public int rekisteroi() {
        id = seuraavaId;
        seuraavaId++;
        return id;
    }
    
    
    /**
     * Palauttaa säätilan tiedot merkkijonona, jonka voi tallentaa tiedostoon.
     * @return säätila tolppaeroteltuna merkkijonona
     * @example
     * <pre name="test">
     * Saatila saa = new Saatila("");
     * saa.parse("3  |    aurinkoinen");
     * saa.toString().startsWith("3|aurinkoinen|") === true;
     * </pre>
     */
    @Override
    public String toString() {
        return "" + 
                getId() + "|" +
                saatila + "|";
    }
    
    
    /**
     * @param rivi jota luetaan
     */
    public void parse(String rivi) {
        StringBuilder sb = new StringBuilder(rivi);
        setTunnusId(Mjonot.erota(sb, '|', getId()));
        saatila = Mjonot.erota(sb, '|', saatila);
        
    }


    /**
     * Arvotaan satunnainen kokonaisluku välille [ala, ylä]
     * @param ala arvonnan alaraja
     * @param yla arvonnan yläraja
     * @return satunnainen luku väliltä [ala, ylä]
     */
    public int rand(int ala, int yla) {
        double n = (yla-ala)*Math.random() + ala;
        return (int)Math.round(n);
    }
    

    /**
     * @return palauttaa säätilan
     */
    public String getSaatila() {
        return saatila;
    }
    
    
    /**
     * @return palauttaa säätilan id:n
     */
    public int getId() {
        return id;
    }
    
    /**
     * asettaa tunnusnron ja samalla varmistaa että
     * seuraava on numero on aina suurempi kuin tähän mennessä suurin
     * @param nro asetettava tunnusnrro
     */
    public void setTunnusId(int nro) {
        id = nro;
        if (id >= seuraavaId)
            seuraavaId = id+1;
    }
    
    
    /**
     * Tulostetaan päivän tiedot
     * @param out mihin virtaa tulostetaan
     */
    public void tulosta(PrintStream out) {
        out.println("Säätila:           " + saatila);
    } 

    
    /**
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        Saatila saa = new Saatila();
        saa.paivanSaa(2);
        saa.tulosta(System.out);
    }

    
    /**
     * Antaa säätilan lisäyslausekkeen
     * @param con tietokanta yhteys
     * @return Säätilan lisäyslauseke
     * @throws SQLException jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaLisayslauseke(Connection con)
        throws SQLException {
    PreparedStatement sql = con.prepareStatement(
            "INSERT INTO Saatilat (saaId, paivanID, saatila) VALUES (?, ?, ?)");
    
    if ( id != 0 ) sql.setInt(1, id); else sql.setString(1, null);
    sql.setInt(2, paivanNro);
    sql.setString(3, saatila);
    
        return sql;
    }

    /**
     * Antaa tietokannan luontilausekkeen Saatilat taululle
     * @return palauttaa Saatila taulun luontilausekkeen
     */
    public String annaLuontilauseke() {
        return "CREATE TABLE Saatilat (" +
                "saaId INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "paivanID INTEGER, " + 
                "saatila VARCHAR(100) NOT NULL, " +
                "FOREIGN KEY (paivanID) REFERENCES Paivat(paivanID)" +
                ")";
    }

    /**
     * Tarkistetaan onko id muuttunut lisäyksessä
     * @param rs lisäyslauseen ResultSet
     * @throws SQLException jos tulee jotakin vikaa
     */
    public void tarkistaId(ResultSet rs) throws SQLException {
        if ( !rs.next() ) return;
        int sid = rs.getInt(1);
        if ( sid == id ) return;
        setTunnusId(id);
    }

    /**
     * @param nro säätilan numero
     */
    public void paivanSaa(int nro) {
        paivanNro = nro;
        saatila = "Säätila " + nro + " on, aurinkoinen " + nro; 
    }

    /**
     * Otetaan tiedot resultsetisdtä
     * @param tulokset Mistä tiedot otetaan
     * @throws SQLException jos jokin menee vikaan
     */
    public void parse(ResultSet tulokset) throws SQLException {
        setTunnusId(tulokset.getInt("saaId"));
        saatila = tulokset.getString("saatila");
    }    
}
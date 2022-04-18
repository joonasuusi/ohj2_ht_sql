package ht.wt;

import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;

import fi.jyu.mit.ohj2.Mjonot;

import static kanta.PaivaLuonti.*;

 /**
 * Tietää päivän kentät(pvm, paikka, kellonaika, lämpötila,säätila jne.                          
 * Osaa tarkistaa tietyn kentän oikeellisuuden (syntaksin).                                    
 * Osaa muuttaa 08.01.2020|Jyväskylä|06:20|..| -merkkijonon päivän tiedoiksi.                  
 * Osaa antaa merkkijonona i:n kentän tideot       
 * Osaa laittaa merkkijonon i:neksi kentäksi  
 * @author Joonas Uusi-Autti & Sini Lällä
 * @version 27.2.2020
 *
 */
public class Paiva {
    
    private int tunnusNro;
    private static int seuraavaNro = 1;
    private String pvm = "";
    private String paikka = "";
    private String kello = "";
    private double alinlampo = 0.0;
    private double ylinlampo = 0.0;
    private double sademaara = 0.0; 
    private String huomiot = "";
    private int saatila;
    
    
    /**
     * Hakee päivämäärän
     * @return palauttaa päivämäärän
     */
    public String getPvm() {
        return pvm;
    }


    /**
     * Hakee paikan
     * @return palauttaa paikan
     */
    public String getPaikka() {
        return paikka;
    } 
    
 
    /**
     * Hakee kellonajan
     * @return palauttaa kellonajan
     */
    public String getKello() {
        return kello;
    }
    
    
    /**
     * Hakee alimman lämpötilan
     * @return palauttaa alimman lämpötilan
     */
    public double getAlinLampo() {
        return alinlampo;
    }
    
    /**
     * Hakee ylimmän lämpötilan
     * @return palauttaa ylimmän lämpötilan
     */
    public double getYlinLampo() {
        return ylinlampo;
    }
    
    /**
     * Hakee sademäärän
     * @return palauttaa sademäärän
     */
    public double getSademaara() {
        return sademaara;
    }
    
    /**
     * Hakee päivän huomiot
     * @return palauttaa päivän huomiot
     */
    public String getHuomiot() {
        return huomiot;
    }
    
    /**
     * @return palauttaa säätilan
     */
    public int getSaatila() {
        return saatila;
    }
    
    /**
     * Palauttaa päivän tiedot merkkijonona, jonka voi tallentaa tiedostoon.
     * @return päivä tolppaeroteltuna merkkijonona
     * @example
     * <pre name="test">
     * Paiva paiva = new Paiva();
     * paiva.parse("3  |    12.3.2020   |   Orivesi       |  07:18");
     * paiva.toString().startsWith("3|12.3.2020|Orivesi|07:18|") === true;
     * </pre>
     */
    @Override
    public String toString() {
        return "" + 
                getTunnusNro() + "|" +
                pvm + "|" +
                paikka + "|" +
                kello + "|" +
                alinlampo + "|" +
                ylinlampo + "|" +
                sademaara + "|" +
                huomiot + "|" +
                saatila + "|";
    }
    
    
    /**
     * Luetaan tiedoston rivit
     * @param rivi rivi jota luetaan 
     */
    public void parse(String rivi) {
        StringBuilder sb = new StringBuilder(rivi);
        setTunnusNro(Mjonot.erota(sb, '|', getTunnusNro()));
        pvm = Mjonot.erota(sb, '|', pvm);
        paikka = Mjonot.erota(sb, '|', paikka);
        kello = Mjonot.erota(sb, '|', kello);
        alinlampo = Mjonot.erota(sb, '|', alinlampo);
        ylinlampo = Mjonot.erota(sb, '|', ylinlampo);
        sademaara = Mjonot.erota(sb, '|', sademaara);
        huomiot = Mjonot.erota(sb, '|', huomiot);
        saatila = Mjonot.erota(sb, '|', saatila);
    }
    
    
    /**
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        Paiva pvm = new Paiva();
        Paiva pvm1 = new Paiva();
        Paiva pvm2 = new Paiva();
        
        pvm.taytaPvmTiedoilla();
        pvm.tulosta(System.out);
        
        pvm1.taytaPvmTiedoilla();
        pvm1.tulosta(System.out);
        
        pvm2.taytaPvmTiedoilla();
        pvm2.tulosta(System.out); 
    }
    
    
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
     * Apumetodi, jolla saadaan täytettyä testiarvot päivämäärälle
     * TODO: poista kun kun kaikki toimii
     */
    public void taytaPvmTiedoilla() {
        pvm = arvoPaiva();
        paikka = "Jyväskylä " +rand(1, 310);
        kello = "10:29";
        alinlampo = -2.2;
        ylinlampo = 3.3;
        saatila = rand(1,6);
        sademaara = 0.2;
        huomiot = "Tämä on päivän huomio: " + rand(0,100);
    }
    
    
    /**
     * 
     * @return palauttaa päivän tunnusnumeron
     */
    public int getTunnusNro() {
        return tunnusNro;
    }
    
    
    /**
     * asettaa tunnusnron ja samalla varmistaa että
     * seuraava on numero on aina suurempi kuin tähän mennessä suurin
     * @param nro asetettava tunnusnrro
     */
    public void setTunnusNro(int nro) {
        tunnusNro = nro;
        if (tunnusNro >= seuraavaNro)
            seuraavaNro = tunnusNro+1;
    }
    
    @Override
    public boolean equals(Object paiva) {
        if (paiva == null) return false;
        return this.toString().equals(paiva.toString());
    }
    
    @Override
    public int hashCode() {
        return tunnusNro;
    }

    
    /**
     * Tulostetaan päivän tiedot
     * @param out mihin virtaa tulostetaan
     */
    public void tulosta(PrintStream out) {
        out.println("Päivämäärä:        " + pvm);
        out.println("Paikka:            " + paikka);
        out.println("Kellonaika:        " + kello);
        out.println("Alin lämpötila:    " + String.format("%2.1f", alinlampo) + "°C");
        out.println("Ylin lämpötila:    " + String.format("%2.1f", ylinlampo) + "°C");
        out.println("Sademäärä:         " + String.format("%2.1f", sademaara) + "mm");
        out.println("Huomiot:           " + huomiot); 
    }
    
    /**
     * @param os tietovirta johon tulostetaan
     */
    public void tulosta(OutputStream os) {
        tulosta(new PrintStream(os));
    }


    /**
     * Asetetetaan päivämäärä ja tarkistetaan se
     * @param s asetettava päivämäärä
     * @return null, jos onnistui
     */
    public String setPvm(String s) {
        if( !s.matches("((((([0-2]{1})[0-9]{1}))|(3{1}[0-1]{1}))\\.((0{1}[1-9])|1{1}[0-2]{1})\\.([0-9]{4}))")) 
            return "Päivämäärän pitää olla muota pp.kk.vvvv";
        pvm = s;
        return null;
    }


    /**
     * Asetetaan paikka
     * @param s asetettava paikka
     * @return null, jos onnistui
     */
    public String setPaikka(String s) {
        paikka = s;
        return null;
    }


    /**
     * Asetetaan kellonaika
     * @param s asetettava kellonaika
     * @return nuul, jos onnistui
     */
    public String setKello(String s) {
        if( !s.matches("^([0-1][0-9]|[2][0-3])[\\\\.|:]([0-5][0-9])$")) return "Kellonaika väärin!";
        kello = s;
        return null;
    }


   /**
    * Asetetaan alinlämpö
    * @param s asetettava alinlämpö
    * @return null, jos onnistui
    */
   public String setAlinLampo(String s) {
       StringBuilder sb = new StringBuilder(s);
       alinlampo = Mjonot.erota(sb, '§', alinlampo);
       return null;
    }
   
   
   /**
    * Asetetaan ylinlämpö
    * @param s asetettavayalinlämpö
    * @return null, jos onnistui
    */
   public String setYlinLampo(String s) {
       StringBuilder sb = new StringBuilder(s);
        ylinlampo = Mjonot.erota(sb, '§', ylinlampo);
        return null;
    }
   
   
   /**
    * Asetetaan sademäärä
    * @param s asetettava sademäärä
    * @return null, jos onnistui
    */
   public String setSademaara(String s) {
       StringBuilder sb = new StringBuilder(s);
        sademaara = Mjonot.erota(sb, '§', sademaara);
        return null;
    }
   
   /**
    * Asetetaan huomio
    * @param s asetettavat huomiot
    * @return null, jos onnistui
    */
   public String setHuomiot(String s) {
       huomiot = s;
       return null;
    }
   
     /**
     * Asettaa säätilan
     * @param s asetettava säätila
     */
    public void setSaatila(int s) {
       //StringBuilder sb = new StringBuilder(s);
       //saatila = Mjonot.erota(sb, '§', saatila);
       //return null;
        saatila = s;
    }
    

   
    /**
     * Palauttaa pyydetyn kentän indeksin
     * @param hk pyydetty kenttö
     * @return pyydetty kenttä
     * TODO: korjaa nää kommentit
     */
    public String anna(int hk) {
        switch(hk) {
            case 1: return "" + pvm;
            case 2: return "" + paikka;
            case 3: return "" + kello;
            case 4: return "" + alinlampo;
            case 5: return "" + ylinlampo;
            case 6: return "" + sademaara;
            case 7: return "" + huomiot;
            case 8: return "" + saatila;
            default: return "jee";
        }
    }

    /**
     * 
     * @param paiva Päivä
     * @return palauttaa päivämäärän muodossa vvvkkpv
     */
    public static String vertailija(String paiva) {
        StringBuilder sb = new StringBuilder(paiva);
        String pv = Mjonot.erota(sb, '.');
        String kk = Mjonot.erota(sb, '.');
        String vv = Mjonot.erota(sb, '.');
        
        //String vvkkpp = String.format("%04s", "%02s", "%02s",vv + kk + pv);
        String vvkkpp = vv + kk + pv;
        return vvkkpp;
    }

    /**
     * Antaa tietokannan luontilausekkeen päivätaululle
     * @return luontilauseke päivätaululle
     */
    public String annaLuontilauseke() {
        return "CREATE TABLE Paivat (" +
                "paivanID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "pvm VARCHAR(10) NOT NULL, " +
                "paikka VARCHAR(50), " +
                "kello VARCHAR(5), " +
                "alinlampo DECIMAL(10,2), " +
                "ylinlampo DECIMAL(10,2), " +
                "sademaara DECIMAL(10,2), " +
                "huomiot VARCHAR(200), " +
                "saatila INTEGER" +
                ")";
    }

    /**
     * Antaa päivän lisäyslausekkeen
     * @param con tietokantayhteys
     * @return päivän lisäyslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaLisayslauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement("INSERT INTO Paivat" +
                "(paivanID, pvm, paikka, kello, alinlampo, ylinlampo, " +
                "sademaara, huomiot, saatila) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        
        if ( tunnusNro != 0 ) sql.setInt(1, tunnusNro); else sql.setString(1, null);
        sql.setString(2, pvm);
        sql.setString(3, paikka);
        sql.setString(4, kello);
        sql.setDouble(5, alinlampo);
        sql.setDouble(6, ylinlampo);
        sql.setDouble(7, sademaara);
        sql.setString(8, huomiot);
        sql.setInt(9, saatila);
        
        return sql;
    }
    
    /**
     * Tarkistetaan onko id muuttunut lisäyksessä
     * @param rs lisäyslauseen ResultSet
     * @throws SQLException jos tulee jotakin vikaa
     */
    public void tarkistaId(ResultSet rs) throws SQLException {
        if ( !rs.next() ) return;
        int id = rs.getInt(1);
        if ( id == tunnusNro ) return;
        setTunnusNro(id);
    }

    /**
     * Palauttaa k:tta päivän kenttää vastaavan kysymyksen
     * @param k kuinka monennen kentän kysymys palautetaan (0-alkuinen)
     * @return k:netta kenttää vastaava kysymys
     */
    public String getKysymys(int k) {
        switch ( k ) {
            case 0: return "paivanID";
            case 1: return "pvm";
            case 2: return "paikka";
            case 3: return "kello";
            case 4: return "alinlampo";
            case 5: return "ylinlampo";
            case 6: return "sademaara";
            case 7: return "huomiot";
            case 8: return "saatila";
            default: return "Ei näinnnnn";
        }
    }
    
    /** 
     * Ottaa paivan tiedot ResultSetistä
     * @param tulokset mistä tiedot otetaan
     * @throws SQLException jos jokin menee väärin
     */
    public void parse(ResultSet tulokset) throws SQLException {
        setTunnusNro(tulokset.getInt("paivanID"));
        pvm = tulokset.getString("pvm");
        paikka = tulokset.getString("paikka");
        kello = tulokset.getString("kello");
        alinlampo = tulokset.getDouble("alinlampo");
        ylinlampo = tulokset.getDouble("ylinlampo");
        sademaara =tulokset.getDouble("sademaara");
        huomiot = tulokset.getString("huomiot");
        saatila = tulokset.getInt("saatila");
    }

}

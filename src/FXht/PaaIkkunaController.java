package FXht;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ListChooser;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.TextAreaOutputStream;
import fi.jyu.mit.ohj2.Mjonot;
import ht.wt.Paiva;
import ht.wt.Saatila;
import ht.wt.SailoException;
import ht.wt.WeatherTracker;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Font;

/**
 * @author Joonas Uusi-Autti & Sini Lällä
 * @version 7.2.2020
 *
 */
public class PaaIkkunaController implements Initializable {

    @FXML private Button textCancel;
    @FXML ListChooser<Paiva> chooserPaivat;
    @FXML private ScrollPane panelPaiva;
    @FXML private ComboBoxChooser<String> hakuKentta;
    @FXML private TextField hakuehto;
    @FXML private Label labelVirhe;
    
    
    @FXML private void handleUusi() {
        uusiPaiva();
    } 
    
    @FXML private void handleHakuehto() {
        if (paivaKohdalla != null) 
            hae(paivaKohdalla.getTunnusNro());
    }
    
    @FXML private void handleSaatila() {
        //TextInputDialog dialog = new TextInputDialog("");
        //dialog.setHeaderText("Lisätään uusi säätila");
        //dialog.setTitle("Lisää säätila");
        //dialog.setContentText("Lisää uusi säätila:");
        //Optional<String> saa = dialog.showAndWait();
        //if (saa.isPresent()) { 
        //   String s = saa.get();
        //   uusiSaa();
        //}
        uusiSaa();
    }
    
    @FXML private void handlePoistaSaa() {
        ModalController.showModal(SaatilaController.class.getResource("poistaSaatila.fxml"),
                "Säätilan poisto", null, weathertracker);
    }
    
    @FXML private void handleLopeta() {
        tallenna();
        Platform.exit();
    }
    
    @FXML private void handleMuokkaa() {
        muokkaa();
    }

    @FXML private void handleTallenna() {
        tallenna();
    }
    
    @FXML private void handleApua() {
        apua();
    }
    
    @FXML private void handleTietoja() {
        ModalController.showModal(TietojaController.class.getResource("tietoja.fxml"),
                "Tietoja", null, "");
    }
    
    @FXML private void handleTulosta() {
        TulostusController tulostusCtrl = TulostusController.tulosta(null);
        tulostaValitut(tulostusCtrl.getTextArea());
    }

    @FXML private void handlePoista() {
        poistaPaiva();
    }

    
    @FXML private void handleCancel() {
        ModalController.closeStage(textCancel);
    }
    
    /**
     * Onko sama kkuin tallennna metodi???
     */
    @FXML private void handleOK() {
        tallenna();
    }

    // =============== omat koodit ===============
    
    private WeatherTracker weathertracker;
    private Paiva paivaKohdalla;
    private String wtNimi = "weathertracker";
    private TextArea areaPaiva = new TextArea();
    
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        alusta();
    }
    
    /**
     * Tarkistetaan onko tallennus tehty
     * @return true, jos saa sulkea sovelluksen, false jos ei
     */
    public boolean voikoSulkea() {
        tallenna();
        return true;
    }
    
    /**
     * Aliohjelma joka ohjaa sivustolle jossa käyttöohjeet käyttöliittymälle
     */
    private void apua() {
        Desktop desktop = Desktop.getDesktop();
        try {
            URI uri = new URI("https://tim.jyu.fi/view/kurssit/tie/ohj2/2020k/ht/jopeuusi");
            desktop.browse(uri);
        } catch (URISyntaxException e) {
            return;
        } catch (IOException e) {
            return;
        }
    }
    
    
    /**
     * Aliohjelma joka tallentaa syötetyt tiedot
     */
    private String tallenna() {
        try {
            weathertracker.tallenna();
            return null;
        } catch (SailoException e) {
            Dialogs.showMessageDialog("Tallentamisessa tapahtui virhe: " + e.getMessage());
            return e.getMessage();
        } 
    }
    
    private void poistaPaiva() {
        Paiva paiva = paivaKohdalla;
        if ( paiva == null ) return;
        if ( !Dialogs.showQuestionDialog("Poisto", "Poistetaanko päivä: " + paiva.getPvm(), "Kyllä", "Ei") )
            return;
        weathertracker.poista(paiva);
        int index = chooserPaivat.getSelectedIndex();
        hae(0);
        chooserPaivat.setSelectedIndex(index);
    }

    
    
    /**
     * Lisätään uusi päivämäärä tiedoilla näyttöön
     */
    private void uusiPaiva() {
        Paiva uusi = new Paiva();
        uusi.taytaPvmTiedoilla();
        try {
            weathertracker.lisaa(uusi);
        } catch (SailoException e) {
            e.printStackTrace();
            return;
        }
        hae(uusi.getTunnusNro());
    }
    
    
    /**
     * Lisätään uusi säätila ohjelmalle
     * @param saatila lisättävä säätila
     */
    private void uusiSaa() {
        Saatila saa = new Saatila();
        //saa.rekisteroi(); 
        try {
            weathertracker.lisaa(saa);
        } catch (SailoException e) {
            e.printStackTrace();
        }
        System.out.println(saa.toString());
        //naytaSaa(paivaKohdalla);
    }


    /**
     * Tyhjennetään lista ja haetaan weathertracker luokalta päivämäärä
     * ja lisätään se seuraavaan indeksiin
     * @param pnro päivämäärän järjestysnumero
     */
    protected void hae(int pnro) {
        
        int k = hakuKentta.getSelectionModel().getSelectedIndex();
        String ehto = hakuehto.getText();
        if ( k > 0 || ehto.length() > 0)
            naytaVirhe(String.format("Ei osata hakea (Kenttä: %d, ehto: %s)", k, ehto));
        else 
            naytaVirhe(null);

        chooserPaivat.clear();
        
        int index = 0;
        Collection<Paiva> paivat;
            try {
                paivat = weathertracker.etsi(ehto, k); 
                int i = 0;
                for (Paiva paiva : paivat) {
                    if (paiva.getTunnusNro() == pnro) index = i;
                    chooserPaivat.add(paiva.getPvm(), paiva);
                    i++;
                }
            } catch (SailoException e) {
                e.printStackTrace();
            }
        chooserPaivat.setSelectedIndex(index);
    }

    
    private void naytaVirhe(String virhe) {
        if (virhe == null || virhe.isEmpty()) {
            labelVirhe.setText("");
            labelVirhe.getStyleClass().removeAll("Virhe");
            return;
        }
        labelVirhe.setText(virhe);
        labelVirhe.getStyleClass().add("Virhe");
    }

    /**
     * Alustetaan ja luodaan näyttöön uusi päivä
     */
    private void alusta() {
        panelPaiva.setContent(areaPaiva);
        areaPaiva.setFont(new Font("Courier New", 12));
        
        panelPaiva.setFitToHeight(true);
        
        chooserPaivat.clear();
        chooserPaivat.addSelectionListener(e -> naytaPaiva());
        
    }
    
    
    /**
     * Aliohjelma jo olemassa olevan päivän muokkaamiseksi
     */
    private void muokkaa() {
        //
    }
    
    private void setTitle(String title) {
        ModalController.getStage(hakuehto).setTitle(title);
    }
    
    
    /**
     * "Tulostaa" näyttöön päivän tiedot
     */
    private void naytaPaiva() {
        paivaKohdalla = chooserPaivat.getSelectedObject();
        if ( paivaKohdalla == null) {
            areaPaiva.clear();
            return;
        }
        areaPaiva.setText("");
        try (PrintStream os = TextAreaOutputStream.getTextPrintStream(areaPaiva)) {
            paivaKohdalla.tulosta(os);
        }
        
        naytaSaa(paivaKohdalla);
    }
    
    /**
     * Näytetään pääikkunassa säätila tekstinä
     * @param areaPaiva2 säätilan tekstikenttä
     * @param paiva päivä
     */
    private void naytaSaa(Paiva paiva) {
        
        if (paiva == null) return;
        int numero = paiva.getSaatila();
        Saatila s = new Saatila();
        s.paivanSaa(numero);
        //StringBuilder sb = new StringBuilder(s.toString());
        //String jono = Mjonot.erota(sb, '|');
        //jono = Mjonot.erota(sb, '|');
        //areaPaiva.setText(jono);
        try (PrintStream os = TextAreaOutputStream.getTextPrintStream(areaPaiva)) {
            s.tulosta(os);
        }
    }
    
    
    /**
     * Luetaan tiedosto
     * @param nimi tiedoston nimi
     * @return null jos onnistuu muuten virheteksti
     */
    protected String lueTiedostosta(String nimi) {
        wtNimi = nimi;
        setTitle("WeatherTracker - " + wtNimi);
        try {
            weathertracker.lueTiedostosta(nimi);
            hae(0);
            return null;
        } catch (SailoException e) {
            hae(0);
            String virhe = e.getMessage();
            if (virhe != null)
                Dialogs.showMessageDialog(virhe);
            return virhe;
        }
    }

    
    /**
     * Asetetaan controllerin weathertracker viite
     * @param weathertracker mihin viitataan
     */
    public void setWeatherTracker(WeatherTracker weathertracker) {
        this.weathertracker = weathertracker;
        naytaPaiva();
    }


    /**
     * Käyttäjälle avautuu aloitusikkuna, jossa hän voi päättää
     * aloitetaanko ohjelman käyttäminen
     * @return true jos aloitetaan käyttö, false jos ei
     */
    public boolean avaa() {
        String uusinimi = wtNimi;
        ModalController.showModal(PaaIkkunaController.class.getResource("aloitusikkuna.fxml"),
                "WeatherTracker", null, "");
        lueTiedostosta(uusinimi);
        return true;
    }
    
    /**
     * Tulostaa listassa olevat päivät tekstialueeseen
     * @param text alue, johon tulostetaan
     */
    private void tulostaValitut(TextArea text) {
        try (PrintStream os = TextAreaOutputStream.getTextPrintStream(text)) {
            os.println("Tulostetaan kaikki päivät");
            for (Paiva paiva : chooserPaivat.getObjects()) {
                tulosta(os, paiva);
                os.println();
            }
        } 
    }

    /**
     * Tulostetaan jäsenen tiedot
     * @param os tietovirta johon tulostetaan
     * @param paiva tulostettava päivä
     */
    public void tulosta(PrintStream os, final Paiva paiva) {
        os.println("----------------------------------------------");
        paiva.tulosta(os);
        os.println("----------------------------------------------");
        try {
            List<Saatila> saatilat = weathertracker.annaSaatilat(paiva);
            for (Saatila saa:saatilat) 
                saa.tulosta(os);     
        } catch (SailoException ex) {
            Dialogs.showMessageDialog("Säätilojen hakemisessa ongelmia! " + ex.getMessage());
        }  
    }

}
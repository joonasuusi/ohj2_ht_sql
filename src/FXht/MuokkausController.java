package FXht;

import java.net.URL;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import ht.wt.Paiva;
import ht.wt.Saatila;
import ht.wt.WeatherTracker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlleri päivän muokkaukselle ja lisäykselle
 * @author Joonas Uusi-Autti & Sini Lällä
 * @version 23.4.2020
 *
 */
public class MuokkausController implements ModalControllerInterface<Paiva>, Initializable {
    
    @FXML private TextField editPvm;
    @FXML private TextField editPaikka;
    @FXML private TextField editKello;
    @FXML private TextField editAlinLampo;
    @FXML private TextField editYlinLampo;
    @FXML private TextField editSademaara;
    @FXML private TextField editHuomiot;
    @FXML private Label labelVirhe;
    @FXML private ComboBoxChooser<Saatila> saaLista;
    
    @FXML private void handleTallenna() {
        if (paivaKohdalla != null && paivaKohdalla.getPvm().trim().equals("")) {
            naytaVirhe("Päivämäärä ei saa olla tyhjä");
            return;
        }
        kasitteleMuutosSaahan();
        ModalController.closeStage(labelVirhe);
    }
    

    @FXML private void handleCancel() {
        paivaKohdalla = null;
        ModalController.closeStage(labelVirhe);
    }

    //=========================== omia koodeja ==========================
    private Paiva paivaKohdalla;
    private TextField[] edits;
    
    @Override
    public void handleShown() {
        editPvm.requestFocus();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        alusta();
    }

    @Override
    public Paiva getResult() {
        return paivaKohdalla;
    }

    @Override
    public void setDefault(Paiva oletus) {
        paivaKohdalla = oletus;
        naytaPaiva(paivaKohdalla);
    }
    
    
    /**
     * Tekee tarvittavat muut alustukset
     */
    private void alusta() {
        edits = new TextField[] {editPvm, editPaikka, editKello, editAlinLampo
                                ,editYlinLampo, editSademaara, editHuomiot};
        int i = 0;
        for (TextField edit : edits) {
            final int k = ++i;
            edit.setOnKeyReleased(e -> kasitteleMuutosPaivaan(k,(TextField)(e.getSource())));
        }
    }
    
    /**
     * Käsitellään säätilaan tullut muutos
     */
    private void kasitteleMuutosSaahan() {
        Saatila k = saaLista.getSelectedObject();
        paivaKohdalla.setSaatila(k.getId());  
    }


    /**
     * Käsitellään päivään tullut muutos
     * @param k kentän numero
     * @param edit muuttunut kenttä
     */
    private void kasitteleMuutosPaivaan(int k, TextField edit) {
        if (paivaKohdalla == null) return;
        String s = edit.getText();
        String virhe = null;
        switch (k) {
        case 1 : virhe = paivaKohdalla.setPvm(s); break;
        case 2 : virhe = paivaKohdalla.setPaikka(s); break;
        case 3 : virhe = paivaKohdalla.setKello(s); break;
        case 4 : virhe = paivaKohdalla.setAlinLampo(s); break;
        case 5 : virhe = paivaKohdalla.setYlinLampo(s); break;
        case 6 : virhe = paivaKohdalla.setSademaara(s); break;
        case 7 : virhe = paivaKohdalla.setHuomiot(s); break;
        default:
        }
        if (virhe == null) {
            Dialogs.setToolTipText(edit, "");
            edit.getStyleClass().removeAll("virhe");
            naytaVirhe(virhe);
        } else {
            Dialogs.setToolTipText(edit, virhe);
            edit.getStyleClass().add("virhe");
            naytaVirhe(virhe);
        }
    }
    
    
    /**
     * Näytetään päivän tiedot TextField komponentteihin
     * @param edits taulukko, jossa tekstikenttiä
     * @param paiva näytettävä päivä
     */
    public static void naytaPaiva(TextField[] edits, Paiva paiva) {
        if (paiva == null) return;
        edits[0].setText(paiva.getPvm());
        edits[1].setText(paiva.getPaikka());
        edits[2].setText(paiva.getKello());
        edits[3].setText(String.valueOf(paiva.getAlinLampo()));
        edits[4].setText(String.valueOf(paiva.getYlinLampo()));
        edits[5].setText(String.valueOf(paiva.getSademaara()));
        edits[6].setText(paiva.getHuomiot());
    }
    
    
    /**
     * Näytetään päivän tiedot TextField komponentteihin
     * @param paiva näytettävä päivä
     */
    public void naytaPaiva(Paiva paiva) {
        naytaPaiva(edits, paiva);
    }


    /**
     * Näytetään virheilmoitus käyttäjälle
     * @param virhe syntynyt virhe
     */
    private void naytaVirhe(String virhe) {
        if ( virhe == null || virhe.isEmpty()) {
            labelVirhe.setText("");
            labelVirhe.getStyleClass().removeAll("virhe");
            return;
        }
        labelVirhe.setText(virhe);
        labelVirhe.getStyleClass().add("virhe");
    }
    
    
    /**
     * Luodaan päivän kysymisdialogi ja palautetaan sama tietue muutettuna tai null
     * @param modalityStage mille ollaan modaalisia, null=sovellukselle
     * @param oletus mitä dataa näytetään oletuksena
     * @param weathertracker weathertracker
     * @return null, jos painetaan Cancel, muuten täytetty tietue
     */
      public static Paiva kysyPaiva(Stage modalityStage, Paiva oletus, WeatherTracker weathertracker) {
   
          return ModalController.<Paiva,MuokkausController>showModal(MuokkausController.class.getResource("muokkausikkuna.fxml"),
                  "Muokkaa", modalityStage, oletus, ctrl -> ctrl.asetaChooser(weathertracker, oletus));
      }  
      
    /**
     * Tyhjentää säätilojen valintalistan ja hakee tiedostosta säätilat listaan
     * @param weathertracker weathertracker
     * @param paiva päivä
     */
      public void asetaChooser(WeatherTracker weathertracker, Paiva paiva) {
          saaLista.clear();
          for (int i = 0; i < weathertracker.getSaatilat(); i++) {
              Saatila saa = weathertracker.annaSaa(i);
              saaLista.add(saa.getSaatila(), saa);
          }
          saaLista.setSelectedIndex(weathertracker.annaChooserNro(paiva.getSaatila()));
      }  
}
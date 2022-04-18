package FXht;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import ht.wt.Saatila;
import ht.wt.WeatherTracker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * @author Joonas Uusi-Autti & Sini Lällä
 * @version 24.2.2020
 */
public class SaatilaController implements ModalControllerInterface<WeatherTracker> {
    
    @FXML private Label labelVirhe;
    @FXML private TextField saaLisays;
    @FXML private ComboBoxChooser<Saatila> saaLista;
    
    @FXML private void handleTallenna() {
        poistaSaa();
        ModalController.closeStage(labelVirhe);
    }
    
    @FXML private void handleCancel() {
        ModalController.closeStage(labelVirhe);
    }

    @Override
    public WeatherTracker getResult() {
        return weathertracker;
    }

    @Override
    public void handleShown() {
        asetaChooser();
    }

    @Override
    public void setDefault(WeatherTracker oletus) {
        weathertracker = oletus;
        
    }
    
    // ================= omat koodit ===============
    private WeatherTracker weathertracker;
    
    private void poistaSaa() {
        int k = saaLista.getSelectionModel().getSelectedIndex();
        weathertracker.poista(k);
    }
    
    /**
     * Hakee säiden valintalistan
     */
    public void asetaChooser() {
        saaLista.clear();
        String[] rivit = new String[weathertracker.getSaatilat()];
        for (int i = 0; i < weathertracker.getSaatilat(); i++) {
            Saatila saa = weathertracker.annaSaa(i);
            rivit[i] = saa.getSaatila();
        }
        saaLista.setRivit(rivit);
    }
   
}

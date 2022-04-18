package FXht;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


/**
 * @author Joonas Uusi-Autti & Sini Lällä
 * @version 17.1.2020
 *
 */
public class AloitusIkkunaController implements ModalControllerInterface<String> {
    @FXML private Button textCancel;
    @FXML private Button textAvaa;
    
    @FXML private void handleAloita() {
        ModalController.closeStage(textAvaa);
    }

    @FXML private void handleLopeta() {
        ModalController.closeStage(textCancel);
        Platform.exit();
    }
    
    @FXML private void handleTietoja() {
        ModalController.showModal(AloitusIkkunaController.class.getResource("tietoja.fxml"),
                "Tietoja", null, "");
    }
    
    @FXML private void handleApua() {
        apua();
    }
    
    
   
    @Override
    public String getResult() {
        return null;
    }

    @Override
    public void handleShown() {
        //
        
    }

    @Override
    public void setDefault(String oletus) {
        // 
        
    }
    
    
    /**
     * Avaa selaimeen ohjelman avustuksen
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
}
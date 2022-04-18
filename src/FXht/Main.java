package FXht;
	
import ht.wt.WeatherTracker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;


/**
 * @author Joonas Uusi-Autti & Sini Lällä
 * @version 17.1.2020
 *
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
		    final FXMLLoader ldr = new FXMLLoader(getClass().getResource("paaikkuna.fxml"));
		    final Pane root = (Pane)ldr.load();
		    final PaaIkkunaController paaCtrl = (PaaIkkunaController)ldr.getController();
		            
			final Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("ht.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("WeatherTracker");
			
			primaryStage.setOnCloseRequest((event) -> {
			    if (!paaCtrl.voikoSulkea() ) event.consume();
			});
			
            WeatherTracker weathertracker = new WeatherTracker();
			paaCtrl.setWeatherTracker(weathertracker);
			
			primaryStage.show();
            if(!paaCtrl.avaa()) Platform.exit();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args ei käytössä
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
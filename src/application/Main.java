package application;
	
import java.io.FileInputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			BorderPane root = loader.load(new FileInputStream("src/application/GUI.fxml" ));
			GUIController controller = (GUIController) loader.getController();
			controller.applicationStage = primaryStage;
			Scene scene = new Scene(root,400,620); 
			primaryStage.setScene(scene);
			primaryStage.setTitle("Trigonometry Calculator");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}


//Collection of Java documentation embedded tutorials used:
/**
 * canvas and graphics: https://docs.oracle.com/javafx/2/canvas/jfxpub-canvas.htm
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */

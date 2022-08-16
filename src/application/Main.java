package application;
	
import java.io.FileInputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;

/**
 * Main class. Starting point of the javaFX program, executed when the program is run. 
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//creation and use of FXMLLoader object to get the root node in the FXML for use in creating a scene
			FXMLLoader loader = new FXMLLoader();
			ScrollPane root = loader.load(new FileInputStream("src/application/GUI.fxml" ));
		
			//creating a new scene using the root node in the FXML and putting it on the stage
			Scene scene = new Scene(root,520,630); 
			primaryStage.setScene(scene);
			
			//changing some settings of the stage and displaying it
			primaryStage.setTitle("Right Triangle Trigonometry Calculator");
			primaryStage.setResizable(false);
			primaryStage.show();
			
			//giving the variables in the controller class references to the stage and scene created just now 
			GUIController controller = (GUIController) loader.getController();
			controller.applicationStage = primaryStage;
			controller.mainScene = scene;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

/**
 * official javafx documentation tutorial for canvas and graphics used: https://docs.oracle.com/javafx/2/canvas/jfxpub-canvas.htm
 */

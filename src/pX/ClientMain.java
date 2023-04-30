package pX;

// Imports and
//Special libraries:
//OpenCV-www.opencv.org ,HaarCascade Classifiers-\resources\haarcascades,JavaFX and Java Swings
import org.opencv.core.Core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author MT Lubisi
 * @version Network Project
 *
 */
public class ClientMain extends Application {
	
	public static void main(String[] args) {
		launch(args);
		
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		//Instatiate the ClientPane
		ClientPane root = new ClientPane(primaryStage);
		root.setMinHeight(600);
		root.setMinWidth(400);
	    //Add Pane to the Scene graph
		Scene scene = new Scene(root, primaryStage.getMaxWidth(),primaryStage.getHeight());
		primaryStage.setTitle("Library Face Detector");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
package com;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Screen;

/**
 * Main class used in JavaFX. Launches application based on
 * structuring in Main.fxml and {@link Controller} methods.
 * @author Jagan Prem
 * @see Application
 */
public class View extends Application {
	
	/**
	 * Start method used to create the JavaFX application.
	 * @throws IOException When Main.FXML is not found.
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
		final Parent parent = FXMLLoader.load(getClass().getResource("../Main.fxml"));
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setScene(new Scene(parent, bounds.getWidth() - 10, bounds.getHeight() - 10));
		primaryStage.setMaximized(true);
		primaryStage.setMinHeight(bounds.getHeight());
		primaryStage.setMinWidth(bounds.getWidth());
		primaryStage.setTitle("Assembly Seating Generator");
		File iconFile = new File("icon.png");
		Image icon = new Image(iconFile.toURI().toString());
		primaryStage.getIcons().add(icon);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
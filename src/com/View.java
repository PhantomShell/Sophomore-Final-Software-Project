package com;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;

public class View extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		final Parent parent = FXMLLoader.load(getClass().getResource("../Main.fxml"));
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setScene(new Scene(parent, bounds.getWidth() - 10, bounds.getHeight() - 10));
		primaryStage.setMaximized(true);
		primaryStage.setMinHeight(bounds.getHeight());
		primaryStage.setMinWidth(bounds.getWidth());
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
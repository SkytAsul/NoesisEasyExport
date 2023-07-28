package fr.skytasul.noesiseasyexport;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

	private static Main instance;

	private Stage stage;
	private MainController controller;

	@Override
	public void start(Stage primaryStage) {
		instance = this;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/fxml/Main.fxml"));
			AnchorPane rootLayout = (AnchorPane) loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setTitle("Noesis easy export");
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/icon16.png")));
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/icon32.png")));
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/icon48.png")));
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/icon64.png")));
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/icon128.png")));
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/icon256.png")));
			primaryStage.setScene(scene);
			primaryStage.show();

			controller = loader.getController();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		controller.save();
	}

	public Stage getStage() {
		return stage;
	}

	public MainController getController() {
		return controller;
	}

	public static Main getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		launch(args);
	}
}

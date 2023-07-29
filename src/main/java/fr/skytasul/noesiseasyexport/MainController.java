package fr.skytasul.noesiseasyexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController {

	@FXML
	private ChoiceBox<String> type;

	@FXML
	private ListView<String> files;

	@FXML
	private TextField destination;

	@FXML
	private Button extract;

	@FXML
	private Button exe;

	@FXML
	private TextArea console;

	// options
	@FXML
	private CheckBox flipUVs;

	@FXML
	private CheckBox rotate90;

	@FXML
	private CheckBox noTextures;

	@FXML
	private CheckBox noGeometry;

	@FXML
	private CheckBox noAnimations;

	@FXML
	private TextField advancedOptions;

	private File lastFolder;
	private File noesisFile;

	private PrintStream out;

	private void checkButton() {
		if (destination.getText().isEmpty() || files.getItems().isEmpty() || noesisFile == null) {
			extract.setDisable(true);
		}else extract.setDisable(false);
	}

	public void save() throws IOException {
		try (FileOutputStream stream = new FileOutputStream(new File("nee-properties"))) {
			Properties properties = new Properties();
			if (lastFolder != null) properties.setProperty("lastFolder", lastFolder.getAbsolutePath());
			if (noesisFile != null) properties.setProperty("noesis", noesisFile.getAbsolutePath());
			if (!type.getSelectionModel().isEmpty()) properties.setProperty("type", type.getSelectionModel().getSelectedItem());
			properties.setProperty("destination", destination.getText());
			properties.setProperty("flipuv", String.valueOf(flipUVs.isSelected()));
			properties.setProperty("rotate", String.valueOf(rotate90.isSelected()));
			properties.setProperty("notext", String.valueOf(noTextures.isSelected()));
			properties.setProperty("nogeo", String.valueOf(noGeometry.isSelected()));
			properties.setProperty("noanims", String.valueOf(noAnimations.isSelected()));
			properties.setProperty("advancedOptions", advancedOptions.getText());
			properties.store(stream, "Noesis Easy Export properties");
		}
	}

	@FXML
	private void initialize() {
		type.getItems().addAll(".dae", ".fbx", ".glm", ".gltf", ".iqm", ".kvx", ".md2", ".md3", ".md5mesh", ".mdl", ".mdr", ".obj", ".ply", ".ply2", ".psk", ".rdm", ".smd", ".stl", ".vox");
		files.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		out = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				console.appendText(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				console.appendText(new String(b, off, len));
			}
		}, true);
		System.setOut(out);

		File file = new File("nee-properties");
		if (file.exists()) {
			try (FileInputStream stream = new FileInputStream(file)){
				Properties properties = new Properties();
				properties.load(stream);
				if (properties.containsKey("lastFolder")) lastFolder = new File(properties.getProperty("lastFolder"));
				if (properties.containsKey("noesis")) noesisFile = new File(properties.getProperty("noesis"));
				if (properties.containsKey("type")) type.getSelectionModel().select(properties.getProperty("type"));
				destination.setText(properties.getProperty("destination"));
				flipUVs.setSelected(Boolean.valueOf(properties.getProperty("flipuv")));
				rotate90.setSelected(Boolean.valueOf(properties.getProperty("rotate")));
				noTextures.setSelected(Boolean.valueOf(properties.getProperty("notext")));
				noGeometry.setSelected(Boolean.valueOf(properties.getProperty("nogeo")));
				noAnimations.setSelected(Boolean.valueOf(properties.getProperty("noanims")));
				advancedOptions.setText(properties.getProperty("advancedOptions"));

				if (noesisFile != null) exe.setTextFill(Color.rgb(34, 109, 31));
			}catch (IOException e) {
				System.err.println("An error occured during load of properties.");
				e.printStackTrace();
				e.printStackTrace(out);
			}
		}
	}

	@FXML
	private void selectButton() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Choose file to export");
		chooser.setInitialDirectory(lastFolder);
		List<File> chosenFiles = chooser.showOpenMultipleDialog(Main.getInstance().getStage());
		if (chosenFiles == null) return;
		for (File file : chosenFiles) {
			files.getItems().add(file.getAbsolutePath());
			lastFolder = file.getParentFile();
		}
		checkButton();
	}

	@FXML
	private void removeButton() {
		ObservableList<Integer> indexes = files.getSelectionModel().getSelectedIndices();
		for (int index : indexes) {
			files.getItems().remove(index);
		}
		if (files.getItems().isEmpty()) extract.setDisable(true);
	}

	@FXML
	private void clearButton() {
		files.getItems().clear();
		extract.setDisable(true);
	}

	@FXML
	private void browseButton() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Choose destination");
		File directory = new File(destination.getText());
		if (directory.exists()) chooser.setInitialDirectory(directory);
		directory = chooser.showDialog(Main.getInstance().getStage());
		if (directory != null) destination.setText(directory.getAbsolutePath());
		checkButton();
	}

	@FXML
	private void destinationEdit() {
		checkButton();
	}

	@FXML
	private void exeButton() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Find Noesis executable");
		chooser.getExtensionFilters().add(new ExtensionFilter("Executable files", "*.exe"));
		File file = chooser.showOpenDialog(Main.getInstance().getStage());
		if (file == null) {
			System.out.println("Invalid Noesis executable.");
		}else {
			noesisFile = file;
			exe.setTextFill(Color.rgb(34, 109, 31));
			checkButton();
		}
	}

	@FXML
	private void startButton() {
		File dest = new File(destination.getText());
		if (!dest.isDirectory()) {
			System.out.println("Destination folder is not valid.");
			return;
		}
		String destPath = dest.getAbsolutePath() + File.separatorChar;
		System.out.println("Starting extraction...");
		List<String> arguments = new ArrayList<>();
		arguments.add(noesisFile.getAbsolutePath());
		arguments.add("?cmode");
		arguments.add("");
		arguments.add("");
		if (flipUVs.isSelected()) arguments.add("-flipuv");
		if (rotate90.isSelected()) arguments.add("-rotate 90 0 0");
		if (noTextures.isSelected()) arguments.add("-notex");
		if (noGeometry.isSelected()) arguments.add("-nogeo");
		if (noAnimations.isSelected()) arguments.add("-noanims");
		if (advancedOptions.getText() != "") arguments.addAll(Arrays.asList(advancedOptions.getText().split(" ")));
		for (String item : files.getItems()) {
			try {
				File file = new File(item);
				arguments.set(2, file.getAbsolutePath());
				arguments.set(3, destPath + file.getName().substring(0, file.getName().lastIndexOf(".")) + type.getSelectionModel().getSelectedItem());
				System.out.println("Extraction of \"" + item + "\"... Running " + arguments);

				ProcessBuilder pb = new ProcessBuilder(arguments.toArray(new String[0]));
				pb.redirectErrorStream(true);
				pb.directory(noesisFile.getParentFile());
				Process process = pb.start();
				BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String s;
				while ((s = inStreamReader.readLine()) != null) {
					System.out.println(s);
				}
			}catch (IOException e) {
				e.printStackTrace();
				e.printStackTrace(out);
			}
		}
		System.out.println();
		System.out.println();
		System.out.println("Extraction finished!");
		new Alert(AlertType.INFORMATION, "Extraction finished!").showAndWait();
	}

}

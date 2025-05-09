import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileOrganizerGUI extends Application {
    private TextField directoryField;
    private ComboBox<String> criteriaComboBox;
    private CheckBox subfoldersCheckBox;
    private CheckBox recursiveCheckBox;
    private TextArea beforeTextArea;
    private TextArea afterTextArea;
    private FileOrganizer organizer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("File Organizer");

        // Create UI components
        directoryField = new TextField();
        Button browseButton = new Button("Browse...");
        criteriaComboBox = new ComboBox<>();
        criteriaComboBox.getItems().addAll("Extension", "Name", "Size");
        criteriaComboBox.setValue("Extension");
        subfoldersCheckBox = new CheckBox("Create Subfolders");
        recursiveCheckBox = new CheckBox("Process Recursively");
        Button organizeButton = new Button("Organize Files");
        beforeTextArea = new TextArea();
        afterTextArea = new TextArea();
        beforeTextArea.setEditable(false);
        afterTextArea.setEditable(false);

        // Set up layout
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));

        inputGrid.add(new Label("Directory:"), 0, 0);
        inputGrid.add(directoryField, 1, 0);
        inputGrid.add(browseButton, 2, 0);
        inputGrid.add(new Label("Sort Criteria:"), 0, 1);
        inputGrid.add(criteriaComboBox, 1, 1);
        inputGrid.add(subfoldersCheckBox, 0, 2);
        inputGrid.add(recursiveCheckBox, 1, 2);
        inputGrid.add(organizeButton, 0, 3, 3, 1);

        HBox textAreasBox = new HBox(10);
        VBox beforeBox = new VBox(5, new Label("Before Organization:"), beforeTextArea);
        VBox afterBox = new VBox(5, new Label("After Organization:"), afterTextArea);
        textAreasBox.getChildren().addAll(beforeBox, afterBox);
        textAreasBox.setPadding(new Insets(10));

        VBox mainLayout = new VBox(10, inputGrid, textAreasBox);

        // Set up event handlers
        browseButton.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                directoryField.setText(selectedDirectory.getAbsolutePath());
                loadFilesBeforeOrganization();
            }
        });

        organizeButton.setOnAction(e -> {
            if (directoryField.getText().isEmpty()) {
                showAlert("Error", "Please select a directory first.");
                return;
            }

            try {
                organizeFiles();
                loadFilesAfterOrganization();
                showAlert("Success", "Files organized successfully!");
            } catch (Exception ex) {
                showAlert("Error", "Failed to organize files: " + ex.getMessage());
            }
        });

        // Set up the scene
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadFilesBeforeOrganization() {
        String path = directoryField.getText();
        organizer = new FileOrganizer(path);

        try {
            List<Path> files = organizer.getFilesInDirectory(Paths.get(path));
            StringBuilder sb = new StringBuilder();
            for (Path file : files) {
                sb.append(file.getFileName()).append("\n");
            }
            beforeTextArea.setText(sb.toString());
        } catch (Exception e) {
            beforeTextArea.setText("Error loading files: " + e.getMessage());
        }
    }

    private void organizeFiles() {
        String criteria = criteriaComboBox.getValue();
        boolean createSubfolders = subfoldersCheckBox.isSelected();
        boolean recursive = recursiveCheckBox.isSelected();

        SortStrategy strategy = SortStrategyFactory.create(criteria.toUpperCase());
        OrganizerSettings settings = OrganizerSettings.create(createSubfolders, recursive);

        organizer.setSortStrategy(strategy);
        organizer.organize(settings);
    }

    private void loadFilesAfterOrganization() {
        String path = directoryField.getText();

        String criteria = criteriaComboBox.getValue();
        SortStrategyFactory strategyFactory = new SortStrategyFactory();
        SortStrategy sortStrategy = strategyFactory.create(criteria);


        try {
            List<Path> files_before = organizer.getFilesInDirectory(Paths.get(path));

            List<Path> files = sortStrategy.sort(files_before);

            StringBuilder sb = new StringBuilder();
            for (Path file : files) {
                sb.append(file.getFileName()).append("\n");
            }
            afterTextArea.setText(sb.toString());
        } catch (Exception e) {
            afterTextArea.setText("Error loading files: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
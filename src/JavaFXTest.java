import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JavaFXTest extends Application {
    private Label errorLabel;
    private Label successLabel;
    private ProgressIndicator progressIndicator;
    private ProgressBar progressBar;
    private ImageView logoPreview;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 20px;");
        root.setAlignment(Pos.CENTER);
        root.setFillWidth(false);

        Image backgroundImage = new Image("file:src/EcranDeCreationDeGroupe.png");
        BackgroundSize backgroundSize = new BackgroundSize(
            100, 100, true, true, true, true
        );
        BackgroundImage bgImage = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize
        );
        root.setBackground(new Background(bgImage));

        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Nom du groupe");
        groupNameField.setMaxWidth(300);
        groupNameField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-font-size: 14px; -fx-padding: 10px;");

        logoPreview = new ImageView();
        logoPreview.setFitHeight(80);
        logoPreview.setFitWidth(80);

        TextArea phoneNumbersArea = new TextArea();
        phoneNumbersArea.setPromptText("Collez ici les numéros de téléphone\n(séparés par des virgules ou des espaces)");
        phoneNumbersArea.setMaxWidth(300);
        phoneNumbersArea.setMaxHeight(100);
        phoneNumbersArea.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-font-size: 14px; -fx-padding: 10px;");

        Button uploadLogoButton = createButton("Uploader un logo", "#00A8FF");
        Button createGroupButton = createButton("Créer le groupe", "#128C7E");

        Label messageLabel = createLabel("Entrez les numéros correctement pour continuer.", "#000000", 12, 1);
        successLabel = createLabel("Groupe créé avec succès !", "#128C7E", 12, 0);
        errorLabel = createLabel("Numéro invalide détecté", "#FF4D4D", 12, 0);

        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(300);
        progressBar.setStyle("-fx-accent: #128C7E;");

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        uploadLogoButton.setOnAction(event -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                Image image = new Image(selectedFile.toURI().toString());
                logoPreview.setImage(image);
            }
        });

        createGroupButton.setOnAction(event -> {
            String groupName = groupNameField.getText().trim();
            String phoneNumbers = phoneNumbersArea.getText();

            if (groupName.isEmpty()) {
                showError("Veuillez entrer un nom de groupe.");
                return;
            }

            if (logoPreview.getImage() == null) {
                showError("Veuillez uploader un logo pour le groupe.");
                return;
            }

            if (!validatePhoneNumbers(phoneNumbers)) {
                showError("Veuillez entrer des numéros valides.");
                return;
            }

            createGroup(groupName, phoneNumbers);
        });

        root.getChildren().addAll(
            groupNameField,
            uploadLogoButton,
            logoPreview,
            phoneNumbersArea,
            createGroupButton,
            progressBar,
            progressIndicator,
            messageLabel,
            successLabel,
            errorLabel
        );

        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("Écran de création de groupe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createLabel(String text, String color, int fontSize, double opacity) {
        Label label = new Label(text);
        label.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: %dpx; -fx-opacity: %.1f;", color, fontSize, opacity));
        return label;
    }

    private Button createButton(String text, String backgroundColor) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 14px;", backgroundColor));
        button.setPadding(new Insets(10, 20, 10, 20));
        return button;
    }

    private void showSummary(String groupName, String phoneNumbers, Image logo) {
        int numberOfContacts = phoneNumbers.split("[,\\s]+").length;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Récapitulatif de la création du groupe");
        alert.setHeaderText("Groupe créé avec succès !");
        alert.setContentText(
            "Nom du groupe : " + groupName + "\n" +
            "Nombre de numéros ajoutés : " + numberOfContacts
        );

        // Ajouter l'aperçu du logo dans l'alerte
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(100);
        logoView.setFitHeight(100);
        alert.setGraphic(logoView);

        alert.showAndWait();
    }

    private void createGroup(String groupName, String phoneNumbers) {
        fadeMessage(successLabel, true);
        fadeMessage(errorLabel, false);
        progressIndicator.setVisible(true);

        new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                double progress = i / 100.0;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final double currentProgress = progress;
                Platform.runLater(() -> progressBar.setProgress(currentProgress));

                if (i == 100) {
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);

                        // Afficher le récapitulatif après la fin de la progression
                        showSummary(groupName, phoneNumbers, logoPreview.getImage());

                        // Sauvegarder les informations du groupe
                        saveGroupInfo(groupName, phoneNumbers);
                    });
                }
            }
        }).start();
    }

    private boolean validatePhoneNumbers(String phoneNumbers) {
        String[] numbers = phoneNumbers.split("[,\\s]+");
        for (String number : numbers) {
            if (!number.matches("\\d{10,15}")) {
                return false;
            }
        }
        return true;
    }

    private void saveGroupInfo(String groupName, String phoneNumbers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Admin\\eclipse-workspace\\JavaFXTestProject\\groupe_info.txt", true))) {
            writer.write("Nom du groupe : " + groupName + "\n");
            writer.write("Numéros : " + phoneNumbers + "\n\n");
            writer.write("-------------------------------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        fadeMessage(errorLabel, true);
        fadeMessage(successLabel, false);
    }

    private void fadeMessage(Label label, boolean fadeIn) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), label);
        fadeTransition.setFromValue(fadeIn ? 0 : 1);
        fadeTransition.setToValue(fadeIn ? 1 : 0);
        fadeTransition.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

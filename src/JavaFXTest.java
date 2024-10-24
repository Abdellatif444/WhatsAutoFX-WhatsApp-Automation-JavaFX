import javafx.application.Application;
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

import java.io.File;

public class JavaFXTest extends Application {
    private Label errorLabel; // Déclaration de errorLabel
    private Label successLabel; // Déclaration de successLabel
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

        Button uploadLogoButton = new Button("Uploader un logo");
        uploadLogoButton.setStyle("-fx-background-color: #00A8FF; -fx-text-fill: white; -fx-font-size: 14px;");
        uploadLogoButton.setMaxWidth(150);
        uploadLogoButton.setPadding(new Insets(10, 20, 10, 20));

        ImageView logoPreview = new ImageView();
        logoPreview.setFitHeight(80);
        logoPreview.setFitWidth(80);

        TextArea phoneNumbersArea = new TextArea();
        phoneNumbersArea.setPromptText("Collez ici les numéros de téléphone\n(séparés par des virgules ou des espaces)");
        phoneNumbersArea.setMaxWidth(300);
        phoneNumbersArea.setMaxHeight(100);
        phoneNumbersArea.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-font-size: 14px; -fx-padding: 10px;");

        Button createGroupButton = new Button("Créer le groupe");
        createGroupButton.setStyle("-fx-background-color: #128C7E; -fx-text-fill: white; -fx-font-size: 14px;");
        createGroupButton.setMaxWidth(200);
        createGroupButton.setPadding(new Insets(10, 20, 10, 20));

        Label messageLabel = new Label("Entrez les numéros correctement pour continuer.");
        messageLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 12px;");

        Label successLabel = new Label("Groupe créé avec succès !");
        successLabel.setStyle("-fx-text-fill: #128C7E; -fx-font-size: 12px; -fx-opacity: 0;");

        errorLabel = new Label("Numéro invalide détecté"); // Initialisation de errorLabel
        errorLabel.setStyle("-fx-text-fill: #FF4D4D; -fx-font-size: 12px; -fx-opacity: 0;");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(300);
        progressBar.setStyle("-fx-accent: #128C7E;");

        ProgressIndicator progressIndicator = new ProgressIndicator();
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
                    progressBar.setProgress(currentProgress);

                    if (i == 100) {
                        progressIndicator.setVisible(false);
                    }
                }
            }).start();
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

    private boolean validatePhoneNumbers(String phoneNumbers) {
        String[] numbers = phoneNumbers.split("[,\\s]+");
        for (String number : numbers) {
            if (!number.matches("\\d{10,15}")) {
                return false;
            }
        }
        return true;
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

package sid;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Sid sid;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image sidImage = new Image(this.getClass().getResourceAsStream("/images/DaSid.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Duke instance */
    public void setSid(Sid s) {
        assert s != null : "Sid instance cannot be null";
        sid = s;
        showWelcomeMessage();
    }

    /** Shows the welcome message when the app starts */
    private void showWelcomeMessage() {
        String welcomeMessage = "Hello! I'm Sid\nWhat can I do for you?";
        dialogContainer.getChildren().add(
            DialogBox.getSidDialog(welcomeMessage, sidImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        assert input != null : "User input text cannot be null";
        String response = sid.getResponse(input);
        assert response != null : "Sid response cannot be null";
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getSidDialog(response, sidImage)
        );
        userInput.clear();
        if (response.equals("Goodbye!")) {
            // Create a thread to handle the delayed exit
            new Thread(() -> {
                try {
                    Thread.sleep(500); // 0.5 second delay
                    System.exit(0);
                } catch (InterruptedException e) {
                    System.exit(0);
                }
            }).start();
        }
    }
}

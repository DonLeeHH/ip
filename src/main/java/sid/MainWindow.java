package sid;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

    // Command history functionality
    private List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;

    /** Initializes the controller and sets up responsive layout bindings. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.prefWidthProperty().bind(scrollPane.widthProperty().subtract(5));
        setupCommandHistory();
    }

    /** Injects the Sid instance */
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

    /** Sets up command history functionality with up/down arrow navigation. */
    private void setupCommandHistory() {
        userInput.setOnKeyPressed(this::handleKeyPress);
    }

    /** Handles key press events for command history navigation. */
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            navigateHistoryUp();
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            navigateHistoryDown();
            event.consume();
        }
    }

    /** Navigates up in command history (older commands). */
    private void navigateHistoryUp() {
        if (commandHistory.isEmpty()) {
            return;
        }

        if (historyIndex == -1) {
            historyIndex = commandHistory.size() - 1;
        } else if (historyIndex > 0) {
            historyIndex--;
        }

        userInput.setText(commandHistory.get(historyIndex));
        userInput.positionCaret(userInput.getText().length());
    }

    /** Navigates down in command history (newer commands). */
    private void navigateHistoryDown() {
        if (commandHistory.isEmpty() || historyIndex == -1) {
            return;
        }

        if (historyIndex < commandHistory.size() - 1) {
            historyIndex++;
            userInput.setText(commandHistory.get(historyIndex));
        } else {
            historyIndex = -1;
            userInput.clear();
        }
        userInput.positionCaret(userInput.getText().length());
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Sid's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        assert input != null : "User input text cannot be null";

        // Only process non-empty input
        if (input.isEmpty()) {
            return;
        }

        // Add to command history (avoid duplicate consecutive commands)
        if (commandHistory.isEmpty() || !commandHistory.get(commandHistory.size() - 1).equals(input)) {
            commandHistory.add(input);
        }
        historyIndex = -1; // Reset history navigation

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

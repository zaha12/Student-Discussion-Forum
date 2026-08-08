package guiAdminHome;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.AdminRequest;
import entityClasses.User;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListCell;

/*******
 * <p> Title: ViewAdminRequests Class. </p>
 *
 * <p> Description: Dedicated screen for Admin to view and manage staff AdminRequests.
 * Uses methods from ControllerAdminHome (same behavior as staff screen). </p>
 */
public class ViewAdminRequests {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    private static Stage theStage;
    private static User theUser;
    private static Pane theRootPane;
    private static Scene theScene;

    private static ListView<AdminRequest> adminRequestListView = new ListView<>();

    protected static Label label_PageTitle = new Label("Admin - Staff Admin Requests");
    protected static Button button_ViewAll = new Button("View All Requests");
    protected static Button button_ViewOpen = new Button("View Open Requests");
    protected static Button button_ViewClosed = new Button("View Closed Requests");
    protected static Button button_CloseRequest = new Button("Close Selected Request");
    protected static Button button_Back = new Button("Back to Admin Home");

    /*******
     * <p> Method: displayAdminRequestsScreen(Stage ps, User user) </p>
     *
     * <p> Description: Opens the Admin Requests screen and loads all requests. </p>
     */
    public static void displayAdminRequestsScreen(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theRootPane == null) {
            initializeScreen();
        }

        // Load all requests when the screen opens (using ControllerAdminHome)
        ControllerAdminHome.performViewAllAdminRequests();

        theStage.setTitle("Admin - Staff Admin Requests");
        theStage.setScene(theScene);
        theStage.show();
    }

    private static void initializeScreen() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, width, height);

        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 20);

        // View buttons
        setupButtonUI(button_ViewAll, "Dialog", 16, 220, Pos.CENTER, 40, 80);
        button_ViewAll.setOnAction((_) -> ControllerAdminHome.performViewAllAdminRequests());

        setupButtonUI(button_ViewOpen, "Dialog", 16, 220, Pos.CENTER, 300, 80);
        button_ViewOpen.setOnAction((_) -> ControllerAdminHome.performViewOpenAdminRequests());

        setupButtonUI(button_ViewClosed, "Dialog", 16, 220, Pos.CENTER, 560, 80);
        button_ViewClosed.setOnAction((_) -> ControllerAdminHome.performViewClosedAdminRequests());

        // Close button (only for Admin)
        setupButtonUI(button_CloseRequest, "Dialog", 16, 220, Pos.CENTER, 40, 200);
        button_CloseRequest.setOnAction((_) -> {
            AdminRequest selected = adminRequestListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                displayMessage("Please select a request first.");
                return;
            }
            openCloseRequestWindow(selected);
        });

        // ListView
        adminRequestListView.setCellFactory((_) -> new ListCell<AdminRequest>() {
            protected void updateItem(AdminRequest request, boolean empty) {
                super.updateItem(request, empty);
                setText(empty || request == null ? null : request.format());
            }
        });
        adminRequestListView.setPrefWidth(760);
        adminRequestListView.setPrefHeight(350);
        adminRequestListView.setLayoutX(40);
        adminRequestListView.setLayoutY(260);

        // Back button
        setupButtonUI(button_Back, "Dialog", 18, 250, Pos.CENTER, 40, 650);
        button_Back.setOnAction((_) -> ViewAdminHome.displayAdminHome(theStage, theUser));

        theRootPane.getChildren().addAll(
                label_PageTitle,
                button_ViewAll, button_ViewOpen, button_ViewClosed,
                button_CloseRequest,
                adminRequestListView,
                button_Back
        );
    }

    /** Opens window for admin to close a request with notes */
    private static void openCloseRequestWindow(AdminRequest request) {
        Stage stage = new Stage();
        stage.setTitle("Close AdminRequest");

        Label notesLabel = new Label("Actions Taken / Notes:");
        javafx.scene.control.TextArea notesArea = new javafx.scene.control.TextArea();
        notesArea.setPrefHeight(120);

        Button closeButton = new Button("Close Request");
        Button cancelButton = new Button("Cancel");

        Pane pane = new Pane();
        notesLabel.setLayoutX(20); notesLabel.setLayoutY(20);
        notesArea.setLayoutX(20); notesArea.setLayoutY(45);

        closeButton.setLayoutX(20); closeButton.setLayoutY(190);
        cancelButton.setLayoutX(150); cancelButton.setLayoutY(190);

        pane.getChildren().addAll(notesLabel, notesArea, closeButton, cancelButton);

        closeButton.setOnAction((_) -> {
            String notes = notesArea.getText();
            ControllerAdminHome.performCloseAdminRequest(request, notes);
            stage.close();
        });

        cancelButton.setOnAction((_) -> stage.close());

        stage.setScene(new Scene(pane, 400, 240));
        stage.show();
    }

    public static void displayMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Returns the ListView so ControllerAdminHome can populate it */
    public static ListView<AdminRequest> getAdminRequestListView() {
        return adminRequestListView;
    }

    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}

package guiOTP;

//import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

//import java.util.List;

import database.Database;
import entityClasses.User;
//import guiAddRemoveRoles.ControllerAddRemoveRoles;

/*******
 * <p> Title: ViewOTP Class </p>
 * 
 * Description : GUI view for the One Time PAssword page. Provides UI components for selecting a user
 *              and generating a new password.
 *  
 * @author Zaha Farooq Abbasi
 * <p> Copyright: Zaha Farooq Abbasi  © 2026 </p>
 * @version 1.00		2026-02-11 Initial version
 */
public class ViewOTP {
    // window dimensions
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    //UI LAbels
    protected static Label label_PageTitle = new Label();
    protected static Label label_UserDetails = new Label();
    protected static Label label_SelectUser = new Label("Select a user to reset password:");
    //ComboBox for selecting a user
    protected static ComboBox<String> combobox_SelectUser = new ComboBox<>();
    
     //Buttons
    protected static Button button_GenerateOTP = new Button("Generate One-Time Password");
    protected static Button button_Return = new Button("Return");
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");
    
    
    protected static Stage theStage;  // The Stage that JavaFX has established for us
    protected static Pane theRootPane;  // The Pane that holds all the GUI widgets 
    protected static Scene theScene;    // Access to the one time password page's GUI Widgets
    
    
    protected static User theUser; // The current logged in User
    
    //currently selected user in the ComboBox
    protected static String theSelectedUser = "";

    private static Database theDatabase = applicationMain.FoundationsMain.database;
    
    //Single instance of this view
    private static ViewOTP theView;

   
    /**********
	 * <p> Method: displayOneTimePassword(Stage ps, User user ) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the one time password page to be displayed.
	 * 
	 * 
	 * It checks to see if the page has been setup.  If not, it instantiates the class, 
	 * 
	 * After instantiation, it populates dynamic elements such as the currently logged in admin user and the user selection
	 * ComboBox.
	 *It then sets the Scene onto the stage, and makes it visible to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user the currently logged in admin user
	 * 
	 */
    public static void displayOneTimePassword(Stage ps, User user) {
        theStage = ps;
        theUser = user;
       
        //instantiate the view if it has not been already
        if (theView == null)
            theView = new ViewOTP();
        //Select the first item in the ComboBox by default
        combobox_SelectUser.getSelectionModel().select(0);
        //display the OTP page
        ControllerOTP.repaintWindow();
    }
    
    /*******
     * <p> Method: ViewOTP() </p>
     *  
     * <p> Description: Constructor intializes all UI components and their positions on the pane. </p>
     *  
     */
    public ViewOTP() {

        theRootPane = new Pane();
        theScene = new Scene(theRootPane, width, height);
        
        //setup page title and admin details
        label_PageTitle.setText("One-Time Password Reset");
        setupLabel(label_PageTitle, 28, width, Pos.CENTER, 0, 10);

        label_UserDetails.setText("Admin: " + theUser.getUserName());
        setupLabel(label_UserDetails, 18, width, Pos.BASELINE_LEFT, 20, 60);
        
        //setup label for user selection
        setupLabel(label_SelectUser, 18, 300, Pos.BASELINE_LEFT, 20, 120);

     
		//setup user selection ComboBox
        setupComboBox(combobox_SelectUser, "Dialog", 16, 250, 300, 115);
        combobox_SelectUser.setItems(FXCollections.observableArrayList(
                theDatabase.getUserList()));
        combobox_SelectUser.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        ControllerOTP.doSelectUser());
        //Setup buttons
        setupButton(button_GenerateOTP, 18, 300, 240);
        button_GenerateOTP.setOnAction((_) ->
                ControllerOTP.performGenerateOTP());

        setupButton(button_Return, 18, 20, 520);
        button_Return.setOnAction((_) ->
                ControllerOTP.performReturn());

        setupButton(button_Logout, 18, 300, 520);
        button_Logout.setOnAction((_) ->
                ControllerOTP.performLogout());

        setupButton(button_Quit, 18, 580, 520);
        button_Quit.setOnAction((_) ->
                ControllerOTP.performQuit());
    }

    /*-********************************************************************************************

	Helper methods to reduce code length

	 */
    
    /**********
	 * Private local method to initialize the standard fields for a label
	 */

    private static void setupLabel(Label l, double f, double w,
                                   Pos p, double x, double y) {
        l.setFont(Font.font("Arial", f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }
    /**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param f		The size of the font to be used
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
    private static void setupButton(Button b, double f,double x, double y) {
        b.setFont(Font.font("Dialog", f));
        b.setMinWidth(240);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    /**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
    protected static void setupComboBox(ComboBox <String> c, String ff, double f, double w, double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
   
}

package guiOTP;

import database.Database;
import guiAdminHome.ControllerAdminHome;
import guiAdminHome.ViewAdminHome;
import javafx.scene.control.Alert;

/*******
 * <p> Title: ControllerOTP handles the actions and logic for the one time password page in the admin panel
 *     This class selects a user for OTP, generates OTP, stores them in database and displays alerts to admin </p>
 * @author Zaha Farooq Abbasi
 * 
 * <p> Copyright: Zaha Farooq Abbasi  © 2026 </p>
 * @version 1.00		2026-02-11 Initial version
 */
public class ControllerOTP {
   //Reference for the in-memory database so this package has access
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /*******
     * <p>Method: doSelectUser() </p>
     * 
     * <p>Description: updates the currently selected user when the admin selects a user from the ComboBox </p>
     */
    protected static void doSelectUser() {
        ViewOTP.theSelectedUser =
                ViewOTP.combobox_SelectUser.getValue();
    }

    /*******
     * <p> Method: repaintWindow() </p>
     * 
     * <p> Description: Clears the root pane and add all the components back </p>
     */
    protected static void repaintWindow() {
    	// Clear what had been displayed
        ViewOTP.theRootPane.getChildren().clear();
     // Show all the fields
        ViewOTP.theRootPane.getChildren().addAll(
                ViewOTP.label_PageTitle,
                ViewOTP.label_UserDetails,
                ViewOTP.label_SelectUser,
                ViewOTP.combobox_SelectUser,
                ViewOTP.button_GenerateOTP,
                ViewOTP.button_Return,
                ViewOTP.button_Logout,
                ViewOTP.button_Quit
        );
        
     // Set the title for the window
        ViewOTP.theStage.setTitle("Admin – One-Time Password Reset");
        ViewOTP.theStage.setScene(ViewOTP.theScene);
        ViewOTP.theStage.show();
    }

   /*******
    * <p> Method: performGenerateOTP()
    * 
    * <p> Description: Generates a one time password for the selected user and updates the database.
    *              This method automatically retrieves the user's email address from the database
    *              and sends tje OTP to that email. If no email is found an alert is shown.
    *              Upon success, an alert is shown and details are printed on the console.</p>
    */

    protected static void performGenerateOTP() {
        //check that a valid user is selected.
        if (ViewOTP.theSelectedUser == null ||
        		ViewOTP.theSelectedUser.startsWith("<"))
            return;
        
        
		
        // Retrieve the user's email address from the database
        String emailAddress =
                theDatabase.getEmailAddress(ViewOTP.theSelectedUser);

        // If no email address is found, abort the operation
        if (emailAddress == null || emailAddress.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Not Found");
            alert.setHeaderText("Unable to Generate OTP");
            alert.setContentText("No email address is associated with the selected user.");
            alert.showAndWait();
            return;
        }
        //generate one time password
        String otp = theDatabase.generateOneTimePassword();
        
        //store the OTP in the database and updates the user's password
        if (theDatabase.setOneTimePassword(
        		ViewOTP.theSelectedUser, otp)) {
              theDatabase.updateNewPassword(ViewOTP.theSelectedUser, otp);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("One-Time Password");
            alert.setHeaderText("OTP Generated Successfully");
            alert.setContentText("User: " + ViewOTP.theSelectedUser +"\nOne-Time Password: " + otp + " sent to email address: " + emailAddress);

            alert.showAndWait();
            //print details out on the console
            System.out.println("User: " + ViewOTP.theSelectedUser +"\nOne-Time Password: " + otp + " sent to email address: " + emailAddress);
        }
       
    }

    

  /*******
   * <p> Method: performReturn </p>
   * 
   * <p> Description: returns admin to home page. </p>
   */

    protected static void performReturn() {
        guiAdminHome.ViewAdminHome.displayAdminHome(
        		ViewOTP.theStage,
        		ViewOTP.theUser);
    }
    /*******
     * <p> Method: performLogout </p>
     * 
     * <p> Description: logs out the admin and returns to the user login page </p>
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(
        		ViewOTP.theStage);
    }
     
    /*******
     * <p> Method: performQuit </p>
     * 
     * <p> Description: Quits the application. </p>
     */
    protected static void performQuit() {
        System.exit(0);
    }
}

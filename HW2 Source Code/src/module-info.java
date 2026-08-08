module FoundationsF25 {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;
   // requires h2;
    requires org.junit.jupiter.api;

    exports applicationMain;
    exports passwordEvaluationTestbedMain;

    opens applicationMain to javafx.graphics, javafx.fxml;
    opens passwordEvaluationTestbedMain to javafx.graphics, javafx.fxml;
    opens guiAdminHome to javafx.graphics, javafx.fxml;
    opens guiUserUpdate to javafx.graphics, javafx.fxml;
    opens guiRole2 to javafx.graphics, javafx.fxml;
   // opens prototype to javafx.graphics, javafx.fxml;
    opens database to javafx.graphics;
    opens entityClasses to javafx.graphics;
}


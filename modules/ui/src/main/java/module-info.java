/**
 * @author Harley O'Connor
 */
module gitdesk.ui {
    requires gitdesk.util;
    requires gitdesk.git;
    requires gitdesk.data;
    requires kotlin.stdlib;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    exports com.harleyoconnor.gitdesk.ui;

    opens com.harleyoconnor.gitdesk.ui.node to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.menu to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.menu.open to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.menu.clone to javafx.fxml;

    opens ui.stylesheets;
}
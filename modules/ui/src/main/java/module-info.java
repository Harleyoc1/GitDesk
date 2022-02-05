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
    requires org.fxmisc.richtext;
    requires moshi;
    requires reactfx;
    requires wellbehavedfx;
    requires org.apache.logging.log4j;
    requires kotlin.stdlib.jdk7;

    exports com.harleyoconnor.gitdesk.ui;
    exports com.harleyoconnor.gitdesk.ui.highlighting;

    opens com.harleyoconnor.gitdesk.ui.highlighting to kotlin.reflect;

    opens com.harleyoconnor.gitdesk.ui.node to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.menubar to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.account to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.account.link to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.account.register to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.account.signin to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.menu to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.clone to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.menu.clone to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.menu.create to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.menu.open to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.repository to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.repository.branch to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.repository.changes to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.repository.editor to javafx.fxml;
    opens com.harleyoconnor.gitdesk.ui.repository.issues to javafx.fxml;

    opens ui.images;
    opens ui.stylesheets;
}
/**
 * @author Harley O'Connor
 */
module gitdesk.util {
    requires kotlin.stdlib;
    requires org.apache.logging.log4j;
    requires java.xml;
    exports com.harleyoconnor.gitdesk.util;
    exports com.harleyoconnor.gitdesk.util.process;
    exports com.harleyoconnor.gitdesk.util.syntax;
    exports com.harleyoconnor.gitdesk.util.system;
    exports com.harleyoconnor.gitdesk.util.tree;
    exports com.harleyoconnor.gitdesk.util.tree.traversal;
    exports com.harleyoconnor.gitdesk.util.xml;

    exports com.harleyoconnor.gitdesk.util.logging;
}
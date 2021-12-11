/**
 * @author Harley O'Connor
 */
module gitdesk.git {
    requires kotlin.stdlib;
    requires gitdesk.util;

    exports com.harleyoconnor.gitdesk.git;
    exports com.harleyoconnor.gitdesk.git.repository;
}
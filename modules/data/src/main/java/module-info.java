/**
 * @author Harley O'Connor
 */
module gitdesk.data {
    requires gitdesk.util;
    requires moshi;
    requires moshi.kotlin;
    requires kotlin.stdlib;
    exports com.harleyoconnor.gitdesk.data;
    exports com.harleyoconnor.gitdesk.data.repository;
    exports com.harleyoconnor.gitdesk.data.serialisation.qualifier;
    exports com.harleyoconnor.gitdesk.data.syntax;

    exports com.harleyoconnor.gitdesk.data.serialisation.adapter to moshi;
    opens com.harleyoconnor.gitdesk.data to kotlin.reflect;
    opens com.harleyoconnor.gitdesk.data.repository to kotlin.reflect;
}
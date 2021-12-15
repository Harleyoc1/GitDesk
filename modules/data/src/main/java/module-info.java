/**
 * @author Harley O'Connor
 */
module gitdesk.data {
    requires gitdesk.util;
    requires gitdesk.git;
    requires moshi;
    requires moshi.kotlin;
    requires kotlin.stdlib;
    requires java.net.http;
    requires org.apache.logging.log4j;

    exports com.harleyoconnor.gitdesk.data;
    exports com.harleyoconnor.gitdesk.data.local;
    exports com.harleyoconnor.gitdesk.data.remote;
    exports com.harleyoconnor.gitdesk.data.remote.github;
    exports com.harleyoconnor.gitdesk.data.remote.github.search;
    exports com.harleyoconnor.gitdesk.data.serialisation.qualifier;

    exports com.harleyoconnor.gitdesk.data.serialisation.adapter to moshi;
    opens com.harleyoconnor.gitdesk.data to kotlin.reflect;
    opens com.harleyoconnor.gitdesk.data.local to kotlin.reflect;
    opens com.harleyoconnor.gitdesk.data.remote.github to kotlin.reflect;
    opens com.harleyoconnor.gitdesk.data.remote.github.search to kotlin.reflect;
}
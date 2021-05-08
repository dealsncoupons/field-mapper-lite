package works.hop.field.jdbc.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UpdateTemplate {

    static final Logger log = LoggerFactory.getLogger(UpdateTemplate.class);

    public static String prepareQuery(String tableName, Map<String, Object> fields) {
        //TODO: still contemplating how to do this using 'WITH' syntax (for idempotency)
        return null;
    }
}

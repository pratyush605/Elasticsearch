import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class JsonFieldExtractor {

    static final String[] ROOT_FIELDS = {
            "BUYLEADS_SUBJECT",
            "BUYLEADS_FULL_TEXT",
            "BUYLEADS_EDIT_TEXT",
            "KEYWORDS1",
            "KEYWORDS2",
            "KEYWORDS3",
            "KEYWORDS4",
            "KEYWORDS5",
            "COMPNAME",
            "CITY",
            "CONTINENT_NAME",
            "COUNTRY_NAME",
            "DEAL_SIZE_CURRENCY_NAME",
            "DESTINATION_COUNTRY_NAME",
            "DESTINATION_PORT",
            "PACKAGING_TERMS",
            "PAYMENT_TERMS",
            "PRICE_UNITS",
            "PRICE_PER_UNIT",
            "QUANTITY_DURATION",
            "QUANTITY_UNITS",
            "REQUIREMENT_NATURE",
            "SHIPPING_TERMS",
            "STATE",
            "TARGET_COUNTRIES",
            "TARGET_PRICE_CURRENCY_NAME"
    };

    static final String[] CATEGORY_FIELDS = {
            "CATG_NAME_ID1",
            "CATG_NAME_ID2",
            "CATG_NAME_ID3",
            "CATG_NAME_ID4",
            "CATG_NAME_ID5",
            "CATG_NAME_ID6"
    };

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            String json = new String(
                    Files.readAllBytes(
                            Paths.get("JsonFieldExtractorData.json")));

            ScriptEngine engine = new ScriptEngineManager()
                    .getEngineByName("javascript");

            Object result = engine.eval("Java.asJSONCompatible(" + json + ")");

            Map<String, Object> root = (Map<String, Object>) result;

            String searchable = buildString(root);

            Files.write(
                Paths.get("SemanticVectorData.txt"),
                searchable.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    static String buildString(Map<String, Object> root) {
        StringBuilder sb = new StringBuilder();

        for (String field : ROOT_FIELDS) {
            appendField(sb, field, root.get(field));
        }

        Object categoriesObj = root.get("CATEGORIES");

        if (categoriesObj instanceof List) {
            List<Map<String, Object>> categories = (List<Map<String, Object>>) categoriesObj;

            for (Map<String, Object> category : categories) {
                for (String field : CATEGORY_FIELDS) {
                    appendField(sb, field, category.get(field));
                }
            }
        }

        return sb.toString().trim();
    }

    static void appendField(
            StringBuilder sb,
            String fieldName,
            Object value) {
        if (value != null) {
            String s = value.toString().trim();

            if (!s.isEmpty()
                    && !"null".equalsIgnoreCase(s)
                    && !"NULL".equalsIgnoreCase(s)) {

                sb.append(fieldName)
                        .append(": ")
                        .append(s)
                        .append("\n");
            }
        }
    }
}
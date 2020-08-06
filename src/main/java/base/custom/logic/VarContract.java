package base.custom.logic;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

abstract class VarContract {
    protected static final Charset CHARSET = StandardCharsets.UTF_8;

    protected static final String CLASS_TYPE = "CT:";
    protected static final String FIELD_TYPE_AND_VALUE = "FTaV:";

    protected static final String FIELD_TYPE_AND_VALUE_SEPARATOR = "=";
    protected static final String ORDINARY_SEPARATOR = ",";
    protected static final String COLLECTION_SEPARATOR = ";";

    protected static final String EMPTY_LINE = "";
    protected static final String LEFT_SQUARE_BRACKET = "[";
    protected static final String RIGHT_SQUARE_BRACKET = "]";

    protected static final int TYPE_PARAM_POSITION = 0;
    protected static final int FIRST_FIELD_PARAM_POSITION = 1;

    protected static final List<String> VALID_INTERFACES = List.of("List", "Set", "Queue");

    protected static final int SINGLE_ELEMENT_SEQUENCE_NUMBER = 0;
}
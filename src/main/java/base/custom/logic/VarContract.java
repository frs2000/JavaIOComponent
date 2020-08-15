package base.custom.logic;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

abstract class VarContract {
    static final Charset CHARSET = StandardCharsets.UTF_8;

    static final String CLASS_TYPE = "CT:";
    static final String COLLECTION_ELEMENT_CLASS_TYPE = "CecT:";
    static final String FIELD_TYPE_AND_VALUE = "FTaV:";

    static final String FIELD_TYPE_AND_VALUE_SEPARATOR = "=";
    static final String SEPARATOR = ",";
    static final String COLLECTION_SEPARATOR = ";";
    static final String INNER_OBJECT_SEPARATOR = "~;";
    static final String MULTILEVEL_COLLECTION_SEPARATOR = "~";

    static final String EMPTY_LINE = "";
    static final String LEFT_SQUARE_BRACKET = "[";
    static final String RIGHT_SQUARE_BRACKET = "]";

    static final int TYPE_PARAM_POSITION = 0;
    static final int FIRST_FIELD_PARAM_POSITION = 1;

    static final int SINGLE_ELEMENT_SEQUENCE_NUMBER = 0;
    static final int EMPTY_COLLECTION_SIZE = 0;

    static final String MAIN_PARENT = "java.lang.Object";

    static final List<String> VALID_INTERFACES = List.of("List", "Set", "Queue");
    static final List<String> STANDARD_VAR_TYPES =
            List.of("byte", "short", "int", "long", "char", "float", "double", "boolean", "string");

    static final String THIS_OBJECT = "ThisO";
    static final String NULL_VALUE = "null";
    static final String EMPTY_STRING = "ES";

    static final String REGEXP_COLLECTION_INSIDE_COLLECTION = ".*\\[.*=\\[.*";
    static final String REGEXP_COLLECTION = ".*\\[.*\\].*";
    static final String REGEXP_SIMPLE_COLLECTION_SPLITERATOR = "\\];\\[";
}
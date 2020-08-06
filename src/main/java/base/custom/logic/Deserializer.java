package base.custom.logic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.lang.System.err;
import static java.util.stream.Collectors.toList;

public class Deserializer extends VarContract {
    private static boolean internalObject;

    public static synchronized Object deserialize(byte[] bates) {
        return getObjectFrom(convertToString(bates));
    }

    private static String convertToString(byte[] bytes) {
        return new String(bytes, CHARSET);
    }

    private static Object getObjectFrom(String objContent) {
        String[] objInfo = objContent.split(chooseSeparator());
        String[] fieldsInfo = extractFieldsInfo(objInfo);

        Class reflectInst = getInstance(objInfo);
        Field[] reflectFields = reflectInst.getDeclaredFields();
        fieldCountValidation(fieldsInfo.length, reflectFields.length);

        Object returnedObj = createObjectFrom(reflectInst);
        initFields(reflectFields, fieldsInfo, returnedObj);

        return returnedObj;
    }

    private static String chooseSeparator() {
        return internalObject ? COLLECTION_SEPARATOR : ORDINARY_SEPARATOR;
    }

    private static String[] extractFieldsInfo(String[] objInfo) {
        return Arrays.copyOfRange(objInfo, FIRST_FIELD_PARAM_POSITION, objInfo.length);
    }

    private static Class getInstance(String[] objInfo) {
        String type = extractObjectType(objInfo);
        Class reflectInst = null;
        try {
            reflectInst = Class.forName(type);
        } catch (ClassNotFoundException e) {
            err.println(String.format("ERROR! Problem with class {%s}. Maybe path to original class was changed", type));
            e.printStackTrace();
        }
        return reflectInst;
    }

    private static String extractObjectType(String[] objInfo) {
        return objInfo[TYPE_PARAM_POSITION].replace(CLASS_TYPE, EMPTY_LINE);
    }

    private static void fieldCountValidation(int incomingObjFieldsCount, int reflectFieldsCount) {
        if (incomingObjFieldsCount != reflectFieldsCount) {
            throw new IllegalArgumentException("Fields count of serialized and created objects " +
                    "is different. Most likely, fields number was changed in the current object");
        }
    }

    private static Object createObjectFrom(Class reflectInst) {
        Object returnedObj = null;
        try {
            returnedObj = reflectInst.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return returnedObj;
    }

    private static void initFields(Field[] fields, String[] fieldsInfo, Object returnedObj) {
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            setFullAccessFor(field);
            String value = extractFieldValue(field, fieldsInfo[i]);

            Object initValue = normalizeValueForInit(field, value);
            setValueTo(field, initValue, returnedObj);
        }
    }

    private static void setFullAccessFor(Field field) {
        field.setAccessible(true);
    }

    private static String extractFieldValue(Field field, String fieldInfo) {
        String prefix = String.format("%s%s%s", FIELD_TYPE_AND_VALUE, field.getName(), FIELD_TYPE_AND_VALUE_SEPARATOR);
        return fieldInfo.replace(prefix, EMPTY_LINE);
    }

    private static Object normalizeValueForInit(Field field, String value) {
        return isItCollection(value) ? collectCollection(field, value) : getCastedValue(field, value);
    }

    private static boolean isItCollection(String fieldContent) {
        return fieldContent.contains(LEFT_SQUARE_BRACKET);
    }

    private static Collection collectCollection(Field field, String collectionContent) {
        internalObject = true;

        List<String> elements = splitOnElements(collectionContent);
        Collection initCollection = createCollectionBy(field.getType().toString());

        elements.forEach(el -> initCollection.add(getObjectFrom(el)));

        internalObject = false;
        return initCollection;
    }

    private static List<String> splitOnElements(String collectionContent) {
        String[] elements = collectionContent.split(String.format("%s%s", RIGHT_SQUARE_BRACKET, COLLECTION_SEPARATOR));
        return Arrays.stream(elements)
                .map(it -> it.replace(LEFT_SQUARE_BRACKET, EMPTY_LINE))
                .map(it -> it.replace(RIGHT_SQUARE_BRACKET, EMPTY_LINE))
                .collect(toList());
    }

    private static Collection createCollectionBy(String type) {
        if (type.contains("Set")) return new HashSet();
        if (type.contains("Queue")) return new LinkedList();
        return new ArrayList();
    }

    private static void setValueTo(Field field, Object value, Object returnedObj) {
        try {
            field.set(returnedObj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Object getCastedValue(Field field, String value) {
        String type = field.getType().toString().toLowerCase();
        return convertValueToType(value, type);
    }

    private static Object convertValueToType(String value, String type) {
        if (type.contains("byte")) return Byte.valueOf(value);
        if (type.contains("short")) return Short.valueOf(value);
        if (type.contains("int")) return Integer.valueOf(value);
        if (type.contains("long")) return Long.valueOf(value);
        if (type.contains("char")) return value.charAt(SINGLE_ELEMENT_SEQUENCE_NUMBER);
        if (type.contains("float")) return Float.valueOf(value);
        if (type.contains("double")) return Double.valueOf(value);
        if (type.contains("boolean")) return Boolean.valueOf(value);
        return value;
    }
}
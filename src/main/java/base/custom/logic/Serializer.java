package base.custom.logic;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

public class Serializer extends VarContract {
    private static boolean internalObject;

    public static synchronized byte[] serialize(SerializableMarker object) {
        return getDataForSerialization(object).getBytes(CHARSET);
    }

    private static String getDataForSerialization(SerializableMarker object) {
        Class reflectObj = object.getClass();

        StringBuilder sb = new StringBuilder();
        collectMetadata(sb, reflectObj);
        collectFieldsData(sb, reflectObj, object);

        return removeLastSeparator(sb.toString());
    }

    private static void collectMetadata(StringBuilder sb, Class reflectObj) {
        sb.append(CLASS_TYPE)
                .append(reflectObj.getTypeName())
                .append(chooseSeparator());
    }

    private static String chooseSeparator() {
        return internalObject ? COLLECTION_SEPARATOR : ORDINARY_SEPARATOR;
    }

    private static void collectFieldsData(StringBuilder sb, Class reflectObj, SerializableMarker object) {
        Field[] fields = reflectObj.getDeclaredFields();
        for (Field field : fields) {
            sb.append(FIELD_TYPE_AND_VALUE);
            addFieldName(sb, field);
            addFieldValue(sb, field, object);
        }
    }

    private static void addFieldName(StringBuilder sb, Field field) {
        sb.append(field.getName())
                .append(FIELD_TYPE_AND_VALUE_SEPARATOR);
    }

    private static void addFieldValue(StringBuilder sb, Field field, SerializableMarker object) {
        if (isCollection(field)) {
            collectCollection(field, sb, object);
        } else {
            addValue(field, sb, object);
        }
    }

    private static boolean isCollection(Field field) {
        String fieldType = field.getType().toString();
        for (String type : VALID_INTERFACES) {
            if (fieldType.contains(type)) return true;
        }
        return false;
    }

    private static void collectCollection(Field field, StringBuilder sb, SerializableMarker object) {
        setFullAccessFor(field);

        try {
            Collection collection = (Collection) field.get(object);
            for (Object element : collection) {
                Class elemReflectObj = Class.forName(element.getClass().getName());
                Class<?>[] interfaces = elemReflectObj.getInterfaces();

                if (isHasMarker(interfaces)) collectCollectionElement(sb, element);
            }
            replaceSeparatorAfterCollection(sb);
        } catch (IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void setFullAccessFor(Field field) {
        field.setAccessible(true);
    }

    private static boolean isHasMarker(Class<?>[] interfaces) {
        return Arrays.asList(interfaces).contains(SerializableMarker.class);
    }

    private static void collectCollectionElement(StringBuilder sb, Object element) {
        internalObject = true;
        sb.append(LEFT_SQUARE_BRACKET)
                .append(getDataForSerialization((SerializableMarker) element))
                .append(RIGHT_SQUARE_BRACKET)
                .append(chooseSeparator());
    }

    private static void replaceSeparatorAfterCollection(StringBuilder sb) {
        internalObject = false;
        sb.replace(sb.length() - 1, sb.length(), chooseSeparator());
    }

    private static void addValue(Field field, StringBuilder sb, SerializableMarker object) {
        sb.append(getFieldValue(field, object))
                .append(chooseSeparator());
    }

    private static String getFieldValue(Field field, SerializableMarker object) {
        setFullAccessFor(field);

        Object fieldValue = null;
        try {
            fieldValue = field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return String.valueOf(fieldValue);
    }

    private static String removeLastSeparator(String str) {
        return str.substring(0, str.length() - 1);
    }
}
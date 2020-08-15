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
        Class<?> reflectObj = object.getClass();
        StringBuilder sb = new StringBuilder();
        collectMetadata(sb, reflectObj);

        if (isIncomingObjCollection(reflectObj)) {
            addMetadataCollectionType(sb, object);
            collectObjCollection(object, sb);
        } else {
            collectFieldsData(sb, reflectObj, object);
        }

        return removeLastSeparator(sb.toString());
    }

    private static void collectMetadata(StringBuilder sb, Class<?> reflectObj) {
        sb.append(CLASS_TYPE)
                .append(reflectObj.getTypeName())
                .append(chooseSeparator());
    }

    private static boolean isIncomingObjCollection(Class<?> reflectObj) {
        return isCollection(reflectObj.getTypeName());
    }

    private static boolean isCollection(String type) {
        return VALID_INTERFACES.stream().anyMatch(type::contains);
    }


    private static void addMetadataCollectionType(StringBuilder sb, SerializableMarker object) {
        Collection<?> collection = (Collection<?>) object;
        if (collection.size() != EMPTY_COLLECTION_SIZE) {
            Object firstElement = collection.toArray()[TYPE_PARAM_POSITION];

            sb.append(COLLECTION_ELEMENT_CLASS_TYPE)
                    .append(firstElement.getClass().getTypeName())
                    .append(chooseSeparator());
        }
    }

    private static String chooseSeparator() {
        return internalObject ? COLLECTION_SEPARATOR : SEPARATOR;
    }

    private static void collectObjCollection(SerializableMarker object, StringBuilder sb) {
        Collection<?> collection = (Collection<?>) object;
        collection.forEach(it -> addElementToCollection(sb, it));
        internalObject = false;
    }

    private static void addElementToCollection(StringBuilder sb, Object element) {
        if (isObjMarked(element.getClass())) {
            internalObject = true;
            addElement(sb, getDataForSerialization((SerializableMarker) element));
        }

        if (isStandardType(element)) {
            internalObject = true;
            addElement(sb, element);
        }
    }

    private static void collectFieldsData(StringBuilder sb, Class<?> reflectObj, SerializableMarker object) {
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
        String type = field.getType().toString();
        return isCollection(type);
    }

    private static void collectCollection(Field field, StringBuilder sb, SerializableMarker object) {
        Object collection = getFieldValue(field, object);

        if (collection != null) {
            boolean type = isStandardTypeCollection(field);

            for (Object element : (Collection<?>) collection) {
                if (type) {
                    addElement(sb, element);
                } else {
                    addElementToCollection(sb, element);
                }
            }
            replaceSeparatorAfterCollection(sb);
        } else {
            addValue(sb, null);
        }
    }

    private static Object getFieldValue(Field field, SerializableMarker object) {
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isStandardTypeCollection(Field field) {
        String collectionType = field.getGenericType().toString().toLowerCase();
        return STANDARD_VAR_TYPES.stream().anyMatch(collectionType::contains);
    }

    private static boolean isStandardType(Object element) {
        String elementType = element.getClass().toString().toLowerCase();
        return STANDARD_VAR_TYPES.stream().anyMatch(elementType::contains);
    }

    private static boolean isObjMarked(Class<?> elemReflectObj) {
        Class<?>[] interfaces = elemReflectObj.getInterfaces();
        return Arrays.asList(interfaces).contains(SerializableMarker.class);
    }

    private static void addElement(StringBuilder sb, Object element) {
        internalObject = true;
        sb.append(LEFT_SQUARE_BRACKET)
                .append(element)
                .append(RIGHT_SQUARE_BRACKET)
                .append(chooseSeparator());
    }

    private static void replaceSeparatorAfterCollection(StringBuilder sb) {
        internalObject = false;
        sb.replace(sb.length() - 1, sb.length(), chooseSeparator());
    }

    private static void addValue(Field field, StringBuilder sb, SerializableMarker object) {
        Object obj = getFieldValue(field, object);
        if (obj == null) {
            addValue(sb, NULL_VALUE);
            return;
        }

        if (obj.equals(object)) {
            addValue(sb, THIS_OBJECT);
            return;
        }

        if (obj.equals(EMPTY_LINE)) {
            addValue(sb, EMPTY_STRING);
            return;
        }

        if (isObjMarked(obj.getClass())) {
            internalObject = !internalObject;
            addValue(sb, getDataForSerialization((SerializableMarker) obj));
            return;
        }

        addValue(sb, String.valueOf(obj));
    }

    private static void addValue(StringBuilder sb, Object value) {
        sb.append(value).append(chooseSeparator());
    }

    private static String removeLastSeparator(String str) {
        return str.substring(0, str.length() - 1);
    }
}
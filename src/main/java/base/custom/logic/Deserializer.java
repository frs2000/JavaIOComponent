package base.custom.logic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.lang.System.err;
import static java.util.stream.Collectors.toList;

public class Deserializer extends VarContract {
    private boolean internalObject;

    public Object deserialize(byte[] bates) {
        return getObjectFrom(convertToString(bates));
    }

    private String convertToString(byte[] bytes) {
        return new String(bytes, CHARSET);
    }

    private Object getObjectFrom(String objContent) {
        String[] objInfo = getObjInfo(objContent);
        String[] fieldsInfo = extractFieldsInfo(objInfo);

        Class<?> reflectInst = getInstance(objInfo);
        if (isIncomingObjCollection(reflectInst)) {
            String collectionType = fieldsInfo[TYPE_PARAM_POSITION];
            fieldsInfo = deleteCollectionType(fieldsInfo);
            return collectCollection(reflectInst, collectionType, fieldsInfo);
        }

        Field[] reflectFields = reflectInst.getDeclaredFields();
        fieldCountValidation(fieldsInfo.length, reflectFields.length);

        Object returnedObj = createObjectFrom(reflectInst);
        initFields(reflectFields, fieldsInfo, returnedObj);
        return returnedObj;
    }

    private String[] getObjInfo(String objContent) {
        if (internalObject && isHasField(objContent) && isHasCollection(objContent)) {
            objContent = objContent.replace(";FTaV:", "~;FTaV:");
            return objContent.split(INNER_OBJECT_SEPARATOR);
        }
        return objContent.split(chooseSeparator());
    }

    private boolean isHasField(String objContent) {
        return objContent.contains(FIELD_TYPE_AND_VALUE);
    }

    private boolean isHasCollection(String objContent) {
        return objContent.matches(REGEXP_COLLECTION);
    }

    private String chooseSeparator() {
        return internalObject ? COLLECTION_SEPARATOR : SEPARATOR;
    }

    private String[] extractFieldsInfo(String[] objInfo) {
        return Arrays.copyOfRange(objInfo, FIRST_FIELD_PARAM_POSITION, objInfo.length);
    }

    private Class<?> getInstance(String[] objInfo) {
        String type = extractObjectType(objInfo);
        Class<?> reflectInst = null;
        try {
            reflectInst = Class.forName(type);
        } catch (ClassNotFoundException e) {
            err.println(String.format("ERROR! Problem with class {%s}. Maybe path to original class was changed", type));
            e.printStackTrace();
        }
        return reflectInst;
    }

    private String extractObjectType(String[] objInfo) {
        return objInfo[TYPE_PARAM_POSITION].replace(CLASS_TYPE, EMPTY_LINE);
    }

    private boolean isIncomingObjCollection(Class<?> reflectObj) {
        Class<?>[] interfaces = reflectObj.getInterfaces();
        boolean result = Arrays.stream(interfaces).anyMatch(it -> isCollection(it.getTypeName()));
        if (result) return true;

        Class<?> superclass = reflectObj.getSuperclass();
        if (superclass == null || superclass.getTypeName().equals(MAIN_PARENT)) return false;

        return isIncomingObjCollection(superclass);
    }

    private boolean isCollection(String type) {
        return VALID_INTERFACES.stream().anyMatch(type::contains);
    }

    private String[] deleteCollectionType(String[] objInfo) {
        return extractFieldsInfo(objInfo);
    }

    private Collection<?> collectCollection(Class<?> reflectInst, String collectionType, String[] collectionContent) {
        String elementType = collectionType.toLowerCase();
        Object[] base = getBaseCollection(elementType, getString(collectionContent));
        return createCollectionForInit(reflectInst.getTypeName(), base);
    }

    private String getString(String... array) {
        return String.join("", array);
    }

    private Object[] getBaseCollection(String elementType, String collectionContent) {
        internalObject = true;
        List<String> elements = splitOnElements(collectionContent);
        List<Object> baseCollection = new ArrayList<>();

        if (isStandardVarType(elementType)) {
            elements.forEach(it -> baseCollection.add(convertValueToType(it, elementType)));
        } else {
            elements.forEach(it -> baseCollection.add(getObjectFrom(it)));
        }

        internalObject = false;
        return baseCollection.toArray();
    }

    private List<String> splitOnElements(String collectionContent) {
        if (collectionContent.matches(REGEXP_COLLECTION_INSIDE_COLLECTION)) {
            return splitMultilevelCollection(collectionContent);
        }
        return splitSingleLevelCollection(collectionContent);
    }

    private List<String> splitMultilevelCollection(String collectionContent) {
        String temp = collectionContent.replace("];[CT:", "];~[CT:");
        String[] elements = temp.split(MULTILEVEL_COLLECTION_SEPARATOR);
        return Arrays.stream(elements)
                .map(it -> it.substring(1))
                .map(it -> getString(it, MULTILEVEL_COLLECTION_SEPARATOR))
                .map((it -> it.replace("];~", EMPTY_LINE)))
                .map((it -> it.replace("]~", EMPTY_LINE)))
                .collect(toList());
    }

    private List<String> splitSingleLevelCollection(String collectionContent) {
        String[] elements = collectionContent.split(REGEXP_SIMPLE_COLLECTION_SPLITERATOR);
        return Arrays.stream(elements)
                .map(it -> it.replace(LEFT_SQUARE_BRACKET, EMPTY_LINE))
                .map(it -> it.replace(RIGHT_SQUARE_BRACKET, EMPTY_LINE))
                .collect(toList());
    }

    private boolean isStandardVarType(String type) {
        return STANDARD_VAR_TYPES.stream().anyMatch(type::contains);
    }

    private Object convertValueToType(String value, String type) {
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

    private Collection<?> createCollectionForInit(String type, Object[] base) {
        if (type.contains("Set")) return Set.of(base);
        if (type.contains("Queue")) return new LinkedList(Arrays.asList(base));
        return Arrays.asList(base);
    }

    private void fieldCountValidation(int incomingObjFieldsCount, int reflectFieldsCount) {
        if (incomingObjFieldsCount != reflectFieldsCount) {
            throw new IllegalArgumentException("Fields count of serialized and created objects " +
                    "is different. Most likely, fields number was changed in the current object");
        }
    }

    private Object createObjectFrom(Class<?> reflectInst) {
        Object returnedObj = null;
        try {
            returnedObj = reflectInst.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return returnedObj;
    }

    private void initFields(Field[] fields, String[] fieldsInfo, Object returnedObj) {
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            String fieldInfo = fieldsInfo[i];
            if (!isFieldHasInternalObj(fieldInfo)) {
                if (!isItCollection(fieldInfo) && isFieldValueNull(fieldInfo)) {
                    continue;
                }
            }
            String value = extractFieldValue(field, fieldInfo);
            if (isItCurrentObjRecursiveLink(value)) {
                setValueTo(field, returnedObj, returnedObj);
                continue;
            }

            Object initValue = normalizeValueForInit(field, value);
            setValueTo(field, initValue, returnedObj);
        }
    }

    private boolean isFieldHasInternalObj(String fieldInfo) {
        return fieldInfo.contains(FIELD_TYPE_AND_VALUE_SEPARATOR + CLASS_TYPE);
    }

    private boolean isItCollection(String fieldContent) {
        return isHasCollection(fieldContent);
    }

    private boolean isFieldValueNull(String fieldInfo) {
        return fieldInfo.contains(FIELD_TYPE_AND_VALUE_SEPARATOR + NULL_VALUE);
    }

    private static boolean isItCurrentObjRecursiveLink(String value) {
        return value.equals(THIS_OBJECT);
    }

    private void setValueTo(Field field, Object value, Object returnedObj) {
        field.setAccessible(true);
        try {
            field.set(returnedObj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private String extractFieldValue(Field field, String fieldInfo) {
        String prefix = getString(FIELD_TYPE_AND_VALUE, field.getName(), FIELD_TYPE_AND_VALUE_SEPARATOR);
        return fieldInfo.substring(prefix.length());
    }

    private Object normalizeValueForInit(Field field, String value) {
        if (isItCollection(value)) {
            return collectCollection(field, value);
        } else {
            return getCastedValue(field, value);
        }
    }

    private Collection<?> collectCollection(Field field, String collectionContent) {
        String elementType = field.getGenericType().toString().toLowerCase();
        Object[] base = getBaseCollection(elementType, collectionContent);
        return createCollectionForInit(field.getType().getTypeName(), base);
    }

    private Object getCastedValue(Field field, String value) {
        if (value.equals(EMPTY_STRING)) {
            return EMPTY_LINE;
        }

        if (value.contains(CLASS_TYPE)) {
            internalObject = !internalObject;
            return getObjectFrom(value);
        }

        String type = field.getType().toString().toLowerCase();
        return convertValueToType(value, type);
    }
}
package base.custom.logic;

import base.object.ComplexObject;
import base.object.SimpleObject;

import java.util.List;

class Helper {

    static final SimpleObject SIMPLE_OBJECT = new SimpleObject(1, true, "some_text");
    private static final SimpleObject SECOND_SIMPLE_OBJECT = new SimpleObject(2, false, "any_text");
    static final ComplexObject COMPLEX_OBJECT = getComplexObjects();

    private static ComplexObject getComplexObjects() {
        return new ComplexObject(3, List.of(SIMPLE_OBJECT, SECOND_SIMPLE_OBJECT), (byte) 127);
    }

    static final byte[] SERIALIZED_SIMPLE_OBJECT = getSimpleObjectBytes();
    static final byte[] SERIALIZED_COMPLEX_OBJECT = getComplexObjectBytes();

    private static byte[] getSimpleObjectBytes() {
        byte[] b = {67, 84, 58, 98, 97, 115, 101, 46, 111, 98, 106, 101, 99, 116, 46, 83, 105, 109, 112, 108, 101,
                79, 98, 106, 101, 99, 116, 44, 70, 84, 97, 86, 58, 105, 110, 116, 86, 97, 114, 61, 49, 44, 70, 84,
                97, 86, 58, 98, 111, 111, 108, 86, 97, 114, 61, 116, 114, 117, 101, 44, 70, 84, 97, 86, 58, 115,
                116, 114, 86, 97, 114, 61, 115, 111, 109, 101, 95, 116, 101, 120, 116};
        return b;
    }

    private static byte[] getComplexObjectBytes() {
        byte[] b = {67, 84, 58, 98, 97, 115, 101, 46, 111, 98, 106, 101, 99, 116, 46, 67, 111, 109, 112, 108, 101,
                120, 79, 98, 106, 101, 99, 116, 44, 70, 84, 97, 86, 58, 105, 110, 116, 86, 97, 114, 61, 51, 44, 70,
                84, 97, 86, 58, 115, 105, 109, 112, 108, 101, 79, 98, 106, 101, 99, 116, 115, 61, 91, 67, 84, 58, 98,
                97, 115, 101, 46, 111, 98, 106, 101, 99, 116, 46, 83, 105, 109, 112, 108, 101, 79, 98, 106, 101, 99,
                116, 59, 70, 84, 97, 86, 58, 105, 110, 116, 86, 97, 114, 61, 49, 59, 70, 84, 97, 86, 58, 98, 111, 111,
                108, 86, 97, 114, 61, 116, 114, 117, 101, 59, 70, 84, 97, 86, 58, 115, 116, 114, 86, 97, 114, 61,
                115, 111, 109, 101, 95, 116, 101, 120, 116, 93, 59, 91, 67, 84, 58, 98, 97, 115, 101, 46, 111, 98,
                106, 101, 99, 116, 46, 83, 105, 109, 112, 108, 101, 79, 98, 106, 101, 99, 116, 59, 70, 84, 97, 86,
                58, 105, 110, 116, 86, 97, 114, 61, 50, 59, 70, 84, 97, 86, 58, 98, 111, 111, 108, 86, 97, 114, 61,
                102, 97, 108, 115, 101, 59, 70, 84, 97, 86, 58, 115, 116, 114, 86, 97, 114, 61, 97, 110, 121, 95,
                116, 101, 120, 116, 93, 44, 70, 84, 97, 86, 58, 98, 121, 116, 101, 86, 97, 114, 61, 49, 50, 55};
        return b;
    }

    static final int THREAD_COUNT = 250;
}
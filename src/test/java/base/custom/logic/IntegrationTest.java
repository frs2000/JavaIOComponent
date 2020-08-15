package base.custom.logic;

import base.object.AnotherComplexObject;
import base.object.AnotherSimpleObject;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;

public class IntegrationTest {

    @Test
    void simple1() {
        AnotherSimpleObject so = new AnotherSimpleObject(1, true, "");
        assertEquals(Deserializer.deserialize(Serializer.serialize(so)), so);
    }

    @Test
    void simple2() {
        AnotherSimpleObject so = new AnotherSimpleObject(2, true, "2").setInts(Set.of(1, 2, 3));
        assertEquals(Deserializer.deserialize(Serializer.serialize(so)), so);
    }

    @Test
    void complex1() {
        AnotherSimpleObject so1 = new AnotherSimpleObject(1, true, "");
        AnotherSimpleObject so2 = new AnotherSimpleObject(2, true, "2").setInts(Set.of(1, 2, 3));
        AnotherComplexObject co = new AnotherComplexObject(-1, List.of(so1, so2), (byte) 2);
        assertEquals(Deserializer.deserialize(Serializer.serialize(co)), co);
    }

    @Test
    void complex2() {
        AnotherComplexObject co1 = new AnotherComplexObject(2, null, (byte) 1);
        AnotherComplexObject co2 = new AnotherComplexObject(2, null, (byte) 2);
        MyList<AnotherComplexObject> list = new MyList<>(List.of(co1, co2));
        assertEquals(Deserializer.deserialize(Serializer.serialize(list)), list);
    }

    @Test
    void complex3() {
        MyList<Integer> list = new MyList<>(List.of(1, 2, 3));
        assertEquals(Deserializer.deserialize(Serializer.serialize(list)), list);
    }

    @Test
    void complex4() {
        AnotherComplexObject child = new AnotherComplexObject(1, null, (byte) 1);
        AnotherComplexObject co = new AnotherComplexObject(2, null, (byte) 2).setChild(child);
        assertEquals(Deserializer.deserialize(Serializer.serialize(co)), co);
    }

    @Test(expectedExceptions = StackOverflowError.class)
    void complex5() {
        AnotherComplexObject co = new AnotherComplexObject(2, null, (byte) 2);
        co.setChild(co);
        assertEquals(Deserializer.deserialize(Serializer.serialize(co)), co);
    }

    private static class MyList<T> extends ArrayList<T> implements SerializableMarker {
        public MyList() {
        }

        public MyList(Collection<? extends T> c) {
            super(c);
        }
    }
}
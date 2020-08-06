package base.object;

import base.custom.logic.SerializableMarker;

import java.util.List;
import java.util.Objects;

public class ComplexObject implements SerializableMarker {

    private int intVar;
    private List<SimpleObject> simpleObjects;
    private byte byteVar;

    public ComplexObject() {
    }

    public ComplexObject(int intVar, List<SimpleObject> simpleObjects, byte byteVar) {
        this.intVar = intVar;
        this.simpleObjects = simpleObjects;
        this.byteVar = byteVar;
    }

    public int getIntVar() {
        return intVar;
    }

    public List<SimpleObject> getSimpleObjects() {
        return simpleObjects;
    }

    public byte getByteVar() {
        return byteVar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexObject that = (ComplexObject) o;
        return intVar == that.intVar &&
                byteVar == that.byteVar &&
                Objects.equals(simpleObjects, that.simpleObjects);
    }
}
package base.object;

import base.custom.logic.SerializableMarker;

import java.util.List;
import java.util.Objects;

public class AnotherComplexObject implements SerializableMarker {

    private int intVar;
    private List<AnotherSimpleObject> simpleObjects;
    private byte byteVar;
    private AnotherComplexObject child;

    public AnotherComplexObject() {
    }

    public AnotherComplexObject(int intVar, List<AnotherSimpleObject> simpleObjects, byte byteVar) {
        this.intVar = intVar;
        this.simpleObjects = simpleObjects;
        this.byteVar = byteVar;
    }

    public int getIntVar() {
        return intVar;
    }

    public List<AnotherSimpleObject> getSimpleObjects() {
        return simpleObjects;
    }

    public byte getByteVar() {
        return byteVar;
    }

    public AnotherComplexObject setChild(AnotherComplexObject child) {
        this.child = child;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnotherComplexObject that = (AnotherComplexObject) o;
        return intVar == that.intVar &&
                byteVar == that.byteVar &&
                Objects.equals(simpleObjects, that.simpleObjects) &&
                Objects.equals(child, that.child);
    }
}
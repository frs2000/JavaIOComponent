package base.object;

import base.custom.logic.SerializableMarker;

import java.util.Objects;
import java.util.Set;

public class AnotherSimpleObject implements SerializableMarker {
    private int intVar;
    private boolean boolVar;
    private String strVar;
    private Set<Integer> ints;

    public AnotherSimpleObject() {
    }

    public AnotherSimpleObject(int intVar, boolean boolVar, String strVar) {
        this.intVar = intVar;
        this.boolVar = boolVar;
        this.strVar = strVar;
    }

    public int getIntVar() {
        return intVar;
    }

    public boolean isBoolVar() {
        return boolVar;
    }

    public String getStrVar() {
        return strVar;
    }

    public AnotherSimpleObject setInts(Set<Integer> ints) {
        this.ints = ints;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnotherSimpleObject that = (AnotherSimpleObject) o;
        return intVar == that.intVar &&
                boolVar == that.boolVar &&
                Objects.equals(strVar, that.strVar) &&
                Objects.equals(ints, that.ints);
    }
}
package base.object;

import base.custom.logic.SerializableMarker;

import java.util.Objects;

public class SimpleObject implements SerializableMarker {
    private int intVar;
    private boolean boolVar;
    private String strVar;

    public SimpleObject() {
    }

    public SimpleObject(int intVar, boolean boolVar, String strVar) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleObject that = (SimpleObject) o;
        return intVar == that.intVar &&
                boolVar == that.boolVar &&
                Objects.equals(strVar, that.strVar);
    }
}
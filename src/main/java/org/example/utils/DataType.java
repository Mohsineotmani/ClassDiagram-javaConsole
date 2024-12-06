package org.example.utils;

public enum DataType {
    INT("int"),
    DOUBLE("double"),
    STRING("String"),
    BOOLEAN("boolean"),
    CHAR("char"),
    VOID("void");

    private final String typeName;

    DataType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public static DataType fromInt(int choice) {
        switch (choice) {
            case 1: return INT;
            case 2: return DOUBLE;
            case 3: return STRING;
            case 4: return BOOLEAN;
            case 5: return CHAR;
            case 6: return VOID;
            default: throw new IllegalArgumentException("Type inconnu");
        }
    }
}

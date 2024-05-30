package org.example.enums;

public enum Gender {
    MALE("М"),
    FEMALE("Ж");
    String code;
    Gender(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}

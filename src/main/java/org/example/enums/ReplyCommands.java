package org.example.enums;

public enum ReplyCommands{
    START("/start"),
    GET_FIRST_NAME(null),
    GET_LAST_NAME(null),
    GET_GENDER(null),
    DISPLAY_ALL_FUTURE_TOURNAMENTS("Предстоящие турниры"),
    DISPLAY_ALL_PAST_TOURNAMENTS("Прошедшие турниры"),
    DISPLAY_PROFILE("Мой профиль"),
    DISPLAY_CLUBS("Теннисные клубы СПб");
    String text;

    public String getText(){
        return text;
    }
    ReplyCommands(){}
    ReplyCommands(String text){
        this.text = text;
    }


}

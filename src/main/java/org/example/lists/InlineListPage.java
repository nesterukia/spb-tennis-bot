package org.example.lists;

import org.example.MainMenu;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class InlineListPage {
    public int getNumber() {
        return number;
    }

    private int number;
    private String msgText;
    private List<List<InlineKeyboardButton>> rowList;

    public InlineListPage(int number, String msgText, List<List<InlineKeyboardButton>> rowList){
        this.number = number;
        this.msgText = msgText;
        this.rowList = rowList;
        this.rowList.add(new MainMenu().inlineButtonRow());
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public List<List<InlineKeyboardButton>> getRowList() {
        return rowList;
    }

    public void setRowList(List<List<InlineKeyboardButton>> rowList) {
        this.rowList = rowList;
    }

    public String toString(){
        return msgText +"\n\n" + rowList;
    }
}

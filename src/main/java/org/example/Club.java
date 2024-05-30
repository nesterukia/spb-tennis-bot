package org.example;

import org.example.enums.Emojis;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Club {
    private long id;
    private String name;
    private String address;
    private String phoneNumber;

    public Club(){}
    public Club(long id, String name, String address, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return Emojis.TENNIS.getUnicode() + " <b>" + name + "</b>\n" +
                Emojis.PIN.getUnicode() + " " + address + '\n' +
                Emojis.PHONE.getUnicode() + " " + phoneNumber + '\n';
    }

    public SendMessage toSendMessage(long id){
        SendMessage message = new SendMessage();
        message.setChatId(Long.toString(id));
        message.setText(this.toString());
        message.setReplyMarkup(inlineKeyboardMarkup());
        return message;
    }

    public EditMessageText toEditMessageText(long chatId, int msgId){
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(Long.toString(chatId));
        editMessage.setMessageId(msgId);
        editMessage.setText(this.toString());
        editMessage.setParseMode("HTML");
        editMessage.setReplyMarkup(inlineKeyboardMarkup());
        return editMessage;
    }

    public InlineKeyboardMarkup inlineKeyboardMarkup(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText(Emojis.CUP.getUnicode()+" Предстоящие турниры");
        btn1.setCallbackData("displayClubTournaments/"+this.id);
        row1.add(btn1);
        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(row1);
        rowList.add(new MainMenu().inlineButtonRow());
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public void setId(long id) {
        this.id = id;
    }
    public long getId(){
        return id;
    }
}

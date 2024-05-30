package org.example;

import org.example.enums.Emojis;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class FutureTournament extends Tournament{

    protected int price;
    public int getPrice() {
        return price;
    }
    public FutureTournament setPrice(int price) {
        this.price = price;
        return this;
    }
    public boolean isRegistered(long playerId){
        Database db = new Database();
        return db.isRegisteredForTournament(this.id, playerId);
    }
    @Override
    public String toString() {
        return Emojis.CALENDAR.getUnicode() + " " + datetime + '\n'+
                Emojis.CUP.getUnicode() + " " + name + '\n'+
                Emojis.TENNIS.getUnicode() +" Категория: "+ category + " " + level + '\n' +
                Emojis.CASTLE.getUnicode() + " \"" + clubName + "\"\n" +
                Emojis.COST.getUnicode() + " " + price +"₽\n";
    }

    public SendMessage toSendMessage(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(Long.toString(chatId));
        if(isRegistered(chatId)){
            message.setText(this + "\nВы уже зарегистрированы на турнир");
        } else{
            message.setText(this.toString());
        }
        message.setReplyMarkup(inlineKeyboardMarkup(chatId));
        return message;
    }

    public EditMessageText toEditMessageText(long chatId, int msgId){
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(Long.toString(chatId));
        editMessage.setMessageId(msgId);
        if(isRegistered(chatId)){
            editMessage.setText(this + "\nВы уже зарегистрированы на турнир");
        } else if(!canRegister(chatId)){
            editMessage.setText(this + "\nВы не можете зарегистрироваться на этот турнир");
        } else{
            editMessage.setText(this.toString());
        }
        editMessage.setParseMode("HTML");
        editMessage.setReplyMarkup(inlineKeyboardMarkup(chatId));
        return editMessage;
    }

    private boolean canRegister(long chatId){
        Database db = new Database();
        return getCategory().charAt(0) == db.getUser(chatId).getGender();
    }
    private InlineKeyboardMarkup inlineKeyboardMarkup(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton regButton = new InlineKeyboardButton();
        regButton.setText(Emojis.CHECK.getUnicode()+" Зарегистрироваться");
        regButton.setCallbackData("regForTournament/" + this.id);

        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText(Emojis.CROSS.getUnicode() +" Отменить регистрацию");
        cancelButton.setCallbackData("cancelRegistration/" + this.id);
        if(isRegistered(chatId)){
            row1.add(cancelButton);
        } else{
            row1.add(regButton);
        }
        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        if(canRegister(chatId)){
            rowList.add(row1);
        }
        rowList.add(new MainMenu().inlineButtonRow());
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

}

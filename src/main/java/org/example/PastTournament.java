package org.example;

import org.example.enums.Emojis;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class PastTournament extends Tournament{

    private String winner;

    public String getWinner() {
        return winner;
    }

    public Tournament setWinner(String winner) {
        this.winner = winner;
        return this;
    }

    @Override
    public String toString() {
        return Emojis.CUP.getUnicode() + " <b>" + name + " " + datetime +"</b>\n"+
                Emojis.TENNIS.getUnicode() + " Категория: "+ category + " " + level + '\n' +
                Emojis.CASTLE.getUnicode() + " \"" + clubName + "\"\n" +
                Emojis.GOLD_MEDAL.getUnicode() + " " + winner +"\n";
    }

    public SendMessage toSendMessage(long id){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Подробнее");
        inlineKeyboardButton.setCallbackData("displayPastTournamentInfo/" + this.id);
        row1.add(inlineKeyboardButton);
        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(row1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        SendMessage message = new SendMessage();
        message.setChatId(Long.toString(id));
        message.setParseMode("HTML");
        message.setText(this.toString());
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
}

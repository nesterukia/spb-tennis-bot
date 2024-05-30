package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {

    public ReplyKeyboardMarkup replyKeyboardMarkup(){
        KeyboardButton btn = KeyboardButton.builder()
                .text("Главное меню").build();
        KeyboardRow row = new KeyboardRow(List.of(btn));

        ReplyKeyboardMarkup replyMarkup = ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .keyboardRow(row)
                .build();
        return replyMarkup;
    }
    public SendMessage display(long chatId) {
        KeyboardButton oncomingTournaments = KeyboardButton.builder()
                .text("Предстоящие турниры").build();
        KeyboardButton pastTournaments = KeyboardButton.builder()
                .text("Прошедшие турниры").build();
        KeyboardButton tennisClubs = KeyboardButton.builder()
                .text("Теннисные клубы СПб").build();
        KeyboardButton myProfile = KeyboardButton.builder()
                .text("Мой профиль").build();

        KeyboardRow row1 = new KeyboardRow(List.of(oncomingTournaments, pastTournaments));
        KeyboardRow row2 = new KeyboardRow(List.of(tennisClubs, myProfile));

        ReplyKeyboardMarkup mainMenu = ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .keyboardRow(row1)
                .keyboardRow(row2).build();
        SendMessage sm = SendMessage.builder().chatId(Long.toString(chatId))
                .parseMode("HTML").text("Добро пожаловать в главное меню! \n\nВыберите нужный пункт.")
                .replyMarkup(mainMenu).build();
        return sm;
    }

    public List<InlineKeyboardButton> inlineButtonRow() {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        List<InlineKeyboardButton> row = new ArrayList<>();
        btn.setText("Главное меню");
        btn.setCallbackData("displayMainMenu");
        row.add(btn);
        return row;
    }
}

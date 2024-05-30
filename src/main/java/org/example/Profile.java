package org.example;

import org.example.enums.Emojis;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    private int ranking;
    private String fullName;
    private int totalPoints;
    private int gold;
    private int silver;
    private int bronze;

    private ArrayList<FutureTournament> myFutureTournaments;

    public int getRanking() {
        return ranking;
    }
    public Profile setRanking(int ranking) {
        this.ranking = ranking;
        return this;
    }
    public String getFullName() {
        return fullName;
    }

    public Profile setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public Profile setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
        return this;
    }

    public int getGold() {
        return gold;
    }

    public Profile setGold(int gold) {
        this.gold = gold;
        return this;
    }

    public int getSilver() {
        return silver;
    }

    public Profile setSilver(int silver) {
        this.silver = silver;
        return this;
    }

    public int getBronze() {
        return bronze;
    }

    public Profile setBronze(int bronze) {
        this.bronze = bronze;
        return this;
    }

    public String toString(){
        String tournamentsText = "";
        String rankingText = "";
        if(myFutureTournaments != null && !myFutureTournaments.isEmpty()){
            int num = myFutureTournaments.size();
            tournamentsText = "\n\nВы зарегистрированы на " + num;
            if(num % 10 > 0 && num % 10 < 5 && ((num % 100) / 10 != 1)){
                tournamentsText += (num % 10 == 1) ? " турнир:\n" : " турнирa:\n";
            } else tournamentsText += " турниров:\n";

            for(FutureTournament ft: myFutureTournaments){
                tournamentsText += "\n" + ft;
            }
        }
        if (totalPoints == 0){
            rankingText += "-";
        } else rankingText += ranking;
        return Emojis.USER.getUnicode() + " " + fullName + "\n\n"
                + "<b>Очки:</b> " + totalPoints + "\n"
                + "<b>Общий рейтинг:</b> " + rankingText + "\n"
                + "<b>Медали:</b> " + allMedals()
                + tournamentsText;
    }

    public SendMessage toSendMessage(long id){
        SendMessage message = new SendMessage();
        message.setChatId(Long.toString(id));
        message.setParseMode("HTML");
        message.setText(this.toString());
        message.setReplyMarkup(inlineKeyboardMarkup());
        return message;
    }

    private String allMedals(){
        String allMedals = "";
        if(gold+silver+bronze == 0){
            allMedals = "Все еще впереди!";
        } else{
            for(int i = 0; i < gold; i++){
                allMedals+=Emojis.GOLD_MEDAL.getUnicode();
            }
            for(int i = 0; i < silver; i++){
                allMedals+=Emojis.SILVER_MEDAL.getUnicode();
            }
            for(int i = 0; i < bronze; i++){
                allMedals+=Emojis.BRONZE_MEDAL.getUnicode();
            }
        }
        return allMedals;
    }

    public Profile setMyFutureTournaments(ArrayList<FutureTournament> myFutureTournaments) {
        this.myFutureTournaments = myFutureTournaments;
        return this;
    }

    public InlineKeyboardMarkup inlineKeyboardMarkup(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText(Emojis.CUP.getUnicode() +" Перейти к предстоящим турнирам");
        btn1.setCallbackData("displayAllFutureTournaments");
        row1.add(btn1);
        List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
        rowList.add(row1);
        rowList.add(new MainMenu().inlineButtonRow());
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}

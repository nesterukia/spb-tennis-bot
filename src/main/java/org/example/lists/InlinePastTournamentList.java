package org.example.lists;

import org.example.enums.Emojis;
import org.example.PastTournament;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlinePastTournamentList extends InlineList {
    public InlinePastTournamentList(ArrayList<PastTournament> pastTournaments){
        super();
        aboutBtns = "Для подробной информации о турнире, нажмите на кнопку с его названием.";
        maxPage = (pastTournaments.size()-1)/5;
        String msgText="";
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for(int i = 0; i < pastTournaments.size(); i++){
            if((i > 0) && (i % 5==0)){
                if(maxPage > 0){
                    rowList.add(nav(pages.size()));
                }
                InlineListPage page = new InlineListPage(pages.size(),msgText,rowList);
                pages.add(page);
                msgText = "";
                rowList = new ArrayList<>();
            }
            PastTournament pastTournament = pastTournaments.get(i);
            msgText+=pastTournament +"\n";
            InlineKeyboardButton btn = new InlineKeyboardButton();
            List<InlineKeyboardButton> row = new ArrayList<>();
            btn.setText(Emojis.CUP.getUnicode() + " " + pastTournament.getName()+ " " +pastTournament.getDatetime());
            btn.setCallbackData("displayPastTournamentInfo/" + pastTournament.getId());
            row.add(btn);
            rowList.add(row);
            if((i == pastTournaments.size()-1)){
                if(maxPage > 0){
                    rowList.add(nav(pages.size()));
                }
                InlineListPage page = new InlineListPage(pages.size(),msgText,rowList);
                pages.add(page);
            }
        }
    }
}

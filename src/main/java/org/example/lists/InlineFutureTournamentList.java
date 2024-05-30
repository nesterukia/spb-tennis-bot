package org.example.lists;

import org.example.enums.Emojis;
import org.example.FutureTournament;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineFutureTournamentList extends InlineList {
    public InlineFutureTournamentList(ArrayList<FutureTournament> futureTournaments){
        super();
        aboutBtns = "Для подробной информации о турнире, нажмите на кнопку с его названием.";
        maxPage = (futureTournaments.size()-1)/5;
        String msgText="";
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for(int i = 0; i < futureTournaments.size(); i++){
            if((i > 0) && (i % 5==0)){
                if(maxPage > 0){
                    rowList.add(nav(pages.size()));
                }
                InlineListPage page = new InlineListPage(pages.size(),msgText,rowList);
                pages.add(page);
                msgText = "";
                rowList = new ArrayList<>();
            }
            FutureTournament futureTournament = futureTournaments.get(i);
            msgText+=futureTournament +"\n";
            InlineKeyboardButton btn = new InlineKeyboardButton();
            List<InlineKeyboardButton> row = new ArrayList<>();
            btn.setText(Emojis.CUP.getUnicode() + " " + futureTournament.getName()+ " " +futureTournament.getDatetime());
            btn.setCallbackData("displayFutureTournamentInfo/" + futureTournament.getId());
            row.add(btn);
            rowList.add(row);
            if((i == futureTournaments.size()-1)){
                if(maxPage > 0){
                    rowList.add(nav(pages.size()));
                }
                InlineListPage page = new InlineListPage(pages.size(),msgText,rowList);
                pages.add(page);
            }
        }
    }
}

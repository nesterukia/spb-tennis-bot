package org.example.lists;

import org.example.Club;
import org.example.enums.Emojis;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineClubList extends InlineList {

    public InlineClubList(ArrayList<Club> clubs) {
        super();
        aboutBtns = "Для подробной информации о клубе, нажмите на кнопку с его названием.";
        maxPage = (clubs.size() - 1) / 5;
        String msgText = "";
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (int i = 0; i < clubs.size(); i++) {
            if ((i > 0) && (i % 5 == 0)) {
                if (maxPage > 0) {
                    rowList.add(nav(pages.size()));
                }
                InlineListPage page = new InlineListPage(pages.size(), msgText, rowList);
                pages.add(page);
                msgText = "";
                rowList = new ArrayList<>();
            }
            Club club = clubs.get(i);
            msgText += Emojis.TENNIS.getUnicode() + " " + club.getName() + "\n\n";
            InlineKeyboardButton btn = new InlineKeyboardButton();
            List<InlineKeyboardButton> row = new ArrayList<>();
            btn.setText(Emojis.TENNIS.getUnicode() + " " + club.getName());
            btn.setCallbackData("displayClubInfo/" + club.getId());
            row.add(btn);
            rowList.add(row);
            if ((i == clubs.size() - 1)) {
                if (maxPage > 0) {
                    rowList.add(nav(pages.size()));
                }
                InlineListPage page = new InlineListPage(pages.size(), msgText, rowList);
                pages.add(page);
            }
        }
    }
}

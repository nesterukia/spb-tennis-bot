package org.example.lists;

import org.example.enums.Emojis;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public abstract class InlineList{

    protected long listId;
    protected ArrayList<InlineListPage> pages = new ArrayList<>();
    protected int curPage;
    protected int maxPage;

    protected String aboutBtns;

    public InlineList(){
        curPage = 0;
        listId = System.currentTimeMillis() / 1000L;
    }

    public void setAboutBtns(String aboutBtns) {
        this.aboutBtns = aboutBtns;
    }
    public SendMessage sendList(long id){
        InlineListPage page = pages.get(curPage);
        List<List<InlineKeyboardButton>> rowList = page.getRowList();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        String msgText = "Страница " +(curPage+1) + "/" +(maxPage+1)
                +". "+ aboutBtns +"\n\n"+ page.getMsgText();
        SendMessage message = new SendMessage();
        message.setChatId(Long.toString(id));
        message.setParseMode("HTML");
        message.setText(msgText);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }

    protected List<InlineKeyboardButton> nav(int num){
        InlineKeyboardButton btnPrev = new InlineKeyboardButton();
        btnPrev.setText(Emojis.LEFT_ARROW.getUnicode());
        btnPrev.setCallbackData("inlineListPrev/" + listId);

        InlineKeyboardButton btnNext = new InlineKeyboardButton();
        btnNext.setText(Emojis.RIGHT_ARROW.getUnicode());
        btnNext.setCallbackData("inlineListNext/" + listId);
        List<InlineKeyboardButton> rowBoth = new ArrayList<>();
        rowBoth.add(btnPrev);
        rowBoth.add(btnNext);
        List<InlineKeyboardButton> rowPrev = new ArrayList<>();
        rowPrev.add(btnPrev);
        List<InlineKeyboardButton> rowNext = new ArrayList<>();
        rowNext.add(btnNext);
        if(num == 0){
            return rowNext;
        } else if (num == maxPage){
            return rowPrev;
        } else return rowBoth;
    }

    public EditMessageText nextPage(long chatId, int msgId){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        curPage++;
        InlineListPage page = pages.get(curPage);
        EditMessageText message = new EditMessageText();
        String msgText = "Страница " +(curPage+1) + "/" +(maxPage+1)
                +". " + aboutBtns + "\n\n"+ page.getMsgText();
        message.setText(msgText);
        message.setChatId(Long.toString(chatId));
        message.setMessageId(msgId);
        message.setParseMode("HTML");
        inlineKeyboardMarkup.setKeyboard(page.getRowList());
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
    public EditMessageText prevPage(long chatId, int msgId){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        curPage--;
        InlineListPage page = pages.get(curPage);
        EditMessageText message = new EditMessageText();
        String msgText = "Страница " +(curPage+1) + "/" +(maxPage+1)
                +". " + aboutBtns + "\n\n" + page.getMsgText();
        message.setText(msgText);
        message.setChatId(Long.toString(chatId));
        message.setMessageId(msgId);
        message.setParseMode("HTML");
        inlineKeyboardMarkup.setKeyboard(page.getRowList());
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }
    public long getListId() {
        return listId;
    }

    protected void setListId(int list_id) {
        this.listId = list_id;
    }
}

package org.example;

import org.apache.commons.lang3.StringUtils;
import org.example.enums.Emojis;
import org.example.enums.Gender;
import org.example.enums.InlineCommands;
import org.example.enums.ReplyCommands;
import org.example.lists.InlineClubList;
import org.example.lists.InlineFutureTournamentList;
import org.example.lists.InlineList;
import org.example.lists.InlinePastTournamentList;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class Bot extends TelegramLongPollingBot {

    private Database db = new Database();
    private MainMenu mainMenu = new MainMenu();
    private ConcurrentHashMap<Long, User> newUsers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, ReplyCommands> replyCommands = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, InlineCommands> inlineCommands = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, InlineList> userLists = new ConcurrentHashMap<>();
    private static final int MAX_LISTS = 5;
    @Override
    public String getBotUsername() {
        return "united_tennis_tour_bot";
    }

    @Override
    public String getBotToken() {
        return "6576980389:AAGIB2C8XSdzvnj0dPcVUtwE26wxaoRK8mE";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            long chatId;
            if (update.hasMessage() && update.getMessage().hasText()) {
                System.out.println(update.getMessage().getChatId() + " "
                        + LocalTime.now() + ": "
                        + update.getMessage().getText());
                Message msg = update.getMessage();
                String text = msg.getText();
                chatId = update.getMessage().getChatId();

                for (ReplyCommands comm : ReplyCommands.values()) {
                    if (text.equals(comm.getText())) {
                        replyCommands.put(chatId, comm);
                        break;
                    }
                }

                switch (replyCommands.get(chatId)) {
                    case START -> {
                        if (db.isRegisteredPlayer(chatId)) {
                            send(mainMenu.display(chatId));
                            replyCommands.remove(chatId);
                        } else {
                            sendText(chatId, "Физкульт-привет! Перед началом, давай познакомимся!");
                            sendText(chatId, "Введите имя:");
                            replyCommands.put(chatId, ReplyCommands.GET_FIRST_NAME);
                        }
                    }
                    case GET_FIRST_NAME -> {
                        String firstName = getName(text);
                        if(firstName == null){
                            sendText(chatId, "Ошибка. Введите имя еще раз:");
                        } else{
                            User user = new User();
                            user.setFirstName(firstName);
                            newUsers.put(chatId,user);
                            sendText(chatId, "Введите фамилию:");
                            replyCommands.put(chatId, ReplyCommands.GET_LAST_NAME);
                        }

                    }
                    case GET_LAST_NAME -> {
                        String lastName = getName(text);
                        if(lastName == null){
                            sendText(chatId, "Ошибка. Введите фамилию еще раз:");
                        } else{
                            newUsers.get(chatId).setLastName(lastName);
                            KeyboardButton male = KeyboardButton.builder()
                                    .text("М").build();
                            KeyboardButton female = KeyboardButton.builder()
                                    .text("Ж").build();
                            ReplyKeyboardMarkup genderMenu;
                            KeyboardRow row = new KeyboardRow(List.of(male, female));
                            genderMenu = ReplyKeyboardMarkup.builder()
                                    .oneTimeKeyboard(true)
                                    .resizeKeyboard(true)
                                    .keyboardRow(row).build();
                            SendMessage sm = new SendMessage();
                            sm.setText("Выберите ваш пол:");
                            sm.setChatId(Long.toString(chatId));
                            sm.setReplyMarkup(genderMenu);
                            send(sm);
                            replyCommands.put(chatId, ReplyCommands.GET_GENDER);
                        }
                    }
                    case GET_GENDER ->{
                        if(Objects.equals(text, "М") || Objects.equals(text, "Ж")){
                            char gender = text.charAt(0);
                            newUsers.get(chatId).setGender(gender);
                            String fName = newUsers.get(chatId).getFirstName();
                            String lName = newUsers.get(chatId).getLastName();
                            char userGender = newUsers.get(chatId).getGender();
                            db.addNewPlayer(chatId, fName, lName, userGender);
                            newUsers.remove(chatId);
                            sendText(chatId, fName + " " + lName + ", теперь вы - часть United Tennis SPb!");
                            replyCommands.remove(chatId);
                            send(mainMenu.display(chatId));
                        } else{
                            KeyboardButton male = KeyboardButton.builder()
                                    .text("М").build();
                            KeyboardButton female = KeyboardButton.builder()
                                    .text("Ж").build();
                            ReplyKeyboardMarkup genderMenu;
                            KeyboardRow row = new KeyboardRow(List.of(male, female));
                            genderMenu = ReplyKeyboardMarkup.builder()
                                    .oneTimeKeyboard(true)
                                    .resizeKeyboard(true)
                                    .keyboardRow(row).build();
                            SendMessage sm = new SendMessage();
                            sm.setText("Ошибка. Выберите пол еще раз:");
                            sm.setChatId(Long.toString(chatId));
                            sm.setReplyMarkup(genderMenu);
                            send(sm);
                        }
                    }
                    case DISPLAY_CLUBS -> {
                        ArrayList<Club> clubs = db.getAllClubs();
                        if (db.getAllClubs().isEmpty()) {
                            String newText = "Нет доступных клубов.";
                            sendText(chatId, newText);
                            send(mainMenu.display(chatId));
                        } else {
                            sendText(chatId, "Найдено клубов: " + clubs.size());
                            InlineClubList clubList = new InlineClubList(clubs);

                            userLists.put(chatId, clubList);
                            send(clubList.sendList(chatId));
                        }
                        replyCommands.remove(chatId);
                    }
                    case DISPLAY_ALL_FUTURE_TOURNAMENTS -> {
                        String newText = "";
                        ArrayList<FutureTournament> futureTournaments = db.getFutureTournaments();
                        if (futureTournaments.isEmpty()) {
                            newText = "Нет доступных турниров.";
                            sendText(chatId, newText);
                            send(mainMenu.display(chatId));
                        } else {
                            sendText(chatId, "Найдено турниров: " + futureTournaments.size());
                            InlineFutureTournamentList futureTournamentList = new InlineFutureTournamentList(futureTournaments);
                            userLists.put(chatId, futureTournamentList);
                            send(futureTournamentList.sendList(chatId));
                        }
                        replyCommands.remove(chatId);
                    }
                    case DISPLAY_ALL_PAST_TOURNAMENTS -> {
                        ArrayList<PastTournament> pastTournaments = db.getPastTournaments();
                        if (pastTournaments.isEmpty()) {
                            String newText = "Нет доступных турниров.";
                            sendText(chatId, newText);
                            send(mainMenu.display(chatId));
                        } else {
                            sendText(chatId, "Найдено турниров: " + pastTournaments.size());
                            InlinePastTournamentList pastTournamentList = new InlinePastTournamentList(pastTournaments);
                            userLists.put(chatId, pastTournamentList);
                            send(pastTournamentList.sendList(chatId));
                        }
                        replyCommands.remove(chatId);
                    }
                    case DISPLAY_PROFILE -> {
                        Profile profile = db.getProfileById(chatId);
                        send(profile.toSendMessage(chatId));
                        replyCommands.remove(chatId);
                    }
                    case null -> {
                        send(mainMenu.display(chatId));
                    }
                    default -> {
                        sendText(chatId, "Error.");
                    }

                }
            } else if (update.hasCallbackQuery()) {
                    System.out.println(update.getCallbackQuery().getMessage().getChatId() +" "
                            + LocalTime.now() + ": "
                            + update.getCallbackQuery().getData());

                    CallbackQuery callbackQuery = update.getCallbackQuery();
                    String data = callbackQuery.getData();
                    Message msg = callbackQuery.getMessage();
                    chatId = msg.getChat().getId();
                    for (InlineCommands comm : InlineCommands.values()) {
                        if (data.contains(comm.text())) {
                            inlineCommands.put(chatId, comm);
                            break;
                        }
                    }
                    switch (inlineCommands.get(chatId)) {
                        case DISPLAY_CLUB_INFO -> {
                            long clubId = Long.valueOf(data.split("/")[1]);
                            Club club = db.getClubById(clubId);
                            if (club != null) {
                                send(club.toEditMessageText(chatId, msg.getMessageId()));
                            } else System.out.println("Error, club not found.");
                        }
                        case DISPLAY_FUTURE_TOURNAMENT_INFO -> {
                            long tournamentId = Long.valueOf(data.split("/")[1]);
                            ArrayList<FutureTournament> allFutureTournaments = db.getFutureTournaments();
                            for (FutureTournament ft : allFutureTournaments) {
                                if (ft.getId() == tournamentId) {
                                    send(ft.toEditMessageText(chatId, msg.getMessageId()));
                                    break;
                                }
                            }
                        }
                        case DISPLAY_PAST_TOURNAMENT_INFO -> {
                            long tournamentId = Long.valueOf(data.split("/")[1]);
                            Tournament tournament = db.getTournament(tournamentId);
                            if (tournament != null) {
                                Formatter formatter = new Formatter();
                                String text = Emojis.CUP.getUnicode() + " <b>" + tournament.getName() + " " + tournament.getDatetime() + "</b>\n"
                                        + Emojis.TENNIS.getUnicode() + "Категория: " + tournament.getCategory() + " " + tournament.getLevel() + "\n"
                                        + Emojis.CASTLE.getUnicode() + tournament.getClubName()
                                        + "\n\n<pre>Результаты турнира:\n";
                                ArrayList<Ranking> rankings = db.getTournamentRankings(tournamentId);
                                String fillLine = "+-----+--------------------+-----+";
                                text += fillLine + "\n"
                                        + formatter.format("|%5s|%20s|%5s|", "Место", "Игрок", "Очки") + "\n";
                                for (Ranking r : rankings) {
                                    text += fillLine + "\n" + r + "\n";
                                }
                                text += fillLine + "</pre>";

                                EditMessageText editMessageText = new EditMessageText();
                                editMessageText.setText(text);
                                editMessageText.setParseMode("HTML");
                                editMessageText.setChatId(Long.toString(chatId));
                                editMessageText.setMessageId(msg.getMessageId());
                                send(editMessageText);
                                send(mainMenu.display(chatId));
                            }
                        }
                        case DISPLAY_ALL_FUTURE_TOURNAMENTS -> {
                            String newText = "";
                            ArrayList<FutureTournament> futureTournaments = db.getFutureTournaments();
                            if (futureTournaments.isEmpty()) {
                                newText = "Нет доступных турниров.";
                                EditMessageText editMsg = new EditMessageText();
                                editMsg.setMessageId(msg.getMessageId());
                                editMsg.setChatId(Long.toString(chatId));
                                editMsg.setText(newText);
                                send(editMsg);
                                send(mainMenu.display(chatId));
                            } else {
                                newText = "Найдено турниров: " + futureTournaments.size();
                                EditMessageText editMsg = new EditMessageText();
                                editMsg.setMessageId(msg.getMessageId());
                                editMsg.setChatId(Long.toString(chatId));
                                editMsg.setText(newText);
                                send(editMsg);
                                InlineFutureTournamentList futureTournamentList =
                                        new InlineFutureTournamentList(futureTournaments);

                                if (!userLists.containsKey(chatId)) {
                                    userLists.put(chatId, futureTournamentList);
                                }
                                send(futureTournamentList.sendList(chatId));
                            }
                            inlineCommands.remove(chatId);
                        }
                        case DISPLAY_FUTURE_TOURNAMENTS_IN_CLUB -> {
                            String newText = "";
                            long clubId = Long.valueOf(data.split("/")[1]);
                            Club club = db.getClubById(clubId);
                            ArrayList<FutureTournament> futureTournaments = db.getFutureTournamentsInClub(clubId);
                            if (futureTournaments.isEmpty()) {
                                newText = "Нет доступных турниров в теннисном клубе " + club.getName() + ".";
                                EditMessageText editMsg = new EditMessageText();
                                editMsg.setMessageId(msg.getMessageId());
                                editMsg.setChatId(Long.toString(chatId));
                                editMsg.setText(newText);
                                send(editMsg);
                                send(mainMenu.display(chatId));
                            } else {
                                newText = "Найдено турниров в теннисном клубе "
                                        + club.getName() + ": " + futureTournaments.size();
                                EditMessageText editMsg = new EditMessageText();
                                editMsg.setMessageId(msg.getMessageId());
                                editMsg.setChatId(Long.toString(chatId));
                                editMsg.setText(newText);
                                send(editMsg);
                                InlineFutureTournamentList futureTournamentsInClubList =
                                        new InlineFutureTournamentList(futureTournaments);
                                if (!userLists.containsKey(chatId)) {
                                    userLists.put(chatId, futureTournamentsInClubList);
                                }
                                send(futureTournamentsInClubList.sendList(chatId));
                            }
                            inlineCommands.remove(chatId);
                        }
                        case NEXT_PAGE -> {
                            long listId = Integer.valueOf(data.split("/")[1]);
                            InlineList list = userLists.get(chatId);
                            System.out.println("list == null ? " + (list == null));
                            System.out.println("listId != list.getListId() ? " + (listId != list.getListId()));

                            if (list == null || listId != list.getListId()) {
                                EditMessageText editMsg = new EditMessageText();
                                editMsg.setText("Время ожидания истекло. Попробуйте еще раз.");
                                editMsg.setChatId(Long.toString(chatId));
                                editMsg.setMessageId(msg.getMessageId());
                                send(editMsg);
                            } else {
                                send(list.nextPage(chatId, msg.getMessageId()));
                            }
                        }
                        case PREV_PAGE -> {
                            long listId = Integer.valueOf(data.split("/")[1]);
                            InlineList list = userLists.get(chatId);
                            if ((list == null) || (listId != list.getListId())) {
                                EditMessageText editMsg = new EditMessageText();
                                editMsg.setText("Время ожидания истекло. Попробуйте еще раз.");
                                editMsg.setChatId(Long.toString(chatId));
                                editMsg.setMessageId(msg.getMessageId());
                                send(editMsg);
                            } else {
                                send(list.prevPage(chatId, msg.getMessageId()));
                            }
                        }
                        case REGISTER_FOR_TOURNAMENT -> {
                            int tournamentId = Integer.valueOf(data.split("/")[1]);
                            db.registerForTournament(tournamentId, chatId);
                            EditMessageText editMessage = new EditMessageText();
                            editMessage.setChatId(Long.toString(chatId));
                            editMessage.setMessageId(msg.getMessageId());
                            editMessage.setText(msg.getText() + "\n\nВы успешно зарегистрированы на турнир");
                            send(editMessage);
                            send(mainMenu.display(chatId));
                        }
                        case CANCEL_REGISTRATION -> {
                            int tournamentId = Integer.valueOf(data.split("/")[1]);
                            db.cancelRegistrationForTournament(tournamentId, chatId);
                            ArrayList<FutureTournament> allFutureTournaments = db.getFutureTournaments();
                            for (FutureTournament ft : allFutureTournaments) {
                                if (ft.getId() == tournamentId) {
                                    EditMessageText editMsg = ft.toEditMessageText(chatId, msg.getMessageId());
                                    editMsg.setReplyMarkup(null);
                                    editMsg.setText(editMsg.getText() + "\n"+ "Регистрация отменена");
                                    send(editMsg);
                                    break;
                                }
                            }
                            send(mainMenu.display(chatId));

                        }
                        case DISPLAY_MAIN_MENU -> {
                            DeleteMessage dm = new DeleteMessage(Long.toString(chatId), msg.getMessageId());
                            send(dm);
                            send(mainMenu.display(chatId));
                        }
                        default -> {
                            sendText(chatId, "Error.");
                        }
                    }
            } else {
                    chatId = update.getMessage().getChatId();
                    sendText(chatId, "Все доступные функции находятся в главном меню!");
                    send(mainMenu.display(chatId));
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            sleep(350);
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        } catch (Exception e){
        System.out.println(e);
    }
    }

    public void send(BotApiMethod sm){//Message content
        try {
            sleep(350);
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        } catch (Exception e){
            System.out.println(e);
        }

    }

    public String getName(String firstName){
//        TODO: checkName
        String regex = "^[а-яА-Я]{2,50}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(firstName);
        if(m.matches()){
            String lowerCase = firstName.toLowerCase(new Locale("ru"));
            String finalCapitalize = StringUtils.capitalize(lowerCase);
            return finalCapitalize;
        } else return null;
    }
}

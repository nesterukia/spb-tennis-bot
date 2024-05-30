package org.example.enums;

public enum InlineCommands{
    DISPLAY_CLUB_INFO("displayClubInfo/"),
    DISPLAY_FUTURE_TOURNAMENTS_IN_CLUB("displayClubTournaments/"),
    DISPLAY_ALL_FUTURE_TOURNAMENTS("displayAllFutureTournaments"),
    REGISTER_FOR_TOURNAMENT("regForTournament/"),
    DISPLAY_PAST_TOURNAMENT_INFO("displayPastTournamentInfo/"),
    DISPLAY_FUTURE_TOURNAMENT_INFO("displayFutureTournamentInfo/"),
    CANCEL_REGISTRATION("cancelRegistration/"),
    NEXT_PAGE("inlineListNext/"),
    DISPLAY_MAIN_MENU("displayMainMenu"),
    PREV_PAGE("inlineListPrev/");
    String data;
    public String text(){
        return data;
    }
    InlineCommands(){}
    InlineCommands(String text){
        this.data = text;
    }
}

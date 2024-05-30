package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {
    private String oracleDriver = "oracle.jdbc.driver.OracleDriver";
    private String url ="jdbc:oracle:thin:@localhost:1521:xe";
    private String dbUser = "C##NESTERUKIA";
    private String dbPassword = "111111";

    public void cancelRegistrationForTournament(long tournamentId, long playerId){
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "DELETE FROM \"C##NESTERUKIA\".\"REGISTRATION\" \n" +
                    "WHERE TOURNAMENT_ID = "+ tournamentId +" AND PLAYER_ID = " + playerId +
                    "\nand ( \"TOURNAMENT_ID\" is null or \"TOURNAMENT_ID\" is not null )";
            stmt.executeQuery(sql);
            con.close();

        }catch(Exception e){ System.out.println(e);}
    }

    public String getFullNameById(long playerId){
        String fullName = "";

        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT FIRST_NAME || ' ' || LAST_NAME FULL_NAME \n" +
                    "FROM \"C##NESTERUKIA\".\"PLAYER\" WHERE ID = " + playerId;
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                fullName = rs.getString("FULL_NAME");
            }
        } catch(Exception e){ System.out.println(e);}
        return fullName;
    }
    public Profile getProfileById(long playerId){
        Profile profile = new Profile();
        ArrayList<FutureTournament> profileFutureTournaments = new ArrayList<>();
        ArrayList<FutureTournament> allFutureTournaments = getFutureTournaments();
        for(FutureTournament ft: allFutureTournaments){
            System.out.println("All: " + ft.getId() + " " + isRegisteredForTournament(ft.getId(), playerId));
            if(isRegisteredForTournament(ft.getId(), playerId)){
                profileFutureTournaments.add(ft);
            }
        }
        for(FutureTournament ft: profileFutureTournaments){
            System.out.println("profile: " + ft.getId() + " " + isRegisteredForTournament(ft.getId(), playerId));
        }
        profile.setMyFutureTournaments(profileFutureTournaments);
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT PROFILE.RANKING, PL.FIRST_NAME ||' '|| PL.LAST_NAME FULL_NAME, PROFILE.POINTS, PROFILE.GOLD, PROFILE.SILVER, PROFILE.BRONZE FROM\n" +
                    "(SELECT \n" +
                    "ROW_NUMBER()OVER (ORDER BY POINTS DESC) RANKING,\n" +
                    "TEMP.ID, TEMP.POINTS, TEMP.GOLD, TEMP.SILVER, TEMP.BRONZE\n" +
                    "FROM(\n" +
                    "SELECT PL.ID ID, SUM(NVL(RS.POINTS,0)) POINTS, \n" +
                    "COUNT(CASE RS.PLACE WHEN 1 THEN 1 ELSE NULL END) GOLD,\n" +
                    "COUNT(CASE RS.PLACE WHEN 2 THEN 1 ELSE NULL END) SILVER,\n" +
                    "COUNT(CASE RS.PLACE WHEN 3 THEN 1 ELSE NULL END) BRONZE\n" +
                    "FROM \n" +
                    "(\"C##NESTERUKIA\".\"RESULTS\" RS FULL JOIN \"C##NESTERUKIA\".\"PLAYER\" PL ON (RS.PLAYER_ID = PL.ID))\n" +
                    "GROUP BY PL.ID ) TEMP) PROFILE JOIN \"C##NESTERUKIA\".\"PLAYER\" PL ON(PROFILE.ID = PL.ID) " +
                    "WHERE PL.ID = " + playerId +
                    " ORDER BY RANKING";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                profile
                        .setFullName(rs.getString("FULL_NAME"))
                        .setRanking(rs.getInt("RANKING"))
                        .setTotalPoints(rs.getInt("POINTS"))
                        .setGold(rs.getInt("GOLD"))
                        .setSilver(rs.getInt("SILVER"))
                        .setBronze(rs.getInt("BRONZE"));
            } else profile = null;

            con.close();
        }catch(Exception e){ System.out.println(e);}
        return profile;
    }
    public ArrayList<Ranking> getTournamentRankings(long tournamentId){
        ArrayList<Ranking> rankings = new ArrayList<>();
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT RS.PLACE, PL.FIRST_NAME ||' '||PL.LAST_NAME,RS.POINTS FROM \n" +
                    "(\"C##NESTERUKIA\".\"RESULTS\" RS JOIN \"C##NESTERUKIA\".\"PLAYER\" PL ON (RS.PLAYER_ID = PL.ID)) \n" +
                    "WHERE RS.TOURNAMENT_ID = " + tournamentId + " ORDER BY RS.PLACE";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                Ranking ranking = new Ranking();
                ranking
                        .setPlace(rs.getInt(1))
                        .setFullName(rs.getString(2))
                        .setPoints(rs.getInt(3));
                rankings.add(ranking.getPlace()-1, ranking);
            }
            con.close();

        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println(e.getLocalizedMessage());
        } catch(Exception e){
            System.out.println(e);
        }
        return rankings;
    }
    public Tournament getTournament(long tournamentId){
        Tournament tournament = new Tournament();
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT T.ID ID, T.NAME NAME,T.DATETIME DATETIME, T.PRICE PRICE, CL.NAME CLUB_NAME, CT.NAME CATEGORY, LV.NAME \"LEVEL\" \n" +
                    "FROM (((TOURNAMENT T JOIN \"C##NESTERUKIA\".\"CLUB\" CL ON (T.CLUB_ID = CL.ID)) \n" +
                    "JOIN \"C##NESTERUKIA\".\"CATEGORY\" CT ON (T.CATEGORY_ID = CT.ID)) \n" +
                    "JOIN \"C##NESTERUKIA\".\"LEVEL\" LV ON (T.LEVEL_ID = LV.ID)) \n" +
                    "WHERE T.ID = " + tournamentId;
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                tournament
                        .setId(rs.getLong("ID"))
                        .setName(rs.getString("NAME"))
                        .setDatetime(rs.getDate("DATETIME"))
                        .setClubName(rs.getString("CLUB_NAME"))
                        .setCategory(rs.getString("CATEGORY"))
                        .setLevel(rs.getString("LEVEL"))
                        .setPrice(rs.getInt("PRICE"));
            } else tournament = null;
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return tournament;
    }
    public Club getClubById(long id){
        Club club = new Club();
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT * FROM \"C##NESTERUKIA\".\"CLUB\" WHERE ID = " + id;
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                club.setId(rs.getLong(1));
                club.setName(rs.getString(2));
                club.setAddress(rs.getString(3));
                club.setPhoneNumber(rs.getString(4));
            } else club = null;
            con.close();

        }catch(Exception e){ System.out.println(e);}
        return club;
    }

    public ArrayList<Club> getAllClubs(){
        ArrayList<Club> clubs = new ArrayList<>();
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT * FROM \"C##NESTERUKIA\".\"CLUB\"";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                Club club = new Club();
                club.setId(rs.getLong(1));
                club.setName(rs.getString(2));
                club.setAddress(rs.getString(3));
                club.setPhoneNumber(rs.getString(4));
                clubs.add(club);
            }
            con.close();

        }catch(Exception e){ System.out.println(e);}
        return clubs;
    }
    public ArrayList<PastTournament> getPastTournaments(){
        ArrayList<PastTournament> tournaments = new ArrayList<>();
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT T.ID, T.NAME,T.DATETIME, CL.NAME, CT.NAME, LV.NAME, PL.FIRST_NAME ||' '||PL.LAST_NAME FROM \n" +
                    "(((((TOURNAMENT T JOIN \"C##NESTERUKIA\".\"CLUB\" CL ON (T.CLUB_ID = CL.ID)) \n" +
                    "JOIN \"C##NESTERUKIA\".\"CATEGORY\" CT ON (T.CATEGORY_ID = CT.ID)) \n" +
                    "JOIN \"C##NESTERUKIA\".\"LEVEL\" LV ON (T.LEVEL_ID = LV.ID)) \n" +
                    "JOIN \"C##NESTERUKIA\".\"RESULTS\" RS ON (T.ID = RS.TOURNAMENT_ID))\n" +
                    "JOIN \"C##NESTERUKIA\".\"PLAYER\" PL ON (RS.PLAYER_ID = PL.ID))\n" +
                    "WHERE T.DATETIME < CURRENT_DATE AND RS.PLACE = 1 ORDER BY T.DATETIME DESC";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                PastTournament tournament = new PastTournament();
                tournament
                        .setWinner(rs.getString(7))
                        .setId(rs.getLong(1))
                        .setName(rs.getString(2))
                        .setDatetime(rs.getDate(3))
                        .setClubName(rs.getString(4))
                        .setCategory(rs.getString(5))
                        .setLevel(rs.getString(6));
                tournaments.add(tournament);
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}

        return tournaments;
    }
    public ArrayList<FutureTournament> getFutureTournaments(){
        ArrayList<FutureTournament> tournaments = new ArrayList<>();
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT T.ID, T.NAME,T.DATETIME, T.PRICE, CL.NAME, CT.NAME, LV.NAME FROM " +
                    "(((TOURNAMENT T JOIN \"C##NESTERUKIA\".\"CLUB\" CL ON (T.CLUB_ID = CL.ID)) " +
                    "JOIN \"C##NESTERUKIA\".\"CATEGORY\" CT ON (T.CATEGORY_ID = CT.ID)) " +
                    "JOIN \"C##NESTERUKIA\".\"LEVEL\" LV ON (T.LEVEL_ID = LV.ID)) WHERE T.DATETIME >= CURRENT_DATE";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                FutureTournament tournament = new FutureTournament();
                tournament.setPrice(rs.getInt(4))
                        .setId(rs.getLong(1))
                        .setName(rs.getString(2))
                        .setDatetime(rs.getDate(3))
                        .setClubName(rs.getString(5))
                        .setCategory(rs.getString(6))
                        .setLevel(rs.getString(7));
                tournaments.add(tournament);
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return tournaments;
    }

    public ArrayList<FutureTournament> getFutureTournamentsInClub(long clubId){
        ArrayList<FutureTournament> tournaments = new ArrayList<>();
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT T.ID, T.NAME,T.DATETIME, T.PRICE, CL.NAME, CT.NAME, LV.NAME FROM (((TOURNAMENT T JOIN \"C##NESTERUKIA\".\"CLUB\" CL ON (T.CLUB_ID = CL.ID)) JOIN \"C##NESTERUKIA\".\"CATEGORY\" CT ON (T.CATEGORY_ID = CT.ID)) JOIN \"C##NESTERUKIA\".\"LEVEL\" LV ON (T.LEVEL_ID = LV.ID)) WHERE T.DATETIME >= CURRENT_DATE AND CL.ID = " + clubId;
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                FutureTournament tournament = new FutureTournament();
                tournament.setId(rs.getLong(1));
                tournament.setName(rs.getString(2));
                tournament.setDatetime(rs.getDate(3));
                tournament.setPrice(rs.getInt(4));
                tournament.setClubName(rs.getString(5));
                tournament.setCategory(rs.getString(6));
                tournament.setLevel(rs.getString(7));
                tournaments.add(tournament);
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}

        return tournaments;
    }
    public void addNewPlayer(long id, String firstName, String lastName, char gender){
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "INSERT INTO \"C##NESTERUKIA\".\"PLAYER\" (ID, FIRST_NAME, LAST_NAME, GENDER) VALUES ('"+ id +"', '"+ firstName +"', '"+ lastName +"', '"+ gender +"')";
            stmt.executeQuery(sql);
            con.close();

        }catch(Exception e){ System.out.println(e);}
    }

    public void registerForTournament(long tournamentId, long playerId){
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "INSERT INTO \"C##NESTERUKIA\".\"REGISTRATION\" (TOURNAMENT_ID, PLAYER_ID) VALUES ('"+ tournamentId +"', '"+ playerId +"')";
            stmt.executeQuery(sql);
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }

    public boolean isRegisteredPlayer(long id){
        boolean result = false;
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT COUNT(*) FROM \"C##NESTERUKIA\".\"PLAYER\" WHERE ID = " + id;
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                result = rs.getInt(1)>0;
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return result;
    }
    public User getUser(long id){
        User user = new User();
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT * FROM \"C##NESTERUKIA\".\"PLAYER\" WHERE ID = " + id;
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                user.setId(rs.getLong("ID"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setGender(rs.getString("GENDER").charAt(0));
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return user;
    }
    public boolean isRegisteredForTournament(long tournamentId, long playerId){
        boolean result = false;
        try{
            Class.forName(oracleDriver);
            Connection con= DriverManager.getConnection(url,dbUser, dbPassword);
            Statement stmt=con.createStatement();
            String sql = "SELECT COUNT(*) FROM \"C##NESTERUKIA\".\"REGISTRATION\" " +
                    "WHERE (TOURNAMENT_ID = " + tournamentId + ") AND (PLAYER_ID = " + playerId +")";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                result = rs.getInt(1)>0;
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}
        return result;
    }


}

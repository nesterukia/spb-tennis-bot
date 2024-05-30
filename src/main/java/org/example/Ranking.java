package org.example;

import java.util.Formatter;

public class Ranking {
    private int place;
    private String fullName;
    private int points;

    public int getPlace() {
        return place;
    }

    public Ranking setPlace(int place) {
        this.place = place;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public Ranking setFullName(String fullName) {
        if(fullName.length() > 20){
            String firstName = fullName.split(" ")[0];
            String lastName = fullName.split(" ")[1];
            this.fullName = firstName.charAt(0) + ". " +lastName;
        } else this.fullName = fullName;
        return this;
    }

    public int getPoints() {
        return points;
    }

    public Ranking setPoints(int points) {
        this.points = points;
        return this;
    }

    public String toString(){
        Formatter formatter = new Formatter();
        return formatter.format("|%5d|%20s|%5d|", place, fullName, points).toString();
    }
}

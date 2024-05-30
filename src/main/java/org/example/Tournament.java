package org.example;

import java.sql.Date;

public class Tournament {

    protected long id;
    protected String name;
    protected Date datetime;
    protected String clubName;
    protected String category;
    protected String level;
    protected int price;
    public int getPrice() {
        return price;
    }
    public Tournament setPrice(int price) {
        this.price = price;
        return this;
    }
    public Tournament() {}
    public long getId() {
        return id;
    }

    public Tournament setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Tournament setName(String name) {
        this.name = name;
        return this;
    }

    public Date getDatetime() {
        return datetime;
    }

    public Tournament setDatetime(Date datetime) {
        this.datetime = datetime;
        return this;
    }

    public String getClubName() {
        return clubName;
    }

    public Tournament setClubName(String clubName) {
        this.clubName = clubName;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Tournament setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public Tournament setLevel(String level) {
        this.level = level;
        return this;
    }
}

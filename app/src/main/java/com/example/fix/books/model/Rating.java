package com.example.fix.books.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;


@RealmClass
public class Rating extends RealmObject {
    @PrimaryKey
    private String id_rate;


    public String getId_rate() {
        return id_rate;
    }

    public void setId_rate(String id_rate) {
        this.id_rate = id_rate;
    }

    public Rating(String id_rate) {
        this.id_rate = id_rate;
    }

    public Rating() {
    }
}
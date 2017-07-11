package com.example.fix.books.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;


@RealmClass
public class Readed extends RealmObject {

    @PrimaryKey
    private String id_book;

    public String getId_book() {
        return id_book;
    }

    public void setId_book(String id_book) {
        this.id_book = id_book;
    }

    public Readed(String id_book){
        this.id_book=id_book;
    }
    public Readed(){
    }
}


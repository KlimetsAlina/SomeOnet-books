package com.example.fix.books.model;


import com.example.fix.books.R;

import org.parceler.Parcel;

import io.realm.BooksRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;


@RealmClass
//для передачи данных
@Parcel(implementations =
        {BooksRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Books.class})
//POJO (Plain Old Java Object)
public class Books extends RealmObject {
    private String photo;
    private String images;
    private String rate;
    private String rating;
    @PrimaryKey
    private String hid;
    private String fio;
    private String god;
    private String otkril;
    private String full_desc;
    private String name;
    private String secondphoto;
    private String short_desc;
    private String gallery;
    private String author;


    private int back;
    private int img;

    public Books() {
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }



    public int getBack() {
        int rating1 = Integer.valueOf(rate);
        if (rating1 >= 90) {
            return R.color.hight;
        } else if (rating1 < 85) {
            return R.color.low;
        }else if (rating1 >= 85 && rating1 < 90){
            return R.color.orange;
        }
        return back;
    }

    public int getImg() {
        int rating1 = Integer.valueOf(rate);
        if (rating1 >= 85) {
            return R.drawable.quality;
        }
        return img;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }


    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSecondphoto() {
        return secondphoto;
    }

    public void setSecondphoto(String secondphoto) {
        this.secondphoto = secondphoto;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String  id) {
        this.hid = id;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFull_desc() {
        return full_desc;
    }

    public void setFull_desc(String full_desc) {
        this.full_desc = full_desc;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getGod() {
        return god;
    }

    public void setGod(String god) {
        this.god = god;
    }

    public String getOtkril() {
        return otkril;
    }

    public void setOtkril(String otkril) {
        this.otkril = otkril;
    }
}

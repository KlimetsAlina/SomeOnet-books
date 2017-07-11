package com.example.fix.books.activity;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MyApp extends Application {

    //версия реалма
    private static final int REALM_SCHEMA_VERSION = 2;
    public static final String NAME = "app.realm";


    private static MyApp instance;


    public MyApp(){
    }

    public static MyApp getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initRealm();
    }

    private void initRealm(){
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(NAME)
                .schemaVersion(REALM_SCHEMA_VERSION)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

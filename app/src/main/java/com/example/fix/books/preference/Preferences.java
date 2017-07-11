package com.example.fix.books.preference;

import com.example.fix.books.Constants;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

//для хранения данных,не бд
@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface Preferences {
    @DefaultString(Constants.NAME)
    String name();

    @DefaultString(Constants.EMAIL)
    String mail();

    @DefaultBoolean(true)
    boolean login();

    @DefaultString(Constants.UNIQUE_ID)
    String id();

}

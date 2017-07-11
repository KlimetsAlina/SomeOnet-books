package com.example.fix.books.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.example.fix.books.MainActivity_;
import com.example.fix.books.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.vstechlab.easyfonts.EasyFonts;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

//макет
@EActivity(R.layout.activity_splash_screen)
public class SplashScreen extends Activity implements ConnectionCallbacks,OnConnectionFailedListener {

    @ViewById(R.id.relative)
    RelativeLayout relativeLayout;

    @ViewById(R.id.tv_splash)
    TextView tv;

    @ViewById(R.id.sun)
    ImageView sun;

    @ViewById(R.id.cloud_1)
    ImageView cloud_1;

    @ViewById(R.id.cloud_2)
    ImageView cloud_2;

    @ViewById(R.id.mount)
    ImageView mount;

    private GoogleApiClient googleApiClient;



    @AfterViews
    void doit() {
        //подключаем наш google +
        googleApiClient = new GoogleApiClient.Builder(SplashScreen.this)
                //обработчики событий
                .addConnectionCallbacks(SplashScreen.this)
                .addOnConnectionFailedListener(SplashScreen.this)
                //подключаем гугл +
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        welcome();
    }



    @Background(id = "start", delay = 2600)
    void goo() {
        Login_.intent(this).start();
    }

    @Background(id = "start", delay = 2600)
    void gooo() {
        Person p = Plus.PeopleApi.getCurrentPerson(googleApiClient);
        String name = p.getDisplayName();
        String imageUrl = p.getImage().getUrl();
        String img = imageUrl.substring(0, imageUrl.length() - 2) + "200";
        String email = Plus.AccountApi.getAccountName(googleApiClient);
        startActivity(new Intent(SplashScreen.this, MainActivity_.class).putExtra("id", name).putExtra("img", img).putExtra("xz", email));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }


    //уничтожаем наш поток
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BackgroundExecutor.cancelAll("start", true);
    }


    //анимация и т.д
    private void welcome() {
        DateTime dt = new DateTime();
        DateTime dtMoscow = dt.withZone(DateTimeZone.forID("Europe/Moscow"));
        int hour = dtMoscow.getHourOfDay();
        if (hour >= 0 && hour <= 4) {
            relativeLayout.setBackgroundResource(R.drawable.back_night);
            anim2();
            tv.setText("Доброй Ночи");
            tv.setTypeface(EasyFonts.caviarDreamsBold(this));
        } else if (hour >= 17 && hour <= 23) {
            relativeLayout.setBackgroundResource(R.drawable.back_evening);
            anim();
            tv.setText("Добрый Вечер");
            tv.setTypeface(EasyFonts.ostrichBold(this));
        } else if (hour >= 5 && hour < 12) {
            relativeLayout.setBackgroundResource(R.drawable.back_morning);
            anim();
            tv.setText("Доброе Утро");
            tv.setTypeface(EasyFonts.caviarDreams(this));
        } else if (hour >= 12 && hour < 17) {
            relativeLayout.setBackgroundResource(R.drawable.back);
            anim();
            tv.setText("Добрый День");
            tv.setTypeface(EasyFonts.ostrichBold(this));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void anim() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                Glider.glide(Skill.BackEaseIn, 1700, ObjectAnimator.ofFloat(cloud_1, "translationX", -560, 20))
        );
        set.playTogether(
                Glider.glide(Skill.BackEaseIn, 1400, ObjectAnimator.ofFloat(cloud_2, "translationX", 730, 200))
        );
        set.playTogether(
                Glider.glide(Skill.BackEaseIn, 2500, ObjectAnimator.ofFloat(sun, "translationY", -270, 200))
        );
        set.playSequentially(
                Glider.glide(Skill.BackEaseIn, 1450, ObjectAnimator.ofFloat(tv, "translationY", -500, 10))
        );
        set.playTogether(
                Glider.glide(Skill.CircEaseIn, 1700, ObjectAnimator.ofFloat(mount, "translationY", 450, 0))
        );
        set.setDuration(2350);
        set.start();
        cloud_1.setImageResource(R.drawable.cloud_e_1);
        cloud_2.setImageResource(R.drawable.cloud_e_2);
        sun.setImageResource(R.drawable.sun_day);
        mount.setImageResource(R.drawable.mount);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void anim2() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                Glider.glide(Skill.BackEaseIn, 1700, ObjectAnimator.ofFloat(cloud_1, "translationX", -560, 20))
        );
        set.playTogether(
                Glider.glide(Skill.BackEaseIn, 1400, ObjectAnimator.ofFloat(cloud_2, "translationX", 730, 200))
        );
        set.playTogether(
                Glider.glide(Skill.BackEaseIn, 2500, ObjectAnimator.ofFloat(sun, "translationY", -270, 200))
        );
        set.playSequentially(
                Glider.glide(Skill.BackEaseIn, 1450, ObjectAnimator.ofFloat(tv, "translationY", -500, 10))
        );
        set.playTogether(
                Glider.glide(Skill.CircEaseIn, 1700, ObjectAnimator.ofFloat(mount, "translationY", 450, 0))
        );
        set.setDuration(2350);
        set.start();
        cloud_1.setImageResource(R.drawable.cloud_e_1);
        cloud_2.setImageResource(R.drawable.cloud_e_2);
        sun.setImageResource(R.drawable.moon);
        mount.setImageResource(R.drawable.mount);
    }

    @Override
    public void onConnected(Bundle bundle) {
        gooo();
//        welcome();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        goo();
    }
}

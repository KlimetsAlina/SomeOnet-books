package com.example.fix.books.activity;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fix.books.Constants;
import com.example.fix.books.R;
import com.example.fix.books.model.server.ServerRequest;
import com.example.fix.books.model.server.ServerResponse;
import com.example.fix.books.model.server.User;
import com.example.fix.books.preference.Preferences_;
import com.example.fix.books.rest.ApiPost;
import com.example.fix.books.rest.ApiService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import org.androidannotations.annotations.AfterPreferences;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.PreferenceClick;
import org.androidannotations.annotations.PreferenceScreen;
import org.androidannotations.annotations.sharedpreferences.Pref;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@PreferenceScreen(R.xml.preferences)
@EActivity
public class Settings extends PreferenceActivity implements ConnectionCallbacks, OnConnectionFailedListener {
    @Pref
    Preferences_ preference;

    private AlertDialog dialog;

    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    ProgressBar progress;

    TextView tv_message;

    EditText et_old_password;

    EditText et_new_password;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    //гугл апи
    protected GoogleApiClient mGoogleApiClient;

    public boolean mIntentInProgress;
    public boolean mSignInClicked;

    @Pref
    Preferences_ preferences;

    @AfterPreferences
    public void afterviews() {
//        logout.setOnClickListener(v -> gologout());
        //конект от google +
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)//для управления состоянием
                .addOnConnectionFailedListener(this) //
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)// детали юзера
                .build();
        //подключиться
        mGoogleApiClient.connect();
    }

    @PreferenceClick(R.string.logout_settings)
    void gologout() {
        //если вход был из гугл + то выход
        if (mGoogleApiClient.isConnected()) {
            revokeGplusAccess();
        } else if (!mGoogleApiClient.isConnected()) {
            //если через сервер то изменяем преференс
            preferences.edit()
                    .login()
                    .put(false)
                    .mail()
                    .put(Constants.EMAIL)
                    .name()
                    .put(Constants.NAME)
                    .id()
                    .put(Constants.UNIQUE_ID)
                    .apply();
            Login_.intent(this).start();
        }

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i("google base class", "onConnected invoked");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("google base class", "onConnectionSuspended invoked");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("google base class", "onConnectionFailed invoked");

        if (!mIntentInProgress) {
            if (mSignInClicked && connectionResult.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    public void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(arg0 -> startActivity(new Intent(Settings.this, Login_.class)));
        }
    }


    @PreferenceClick(R.string.change_password_settings)
    void showDialog() {
        //алерт дайлог
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        //макет для него свой находим
        View view = inflater.inflate(R.layout.dialog_change_password, null);
        //находим поля
        et_old_password = (EditText) view.findViewById(R.id.et_old_password);
        et_new_password = (EditText) view.findViewById(R.id.et_new_password);
        tv_message = (TextView) view.findViewById(R.id.tv_message);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        //кидаем макет нашему дайлогу
        builder.setView(view);
        //название
        builder.setTitle("Изменить пароль:");
        //изменить пароль
        builder.setPositiveButton("Изменить", (dialog1, which) -> {

        });
        //закрыть диалог
        builder.setNegativeButton("Отмена", (dialog1, which) -> {
            dialog1.dismiss();
        });
        //собрать диалог
        dialog = builder.create();
        //показать
        dialog.show();
        //слушатель для позитивной кнопки
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String old_password = et_old_password.getText().toString();
            String new_password = et_new_password.getText().toString();
            if (!old_password.isEmpty() && !new_password.isEmpty()) {

                progress.setVisibility(View.VISIBLE);
                changePasswordProcess(preference.mail().get(), old_password, new_password);

            } else {

                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText("Fields are empty");
            }
        });

    }

    private void changePasswordProcess(String email, String old_password, String new_password) {
        //находим модель
        User user = new User();
        //посылаем эмэил и пароли
        user.setEmail(email);
        user.setOld_password(old_password);
        user.setNew_password(new_password);
        //указываем операцию для приема данных потом
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setOperation(Constants.CHANGE_PASSWORD_OPERATION);
        serverRequest.setUser(user);
        //находим пост метод
        ApiService apiService = ApiPost.getPostApi();
        //подключаем rx retrofit
        Observable<ServerResponse> observable = apiService.post(serverRequest);
        mSubscriptions.add(
                observable
                        .doOnNext(responses -> {
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Observer<ServerResponse>() {
                            @Override
                            public void onCompleted() {
                                progress.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(Constants.TAG, "failed");
                                progress.setVisibility(View.GONE);
                                tv_message.setVisibility(View.VISIBLE);
                                tv_message.setText(e.getLocalizedMessage());
                            }

                            @Override
                            public void onNext(ServerResponse responses) {
                                if (responses.getResult().equals(Constants.SUCCESS)) {
                                    //если все ок то выключаем прогрессбар и диалог
                                    progress.setVisibility(View.GONE);
                                    tv_message.setVisibility(View.GONE);
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), responses.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    progress.setVisibility(View.GONE);
                                    tv_message.setVisibility(View.VISIBLE);
                                    tv_message.setText(responses.getMessage());
                                }
                            }
                        })
        );

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}

package com.example.fix.books.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fix.books.Constants;
import com.example.fix.books.MainActivity_;
import com.example.fix.books.R;
import com.example.fix.books.preference.Preferences_;
import com.example.fix.books.model.server.ServerRequest;
import com.example.fix.books.model.server.ServerResponse;
import com.example.fix.books.model.server.User;
import com.example.fix.books.rest.ApiPost;
import com.example.fix.books.rest.ApiService;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

//указываем макет
@EActivity(R.layout.activity_auth)
public class Login extends Activity implements  ConnectionCallbacks, OnConnectionFailedListener {
    //вводим переменные типа булеан для проверки.
    private boolean isConsentScreenOpened;
    private boolean isSignInButtonClicked;
    //находим все виджеты
    @ViewById(R.id.progress)
    ProgressBar progress;

    //подписка,для работы с жизненным циклом rx retrofit
    private CompositeSubscription mSubscriptions = new CompositeSubscription();


    //наш преференс(он же типо орм,только без записи в какое-либо хранилище)
    @Pref
    Preferences_ preferences;

    @ViewById(R.id.input_email)
    EditText _emailText;

    @ViewById(R.id.input_password)
    EditText _passwordText;

    @ViewById(R.id.link_signup)
    TextView _signupLink;

    @ViewById(R.id.btn_login)
    Button _login;

    @ViewById(R.id.btSignInDefault)
    SignInButton signInButton;

    @ViewById(R.id.fb_login_button)
    LoginButton enterByFB;

    private UiLifecycleHelper uiHelper;


    private ConnectionResult connectionResult;
    private GoogleApiClient googleApiClient;

    private static final int SIGN_IN_CODE = 56465;



    @AfterViews
    void go() {
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        enterByFB.setUserInfoChangedCallback(user -> {
            if (user != null) {
                preferences.edit()
                        .name()
                        .put(user.getName())
                        .mail()
                        .put(user.getId())
                        .apply();
                //получаема имя и выводим в текст вью
                MainActivity_.intent(Login.this).start();
            }
        });
        //подключаем наш google +
        googleApiClient = new GoogleApiClient.Builder(Login.this)
                //обработчики событий
                .addConnectionCallbacks(Login.this)
                .addOnConnectionFailedListener(Login.this)
                //подключаем гугл +
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        //если нажали на регистрацию,то переходим туда
        _signupLink.setOnClickListener(v -> SignupActivity_.intent(this).start());
        _login.setOnClickListener(v -> {
            MainActivity_.intent(Login.this).start();
//            String email = _emailText.getText().toString();
//            String password = _passwordText.getText().toString();
//
//            //если строчки не пустые то логинимся
//            if (!email.isEmpty() && !password.isEmpty()) {
//                progress.setVisibility(View.VISIBLE);
//                loginProcess(email, password);
//            } else {
//                //иначе выскакивает "у вас пустые поля"
//                Snackbar.make(findViewById(android.R.id.content), "Fields are empty !", Snackbar.LENGTH_LONG).show();
//            }
        });
        signInButton.setOnClickListener(v -> {
            if (!googleApiClient.isConnecting()) {
                isSignInButtonClicked = true;
                resolveSignIn();
            }
        });
    }

    private Session.StatusCallback statusCallback = (session, state, exception) -> {
        if (state.isOpened()) {
            Log.d("FacebookSampleActivity", "Facebook session opened");
        } else if (state.isClosed()) {
            Log.d("FacebookSampleActivity", "Facebook session closed");
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    private void loginProcess(String email, String password) {
        //находим модель
        User user = new User();
        //посылаем данные
        user.setEmail(email);
        user.setPassword(password);
        ServerRequest serverRequest = new ServerRequest();
        //указываем опирацию,для разбора с сервера
        serverRequest.setOperation(Constants.LOGIN_OPERATION);
        serverRequest.setUser(user);
        //находим интерфейс
        ApiService apiService = ApiPost.getPostApi();
        //подключаем Rx retrofit
        Observable<ServerResponse> observable = apiService.post(serverRequest);
        //делаем подписку
        mSubscriptions.add(
                observable
                        //начинаем,загружать все то что отправили
                        .doOnNext(responses -> {
                        })
                        //в гл.потоке (UI)
                        .observeOn(AndroidSchedulers.mainThread())
                        //т.е для управления потоками > Executor
                        .subscribeOn(Schedulers.io())
                        //Подписсываес
                        .subscribe(new Observer<ServerResponse>() {
                            @Override
                            public void onCompleted() {
                                progress.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                progress.setVisibility(View.INVISIBLE);
                                Log.d(Constants.TAG,"failed");
                                Snackbar.make(findViewById(android.R.id.content), e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                            }

                            @Override
                            public void onNext(ServerResponse responses) {
                                //если мы благополучно подписались,к нам что то пришли,и мы смотрим,эти данные подходят
                                //под константу SUCCESS,если да,то начинаем определенные действия,иначе ошибка
                                if (responses.getResult().equals(Constants.SUCCESS)) {
                                    //изменим наши преференсы
                                    preferences.edit()
                                            //кидаем в логин значение true
                                            .login()
                                            .put(true)
                                            //кидаем почту,полученную от данных с сервера
                                            .mail()
                                            .put(responses.getUser().getEmail())
                                            //имя
                                            .name()
                                            .put(responses.getUser().getName())
                                            //уникальный Id
                                            .id()
                                            .put(responses.getUser().getUnique_id())
                                            //применяем
                                            .apply();
                                    //и переходим в гл.активити
                                    startActivity(new Intent(Login.this, MainActivity_.class));
                                } else if (!responses.getResult().equals(Constants.SUCCESS))
                                {
                                    Snackbar.make(findViewById(android.R.id.content), R.string.Un_correctly, Snackbar.LENGTH_LONG).show();
                                }


                            }
                        })
        );
    }

    //для UI тестирования нужен этот метод
    @TextChange(R.id.btn_login)
    void credentialChenged() {
        _login.setEnabled(!TextUtils.isEmpty(_emailText.getText()) && !TextUtils.isEmpty(_passwordText.getText()));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    //метод-подключение к google +
    public void resolveSignIn() {
        if (connectionResult != null && connectionResult.hasResolution()) {
            try {
                isConsentScreenOpened = true;
                connectionResult.startResolutionForResult(Login.this, SIGN_IN_CODE);
            } catch (IntentSender.SendIntentException e) {
                isConsentScreenOpened = false;
                googleApiClient.connect();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == SIGN_IN_CODE) {
//            isConsentScreenOpened = false;
//
//            if (resultCode != RESULT_OK) {
//                isSignInButtonClicked = false;
//            }
//
//            if (!googleApiClient.isConnecting()) {
//                googleApiClient.connect();
//            }
//        }
//    }


    //если у нас подключен google+ аккаунт,то
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isSignInButtonClicked = false;
        doInBackground();
    }

    //берем опр.данные от подключившегося аккаунта и уходим с ними в гл.активити
    @Background()
    void doInBackground() {
        Person p = Plus.PeopleApi.getCurrentPerson(googleApiClient);
        String name = p.getDisplayName();
        String imageUrl = p.getImage().getUrl();
        String img = imageUrl.substring(0, imageUrl.length() - 2) + "200";
        String email = Plus.AccountApi.getAccountName(googleApiClient);
        startActivity(new Intent(Login.this, MainActivity_.class)
                .putExtra("id", name)
                .putExtra("img", img)
                .putExtra("xz", email));
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), Login.this, 0).show();
            MainActivity_.intent(this).start();
            return;
        }

        if (!isConsentScreenOpened) {
            connectionResult = result;

            if (isSignInButtonClicked) {
                resolveSignIn();
            }
        }
    }

    //отписываемся,исходя из lifecycle rx retrofit 2
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
        uiHelper.onDestroy();
    }
}

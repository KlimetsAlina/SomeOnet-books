package com.example.fix.books.activity;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fix.books.Constants;
import com.example.fix.books.R;
import com.example.fix.books.feedback.MailSenderClass;
import com.example.fix.books.model.server.ServerRequest;
import com.example.fix.books.model.server.ServerResponse;
import com.example.fix.books.model.server.User;
import com.example.fix.books.rest.ApiPost;
import com.example.fix.books.rest.ApiService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

//макет
@EActivity(R.layout.activity_signup)
public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    @ViewById(R.id.input_name)
    EditText _nameText;
    @ViewById(R.id.input_email)
    EditText _emailText;
    @ViewById(R.id.input_password)
    EditText _passwordText;
    @ViewById(R.id.btn_signup)
    Button _signupButton;
    @ViewById(R.id.link_login)
    TextView _loginLink;


    String all_text;
    String from;
    String attach = "";
    String title;

    @AfterViews
    void go() {

        _signupButton.setOnClickListener(v ->
                sign()

        );

        _loginLink.setOnClickListener(v ->

                {
                    // Finish the registration screen and return to the Login activity
                    finish();
                }

        );
    }



    @Background()
    void sign() {
        Log.d(TAG, "Signup");
        signup();
        //Все тоже самое,только тут мы регестрируемся
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.REGISTER_OPERATION);
        request.setUser(user);
        ApiService apiService = ApiPost.getPostApi();
        Observable<ServerResponse> observable = apiService.post(request).cache();
        mSubscriptions.add(
                observable
                        .doOnNext(responses -> {
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Observer<ServerResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(Constants.TAG,"failed");
                                Snackbar.make(findViewById(android.R.id.content), e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(ServerResponse responses) {

                            }
                        })
        );
        try {
            title = "Добро пожаловать";
            from = "feedbackmail@gmail.ru";
            all_text = ("E-mail: " + email + "\n" + "Пароль: " + password);
            MailSenderClass sender = new MailSenderClass("Feedmailback@gmail.com", "654321ytrewq");
            sender.sendMail(title, all_text, from, email, attach);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //в отедльном потоке
    @UiThread()
    void signup() {
        if (!validate()) {
            onSignupFailed();
        } else if (validate()) {
            Login_.intent(this).start();
        }
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        //если не корректно введены данные,то заостряем внимание пользователя

        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    //отписка
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
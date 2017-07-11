package com.example.fix.books.fragment;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fix.books.R;
import com.example.fix.books.feedback.MailSenderClass;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.send)
public class BaseDialogFragmentMessage extends DialogFragment {
    @ViewById(R.id.txt1)
    TextInputLayout title1;

    @ViewById(R.id.txt2)
    TextInputLayout name1;

    @ViewById(R.id.txt3)
    TextInputLayout text1;

    @ViewById(R.id.up_title)
    TextView up_title;

    protected String WhoEmail = "Someevill@gmail.com";

    String title;
    String from;
    String attach = "";
    String text;
    String name;
    String all_text;

    @AfterViews
    public void init() {
        up_title.setText("Сообщить об ошибке!");
    }

    @Click(R.id.fab)
    public void click() {
        sender_mail();
        update();
    }

    @Click(R.id.cancel)
    void canceled(){
        BaseDialogFragmentMessage.this.dismiss();
    }

    @Background()
    void sender_mail() {
        try {
            from = "feedbackmail@gmail.ru";
            title = ((EditText) title1.findViewById(R.id.your_name)).getText().toString();
            name = ((EditText) name1.findViewById(R.id.name_of_error)).getText().toString();
            text = ((EditText) text1.findViewById(R.id.error)).getText().toString();
            all_text = ("Контактные данные: " + name + "\n" + "Отзыв|Предложение: " + text);
            MailSenderClass sender = new MailSenderClass("Feedmailback@gmail.com", "654321ytrewq");
            sender.sendMail(title, all_text, from, WhoEmail, attach);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "упс!", Toast.LENGTH_SHORT).show();
        }
    }

    void update() {
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.dismiss();
        Toast.makeText(getActivity(), "Ваши пожелания учтены,спасибо!", Toast.LENGTH_LONG).show();
        BaseDialogFragmentMessage.this.dismiss();
    }
}


package com.example.fix.books;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.fix.books.Tutorial.PrefConstants;
import com.example.fix.books.Tutorial.ProductTourActivity;
import com.example.fix.books.Tutorial.SAppUtil;
import com.example.fix.books.activity.Settings_;
import com.example.fix.books.adapter.ViewPagerAdapter;
import com.example.fix.books.fragment.BaseDialogFragmentMessage_;
import com.example.fix.books.preference.Preferences_;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vstechlab.easyfonts.EasyFonts;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import io.realm.Realm;


//Принцип работы AA довольно прост:
// Annotation Processor создает дочерний класс для каждого проаннотированного класса,
// в который помещает весь необходимый код, т.е.
// все наши setContentView никуда не деваются, а просто переносятся из основного кода в сгенерированный.
//  Благодаря такому подходу использование AA никак не влияет на производительность готового приложения,
// в тоже время все аннотированные элементы должны иметь область видимости не ниже package-private,
// чтобы дочерний класс мог к ним обратиться.

//Находим макет и подключаем андроид анотации
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    //виджеты,тулбар,листалка,табы,меню
    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @ViewById(R.id.container)
    ViewPager viewPager;


    //преференс,хранение
    @Pref
    Preferences_ preferences;

    @ViewById(R.id.tabs)
    TabLayout tabLayout;

    @ViewById(R.id.drawer_layout)
    DrawerLayout drawer;

    @ViewById(R.id.nav_view)
    NavigationView nvDrawer;

    ActionBarDrawerToggle toggle;

    //бд
    Realm realm;

    MenuItem selectItem = null;

    //Здесь важно заметить, что внедрение ViewById произойдет уже после завершения метода OnCreate,
    // чтобы выполнить какие-то действия сразу после внедрения ViewById нужно использовать аннотацию @AfterViews
    @AfterViews
    public void afterViews() {
        //фреско-для изображений из сети
        Fresco.initialize(this);
        checkShowTutorial();
        //настройки орм
        realm = Realm.getDefaultInstance();
        setToolbar();
        DrawerContent();
        Tabs();
        drawer.setDrawerListener(toggle);
        nvDrawer.setNavigationItemSelectedListener((menuItem) -> {
            if (selectItem != menuItem) {
                selectItem = menuItem;
                switch (menuItem.getItemId()) {
                    case R.id.tutorial:
                        //явный интент
                        startActivity(new Intent(this, ProductTourActivity.class));
                        break;
                    case R.id.feedback:
                        DialogFragment dialogFragment = new BaseDialogFragmentMessage_();
                        dialogFragment.show(getSupportFragmentManager(), "developer");
                        break;
                    case R.id.nav_settings:
                        Settings_.intent(this).start();
                        break;
                    default:
                        break;
                }
                setTitle(menuItem.getTitle());
                drawer.closeDrawers();
            }
            return true;
        });
        //получаем данные из гугл +
        String getName = getIntent().getStringExtra("id");
        String getMail = getIntent().getStringExtra("xz");
        String getPhoto = getIntent().getStringExtra("img");
        //находим виджеты из макета меню
        View headerView = nvDrawer.inflateHeaderView(R.layout.nav_header_main);
        TextView tv = (TextView) headerView.findViewById(R.id.account_email);
        TextView ml = (TextView) headerView.findViewById(R.id.profile_mail);
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) headerView.findViewById(R.id.profile_img);
        String path = "res:/" + R.drawable.mortarboard;
        tv.setTypeface(EasyFonts.caviarDreams(this));
        ml.setTypeface(EasyFonts.droidSerifRegular(this));
        try {
            //пытаемся грузить данные из гугль +
            simpleDraweeView.setImageURI(Uri.parse(getPhoto));
            ml.setText(getMail);
            tv.setText(getName);
            //если человек не из гугль + зашел то др данные из сервера
        } catch (NullPointerException e) {
            e.printStackTrace();
            simpleDraweeView.setImageURI(Uri.parse(path));
            tv.setText(preferences.name().get());
            ml.setText(preferences.mail().get());
        }
    }

    private void Tabs() {
        //листалка
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        //табы
        tabLayout.addTab(tabLayout.newTab().setText(R.string.books).setIcon(R.drawable.tab_select1));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.magazine).setIcon(R.drawable.tab_select2));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            //по нажатию шлем опредленный фрагмент
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }



    @SuppressWarnings("ConstantConditions")
    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //название
            getSupportActionBar().setTitle(R.string.dear_evening);
            //кнопка назад
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void DrawerContent() {
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
    }


    //чтобы стрелка крутилась)
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawer != null)
            toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawer != null)
            toggle.onConfigurationChanged(newConfig);
    }

    private void checkShowTutorial() {
        int oldVersionCode = PrefConstants.getAppPrefInt(this, "version_code");
        int currentVersionCode = SAppUtil.getAppVersionCode(this);
        if (currentVersionCode > oldVersionCode) {
            startActivity(new Intent(MainActivity.this, ProductTourActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            PrefConstants.putAppPrefInt(this, "version_code", currentVersionCode);
        }
    }


    @Override
    protected void onDestroy() {
        //чтобы небыло Out Of Memory закрываем бд
        realm.close();
        super.onDestroy();
    }
}

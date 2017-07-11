package com.example.fix.books.activity;

import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fix.books.R;
import com.example.fix.books.adapter.GalleryAdapter;
import com.example.fix.books.db.RealmHelper;
import com.example.fix.books.model.Books;
import com.example.fix.books.model.Gallery;
import com.example.fix.books.model.Readed;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vstechlab.easyfonts.EasyFonts;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

//указываем макет
@EActivity(R.layout.activity_books_details)
public class BooksDetails extends AppCompatActivity {
    private String img;

    //находим тулбар,место для картинки и т.д
    @ViewById(R.id.main_toolbar)
    Toolbar toolbar;


    @ViewById(R.id.main_collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @ViewById(R.id.book_image_view)
    SimpleDraweeView imageView;

    @ViewById(R.id.author_pic)
    SimpleDraweeView author_pic;

    @ViewById(R.id.detail_date)
    TextView detail_date;

    @ViewById(R.id.book_rate)
    TextView book_rate;

    @ViewById(R.id.color_back)
    ImageView color_back;

    @ViewById(R.id.quality_view)
    ImageView quality;

    @ViewById(R.id.detail_desription)
    TextView detail_description;

    @ViewById(R.id.ganre)
    TextView genre;

    @ViewById(R.id.page)
    TextView page;

    @ViewById(R.id.genre1)
    TextView genre_second;

    @ViewById(R.id.release_date)
    TextView release_date;

    @ViewById(R.id.save_scores)
    AppCompatButton save_scores;


    @ViewById(R.id.readed)
    TextView readed;

    @ViewById(R.id.author_name)
    TextView author_name;

    @ViewById(R.id.rating_detail)
    TextView rating_detail;

    Realm realm;

    @ViewById(R.id.main_appbar)
    AppBarLayout appBarLayout;

    @AfterViews
    public void afterViews() {
        Fresco.initialize(this);
        realm = Realm.getDefaultInstance();
        Books item = Parcels.unwrap(this.getIntent().getParcelableExtra("example"));
        imageView.setImageURI(Uri.parse(item.getSecondphoto()));
        author_pic.setImageURI(Uri.parse(item.getImages()));
        detail_date.setText("");
        genre.setText(item.getFio());
        genre.setTypeface(EasyFonts.caviarDreamsBold(getApplicationContext()));
        page.setText(item.getGod());
        color_back.setImageResource(item.getBack());
        quality.setImageResource(item.getImg());
        author_name.setText(item.getAuthor());
        author_name.setTypeface(EasyFonts.caviarDreamsBold(getApplicationContext()));
        release_date.setText(item.getFull_desc());
        release_date.setTypeface(EasyFonts.walkwayBold(getApplicationContext()));
        detail_date.setTypeface(EasyFonts.walkwayBlack(getApplicationContext()));
        genre_second.setText(item.getOtkril());
        genre_second.setTypeface(EasyFonts.walkwayBold(getApplicationContext()));

        book_rate.setText(item.getRate());
        book_rate.setTypeface(EasyFonts.walkwayBold(getApplicationContext()));
        page.setTypeface(EasyFonts.funRaiser(getApplicationContext()));
        //описание
        detail_description.setText(item.getShort_desc());
        rating_detail.setText(item.getRating() + " Рейтинг пользователей");

        detail_description.setTypeface(EasyFonts.caviarDreams(getApplicationContext()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitle(item.getName());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        img = item.getGallery();
        Log.e("Stroka", img);

        String[] arr = img.split(" ");
        List<Gallery> list = new ArrayList<>();
        for (String anArr : arr) {
            list.add(new Gallery(anArr));
        }

        GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), list);
        recyclerView.setAdapter(adapter);
        RealmResults<Readed> read = realm.where(Readed.class).equalTo("id_book", item.getHid()).findAll();
        if (read.size() > 0) {
            readed.setVisibility(View.VISIBLE);
            save_scores.setVisibility(View.GONE);
        } else {
            readed.setVisibility(View.GONE);
            save_scores.setVisibility(View.VISIBLE);
        }

        save_scores.setOnClickListener(v -> {
            RealmHelper.copyOrUpdate(new Readed(item.getHid()));
            readed.setVisibility(View.VISIBLE);
            save_scores.setVisibility(View.GONE);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

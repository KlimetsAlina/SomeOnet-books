package com.example.fix.books.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fix.books.R;
import com.example.fix.books.db.RealmHelper;
import com.example.fix.books.model.Books;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vstechlab.easyfonts.EasyFonts;

import io.realm.RealmResults;

//адаптер
public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.CardViewHolder> {

    //коллекция
    private RealmResults<Books> mList;
    //контекст
    Context context;

    //конструктор
    public BooksAdapter(Context context) {
        this.context = context;
    }

    //статик потому,что единственный экземпляр
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        //находим виджеты
        TextView name_book;
        SimpleDraweeView imageView;
        SimpleDraweeView authorpic;
        TextView numbers;
        TextView rate;
        TextView meta;
        TextView rating;
        ImageView back_color;
        ImageView quality;

        public CardViewHolder(final View item) {
            super(item);
            this.name_book = (TextView) item.findViewById(R.id.book_name);
            this.numbers = (TextView) item.findViewById(R.id.numbers);
            this.imageView = (SimpleDraweeView) item.findViewById(R.id.card_view_image);
            this.meta = (TextView) item.findViewById(R.id.textView);
            this.authorpic = (SimpleDraweeView) item.findViewById(R.id.card_view_portrait);
            this.rate = (TextView) item.findViewById(R.id.book_rate);
            this.rating = (TextView) item.findViewById(R.id.book_rating);
            this.back_color = (ImageView) item.findViewById(R.id.color_back);
            this.quality = (ImageView) item.findViewById(R.id.quality_view);
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //инцилизируем библиотеку по работе с кратинками
        Fresco.initialize(context);
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_view, parent, false);
        //находим наш макет для адаптера
        return new CardViewHolder(v);
    }



    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        if(mList.get(position).isValid()) {
            //выводим на экран все что получили
            Books item = mList.get(position);
            holder.meta.setTypeface(EasyFonts.walkwayUltraBold(context));
            holder.meta.setText(R.string.lvl);
            holder.numbers.setText(String.valueOf(item.getHid()));
            holder.numbers.setTypeface(EasyFonts.walkwayUltraBold(context));
            holder.name_book.setTypeface(EasyFonts.caviarDreams(context));
            holder.name_book.setText(item.getName());
            holder.rate.setText(item.getRate());
            holder.rating.setText(item.getRating() + " *");
            holder.imageView.setImageURI(Uri.parse(item.getPhoto()));
            holder.authorpic.setImageURI(Uri.parse(item.getImages()));
            holder.back_color.setImageResource(item.getBack());
            holder.quality.setImageResource(item.getImg());
        }
    }

    //метод для удаления
    public void remove(int position) {
        Books item = mList.get(position);
        RealmHelper.removeEntry(item);
        notifyItemRemoved(position);
    }

    //получить итем из коллекции
    public Books getItem(int position) {
        return mList.get(position);
    }

    //размер коллекции
    @Override
    public int getItemCount() {
        if (mList!= null)
            return mList.size();
        else
            return 0;
    }

    //получить список коллекции
    public RealmResults<Books> getList() {
        return mList;
    }


    public void setList(RealmResults<Books> list) {
        mList = list;
    }

    public boolean isEmpty() {
        return mList == null || mList.size() == 0;
    }
}

package com.example.fix.books.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fix.books.R;
import com.example.fix.books.model.Practice;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.ArrayList;
import java.util.Collection;

//адаптер
public class PracticeAdapter extends RecyclerView.Adapter<PracticeAdapter.CardViewHolder> {

    //коллекция
    private ArrayList<Practice> mList;
    //контекст
    Context context;

    //конструктор
    public PracticeAdapter(Context context) {
        this.context = context;
        this.mList = new ArrayList<>();
    }

    //статик потому,что единственный экземпляр
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        //находим виджеты
        TextView name_title;
        SimpleDraweeView practiceImage;
        Button otvet1;
        Button otvet2;
        Button otvet3;
        Button otvet4;
        LinearLayout linear;


        public CardViewHolder(final View item) {
            super(item);
            this.name_title = (TextView) item.findViewById(R.id.title_practice);
            this.otvet1 = (Button) item.findViewById(R.id.otvet1);
            this.otvet2 = (Button) item.findViewById(R.id.otvet2);
            this.linear = (LinearLayout)item.findViewById(R.id.linear);
            this.practiceImage = (SimpleDraweeView) item.findViewById(R.id.ccard_image);
            this.otvet3 = (Button) item.findViewById(R.id.otvet3);
            this.otvet4 = (Button) item.findViewById(R.id.otvet4);
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //инцилизируем библиотеку по работе с кратинками
        Fresco.initialize(context);
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_view_practice, parent, false);
        //находим наш макет для адаптера
        return new CardViewHolder(v);
    }


    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        //выводим на экран все что получили
        Practice elemnts = mList.get(position);
        holder.otvet1.setText(elemnts.getO1());
        holder.otvet2.setText(elemnts.getO2());
        holder.otvet3.setText(elemnts.getO3());
        holder.name_title.setText(elemnts.getTtitle());
        holder.name_title.setTypeface(EasyFonts.caviarDreams(context));
        holder.otvet4.setText(elemnts.getO4());
        holder.practiceImage.setImageURI(Uri.parse(elemnts.getPhoto()));
        holder.otvet3.setOnClickListener(view -> {
            if (elemnts.getOtvet().equals(elemnts.getO3()))
                Snackbar.make(holder.linear, "Правильно", Toast.LENGTH_SHORT).show();
            else {
                Snackbar.make(holder.linear, "Не правильно!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.otvet1.setOnClickListener(view -> {
            if (elemnts.getOtvet().equals(elemnts.getO1()))
                Snackbar.make(holder.linear, "Правильно", Toast.LENGTH_SHORT).show();
            else {
                Snackbar.make(holder.linear, "Не правильно!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.otvet2.setOnClickListener(view -> {
            if (elemnts.getOtvet().equals(elemnts.getO2()))
                Snackbar.make(holder.linear, "Правильно", Toast.LENGTH_SHORT).show();
            else {
                Snackbar.make(holder.linear, "Не правильно!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.otvet4.setOnClickListener(view -> {
            if (elemnts.getOtvet().equals(elemnts.getO4()))
                Snackbar.make(holder.linear, "Правильно", Toast.LENGTH_SHORT).show();
            else {
                Snackbar.make(holder.linear, "Не правильно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Pagination(@NonNull Collection collection) {
        //добавить в массив коллекцию
        mList.addAll(collection);
        //Notify that item reflected at position has been newly inserted.
        //Обновить элементы исходи из позиции вставки нового элемента
        notifyItemRangeInserted(getItemCount(), getItemCount());
    }

    //размер коллекции
    @Override
    public int getItemCount() {
         return mList.size();
    }
}

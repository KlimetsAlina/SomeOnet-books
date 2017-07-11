package com.example.fix.books.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fix.books.R;
import com.example.fix.books.model.Gallery;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.CardViewHolder> {
    private List<Gallery> galleryList;
    private Context context;

    public GalleryAdapter(Context context, List<Gallery> arrayList) {
        this.context = context;
        this.galleryList = arrayList;
    }

    @Override
    public GalleryAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Fresco.initialize(context);
        View v = LayoutInflater.from(context)
                .inflate(R.layout.img, parent, false);
        //находим наш макет для адаптера
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.CardViewHolder holder, int position) {
        Gallery gallery = galleryList.get(position);
        holder.image.setImageURI(Uri.parse(gallery.getLink()));

    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView image;
        public CardViewHolder(View itemView) {
            super(itemView);
            image = (SimpleDraweeView) itemView.findViewById(R.id.image);
        }
    }
}

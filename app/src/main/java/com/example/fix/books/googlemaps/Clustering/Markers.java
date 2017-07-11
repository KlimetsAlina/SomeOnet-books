package com.example.fix.books.googlemaps.Clustering;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


public class Markers implements ClusterItem {
    public final String name;
    public final int profilePhoto;
    private final LatLng mPosition;

    public Markers(LatLng position, String name, int pictureResource) {
        this.name = name;
        profilePhoto = pictureResource;
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
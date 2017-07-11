package com.example.fix.books.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fix.books.R;
import com.example.fix.books.googlemaps.Clustering.ClusterDB;
import com.example.fix.books.googlemaps.Clustering.Markers;
import com.example.fix.books.googlemaps.Clustering.MultiDrawable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterInfoWindowClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemInfoWindowClickListener;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_blank)
public class MapFragment extends SupportMapFragment implements ConnectionCallbacks,
        OnConnectionFailedListener,
        OnClusterClickListener<Markers>,
        OnClusterInfoWindowClickListener<Markers>,
        OnClusterItemClickListener<Markers>,
        OnClusterItemInfoWindowClickListener<Markers> {

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private ClusterManager<Markers> mClusterManager;

    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};

    private int curMapTypeIndex = 1;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        initCamera(mCurrentLocation);

    }


    private class PersonRenderer extends DefaultClusterRenderer<Markers> {
        private final IconGenerator mIconGenerator = new IconGenerator(getContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PersonRenderer() {
            super(getContext(), getMap(), mClusterManager);

            View multiProfile = getActivity().getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }



        @Override
        protected void onBeforeClusterItemRendered(Markers person, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their message.
            mImageView.setImageResource(person.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Markers> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Markers p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<Markers> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(getContext(), cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();
        return true;
    }


    private void initCamera(Location location) {
        mClusterManager = new ClusterManager<>(getContext(), getMap());
        mClusterManager.setRenderer(new PersonRenderer());
        getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        addItems();
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(),
                        location.getLongitude()))
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        getMap().animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);

        getMap().setMapType(MAP_TYPES[curMapTypeIndex]);
        getMap().setTrafficEnabled(true);
        getMap().setMyLocationEnabled(true);
        getMap().getUiSettings().setZoomControlsEnabled(true);
        mClusterManager.cluster();
    }

    private void addItems() {
        ClusterDB clust = new ClusterDB();
        mClusterManager.addItem(new Markers(clust.position2(),"Coffe Break",R.drawable.coffee));
        mClusterManager.addItem(new Markers(clust.position3(),"Месхи",R.drawable.meshi));
        mClusterManager.addItem(new Markers(clust.position4(),"Звезда",R.drawable.keditbookmarks));
        mClusterManager.addItem(new Markers(clust.position5(),"Библиотека",R.drawable.brain));
        mClusterManager.addItem(new Markers(clust.position6(),"Кофе",R.drawable.coffee));
        mClusterManager.addItem(new Markers(clust.position7(),"Кофе",R.drawable.coffee));
        mClusterManager.addItem(new Markers(clust.position8(),"Кофе",R.drawable.coffee));
        mClusterManager.addItem(new Markers(clust.position9(),"Кофе",R.drawable.coffee));
        mClusterManager.addItem(new Markers(clust.position10(),"Молоко :)",R.drawable.coffee));
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Markers> cluster) {

    }

    @Override
    public boolean onClusterItemClick(Markers markers) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Markers markers) {

    }
}
package cc.ardennehiking.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.djodjo.comm.jus.JusError;
import org.djodjo.comm.jus.Response;
import org.djodjo.comm.jus.toolbox.NetworkImageView;
import org.djodjo.json.JsonArray;
import org.djodjo.json.JsonElement;
import org.djodjo.json.JsonObject;
import org.djodjo.widget.multiselectspinner.MultiSelectSpinner;
import org.djodjo.widget.slidinglayout.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import cc.ardennehiking.app.app.MyJus;
import cc.ardennehiking.app.model.APlace;
import cc.ardennehiking.app.model.GNPlace;
import cc.ardennehiking.app.model.OSMNode;
import cc.ardennehiking.app.model.Place;
import cc.ardennehiking.app.model.Track;
import cc.ardennehiking.app.model.WikiPlace;
import cc.ardennehiking.app.service.GeonamesSearchJsonRequest;
import cc.ardennehiking.app.service.GeonamesWikiBBSearchRequest;
import cc.ardennehiking.app.service.LocalWays;
import cc.ardennehiking.app.service.OsmOverpassRequest;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, MultiSelectSpinner.MultiSpinnerListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private volatile long lastUpdate = SystemClock.elapsedRealtime() - 30 * 1000;
    private volatile boolean processingWki = false;
    private volatile boolean processingGn = false;
    private volatile boolean processingOSM = false;
    private volatile boolean init = false;
    private volatile boolean followLoc = false;


    private HashMap<String, Marker> markers = new HashMap<>();
    private HashMap<String, Place> placesData = new HashMap<>();


    SharedPreferences sharedPref;
    HashMap<Class, Bitmap> markerBitmaps = new HashMap<>();
    HashMap<Class, Float> markerColors = new HashMap<>();


    private SlidingUpPanelLayout slidingLayout;
    private TextView txtItemTitle;
    private TextView txtItemSnippet;
    private Marker lastMarker;
    private ImageView imgDirections;
    private ImageView imgStreetView;
    private ImageView imgOpenWWW;
    private ProgressBar progressBar;
    private CountDownTimer updateTimer;


    private static volatile int lastSelTrack = -1;
    private static ArrayList<Track> tracks = new ArrayList<>();

    private static LinkedHashMap<Track,ArrayList<Polyline>> tracksLines =  new LinkedHashMap<>();

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sharedPref = getPreferences(MODE_PRIVATE);


        updateTimer = new CountDownTimer(3 * 1000, 3 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                updateData();
            }
        };

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new TrackPagerAdapter(getSupportFragmentManager()));


        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingLayout.hidePanel();
        txtItemTitle = (TextView) findViewById(R.id.txt_item_title);
        txtItemSnippet = (TextView) findViewById(R.id.txt_item_snippet);
          txtItemSnippet = (TextView) findViewById(R.id.txt_item_snippet);
        imgDirections = (ImageView) findViewById(R.id.img_directions);
        imgStreetView = (ImageView) findViewById(R.id.img_streetview);
        imgOpenWWW = (ImageView) findViewById(R.id.img_open_www);
        // loadFilter();
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);
        //   multiSelectSpinner = (MultiSelectSpinner) findViewById(R.id.multiselectSpinner);

//        multiSelectSpinner
////                .setListAdapter(adapter, "All Types Selected", "Select Feature Type(s)",MapsActivity.this)
//                .setSelectAll(false)
//                .setMinSelectedItems(0);
        Bitmap markerRedBitmap;
        Bitmap markerBlueBitmap;
        Bitmap markerGreenBitmap;
        int px = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);

        markerRedBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvasRed = new Canvas(markerRedBitmap);
        Drawable shapeRed = getResources().getDrawable(R.drawable.circle_red);
        shapeRed.setBounds(0, 0, markerRedBitmap.getWidth(), markerRedBitmap.getHeight());
        shapeRed.draw(canvasRed);

        markerBlueBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvasBlue = new Canvas(markerBlueBitmap);
        Drawable shapeBlue = getResources().getDrawable(R.drawable.circle_blue);
        shapeBlue.setBounds(0, 0, markerBlueBitmap.getWidth(), markerBlueBitmap.getHeight());
        shapeBlue.draw(canvasBlue);

        markerGreenBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvasGreen = new Canvas(markerGreenBitmap);
        Drawable shapeGreen = getResources().getDrawable(R.drawable.circle_green);
        shapeGreen.setBounds(0, 0, markerGreenBitmap.getWidth(), markerGreenBitmap.getHeight());
        shapeGreen.draw(canvasGreen);

        markerBitmaps.put(WikiPlace.class, markerBlueBitmap);
        markerBitmaps.put(GNPlace.class, markerRedBitmap);
        markerBitmaps.put(APlace.class, markerRedBitmap);
        //markerBitmaps.put(OSMPlace.class, markerGreenBitmap);
        markerBitmaps.put(OSMNode.class, markerGreenBitmap);

        markerColors.put(WikiPlace.class, BitmapDescriptorFactory.HUE_BLUE);
        markerColors.put(GNPlace.class, BitmapDescriptorFactory.HUE_RED);
        markerColors.put(APlace.class, BitmapDescriptorFactory.HUE_RED);
        // markerColors.put(OSMPlace.class,  BitmapDescriptorFactory.HUE_GREEN);
        markerColors.put(OSMNode.class, BitmapDescriptorFactory.HUE_GREEN);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #map} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap() {
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        map.setMyLocationEnabled(true);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
        map.animateCamera(zoom);
        centerMapOnMyLocation();
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);
        //set making a new request when visible region is changed
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("geoplaces", "map camera change");
                updateTimer.cancel();
                updateTimer.start();
            }
        });


    }

    private void centerMapOnMyLocation() {
        map.setMyLocationEnabled(true);

        Location location = map.getMyLocation();
        if (location != null) {

            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                            location.getLongitude()));
            map.moveCamera(center);

            if (!init) {
                init = true;
                //updateData();
                updateTracks();
            }

        } else {
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if (!init || followLoc) {

                        CameraUpdate center =
                                CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                                        location.getLongitude()));
                        map.moveCamera(center);
                        if (!init) {
                            init = true;
                            //updateData();
                            updateTracks();
                        }
                    }
                    //
                }
            });

        }
    }

    private void updateData() {
        updateDataWiki();
        //updateDataGn();
       // updateDataOSM();
    }

    private void updateDataWiki() {
        processingWki = true;
        //make a new initial request for the visible region
        MyJus.getRequestQueue().add(

                new GeonamesWikiBBSearchRequest(map.getProjection().getVisibleRegion().latLngBounds,
                        new Response.Listener<JsonObject>() {
                            @Override
                            public void onResponse(JsonObject entries) {
                                lastUpdate = SystemClock.elapsedRealtime();
                                processingWki = false;
                                JsonArray results = entries.optJsonArray("geonames");
                                if (results != null) {
                                    for (JsonElement je : results) {
                                        WikiPlace place = new WikiPlace().wrap(je);
                                        addMarker(place);
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(JusError jusError) {
                                processingWki = false;
                                String errMsg = jusError.getMessage();
                                errMsg = "Error: No Network";
                                Toast.makeText(MapsActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                            }
                        }));

    }

    private void updateDataOSM() {
        processingOSM = true;
        //make a new initial request for the visible region
        MyJus.getRequestQueue().add(
                new OsmOverpassRequest(null, map.getProjection().getVisibleRegion().latLngBounds,
                        new Response.Listener<JsonObject>() {
                            @Override
                            public void onResponse(JsonObject entries) {
                                lastUpdate = SystemClock.elapsedRealtime();
                                processingOSM = false;
                                JsonElement pois = entries.opt("elements");
                                if (pois != null) {
                                    JsonArray results = pois.asJsonArray();
                                    if (results != null) {
                                        for (JsonElement je : results) {
                                            OSMNode place = new OSMNode().wrap(je);
                                            addMarker(place);
                                        }
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(JusError jusError) {
                                processingOSM = false;
                                String errMsg = jusError.getMessage();
                                errMsg = "Error: No Network";
                                Toast.makeText(MapsActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                            }
                        }
                ));

    }

    private void updateDataGn() {
        //if(true) return;
        processingGn = true;
        //make a new initial request for the visible region
        MyJus.getRequestQueue().add(
                new GeonamesSearchJsonRequest(null, map.getProjection().getVisibleRegion().latLngBounds,
                        new Response.Listener<JsonObject>() {
                            @Override
                            public void onResponse(JsonObject entries) {
                                lastUpdate = SystemClock.elapsedRealtime();
                                processingGn = false;
                                JsonArray results = entries.optJsonArray("geonames");
                                if (results != null) {
                                    for (JsonElement je : results) {
                                        GNPlace gnPlace = new GNPlace().wrap(je);
                                        addMarker(gnPlace);
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(JusError jusError) {
                                processingGn = false;
                                String errMsg = jusError.getMessage();
                                errMsg = "Error: No Network";
                                Toast.makeText(MapsActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                            }
                        }));

    }

    private void updateTracks() {
        JsonArray pois = LocalWays.get().getPois();
        for (JsonElement je:pois) {

            APlace place = new APlace().wrap(je);
            if(place.getLat()==null) continue;
            Marker mm = map.addMarker(new MarkerOptions().position(
                            new LatLng(place.getLat(), place.getLon()))
                            .title(place.getTitle())
                            .snippet(place.getSummary())
                            .icon(BitmapDescriptorFactory.fromBitmap(markerBitmaps.get(place.getClass())))

            );
            markers.put(place.getId(), mm);
            placesData.put(mm.getId(), place);
        }

        JsonArray entries = LocalWays.get().getWays();
        for (JsonElement je : entries) {
            addTrack(je.asJsonObject());

        }
//        MyJus.getRequestQueue().add(new TracksRequest(
//                new Response.Listener<JsonArray>() {
//                    @Override
//                    public void onResponse(JsonArray entries) {
//                        for(JsonElement je : entries) {
//                            addTrack(je.asJsonObject().getJsonArray("features"));
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(JusError jusError) {
//                Toast.makeText(MapsActivity.this, "Error Gettings tracks", Toast.LENGTH_SHORT).show();
//            }
//        }));

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("pos", "pos change:" + lastSelTrack + " :: " + position);
                if(lastSelTrack>-1) {
                    ArrayList<Polyline> polylines = MapsActivity.tracksLines.get(MapsActivity.tracks.get(MapsActivity.lastSelTrack));
                    for(Polyline poly:polylines) {
                        poly.setColor(Color.GRAY);
                    }
                }
                Log.d("pos", "pos change:" + lastSelTrack + " :: " + position);

                ArrayList<Polyline> polylines = MapsActivity.tracksLines.get(MapsActivity.tracks.get(position));
                for(Polyline poly:polylines) {
                    poly.setColor(Color.RED);
                }
                Log.d("pos", "pos change:" + lastSelTrack + " :: " + position);
                lastSelTrack = position;
                Log.d("pos", "pos change:" + lastSelTrack + " :: " + position);

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void addMarker(Place place) {
        if (!markers.containsKey(place.getId())) {
            // Log.d("GeoPlaces", "adding place: " + place);
            Marker mm = map.addMarker(new MarkerOptions().position(
                            new LatLng(place.getLat(), place.getLon()))
                            .title(place.getTitle())
                            .snippet(place.getSummary() + "\n" + generateSnippet(place.getJson()))
                            .icon(BitmapDescriptorFactory.fromBitmap(markerBitmaps.get(place.getClass())))

            );

            markers.put(place.getId(), mm);
            placesData.put(mm.getId(), place);
        }
    }

    private void addTrack(JsonObject jobTrack) {
        JsonArray track = jobTrack.getJsonArray("features");
        Track ttrack = new Track().wrap(jobTrack.asJsonObject());
        tracks.add(ttrack);

        ArrayList<Polyline> polylines =  new ArrayList<>();

        for (JsonElement je : track) {
            if(!je.asJsonObject().getJsonObject("geometry").getString("type").equals("LineString")) {
                continue;
            }
            PolylineOptions polylineOptions = new PolylineOptions();
            JsonArray coords = je.asJsonObject().getJsonObject("geometry").getJsonArray("coordinates");

            for (JsonElement jee : coords) {
                polylineOptions.add(new LatLng(jee.asJsonArray().getDouble(1), jee.asJsonArray().getDouble(0)));
            }
            polylineOptions.width(10)
                    .color(Color.GRAY)
                    .geodesic(true);
            Polyline line = map.addPolyline(polylineOptions);
            polylines.add(line);
        }

        tracksLines.put(ttrack, polylines);
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    private String generateSnippet(JsonObject jsonObject) {
        StringBuilder res = new StringBuilder();

        Iterator<Map.Entry<String, JsonElement>> it = jsonObject.iterator();
        while (it.hasNext()) {
            Map.Entry<String, JsonElement> ff = it.next();
            if (!ff.getKey().startsWith("lat")
                    && !ff.getKey().startsWith("lon")
                    ) {
                if(ff.getKey().equals("tags")) {
                    res.append(generateSnippet(ff.getValue().asJsonObject()));
                } else {
                    res.append(ff.getKey() + ": " + ff.getValue() + "\n");
                }

            }
        }
        return res.toString();
    }


    @Override
    protected void onStop() {
        // saveFilter();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //  Log.d("geoplaces", "onDestroy");
        //MyJus.destroy();

        // markers.clear();
        map.clear();
        map = null;
        super.onDestroy();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        slidingLayout.showPanel();
        final Place place = placesData.get(marker.getId());


        if (lastMarker != null) {
            lastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(markerBitmaps.get(placesData.get(lastMarker.getId()).getClass())));
        }
        lastMarker = marker;

        txtItemTitle.setText(marker.getTitle());
        txtItemSnippet.setText(marker.getSnippet());
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(markerColors.get(place.getClass())));

        if (place.getWikiLink() != null && !place.getWikiLink().isEmpty()) {
            imgOpenWWW.setVisibility(View.VISIBLE);
            imgOpenWWW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(place.getWikiLink()));
                    startActivity(browserIntent);
                }
            });
        } else {
            imgOpenWWW.setVisibility(View.INVISIBLE);
            imgOpenWWW.setOnClickListener(null);
        }
        final Location currLoc = map.getMyLocation();
        imgDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + currLoc.getLatitude() + "," + currLoc.getLongitude() + "&daddr=" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "&mode=walking"));
                startActivity(intent);
            }
        });

        imgStreetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent streetView = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.streetview:cbll=" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "&cbp=1,99.56,,1,-5.27&mz=21"));
                startActivity(streetView);
            }
        });

        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (lastMarker != null) {
            final Place lastPlace = placesData.get(lastMarker.getId());
            lastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(markerBitmaps.get(lastPlace.getClass())));
            lastMarker = null;
        }

        slidingLayout.hidePanel();
    }

    @Override
    public void onItemsSelected(boolean[] booleans) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    public class TrackPagerAdapter extends FragmentPagerAdapter {

        public TrackPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {

            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return tracks.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            Locale l = Locale.getDefault();
//            switch (position) {
//                case 0:
//                    return getString(R.string.title_section1).toUpperCase(l);
//                case 1:
//                    return getString(R.string.title_section2).toUpperCase(l);
//                case 2:
//                    return getString(R.string.title_section3).toUpperCase(l);
//            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private TextView txtTrackTitle;
        private TextView txtTrackDescription;

        private NetworkImageView imgTrackPic;

        private int pos;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                pos = getArguments().getInt(ARG_SECTION_NUMBER);
            }
        }

        @Override
        public  View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.track_info, container, false);

            txtTrackTitle = (TextView) rootView.findViewById(R.id.txt_track_title);
            txtTrackDescription = (TextView) rootView.findViewById(R.id.txt_track_desc);
            imgTrackPic = (NetworkImageView) rootView.findViewById(R.id.imgPic);

            //set track
            txtTrackTitle.setText(MapsActivity.tracks.get(pos).getTitle());
            txtTrackDescription.setText(MapsActivity.tracks.get(pos).getDescription());

                imgTrackPic.setImageUrl(MapsActivity.tracks.get(pos).getPic1(),
                        MyJus.getImageLoader());



            return rootView;
        }
    }
//
//    public class TrackPagerAdapter extends PagerAdapter {
//
//        ArrayList<Track> tracks;
//        public TrackPagerAdapter(ArrayList<Track> tracks) {
//            this.tracks = tracks;
//        }
//        /**
//         * Get a View that displays the data at the specified position in the data set.
//         *
//         * @param position The position of the item within the adapter's data set of the item whose view we want.
//         * @param pager The ViewPager that this view will eventually be attached to.
//         *
//         * @return A View corresponding to the data at the specified position.
//         */
//        public View getView(int position, ViewPager pager) {
//
//        }
//
//        /**
//         * Determines whether a page View is associated with a specific key object as
//         * returned by instantiateItem(ViewGroup, int).
//         *
//         * @param view Page View to check for association with object
//         * @param object Object to check for association with view
//         *
//         * @return true if view is associated with the key object object.
//         */
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//        @Override
//        public int getCount() {
//            return tracks.size();
//        }
//
//        /**
//         * Create the page for the given position.
//         *
//         * @param container The containing View in which the page will be shown.
//         * @param position The page position to be instantiated.
//         *
//         * @return Returns an Object representing the new page. This does not need
//         * to be a View, but can be some other container of the page.
//         */
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            ViewPager pager = (ViewPager) container;
//            View view = getView(position, pager);
//
//            pager.addView(view);
//
//            return view;
//        }
//
//        /**
//         * Remove a page for the given position.
//         *
//         * @param container The containing View from which the page will be removed.
//         * @param position The page position to be removed.
//         * @param view The same object that was returned by instantiateItem(View, int).
//         */
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object view) {
//            ((ViewPager) container).removeView((View) view);
//        }
//
//    }


}

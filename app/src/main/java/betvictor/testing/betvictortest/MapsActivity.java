package betvictor.testing.betvictortest;

import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import betvictor.testing.data.ConnectionDetect;
import betvictor.testing.data.Constants;
import betvictor.testing.data.DatabaseHandler;
import betvictor.testing.data.StreamingTweets;
import betvictor.testing.data.TweetData;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Class responsible to draw the google map
 */
public class MapsActivity extends ActionBarActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    //Declaration of variables. Same structure main Activity
    ArrayList<TweetData> data;
    private CountDownTimer countDownTimer;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    StreamingTweets streaming;
    private TextView timeRem;
    private TextView internetTw;
    ConnectionDetect connectionDetect;
    LinearLayout timeRefreshingLayout;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_bar)));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.background_bar));
        }
        timeRefreshingLayout = (LinearLayout) findViewById(R.id.linearlayout_time_refreshing);
        timeRem = (TextView) findViewById(R.id.time_remaining);
        internetTw = (TextView) findViewById(R.id.internet_text_view);
        //Declaration of the stream
        streaming = new StreamingTweets(this);
        streaming.getStream();
        data = new ArrayList<TweetData>();
        streaming.getTweets();
        //Check if there is connection
        connectionDetect = new ConnectionDetect(this);
        if (!connectionDetect.isConnectingToInternet()){
            internetTw.setVisibility(View.VISIBLE);
            timeRefreshingLayout.setVisibility(View.GONE);
        }else {
            internetTw.setVisibility(View.GONE);

        }
        //Declaration of the timer
        timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data = streaming.getTweets();
                        setUpMap();
                        resetCounterDown();
                        if (connectionDetect.isConnectingToInternet()) {
                            streaming.deleteTweets();
                            timeRefreshingLayout.setVisibility(View.VISIBLE);
                            internetTw.setVisibility(View.GONE);

                        } else {
                            timeRefreshingLayout.setVisibility(View.GONE);
                            internetTw.setVisibility(View.VISIBLE);
                        }

                    }
                });

            }
        };

        timer.schedule(timerTask, 0, Constants.TIME_REPEAT);
        //Setup the map
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
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.clear();
        for (int i = 0; i < data.size();i++) {
            double latit = Double.valueOf(data.get(i).getLatitude());
            double longi = Double.valueOf(data.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.tweeting_marker))
                    .position(new LatLng(latit, longi)).title(data.get(i).getAuthor()));


        }
    }

    /**
     * Method responsible for counter down
     */
    protected void resetCounterDown(){
        countDownTimer = new CountDownTimer(Constants.TIME_REPEAT, Constants.TIME_SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRem.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                timeRem.setText(getString(R.string.refreshing));
            }
        }.start();
    }
}

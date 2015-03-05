package betvictor.testing.betvictortest;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import betvictor.testing.data.ConnectionDetect;
import betvictor.testing.data.Constants;
import betvictor.testing.data.StreamingTweets;
import betvictor.testing.data.TweetData;

/**
 * Main Activity
 */

public class MainActivity extends ActionBarActivity {

//Declaration of variables
    ListView listView;
    ArrayList<TweetData> data;
    TweetsAdapter adapter;
    private CountDownTimer countDownTimer;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    StreamingTweets streaming;
    private TextView timeRem;
    private TextView internetTw;
    ConnectionDetect connectionDetect;
    LinearLayout timeRefreshingLayout;
//To split in versions
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //We declare the actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_bar)));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        //if version is Lollipop, we colour the status bar
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.background_bar));
        }
        //Definitions of elements
        timeRefreshingLayout = (LinearLayout) findViewById(R.id.linearlayout_time_refreshing);
        timeRem = (TextView) findViewById(R.id.time_remaining);
        internetTw = (TextView) findViewById(R.id.internet_text_view);
        //We declare the streaming responsible to get the tweets
        streaming = new StreamingTweets(this);
        streaming.getStream();
        data = new ArrayList<TweetData>();
        streaming.getTweets();
        listView = (ListView) findViewById(R.id.listView);
        adapter = new TweetsAdapter(this, data);
        listView.setAdapter(adapter);
        //We check if there is internet connection
        connectionDetect = new ConnectionDetect(this);
        if (!connectionDetect.isConnectingToInternet()){
            //we make visible/invisible the different items
            internetTw.setVisibility(View.VISIBLE);
            timeRefreshingLayout.setVisibility(View.GONE);
        }else {
            internetTw.setVisibility(View.GONE);

        }
        //We declare the timer responsible to refresh the list
            timerTask = new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //We get the tweets
                            data = streaming.getTweets();
                            adapter.notifyDataSetChanged();
                            //We refresh the adapter
                            adapter = new TweetsAdapter(MainActivity.this, data);
                            listView.setAdapter(adapter);
                            //We reset the counter down
                            resetCounterDown();
                            //We check again if there is internet connection
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
            //We run the timer
            timer.schedule(timerTask, 0, Constants.TIME_REPEAT);

    }


    /**
     * Method where the menu of the action bar is declared
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Method were we declare the actions for the items in the actionbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            return true;
        }
        //If button map is clicked, we open a new activity
        if (id == R.id.action_maps){
            streaming.stopStreaming();
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {

        super.onPause();


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        streaming = new StreamingTweets(this);
    }

    /**
     * Method to control de Counter Down
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

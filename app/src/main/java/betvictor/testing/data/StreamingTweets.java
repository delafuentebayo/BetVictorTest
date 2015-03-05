package betvictor.testing.data;

import android.content.Context;

import java.util.ArrayList;

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
 * Created by Jesus Miguel de la Fuente on 05/03/2015.
 * Class responsible to declare de stream and connect to twitter in order to get
 * the tweets from the stream api of Twitter
 */
public class StreamingTweets {
    //Declaration of variables
    TwitterStream twitterStream;
    //Declaration of database
    static DatabaseHandler db;
    ConfigurationBuilder cb;
    public StreamingTweets(Context ctx){
        db =  new DatabaseHandler(ctx);
        cb = new ConfigurationBuilder();
        //Declaration of the tokens from Twitter
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("1KRHMkEptfdlrDUc9xkVOpwvp");
        cb.setOAuthConsumerSecret("5ZPw3bxndZhNkInZS2S9N7ZCZnGSRsB1q1d4OxPbLM4Scl8U03");
        cb.setOAuthAccessToken("277733936-ReKCQP71AJ4ApIIaTZRdlRd08JQ8WJ3hEKpjmXnI");
        cb.setOAuthAccessTokenSecret("4JF6rSmeJI1wcoahFy5RYSOfEq78fuIPVzWbbcfhygK0U");

        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
    }

    /**
     * Method to declare the listener of the streaming
     */
    public void getStream(){


        StatusListener listener = new StatusListener() {

            @Override
            public void onException(Exception arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrubGeo(long arg0, long arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStallWarning(StallWarning warning) {

            }

            @Override
            public void onStatus(Status status) {

              //  User user = status.getUser();
                TweetData tweet = new TweetData(status);
                if (status.getGeoLocation()!=null){
                    db.addTweet(tweet);
                }


            }

            @Override
            public void onTrackLimitationNotice(int arg0) {
                // TODO Auto-generated method stub

            }

        };
        FilterQuery fq = new FilterQuery();
        //Words to search
        String keywords[] = {"me", "I"};

        fq.track(keywords);

        twitterStream.addListener(listener);
        twitterStream.filter(fq);

    }

    /**
     * We stop the streaming
     */
    public void stopStreaming(){
        twitterStream.cleanUp();
        twitterStream.shutdown();

    }

    /**
     * Method to get the tweets from the database
     * @return ArrayList of TweetData
     */
    public ArrayList<TweetData> getTweets(){
        return db.getAllTweets();
    }

    /**
     * Method to delete the tweets of database
     */
    public void deleteTweets(){
        db.deleteAll();
    }
}

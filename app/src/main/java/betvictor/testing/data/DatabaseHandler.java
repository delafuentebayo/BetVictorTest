package betvictor.testing.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Jesus Miguel de la Fuente on 04/03/2015.
 * Class to declare the database with the table
 * to store the tweets
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    //Declaration of variables for table name, fields, etc.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME= "tweetsManager";
    private static final String TABLE_TWEETS= "tweets";
    private static final String KEY_ID = "id";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_CREATEDAT= "createdAt";

    public DatabaseHandler(Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * We create the table
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TWEETS_TABLE = "CREATE TABLE " + TABLE_TWEETS+
                " ("+KEY_ID+" INTEGER PRIMARY KEY," +KEY_AUTHOR+" TEXT,"+
                KEY_LOCATION+" TEXT,"+ KEY_LATITUDE + " TEXT," + KEY_LONGITUDE+
                " TEXT,"+ KEY_CONTENT + " TEXT,"+ KEY_CREATEDAT + " TEXT)";
        db.execSQL(CREATE_TWEETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWEETS);
        onCreate(db);
    }

    /**
     * Method to add a tweet to the table
     * @param tweet
     */
    public void addTweet(TweetData tweet) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AUTHOR, tweet.getUser().getName()); // Contact Name
        values.put(KEY_CONTENT, tweet.getContent()); // Contact Phone
        values.put(KEY_LATITUDE, tweet.getLatitude());
        values.put(KEY_LONGITUDE, tweet.getLongitude());
        values.put(KEY_LOCATION, tweet.getLocation());
        values.put(KEY_CREATEDAT, tweet.getCreated());
        // Inserting Row
        db.insert(TABLE_TWEETS, null, values);
        db.close(); // Closing database connection
    }

    /**
     * We get all the tweets in the table
     * @return ArrayList of TweetData
     */
    public ArrayList<TweetData> getAllTweets() {
        ArrayList<TweetData> tweetList = new ArrayList<TweetData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TWEETS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TweetData tweet = new TweetData();
                //tweet.setID(Integer.parseInt(cursor.getString(0)));
               tweet.setAuthor(cursor.getString(1));
               tweet.setLocation(cursor.getString(2));
               tweet.setLatitude(cursor.getString(3));
               tweet.setLongitude(cursor.getString(4));
               tweet.setContent(cursor.getString(5));
               tweet.setCreated(cursor.getString(6));
                // Adding contact to list
               tweetList.add(tweet);
            } while (cursor.moveToNext());
        }

        // return contact list
        return tweetList;
    }

    /**
     * Method to delete all records of the table
     */
    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_TWEETS);
        db.close();
    }
}

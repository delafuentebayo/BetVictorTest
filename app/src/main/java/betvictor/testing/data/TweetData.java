package betvictor.testing.data;

import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.User;

/**
 * Created by Jesus Miguel de la Fuente on 04/03/2015.
 * Class to store a tweet
 */
public class TweetData {
    private User user;
    private String author;
    private String location;
    private String latitude;
    private String longitude;
    private String created;
    private long tweetId;
    private String content;

    public TweetData (){

    }
    public TweetData(Status status){
        user = status.getUser();
        author = user.getName();
        location = user.getLocation();
       // created= status.getCreatedAt();
        tweetId = status.getId();
        content = status.getText();
        GeoLocation geo = status.getGeoLocation();
        if(geo!=null){
            latitude = String.valueOf(geo.getLatitude());
            longitude = String.valueOf(geo.getLongitude());
        }
        created = status.getCreatedAt().toString();



    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

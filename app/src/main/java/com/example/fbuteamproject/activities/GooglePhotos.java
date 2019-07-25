//
//package com.codepath.apps.restclienttemplate.models;
//
//import android.text.format.DateUtils;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.parceler.Parcel;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Locale;
//
//// make parcelable
//

//@Parcel
//public class Album {
//
//    // list out the attributes
//    public String body;
//    public long uid;
//    public User user;
//    public String createdAt;
//    public String relativeTime;
//
//
//    public int numberLikes;
//    public int numberRetweets;
//    public boolean liked;
//    public boolean retweeted;
//
//    //like boolean
//    //integer for number of likes
//    // when u use on click you change setting when
//
//    // deserialize the JSON
//    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
//        Tweet tweet = new Tweet();
//
//        // extract the values from JSON
//        tweet.body = jsonObject.getString("text");
//        tweet.uid = jsonObject.getLong("id");
//        tweet.createdAt = jsonObject.getString("created_at");
//        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
//        tweet.relativeTime = getRelativeTimeAgo(tweet.createdAt);
//        tweet.numberLikes = jsonObject.getInt("favorite_count");
//        tweet.liked = jsonObject.getBoolean("favorited");
//        tweet.numberRetweets = jsonObject.getInt("retweet_count");
//        tweet.retweeted = jsonObject.getBoolean("retweeted");
//
//        return tweet;
//    }
//
//
//}
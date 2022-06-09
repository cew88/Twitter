package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import org.parceler.Parcel;

public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public JSONObject extendedEntities;
    public String displayUrl = "";
    public Integer replyCount;
    public Integer heartCount;
    public Integer retweetCount;
    public boolean favorited;
    public Long id;

    // Empty constructor needed by Parceler library
    public Tweet(){}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.heartCount = jsonObject.getInt("favorite_count");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.id = jsonObject.getLong("id");
        // Reply count only available to premium users
        // tweet.replyCount = jsonObject.getInt("reply_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));


        if (jsonObject.has("extended_entities")){
            tweet.extendedEntities = jsonObject.getJSONObject("extended_entities");
            JSONObject media = tweet.extendedEntities.getJSONArray("media").getJSONObject(0);
            tweet.displayUrl= String.format("%s:large", media.getString("media_url_https"));
        }

        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
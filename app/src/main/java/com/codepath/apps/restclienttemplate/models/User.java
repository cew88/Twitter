package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    public String name;
    public String screenName;
    public Integer followingCount;
    public Integer followerCount;
    public String profileImageUrl;
    public String description;
    public String bannerUrl;
    public Boolean defaultProf;
    public long id;

    //Empty constructor needed by Parceler library
    public User(){}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        user.defaultProf = jsonObject.getBoolean("default_profile");

        // Check if the user has updated a profile banner
        if (!user.defaultProf){
            user.bannerUrl = jsonObject.getString("profile_banner_url");
        }

        user.followingCount = jsonObject.getInt("friends_count");
        user.followerCount = jsonObject.getInt("followers_count");
        user.description = jsonObject.getString("description");
        user.id = jsonObject.getLong("id");
        return user;
    }
}
package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "ProfileActivity";

    ImageView ivProfileImage;
    ImageView ivBanner;
    ImageView ivCloseProf;
    TextView tvName;
    TextView tvScreenName;
    TextView tvFollowingCount;
    TextView tvFollowerCount;
    TextView tvDescription;
    public User user;
    private EndlessRecyclerViewScrollListener scrollListener;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        client = TwitterApp.getRestClient(this);

        user = (User) Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        ivProfileImage = findViewById(R.id.ivProfileImageProf);
        ivBanner = findViewById(R.id.ivBanner);
        ivCloseProf = findViewById(R.id.ivCloseProfile);
        tvName = findViewById(R.id.tvNameProf);
        tvScreenName = findViewById(R.id.tvScreenNameProf);
        tvFollowingCount = findViewById(R.id.tvFollowingCount);
        tvFollowerCount = findViewById(R.id.tvFollowerCount);
        tvDescription = findViewById(R.id.tvDescription);

        Glide.with(this).load(user.profileImageUrl).circleCrop().into(ivProfileImage);
        if (!user.defaultProf){
            Glide.with(this).load(user.bannerUrl).into(ivBanner);
        }

        tvName.setText(user.name);
        tvScreenName.setText("@" + user.screenName);
        tvFollowingCount.setText(user.followingCount.toString());
        tvFollowerCount.setText(user.followerCount.toString());
        tvDescription.setText(user.description);

        ivCloseProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Find the recycler view
        rvTweets = findViewById(R.id.rvTweetsProf);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // Initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        // Recycler view setup: layout manager and the adapter
        rvTweets.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(rvTweets.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.divider));
        rvTweets.addItemDecoration(divider);
        rvTweets.setAdapter(adapter);
        populateUserTimeline();

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
    }

    private void populateUserTimeline() {
        client.getUserTimeline(user.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure" + response, throwable);
            }
        });
    }

    public void loadNextDataFromApi(int page){
        // Send the network request to fetch the updated data
        client.getUserTimeline(page+1, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Endless scrolling success! " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "JSON exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "onFailure" + response, throwable);
            }
        });
    }
}
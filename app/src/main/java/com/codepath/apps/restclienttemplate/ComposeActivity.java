package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static int MAX_TWEET_LENGTH = 280;
    public static final String TAG  = "ComposeActivity";
    public String screenName;
    TwitterClient client = TwitterApp.getRestClient(this);
    TextInputEditText etCompose;
    Button btnTweet;
    ImageView closeCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        closeCompose = findViewById(R.id.ivCloseCompose);

        Intent intent = getIntent();
        screenName = intent.getExtras().getString("User");

        if (screenName.length() != 0){
            // setHint does not work
            // etCompose.setHint("Tweet your reply");
            etCompose.setText("Replying to @" + screenName.toString() + "\n");
        }
        else {
            etCompose.setText("");
        }

        // Set click listener on the button
        btnTweet.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                }
                // Make an API call to Twitter to publish the tweet
                // Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess!" + json.toString());
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            Tweet tweet = Tweet.fromJson(jsonObject);
                            Log.i(TAG, "Published Tweet: " + tweet);
                            Intent i = new Intent();
                            i.putExtra("Tweet", Parcels.wrap(tweet));
                            // Set result code and bundle data for response
                            setResult(RESULT_OK, i);
                            // Close the activity and pass to parent
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.i(TAG, "onFailure" + response, throwable);
                    }
                });
            }
        });

        // Set click listener on the close icon
        closeCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



}
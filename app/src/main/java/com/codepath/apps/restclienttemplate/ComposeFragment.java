package com.codepath.apps.restclienttemplate;

/*
Create a modal pop out for composing a new tweet. T
 */

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class ComposeFragment extends DialogFragment {

    public static int MAX_TWEET_LENGTH = 280;
    public static final String TAG  = "ComposeActivity";
    public String screenName;
    TwitterClient client = TwitterApp.getRestClient(getActivity());
    TextInputEditText etCompose;
    Button btnTweet;
    ImageView closeCompose;

    private EditText mEditText;

    public ComposeFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeFragment newInstance() {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etCompose = view.findViewById(R.id.etCompose);
        btnTweet = view.findViewById(R.id.btnTweet);
        closeCompose = view.findViewById(R.id.ivCloseCompose);

        String screenName = getArguments().getString("User");
        if (String.valueOf(screenName) != "null"){
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
                    Toast.makeText(getActivity(), "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(getActivity(), "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
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
                            // Return back to the timeline activity; refreshes the page
                            Intent i = new Intent();
                            startActivity(i);
                            // Close the activity and pass to parent
                            getDialog().dismiss();
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
                getDialog().dismiss();
            }
        });
    }
}
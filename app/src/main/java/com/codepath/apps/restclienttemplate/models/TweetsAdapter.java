package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.ComposeActivity;
import com.codepath.apps.restclienttemplate.ProfileActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{
    Context context;
    List<Tweet> tweets;
    TwitterClient client;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final String TAG = "TweetsAdapter";


    // Pass in the context and the list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);

        // Bind the tweet with the view holder
        try {
            holder.bind(tweet);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweetList){
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    // Define a viewholder
    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvName;
        TextView tvScreenName;
        TextView tvTimestamp;
        ImageView ivMedia;
        ImageView ivHeart;
        ImageView ivReply;
        TextView tvHeartCount;
        TextView tvRetweetCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivReply = itemView.findViewById(R.id.ivReply);
            ivHeart = itemView.findViewById(R.id.ivHeart);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            ivMedia.setVisibility(View.INVISIBLE);

            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvHeartCount = itemView.findViewById(R.id.tvHeartCount);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
        }

        public void bind(Tweet tweet) throws JSONException {
            tvBody.setText(tweet.body);
            tvName.setText(tweet.user.name);
            tvScreenName.setText("@" + tweet.user.screenName);
            tvTimestamp.setText(getRelativeTimeAgo(tweet.createdAt));
            tvHeartCount.setText(tweet.heartCount.toString());
            tvRetweetCount.setText(tweet.retweetCount.toString());

            Glide.with(context).load(tweet.user.profileImageUrl).circleCrop().into(ivProfileImage);

            // Embed images
            if (tweet.displayUrl != ""){
                ivMedia.setVisibility(View.VISIBLE);
                ivMedia.setClipToOutline(true);
                Glide.with(context).load(tweet.displayUrl).into(ivMedia);
            }
            else {
                ivMedia.setVisibility(View.GONE);
            }

            // Change heart icon if tweet is liked
            // Set the heart icon to empty by default
            ivHeart.setImageResource(R.drawable.heart);
            if (tweet.favorited){
                ivHeart.setImageResource(R.drawable.heart_filled);
            }

            client = TwitterApp.getRestClient(itemView.getContext());

            // Bug with heart counts updating
            ivHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!tweet.favorited){
                        client.likeTweet(tweet.id, new JsonHttpResponseHandler(){

                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.d("ItemClicked", "onSuccess!");
                                JSONObject updatedTweet = json.jsonObject;

                                try {
                                    tweet.favorited = updatedTweet.getBoolean("favorited");
                                    ivHeart.setImageResource(R.drawable.heart_filled);
                                    tweet.heartCount = updatedTweet.getInt("favorite_count");
                                    tvHeartCount.setText(tweet.heartCount.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.d("ItemClicked", "onFailure: " + throwable);
                            }
                        });
                    }
                    else {
                        client.unlikeTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                JSONObject updatedTweet = json.jsonObject;
                                Log.d("ItemClicked", "onSuccess!");

                                try {
                                    tweet.favorited = updatedTweet.getBoolean("favorited");
                                    ivHeart.setImageResource(R.drawable.heart);
                                    tweet.heartCount = updatedTweet.getInt("favorite_count");
                                    tvHeartCount.setText(tweet.heartCount.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.d("ItemClicked", "onFailure: " + throwable);
                            }
                        });
                    }
                }
            });

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), ProfileActivity.class);
                    i.putExtra("User", Parcels.wrap(tweet.user));
                    context.startActivity(i);
                }
            });

            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), ComposeActivity.class);
                    i.putExtra("User", tweet.user.screenName.toString());
                    context.startActivity(i);
                }
            });
        }

        // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                long time = sf.parse(rawJsonDate).getTime();
                long now = System.currentTimeMillis();

                final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    return "just now";
                } else if (diff < 2 * MINUTE_MILLIS) {
                    return "a minute ago";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + " m";
                } else if (diff < 90 * MINUTE_MILLIS) {
                    return "an hour ago";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + " h";
                } else if (diff < 48 * HOUR_MILLIS) {
                    return "yesterday";
                } else {
                    return diff / DAY_MILLIS + " d";
                }
            } catch (ParseException e) {
                Log.i(TAG, "getRelativeTimeAgo failed");
                e.printStackTrace();
            }

            return "";
        }
    }
}

package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {
    ImageView ivProfileImage;
    ImageView ivCloseProf;
    TextView tvName;
    TextView tvScreenName;
    TextView tvFollowingCount;
    TextView tvFollowerCount;
    public User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        user = (User) Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        Log.d("here", String.valueOf(user == null));

        ivProfileImage = findViewById(R.id.ivProfileImageProf);
        ivCloseProf = findViewById(R.id.ivCloseProfile);
        tvName = findViewById(R.id.tvNameProf);
        tvScreenName = findViewById(R.id.tvScreenNameProf);
        tvFollowingCount = findViewById(R.id.tvFollowingCount);
        tvFollowerCount = findViewById(R.id.tvFollowerCount);

        Glide.with(this).load(user.profileImageUrl).circleCrop().into(ivProfileImage);
        tvName.setText(user.name);
        tvScreenName.setText("@" + user.screenName);
        tvFollowingCount.setText(user.followingCount.toString());
        tvFollowerCount.setText(user.followerCount.toString());

        ivCloseProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
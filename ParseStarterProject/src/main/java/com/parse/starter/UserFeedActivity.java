package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity {

    // which intent is being clicked

    // username selected
    String activeUsername = "";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.chat_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // if item selected is share
        if(item.getItemId() == R.id.chat) {

            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("username", activeUsername);

            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        // which intent is being clicked
        Intent intent = getIntent();

        // username selected
        activeUsername = intent.getStringExtra("username");

        // setting title
        setTitle(activeUsername + "'s Feed");

        // get image posted by user
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image"); // Image class

        // we find all the images by user
        query.whereEqualTo("username", activeUsername);

        // newest on top
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {

                    if (list.size() > 0) {

                        for (ParseObject object : list) {

                            // get image from server
                            ParseFile file = (ParseFile) object.get("image");

                            // download image
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, ParseException e) {

                                    if (e == null && bytes != null) {

                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                        // creating Imageview not finding it
                                        ImageView imageView = new ImageView(getApplicationContext());

                                        imageView.setLayoutParams(new ViewGroup.LayoutParams(

                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT

                                        ));

                                        imageView.setImageBitmap(bitmap);

                                        linearLayout.addView(imageView);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity
{

    public void getPhoto() {

        Log.i("Photo", "getPhoto");

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("Photo", "onRequestPermissionsResult");


                getPhoto();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.share_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // if item selcted is share
        if(item.getItemId() == R.id.share) {

            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                Log.i("Photo", "onOptionsItemSelected");

                // Goes to onRequestPermissionsResult function
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

            else {
                getPhoto();
            }

        }
        // if item selected is logout
        else if(item.getItemId() == R.id.logout) {

            ParseUser.logOut();

            // jump back to main acitivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();

            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Log.i("Photo", "onActivityResult");

                // Store in Parse Server
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("image.png", byteArray);

                ParseObject object = new ParseObject("Image");

                object.put("image", file);

                object.put("username", ParseUser.getCurrentUser().getUsername());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if(e == null)
                            Toast.makeText(UserListActivity.this, "Image Shared!", Toast.LENGTH_SHORT).show();

                        else
                            Toast.makeText(UserListActivity.this, "Image could not be shared! Please try again.", Toast.LENGTH_SHORT).show();
                    }

                });
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("Socio");

        // declare as final to access them from inner class
        final ArrayList<String> usernames = new ArrayList<>();

        final ListView userListView = (ListView) findViewById(R.id.userListView);

        final ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usernames);

        userListView.setAdapter(arrayAdapter);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                // which user has clicked
                intent.putExtra("username", usernames.get(i));
                startActivity(intent);
            }
        });

        // taking data from parse Server
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        // display everyone apart from our user
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> list, ParseException e)
            {

                if(e == null)
                {
                    if(list.size() > 0)
                    {
                        for (ParseUser parseUser : list)
                            usernames.add(parseUser.getUsername());
                    }

                    arrayAdapter.notifyDataSetChanged();
                }

                else
                    e.printStackTrace();
            }
        });
    }
}
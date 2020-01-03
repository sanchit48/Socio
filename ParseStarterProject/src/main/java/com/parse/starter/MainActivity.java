/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.w3c.dom.Text;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    boolean signupModeActive = true;
    TextView changeSignupModeTextView;
    EditText passwordEditText;


    public void showUserList() {

        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    // if text or, login clicked
    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.changeSignupModeTextView)
        {
            Button signupButton = (Button) findViewById(R.id.signupButton);

            if(signupModeActive)
            {
                signupModeActive = false;
                signupButton.setText("Login");
                changeSignupModeTextView.setText("Or, Signup");
            }

            else
            {
                signupModeActive = true;
                signupButton.setText("Signup");
                changeSignupModeTextView.setText("Or, Login");
            }

            Log.i("AppInfo", "Change signup mode");
        }

        else if(view.getId() == R.id.backgroundConstraintLayout || view.getId() == R.id.logoImageView)
        {
            Log.i("key", "Yes");

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    // when button clicked
    public void signUp(View view)
    {

      EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);

      if(usernameEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals(""))
          Toast.makeText(this, "A username and password is required.", Toast.LENGTH_LONG).show();

      else {

          if(signupModeActive)
          {
              // make new user
              ParseUser user = new ParseUser();
              // setting username and password to server
              user.setUsername(usernameEditText.getText().toString());
              user.setPassword(passwordEditText.getText().toString());

              user.signUpInBackground(new SignUpCallback()
              {
                  @Override
                  public void done(ParseException e)
                  {
                      if (e == null)
                      {
                          Log.i("sign", "Successful");
                          showUserList();
                      }

                      else {
                          String message = e.getMessage();

                          // if java error is before the needed message
                          if(message.toLowerCase().contains("java")) {

                              message = e.getMessage().substring(e.getMessage().indexOf(" "));

                          }

                          Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                      }
                  }
              });
          }

          else {

              // Log in directly
              ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback()
              {
                  @Override
                  public void done(ParseUser parseUser, ParseException e)
                  {
                      if(parseUser != null)
                      {
                          Log.i("Signup", "Successful");
                          showUserList();
                      }

                      else {

                          String message = e.getMessage();

                          // if java error is before the needed message
                          if(message.toLowerCase().contains("java")) {

                              message = e.getMessage().substring(e.getMessage().indexOf(" "));

                          }

                          Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                      }
                  }
              });
          }
      }
    }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Socio");

        changeSignupModeTextView = (TextView)findViewById(R.id.changeSignupModeTextView);

        changeSignupModeTextView.setOnClickListener(this);

        ConstraintLayout backgroundConstraintLayout = (ConstraintLayout) findViewById(R.id.backgroundConstraintLayout);

        ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);

      backgroundConstraintLayout.setOnClickListener(this);

        logoImageView.setOnClickListener(this);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);


        // different from course
        // By pressing enter on keyboard, automatically button is pressed
        passwordEditText.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent)
            {

                if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                    signUp(view);

                return false;
            }
        });

        // if login already, go to userListActivity
        if(ParseUser.getCurrentUser() != null)
        {
            showUserList();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }
}
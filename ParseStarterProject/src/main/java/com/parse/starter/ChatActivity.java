package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    String activeUser = "";

    ArrayList<String> messages = new ArrayList<>();

    ArrayAdapter arrayAdapter;

    public void sendChat(View view) {

        final EditText chatEditText = (EditText) findViewById(R.id.chatEditText);

        // Making new object like images, user etc
        ParseObject message = new ParseObject("Message");

        final String messageContent = chatEditText.getText().toString();

        if(messageContent.equals(""))
            return;

        // me
        message.put("sender", ParseUser.getCurrentUser().getUsername());

        // person I am chatting with
        message.put("recipient", activeUser);

        message.put("message", messageContent);

        chatEditText.setText("");

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if(e == null) {

                    messages.add(messageContent);

                    arrayAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        activeUser = intent.getStringExtra("username");

        setTitle(activeUser);

        ListView chatListView = (ListView) findViewById(R.id.chatListView);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);

        chatListView.setAdapter(arrayAdapter);

        ParseQuery<ParseObject> query1 = new ParseQuery<>("Message");

        query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipient", activeUser);

        ParseQuery<ParseObject> query2 = new ParseQuery<>("Message");

        query2.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender", activeUser);

        // combine both queries
        List<ParseQuery<ParseObject>> queries = new ArrayList<>();

        queries.add(query1);
        queries.add(query2);

        // one set is true or another set is true
        ParseQuery<ParseObject> query = ParseQuery.or(queries);

        query.orderByAscending("createdAt");

        Log.i("in", "Error");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if(e == null) {

                    if(list.size() > 0) {

                        messages.clear();

                        for(ParseObject message: list) {

                            String messageContent = message.getString("message");

                            // if sender is not our user
                            if(!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {

                                messageContent = "> " + messageContent;

                            }

                            messages.add(messageContent);

                        }

                        arrayAdapter.notifyDataSetChanged();

                    }

                }

            }
        });




    }
}

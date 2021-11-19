package com.osmangul.chatbotsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.osmangul.chatbotsample.adapter.MessageAdapter;
import com.osmangul.chatbotsample.models.MessageModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";
    private RequestQueue mRequestQueue;

    private ArrayList<MessageModel> messageList;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue.getCache().clear();
        messageList = new ArrayList<>();
        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userMsgEdt.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(userMsgEdt.getText().toString());
                userMsgEdt.setText("");
            }
        });
        messageAdapter = new MessageAdapter(messageList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                // Call smooth scroll
                if (messageAdapter.getItemCount()>2)
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });
        recyclerView.setAdapter(messageAdapter);
    }

    private void sendMessage(String userMsg) {

        messageList.add(new MessageModel(userMsg, USER_KEY));
        messageAdapter.notifyDataSetChanged();

        String url = "http://api.brainshop.ai/get?bid=161480&key=35nDezZGaDQ3zia5&uid=[uid]&msg=" + userMsg;

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // in on response method we are extracting data
                    // from json response and adding this response to our array list.
                    String botResponse = response.getString("cnt");
                    messageList.add(new MessageModel(botResponse, BOT_KEY));

                    // notifying our adapter as data changed.
                    messageAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                    // handling error response from bot.
                    messageList.add(new MessageModel("No response", BOT_KEY));
                    messageAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                messageList.add(new MessageModel("Sorry no response found", BOT_KEY));
                Log.i("errorssss",error.toString());
                Toast.makeText(MainActivity.this, "No response from the bot..", Toast.LENGTH_SHORT).show();
            }
        });

        // at last adding json object
        // request to our queue.
        queue.add(jsonObjectRequest);
    }
}

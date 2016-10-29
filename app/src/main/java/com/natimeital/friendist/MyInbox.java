package com.natimeital.friendist;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Moti on 23/05/2016.
 */

public class MyInbox extends Activity {

    ListView inboxLV, sentMessegesLV;
    String usernameFromLoginGlobal;
    int FETCH_MODE;
    SharedPreferences sp;
    MessageDetailsAdapter inboxAdapter, sentMessagesAdapter;
    ArrayList<MessageObject> inboxArray, outboxArray;
    ImageButton backBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinbox);

        inboxLV = (ListView) findViewById(R.id.incoming_LV_inbox);
        sentMessegesLV = (ListView) findViewById(R.id.sentmesseges_LV_inbox);
        backBTN = (ImageButton) findViewById(R.id.back_BTN_inbox);


        sp = getSharedPreferences("username", MODE_PRIVATE);
        final String usernameFromLogin = sp.getString("username", "");
        usernameFromLoginGlobal = usernameFromLogin;
        FETCH_MODE = 1;
        inboxArray = new ArrayList<>();
        inboxAdapter = new MessageDetailsAdapter(MyInbox.this, inboxArray);
        outboxArray = new ArrayList<>();
        sentMessagesAdapter = new MessageDetailsAdapter(MyInbox.this, outboxArray);
        new UpdateInbox().execute("received");


        //Start the notification service if its not running!

            new StartTheService().execute();
        Log.d("ServiceLog", "Service Started");


        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInbox.this, MyProfile.class);
                intent.putExtra("username", usernameFromLogin);
                startActivity(intent);

                finish();
            }
        });
        inboxLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageObject messageObject = (MessageObject) parent.getItemAtPosition(position);
                Bundle args = new Bundle();
                args.putString("receivingUsername", messageObject.getSendingUser()); // receiving user will be the one who sent
                args.putString("sendingUsername", messageObject.getRecievingUser());
                args.putString("replyfragment", "reply");

                SendMessageFragment newFragment = new SendMessageFragment();

                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "TAG");
            }
        });
        sentMessegesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageObject messageObject = (MessageObject) parent.getItemAtPosition(position);
                Bundle args = new Bundle();
                args.putString("receivingUsername", messageObject.getRecievingUser()); // receiving user will be the one who got the message
                args.putString("sendingUsername", messageObject.getSendingUser());
                args.putString("replyfragment", "reply");

                SendMessageFragment newFragment = new SendMessageFragment();

                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "TAG");
            }
        });

    }


    class UpdateInbox extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog = new ProgressDialog(MyInbox.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (FETCH_MODE == 1) {
                pDialog.setTitle(getString(R.string.searchUser_dialog));
                pDialog.setMessage(getString(R.string.searching_dialog_Text));
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if (FETCH_MODE == 1) {
                try {
                    String username = usernameFromLoginGlobal;
                    String command = params[0];
                    Log.d("INBOXDEBUG", "Command-->" + command);
                    String link = "http://www.ratedapp.net/nati/getinbox.php";
                    String data = URLEncoder.encode("command", "UTF-8") + "=" + URLEncoder.encode(command, "UTF-8");
                    data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");

                    Log.d("INBOXDEBUG", "Data-->" + data);

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(data);
                    writer.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuilder sb = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        Log.d("INBOXDEBUG", "The Line-->" + sb.toString());
                        break;
                    }

                    return sb.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (FETCH_MODE == 2) {
                try {
                    String username = usernameFromLoginGlobal;
                    String command = params[0];
                    Log.d("INBOXDEBUG", "Command-->" + command);
                    String link = "http://www.ratedapp.net/nati/getinbox.php";
                    String data = URLEncoder.encode("command", "UTF-8") + "=" + URLEncoder.encode(command, "UTF-8");
                    data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");

                    Log.d("INBOXDEBUG", "Data-->" + data);

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(data);
                    writer.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuilder sb = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        Log.d("INBOXDEBUG", "The Line-->" + sb.toString());
                        break;
                    }

                    return sb.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String result = s;
            Log.d("INBOXDEBUG", "The s-->" + s);
            if (FETCH_MODE == 1) {
                try {
                    inboxAdapter.clear();
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        MessageObject messages = new MessageObject();
                        messages.recievingUser = jObject.getString("recievingUser");
                        messages.sendingUser = jObject.getString("sendingUser");
                        messages.theMessege = jObject.getString("theMessege");
                        messages.thedate = jObject.getString("date");
                        inboxAdapter.add(messages);

                    }
                    inboxLV.setAdapter(inboxAdapter);
                    pDialog.dismiss();
                    FETCH_MODE = 2;
                    new UpdateInbox().execute("sent");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (FETCH_MODE == 2) {
                try {
                    sentMessagesAdapter.clear();
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        MessageObject messages = new MessageObject();
                        messages.recievingUser = jObject.getString("recievingUser");
                        messages.sendingUser = jObject.getString("sendingUser");
                        messages.theMessege = jObject.getString("theMessege");
                        messages.thedate = jObject.getString("date");
                        sentMessagesAdapter.add(messages);

                    }
                    sentMessegesLV.setAdapter(sentMessagesAdapter);
                    pDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    class StartTheService extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Intent intent = new Intent(MyInbox.this, InboxNotificationsService.class);
            startService(intent);
            Log.d("ServiceLog", "the line after startService(intent)");

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        new UtilMethods().exitMessage(this);
    }
}


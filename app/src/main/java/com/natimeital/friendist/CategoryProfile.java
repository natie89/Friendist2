package com.natimeital.friendist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
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
 * Created by User on 16/08/2016.
 */
public class CategoryProfile extends Activity {

    TextView categoryName, peopleWhoTakeInterest_TV, joinCat_TV, leaveCat_TV;
    ImageButton joinCat_BTN, leaveCat_BTN;
    ProgressBar loading_PB;
    String categoryStringName, loggedInUSER;
    ListView registeredUsersLV;
    SearchUserListAdapter listAdapter;
    ArrayList<SearchableUserDetails> usersArrayList;
    SharedPreferences sp;
    final String TAG = "CategoryProfile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_profile);

        //init
        categoryName = (TextView) findViewById(R.id.categoryName_TV_categoryProfile);
        joinCat_TV = (TextView) findViewById(R.id.joinCategory_TV_categoryProfile);
        leaveCat_TV = (TextView) findViewById(R.id.leaveCategory_TV_categoryProfile);
        peopleWhoTakeInterest_TV = (TextView) findViewById(R.id.peopleWhoTakeInterest_TV_categoryProfile);
        loading_PB = (ProgressBar) findViewById(R.id.loadingAnimation_categoryProfile);
        registeredUsersLV = (ListView) findViewById(R.id.usersRegisteredToCategory_LV_categoryProfile);
        joinCat_BTN = (ImageButton) findViewById(R.id.joinCategory_IB_categoryProfile);
        leaveCat_BTN = (ImageButton) findViewById(R.id.leaveCategory_IB_categoryProfile);

        //get logged in username
        sp = getSharedPreferences("username", MODE_PRIVATE);
        loggedInUSER = sp.getString("username2", "");
        Log.d(TAG, "The logged in username--> " + loggedInUSER);

        //first visibility
        categoryName.setVisibility(View.GONE);
        peopleWhoTakeInterest_TV.setVisibility(View.GONE);
        registeredUsersLV.setVisibility(View.GONE);
        joinCat_TV.setVisibility(View.INVISIBLE);
        joinCat_BTN.setVisibility(View.INVISIBLE);
        leaveCat_TV.setVisibility(View.INVISIBLE);
        leaveCat_BTN.setVisibility(View.INVISIBLE);
        loading_PB.setVisibility(View.VISIBLE);

        //join category button
        joinCat_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckIfLoggedUserIsRegisteredToCategory().execute("join");

            }
        });

        leaveCat_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckIfLoggedUserIsRegisteredToCategory().execute("leave");
            }
        });

        //get category from search category
        Bundle extras = getIntent().getExtras();
        categoryStringName = extras.getString("category");
        Log.d(TAG, "the category name passed from search -->" + categoryStringName);


        //listview

        usersArrayList = new ArrayList<>();
        listAdapter = new SearchUserListAdapter(this, usersArrayList);
        registeredUsersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchableUserDetails theUserSelected = (SearchableUserDetails) registeredUsersLV.getItemAtPosition(position);
                Intent intent = new Intent(CategoryProfile.this, UserProfile.class);
                intent.putExtra("username", theUserSelected.getUsername());
                startActivity(intent);
                finish();
            }
        });
        //Fetching category profile
        new FetchUsersRegistered().execute(categoryStringName);


    }

    public class FetchUsersRegistered extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String command = "fetch";
                String link = "http://www.ratedapp.net/nati/fetchcategoryusers.php";
                String category = params[0];
                String data = URLEncoder.encode("category", "UTF-8") + "=" + URLEncoder.encode(category, "UTF-8");
                data += "&" + URLEncoder.encode("command", "UTF-8") + "=" + URLEncoder.encode(command, "UTF-8");

                Log.d(TAG, "The data--> " + data);

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
                    Log.d(TAG, "The line--> " + line);

                    break;
                }
                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //  super.onPostExecute(s);
            Log.d(TAG, "The s---> " + s);

            try {
                listAdapter.clear();
                Log.d(TAG, "adapter cleared");
                JSONArray jsonArray = new JSONArray(s);
                Log.d(TAG, "array created");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SearchableUserDetails searchableUserDetails = new SearchableUserDetails();
                    searchableUserDetails.username = jsonObject.getString("username");
                    searchableUserDetails.firstName = jsonObject.getString("firstname");
                    searchableUserDetails.lastName = jsonObject.getString("lastname");
                    searchableUserDetails.photoLink = jsonObject.getString("photo");
                    listAdapter.add(searchableUserDetails);
                }
                Log.d(TAG, "done fetching users");
                registeredUsersLV.setAdapter(listAdapter);

                loading_PB.setVisibility(View.GONE);
                categoryName.setText(categoryStringName);
                categoryName.setVisibility(View.VISIBLE);
                peopleWhoTakeInterest_TV.setVisibility(View.VISIBLE);
                registeredUsersLV.setVisibility(View.VISIBLE);
                //Check if user is related to category
                new CheckIfLoggedUserIsRegisteredToCategory().execute("check");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class CheckIfLoggedUserIsRegisteredToCategory extends AsyncTask<String, Void, String> {
        String theCommand;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                String loggedusername = loggedInUSER;
                Log.d(TAG, "the logged in user-->" + loggedusername);
                String category = categoryStringName;
                String command = params[0];
                theCommand = command;
                String link = "http://www.ratedapp.net/nati/fetchcategoryusers.php";
                String data = URLEncoder.encode("category", "UTF-8") + "=" + URLEncoder.encode(category, "UTF-8");
                data += "&" + URLEncoder.encode("command", "UTF-8") + "=" + URLEncoder.encode(command, "UTF-8");
                data += "&" + URLEncoder.encode("loggeduser", "UTF-8") + "=" + URLEncoder.encode(loggedusername, "UTF-8");

                Log.d(TAG, "The data from checking user belong to category-->\n" + data);

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
                    Log.d(TAG, "The line of check--> " + line);
                    break;
                }
                return sb.toString();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "the s of check--> " + s);


            if (theCommand.equals("check")) {
                if (s.equals("registered")) {
                    leaveCat_BTN.setVisibility(View.VISIBLE);
                    leaveCat_TV.setVisibility(View.VISIBLE);
                } else if (s.equals("not_registered")) {
                    joinCat_BTN.setVisibility(View.VISIBLE);
                    joinCat_TV.setVisibility(View.VISIBLE);
                }
            } else if(theCommand.equals("join")){

                Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
            }else if(theCommand.equals("leave")){

                Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
            }

        }
    }
}

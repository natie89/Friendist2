package com.natimeital.friendist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
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

public class SearchUser extends Activity {

    ListView usersList;
    EditText searchBar;
    ImageButton backBTN;
    ArrayList<SearchableUserDetails> returnedUsersArray, returnedUsersArray2;
    SearchUserListAdapter listAdapter, listAdapter2;
    String usernameString;
    private static int SEARCH_METHOD = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        //KEEPING THE USERNAME FOR REFRESH WHEN GOING BACK TO PROFILE
        Bundle extras = getIntent().getExtras();
        final String PASSED_USERNAME = extras.getString("current_username");
        usernameString = PASSED_USERNAME;
        Log.d("SearchUserSTRING", "username" + usernameString);

        usersList = (ListView) findViewById(R.id.users_LV_SearchUser);
        searchBar = (EditText) findViewById(R.id.search_ET_SearchUser);
        backBTN = (ImageButton) findViewById(R.id.back_BTN_SearchUser);


//          Listener to fetch users after clicking search button
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchUser.this, MyProfile.class);
                i.putExtra("username", PASSED_USERNAME);//BACK TO MYPROFILE WITH THE USERNAME
                startActivity(i);
                finish();
            }
        });
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchETtoString = searchBar.getText().toString();
                    SEARCH_METHOD = 1;
                    returnedUsersArray = new ArrayList<SearchableUserDetails>();
                    listAdapter = new SearchUserListAdapter(SearchUser.this, returnedUsersArray);
                    new SearchUserTask().execute(searchETtoString);
                    return true;
                }
                return false;
            }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    String searchETtoString = searchBar.getText().toString();
                    SEARCH_METHOD = 2;
                    returnedUsersArray2 = new ArrayList<SearchableUserDetails>();
                    listAdapter2 = new SearchUserListAdapter(SearchUser.this, returnedUsersArray2);
                    new SearchUserTask().execute(searchETtoString);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //LISTVIEW LISTENER
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(SearchUser.this, usersList.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                SearchableUserDetails theUserSelected = (SearchableUserDetails) usersList.getItemAtPosition(position);
               Intent intent = new Intent(SearchUser.this,UserProfile.class);
                intent.putExtra("username",theUserSelected.getUsername());
                intent.putExtra("current_username",usernameString);
                startActivity(intent);
                finish();

            }
        });

    }

    public class SearchUserTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog = new ProgressDialog(SearchUser.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(SEARCH_METHOD == 1){
            pDialog.setTitle("Search User");
            pDialog.setMessage("Searching...");
            pDialog.setCancelable(false);
            pDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String searchedFor = params[0];
                String link = "http://www.ratedapp.net/nati/searchuser.php";
                String data = URLEncoder.encode("thesearch", "UTF-8") + "=" + URLEncoder.encode(searchedFor, "UTF-8");
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

            Log.d("Search User", "The s-->" + s);
            String result = s;
            Log.d("Search User", "The result-->" + result);

            if (SEARCH_METHOD == 1) {
                if (s != null) {
                    try {
                        listAdapter.clear();
                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            SearchableUserDetails searchableUserDetails = new SearchableUserDetails();
                            searchableUserDetails.username = jObject.getString("username");
                            Log.d("Search User", "username[" + i + "] = " + searchableUserDetails.getUsername());
                            searchableUserDetails.firstName = jObject.getString("firstname");
                            Log.d("Search User", "firstname[" + i + "] = " + searchableUserDetails.getFirstName());
                            searchableUserDetails.lastName = jObject.getString("lastname");
                            Log.d("Search User", "lastname[" + i + "] = " + searchableUserDetails.getLastName());
                            searchableUserDetails.photoLink = jObject.getString("photo");
                            Log.d("Search User", "photolink[" + i + "] = " + searchableUserDetails.getPhotoLink());
                            // returnedUsersArray.add(searchableUserDetails);
                            listAdapter.add(searchableUserDetails);

                        }


                        usersList.setAdapter(listAdapter);


                        pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (SEARCH_METHOD == 2) {
                if (s != null) {
                    try {

                        JSONArray jsonArray = new JSONArray(result);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            SearchableUserDetails searchableUserDetails = new SearchableUserDetails();
                            searchableUserDetails.username = jObject.getString("username");
                            Log.d("Search User", "username[" + i + "] = " + searchableUserDetails.getUsername());
                            searchableUserDetails.firstName = jObject.getString("firstname");
                            Log.d("Search User", "firstname[" + i + "] = " + searchableUserDetails.getFirstName());
                            searchableUserDetails.lastName = jObject.getString("lastname");
                            Log.d("Search User", "lastname[" + i + "] = " + searchableUserDetails.getLastName());
                            searchableUserDetails.photoLink = jObject.getString("photo");
                            Log.d("Search User", "photolink[" + i + "] = " + searchableUserDetails.getPhotoLink());
                            // returnedUsersArray.add(searchableUserDetails);
                            listAdapter2.add(searchableUserDetails);

                        }


                        usersList.setAdapter(listAdapter2);


                       // pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

package com.natimeital.friendist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MyProfile extends Activity {

    TextView usernameTV, ageTV, genderTV, firstnameTV, lastnameTV;
    ImageButton menuBTN;
    ImageView profilePhoto;
    SharedPreferences sp;
    ListView myFriendsLV, myCategoriesLV;
    String THE_USERNAME_FROM_LOGIN;
    SearchUserListAdapter friendsListAdapter;
    ArrayList<SearchableUserDetails> friendsArrayList;
    ArrayAdapter<String> categoriesListAdapter;
    ArrayList<String> categoriesResult = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_page);

        profilePhoto = (ImageView) findViewById(R.id.myphoto_IV_myprofile_page);
        usernameTV = (TextView) findViewById(R.id.myusername_TV_myprofile_page);
        ageTV = (TextView) findViewById(R.id.myage_TV_myprofile_page);
        genderTV = (TextView) findViewById(R.id.mysex_TV_myprofile_page);
        menuBTN = (ImageButton) findViewById(R.id.menu_BTN_myprofile_page);


        firstnameTV = (TextView) findViewById(R.id.myfirstname_TV_myprofile_page);
        lastnameTV = (TextView) findViewById(R.id.mylastname_TV_myprofile_page);
        firstnameTV.setVisibility(View.GONE);
        lastnameTV.setVisibility(View.GONE);


        ageTV.setVisibility(View.GONE);
        genderTV.setVisibility(View.GONE);
        sp = getSharedPreferences("username", MODE_PRIVATE);

        final String usernameString = sp.getString("username2", "");
        Log.d("NEWUSERNAME", usernameString);
        UpdateProfile updateProfile = new UpdateProfile();
        updateProfile.execute(usernameString);
        new FetchFriendList().execute(usernameString);

        //button listeners
        menuBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //creating the instance of popup menu
                PopupMenu pop = new PopupMenu(MyProfile.this, menuBTN);

                //Inflating the popup using xml file
                pop.getMenuInflater().inflate(R.menu.myprofile_menu, pop.getMenu());

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.inboxBTN_myprofilepage:
                                Intent intentINBOX = new Intent(MyProfile.this, MyInbox.class);
                                startActivity(intentINBOX);
                                finish();
                                break;
                            case R.id.searchuserBTN_myprofilepage:
                                Intent intent = new Intent(MyProfile.this, SearchUser.class);
                                intent.putExtra("current_username", usernameString);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.searchCategoryBTN_myprofilepage:
                                Intent catIntent = new Intent(MyProfile.this, SearchCategory.class);
                                startActivity(catIntent);
                                finish();
                                break;
                            case R.id.editprofileBTN_myprofilepage:
                                Intent i = new Intent(MyProfile.this, EditMyProfile.class);
                                i.putExtra("current_username", usernameString);
                                startActivity(i);
                                finish();
                                break;
                            case R.id.logoutBTN_myprofilepage:
                                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
                                builder.setTitle(R.string.logout_menuItem)
                                        .setMessage(R.string.userprofile_logout_dialogBody)
                                        .setIcon(android.R.drawable.alert_light_frame)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.logout_menuItem, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                SharedPreferences.Editor spE = sp.edit();
                                                spE.putString("isLogged", "no");
                                                spE.apply();
                                                Intent i = new Intent(MyProfile.this, LoginPage.class);
                                                startActivity(i);
                                                Intent intent = new Intent(MyProfile.this, InboxNotificationsService.class);
                                                stopService(intent);
                                                Log.d("ServiceLog", "stopService() was called");
                                                finish();

                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Toast.makeText(MyProfile.this, "Cancelled logout", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .show();
                                break;
                        }

                        return true;
                    }
                });
                pop.show();
            }
        });

//friendslist
        myFriendsLV = (ListView) findViewById(R.id.myfriends_LV_myprofile_page);
        friendsArrayList = new ArrayList<>();
        friendsListAdapter = new SearchUserListAdapter(this, friendsArrayList);

        //LISTVIEW LISTENER
        myFriendsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(SearchUser.this, usersList.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                SearchableUserDetails theUserSelected = (SearchableUserDetails) myFriendsLV.getItemAtPosition(position);
                Intent intent = new Intent(MyProfile.this, UserProfile.class);
                intent.putExtra("username", theUserSelected.getUsername());
                intent.putExtra("current_username", usernameString);
                startActivity(intent);
                finish();

            }
        });
        //my categories list
        myCategoriesLV = (ListView) findViewById(R.id.mycategories_LV_myprofile_page);
        categoriesListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoriesResult);
        new FetchMyCategories().execute(usernameString);
        myCategoriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String categoryTransfer = (myCategoriesLV.getItemAtPosition(position)).toString();
                Intent intent = new Intent(MyProfile.this,CategoryProfile.class);
                intent.putExtra("category",categoryTransfer);
                startActivity(intent);
            }
        });

    }


    class UpdateProfile extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog = new ProgressDialog(MyProfile.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setTitle(getString(R.string.fetching_DialogTitle));
            pDialog.setMessage(getString(R.string.fetching_DialogBody));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String username = params[0];
                THE_USERNAME_FROM_LOGIN = username;

                Log.d("MyProfilePage", "The username-->" + username);
                String link = "http://www.ratedapp.net/nati/fetchprofile.php";
                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");

                Log.d("MyProfilePage", "The data-->" + data);


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
                    Log.d("MyProfilePage", "The line-->" + line);
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
            SharedPreferences.Editor editor = getSharedPreferences("location", MODE_PRIVATE).edit();
            String[] returnedValuesFromServer = s.split("\\s+");
            ageTV.setText(returnedValuesFromServer[0]);
            ageTV.setVisibility(View.VISIBLE);
            usernameTV.setText(THE_USERNAME_FROM_LOGIN);
            if (returnedValuesFromServer[1].equals("1")) {
                genderTV.setText(getString(R.string.male));
            } else if (returnedValuesFromServer[1].equals("2")) {
                genderTV.setText(R.string.female);
            }
            genderTV.setVisibility(View.VISIBLE);

            if (!returnedValuesFromServer[2].equals("empty") && !returnedValuesFromServer[3].equals("empty")) {

                firstnameTV.setText(returnedValuesFromServer[2]);
                firstnameTV.setVisibility(View.VISIBLE);

                lastnameTV.setText(returnedValuesFromServer[3]);
                lastnameTV.setVisibility(View.VISIBLE);

            } else if (!returnedValuesFromServer[2].equals("empty")) {

                firstnameTV.setText(returnedValuesFromServer[2]);
                firstnameTV.setVisibility(View.VISIBLE);

            } else if (!returnedValuesFromServer[3].equals("empty")) {

                lastnameTV.setText(returnedValuesFromServer[3]);
                lastnameTV.setVisibility(View.VISIBLE);

            }
            if (!returnedValuesFromServer[4].equals("empty")) {
                new ImageLoadTask(returnedValuesFromServer[4], profilePhoto).execute();

            }

            //latitude
            if (!returnedValuesFromServer[5].equals(null)) {

                editor.putFloat("latitude", Float.valueOf(returnedValuesFromServer[5]));
                editor.apply();
            }
            //longitude
            if (!returnedValuesFromServer[6].equals(null)) {

                editor.putFloat("longitude", Float.valueOf(returnedValuesFromServer[6]));
                editor.apply();
            }


            pDialog.dismiss();

            Log.d("MyProfilePage", "the s-->" + s);
            Log.d("MyProfilePage", "Ended");

        }
    }

    class FetchFriendList extends AsyncTask<String, Void, String> {
        final String TAG = "fetchfriendlist";

        @Override
        protected String doInBackground(String... params) {
            String link = "http://www.ratedapp.net/nati/friendlist.php";


            try {
                String username = params[0];

                Log.d(TAG, "the username->" + username);
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(sp.getString("username", ""), "UTF-8");


                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "the line->" + line);
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

            Log.d(TAG, "the s->" + s);
            try {
                friendsListAdapter.clear();
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    SearchableUserDetails searchableUserDetails = new SearchableUserDetails();
                    searchableUserDetails.username = jObject.getString("username");
                    Log.d(TAG, "username[" + i + "] = " + searchableUserDetails.getUsername());
                    searchableUserDetails.firstName = jObject.getString("firstname");
                    Log.d(TAG, "firstname[" + i + "] = " + searchableUserDetails.getFirstName());
                    searchableUserDetails.lastName = jObject.getString("lastname");
                    Log.d(TAG, "lastname[" + i + "] = " + searchableUserDetails.getLastName());
                    searchableUserDetails.photoLink = jObject.getString("photo");
                    Log.d(TAG, "photolink[" + i + "] = " + searchableUserDetails.getPhotoLink());
                    friendsListAdapter.add(searchableUserDetails);
                }
                myFriendsLV.setAdapter(friendsListAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class FetchMyCategories extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String username = params[0];
                String link = "http://www.ratedapp.net/nati/fetchmycategories.php";
                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");

                Log.d("fetchmycat", "the data --> " + data);
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
                    Log.d("fetchmycat", "The line -> " + line);
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
            Log.d("fetchmycat", "the s--> " + s);
            try {
                categoriesListAdapter.clear();
                JSONArray jsonArray = new JSONArray(s);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String category = jsonObject.getString("category");
                    categoriesListAdapter.add(category);
                }
                myCategoriesLV.setAdapter(categoriesListAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onBackPressed() {
        new UtilMethods().exitMessage(this);
    }
}

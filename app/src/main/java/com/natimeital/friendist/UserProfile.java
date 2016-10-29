package com.natimeital.friendist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
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
import java.util.List;

/**
 * Created by nati on 5/22/2016.
 */
public class UserProfile extends Activity {

    TextView usernameTV, firstnameTV, lastnameTV, ageTV, genderTV, distanceFromMeTV;
    ImageView profilePhotoIV;
    ImageButton menuIB, messageIB, addUserIB, removeUserIB;
    Location loggedInUserLocation, searchedForUserLocation;
    String theUserImLookingAt;
    ListView userCategories;
    ArrayList<String> results = new ArrayList<String>();
    ArrayAdapter<String> categoryListAdapter;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        //instantiations
        usernameTV = (TextView) findViewById(R.id.myusername_TV_UserProfile_page);
        firstnameTV = (TextView) findViewById(R.id.myfirstname_TV_UserProfile_page);
        lastnameTV = (TextView) findViewById(R.id.mylastname_TV_UserProfile_page);
        ageTV = (TextView) findViewById(R.id.myage_TV_UserProfile_page);
        genderTV = (TextView) findViewById(R.id.mysex_TV_UserProfile_page);
        distanceFromMeTV = (TextView) findViewById(R.id.distancebetween_TV_UserProfile_page);
        profilePhotoIV = (ImageView) findViewById(R.id.myphoto_IV_UserProfile_page);
        menuIB = (ImageButton) findViewById(R.id.menu_BTN_UserProfile_page);
        messageIB = (ImageButton) findViewById(R.id.messageUser_BTN_UserProfile_page);
        addUserIB = (ImageButton) findViewById(R.id.addUser_BTN_UserProfile_page);
        removeUserIB = (ImageButton) findViewById(R.id.removeUser_BTN_UserProfile_page);
        addUserIB.setVisibility(View.GONE);
        messageIB.setVisibility(View.GONE);
        removeUserIB.setVisibility(View.GONE);

        sp = getSharedPreferences("username", MODE_PRIVATE);
        final String usernameSendingMessage = sp.getString("username", "");
        Log.d("SearchUserSTRING", "username sending messsage " + usernameSendingMessage);


        loggedInUserLocation = new Location("");
        searchedForUserLocation = new Location("");
        firstnameTV.setVisibility(View.GONE);
        lastnameTV.setVisibility(View.GONE);
        genderTV.setVisibility(View.GONE);
        ageTV.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        final String usernameFromSearchUser = extras.getString("username");
        String username = usernameFromSearchUser;
        Log.d("SearchUserSTRING", "usernamefromsearchuser" + username);
        final String loggedInUsername = extras.getString("current_username");
        Log.d("SearchUserSTRING", "username" + loggedInUsername);
        usernameTV.setText(usernameFromSearchUser);
        theUserImLookingAt = usernameFromSearchUser;
        Log.d("fetchmycat", "the user im looking at--> " +theUserImLookingAt);



        //the list view of categories
        userCategories = (ListView)findViewById(R.id.mycategories_LV_UserProfile_page);
        categoryListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,results);
        new FetchMyCategories().execute(theUserImLookingAt);
        userCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String categoryTransfer = (userCategories.getItemAtPosition(position)).toString();
                Intent intent = new Intent(UserProfile.this,CategoryProfile.class);
                intent.putExtra("category",categoryTransfer);
                startActivity(intent);
            }
        });


        //menu btn
        menuIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //creating the instance of popup menu
                PopupMenu pop = new PopupMenu(UserProfile.this, menuIB);

                //Inflating the popup using xml file
                pop.getMenuInflater().inflate(R.menu.userprofile_menu, pop.getMenu());

                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.searchuserBTN_UserProfilePage:
                                Intent intent = new Intent(UserProfile.this, SearchUser.class);
                                intent.putExtra("current_username", usernameFromSearchUser);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.backtoprofileBTN_UserProfilePage:
                                SharedPreferences.Editor spE = getSharedPreferences("username", MODE_PRIVATE).edit();
                                spE.putString("FKIN_USER", loggedInUsername);
                                spE.commit();

                                Intent i = new Intent(UserProfile.this, MyProfile.class);
                                i.putExtra("current_username", loggedInUsername);
                                startActivity(i);

                                finish();
                                break;
                            case R.id.logoutBTN_UserProfilePage:
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                                builder.setTitle(getString(R.string.logout_menuItem))
                                        .setMessage(R.string.userprofile_logout_dialogBody)
                                        .setIcon(android.R.drawable.alert_light_frame)
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.logout_menuItem), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent i = new Intent(UserProfile.this, LoginPage.class);
                                                SharedPreferences.Editor spE = sp.edit();
                                                spE.putString("isLogged", "no");
                                                spE.apply();
                                                startActivity(i);
                                                Intent intent = new Intent(UserProfile.this, InboxNotificationsService.class);
                                                stopService(intent);
                                                finish();

                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
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

        new GetProfile().execute(username);
        messageIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("receivingUsername", usernameFromSearchUser);
                args.putString("sendingUsername", usernameSendingMessage);
                args.putString("replyfragment", "sendnew");

                DialogFragment newFragment = new SendMessageFragment();
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "TAG");

            }
        });

        addUserIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddUserToFriendList().execute();

            }
        });
        new checkIfFriends().execute();

        removeUserIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new removeUserFromFriends().execute();
            }
        });
    }

    class GetProfile extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog = new ProgressDialog(UserProfile.this);

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
                Log.d("Selected profile", "the Username-->" + username);
                String link = "http://www.ratedapp.net/nati/fetchprofile.php";
                Log.d("Selected profile", "the Link-=>" + link);
                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                Log.d("Selected profile", "the Data-->" + data);
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
                    Log.d("Selected profile", "the Line-->" + line);
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
            SharedPreferences sharedPreferences = getSharedPreferences("location", MODE_PRIVATE);
            String[] returnedValuesFromServer = s.split("\\s+");
            ageTV.setText(returnedValuesFromServer[0]);
            ageTV.setVisibility(View.VISIBLE);
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
                new ImageLoadTask(returnedValuesFromServer[4], profilePhotoIV).execute();

            }

            //latitude
            if (!returnedValuesFromServer[5].equals(null)) {
                float latitude = sharedPreferences.getFloat("latitude", 0);
                Log.d("LocationGetter", "theUser latitude-->" + latitude);
                Log.d("LocationGetter", "theSearchedUser latitude" + returnedValuesFromServer[5]);
                loggedInUserLocation.setLatitude(latitude);
                searchedForUserLocation.setLatitude(Float.valueOf(returnedValuesFromServer[5]));
            }
            //longitude
            if (!returnedValuesFromServer[6].equals(null)) {
                float longitude = sharedPreferences.getFloat("longitude", 0);
                Log.d("LocationGetter", "theUser latitude-->" + longitude);
                Log.d("LocationGetter", "theSearchedUser longitude" + returnedValuesFromServer[6]);
                loggedInUserLocation.setLongitude(longitude);
                searchedForUserLocation.setLongitude(Float.valueOf(returnedValuesFromServer[6]));
            }
            if (loggedInUserLocation.getLatitude() != 0 || loggedInUserLocation.getLongitude() != 0
                    || searchedForUserLocation.getLatitude() != 0 || searchedForUserLocation.getLongitude() != 0) {
                float[] dist = new float[1];
                loggedInUserLocation.distanceBetween(loggedInUserLocation.getLatitude(), loggedInUserLocation.getLongitude(), searchedForUserLocation.getLatitude(), searchedForUserLocation.getLongitude(), dist);
                //distanceFromMeTV.setText("Distance: "+String.valueOf((int)(dist[0])*0.001)+"km");

                distanceFromMeTV.setText(String.format(getString(R.string.distance_userProfile) + " %.1f" + getString(R.string.km), (dist[0]) * 0.001));

            }
            messageIB.setVisibility(View.VISIBLE);

            pDialog.dismiss();
        }


    }

    public class AddUserToFriendList extends AsyncTask<String, Void, String> {
        String TAG = "addinguserbutton";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String friend = usernameTV.getText().toString();
            Toast.makeText(UserProfile.this, "Adding " + friend + " to friends list", Toast.LENGTH_SHORT);

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String username = sp.getString("username2", "");
                String friend = theUserImLookingAt;
                String command = "add";
                String link = "http://www.ratedapp.net/nati/friendlist.php";

                String data = URLEncoder.encode("command", "UTF-8") + "=" + URLEncoder.encode(command, "UTF-8");
                data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("friend", "UTF-8") + "=" + URLEncoder.encode(friend, "UTF-8");
                Log.d(TAG, "the data->" + data);

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
            String splitResult[] = s.split("\\s+");
            String stringToShow = "";
            if (splitResult.length > 6) {
                for (int i = 0; i < 7; i++) {
                    stringToShow += " " + splitResult[i];
                }
            } else {
                stringToShow = "User is already your friend";
            }
            Toast.makeText(UserProfile.this, stringToShow, Toast.LENGTH_SHORT).show();
        }


    }

    public class removeUserFromFriends extends AsyncTask<String, Void, String> {
        String TAG = "RemoveFriend";

        @Override
        protected String doInBackground(String... params) {
            try {
                String username = sp.getString("username2", "");
                String friend = theUserImLookingAt;
                String command = "remove";
                String link = "http://www.ratedapp.net/nati/friendlist.php";

                String data = URLEncoder.encode("command", "UTF-8") + "=" + URLEncoder.encode(command, "UTF-8");
                data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("friend", "UTF-8") + "=" + URLEncoder.encode(friend, "UTF-8");
                Log.d(TAG, "the data->" + data);

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

            Log.d(TAG, s);
            if (s.contains("has")) {
                Toast.makeText(UserProfile.this, s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class checkIfFriends extends AsyncTask<String, Void, String> {

        String TAG = "checkifFriend";

        @Override
        protected String doInBackground(String... params) {
            try {
                String username = sp.getString("username", "");
                String friend = theUserImLookingAt;
                String command = "checkfriend";
                String link = "http://www.ratedapp.net/nati/friendlist.php";

                String data = URLEncoder.encode("command", "UTF-8") + "=" + URLEncoder.encode(command, "UTF-8");
                data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("friend", "UTF-8") + "=" + URLEncoder.encode(friend, "UTF-8");
                Log.d(TAG, "the data->" + data);

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
            Log.d(TAG, "s-->" + s);

            if (s.equals("not_friend")) {
                addUserIB.setVisibility(View.VISIBLE);
            } else {
                removeUserIB.setVisibility(View.VISIBLE);
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
                categoryListAdapter.clear();
                JSONArray jsonArray = new JSONArray(s);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String category = jsonObject.getString("category");
                    categoryListAdapter.add(category);
                }
                userCategories.setAdapter(categoryListAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void onBackPressed() {
        new UtilMethods().exitMessage(this);
    }
}

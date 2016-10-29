package com.natimeital.friendist;
/*
* Database name : 2099774_friend
* Database password: friendist1
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;

public class LoginPage extends AppCompatActivity {
    EditText username_ET, password_ET;
    Button login_BTN, register_BTN;
    LocationManager lm;
    public double longitude, latitude;
    public final String THE_USERNAME_LOGGED_IN = "username";
    public final String IS_SERVICE_RUNNING = "theService";
    SharedPreferences sp;

    public String getIS_SERVICE_RUNNING() {
        return IS_SERVICE_RUNNING;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //startof Force Locale
/*        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/
        //endof Force Locale

        Log.d("LOCALEDEVICE", Locale.getDefault().getDisplayLanguage());
        Log.d("LOCALEDEVICE", Locale.getDefault().toString());
        Log.d("LOCALEDEVICE", Locale.getDefault().getLanguage());

        sp = getSharedPreferences("username", MODE_PRIVATE);

        SharedPreferences.Editor spE = sp.edit();
        spE.putString("isLogged", "no");

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 3000, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                longitude = location.getLongitude();
                latitude = location.getLatitude();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                AlertDialog.Builder adb = new AlertDialog.Builder(LoginPage.this);
                adb.setTitle(R.string.gps_dialog_title).setMessage(R.string.gps_dialog_message)
                        .setCancelable(false).setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).show();
            }
        });
        //noinspection ResourceType
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        Log.d("THEDATA", "longitude is:" + Double.toString(longitude));
        Log.d("THEDATA", "latitude is:" + Double.toString(latitude));
        }
        //noinspection ResourceType



        //elemnts on screen
        username_ET = (EditText) findViewById(R.id.username_ET_login);
        password_ET = (EditText) findViewById(R.id.password_ET_login);
        username_ET.setText("nati");
        password_ET.setText("nati");
        login_BTN = (Button) findViewById(R.id.login_BTN_login);
        register_BTN = (Button) findViewById(R.id.register_BTN_login);

        register_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
                finish();
            }
        });
        //button listeners
        login_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //getting info from ETs
                String username_TXT = username_ET.getText().toString();
                String password_TXT = password_ET.getText().toString();

                //checking with server
                LoginUser loginUser = new LoginUser();
                loginUser.execute(username_TXT, password_TXT);


            }
        });

    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    class LoginUser extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        LoginPage loginPage = LoginPage.this;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginPage.this);
            pDialog.setTitle(getString(R.string.logging_in_dialog_title));
            pDialog.setMessage(getString(R.string.please_wait_dialog_message));
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Log.d("mylog", "entered_try_yes");
                String username = params[0];
                String password = params[1];
                double longitude = loginPage.getLongitude();
                double latitude = loginPage.getLatitude();
                String link = "http://www.ratedapp.net/nati/loginwithlocation.php";

                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                data += "&" + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(Double.toString(longitude), "UTF-8");
                data += "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(Double.toString(latitude), "UTF-8");

                Log.d("THEDATA", data);


                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                Log.d("mylog", "before writer");
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                Log.d("mylog", "after writer");

                wr.write(data);
                wr.flush();

                Log.d("mylog", "before reader");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                Log.d("mylog", "after reader");

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    Log.d("mylog", "in while sb" + " " + line);
                    sb.append(line);
                    break;
                }
          /*          if(sb.equals("Connected")){
                        String data2 = URLEncoder.encode("longitude","UTF-8") + "=" + URLEncoder.encode(Double.toString(longitude),"UTF-8");
                        data2 += "&" + URLEncoder.encode("latitude","UTF-8") + "=" + URLEncoder.encode(Double.toString(latitude),"UTF-8");

                    }*/
                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            //Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG);
            Log.d("RETURNED", s);
            if (s.equals("Connected")) {
                Toast.makeText(getBaseContext(), R.string.connected_Toast_loginpage, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginPage.this,MyProfile.class);
                intent.putExtra("username", username_ET.getText().toString());
                startActivity(intent);
                sp = getSharedPreferences(THE_USERNAME_LOGGED_IN,MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(THE_USERNAME_LOGGED_IN, username_ET.getText().toString());
                editor.putString(IS_SERVICE_RUNNING,"no");
                editor.putString("isLogged","yes");
                editor.putString("username2",username_ET.getText().toString());
                editor.commit();
                finish();
            } else if (s.equals("Wrong Details")) {
                Toast.makeText(getBaseContext(), R.string.bad_credentials_login_error_msg, Toast.LENGTH_LONG).show();
            } else if (s.equals("Empty Details")) {
                Toast.makeText(getBaseContext(), R.string.empty_credentials_error_message, Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onBackPressed() {
      new UtilMethods().exitMessage(this);

    }
}

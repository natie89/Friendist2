package com.natimeital.friendist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by nati on 5/26/2016.
 */
public class InboxNotificationsService extends Service {
    SharedPreferences sp;
    int notificationID = 0;
    Thread notifyThread;
    NotificationCompat.Builder notification;
    String loggedUser;
    String TAG = "ServiceLog";
    String fromUser = "", theMessage = "";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "On create is called");
        sp = getSharedPreferences("username", MODE_PRIVATE);//get the username
        String sharedPrefUsername = sp.getString("username", "");//store username
        loggedUser = sharedPrefUsername;
        Log.d(TAG, "in service username-=>" + loggedUser);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "On start command is called");
        notifyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!notifyThread.isInterrupted()) {
                    synchronized (this) {
                        if (isInternetAvailable()) {
                            new CheckNewInbox().execute();
                            Log.d(TAG, "Checked for new inbox");
                            try {
                                Thread.sleep(1000 * 30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                Thread.sleep(1000 * 30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        //function
                    }
                }

            }
        });
        notifyThread.start();

    /*    sp = getSharedPreferences("username", MODE_PRIVATE);//get the username
        String sharedPrefUsername = sp.getString("username", "");//store username
        loggedUser = sharedPrefUsername;*/


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notifyThread.interrupt();
        Log.d(TAG, "thread interrupted");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class CheckNewInbox extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String username = loggedUser;
                String command = "update";
                String link = "http://www.ratedapp.net/nati/getinbox.php";

                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("command", "UTF-8") + "=" + URLEncoder.encode(command, "UTF-8");

                Log.d(TAG, "The data -=>" + data);
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
                    Log.d(TAG, "The line -=>" + line);

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
            String result = s;
            Log.d(TAG, "The s-=>" + s);
            if (!result.equals("nomessages")) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        notificationID = jObject.getInt("messageid");
                        fromUser = jObject.getString("from");
                        theMessage = jObject.getString("message");
                        notifyMessage(notificationID, fromUser, theMessage);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "No Messages");
            }
        }
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnected()) {
            Log.d(TAG,"INTERENET ACCESS");
            return true;
        } else {
            Log.d(TAG,"NO INTERENET ACCESS");
            return false;
        }
    }

    public void notifyMessage(int notifID, String fromuser, String themessage) {
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.mipmap.ic_launcher);

        notification.setTicker("New message in Friendist!");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Message from " + fromuser + ".");
        notification.setContentText(themessage);
        Intent intent = new Intent(this, MyInbox.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        notification.setSound(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION));
        notification.setLights(Color.DKGRAY, 1000, 2000);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(notifID, notification.build());
    }
}

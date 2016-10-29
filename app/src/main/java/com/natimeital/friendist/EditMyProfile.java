package com.natimeital.friendist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
/*
*
*       TO DO
*     ---------
*     Command:Fetch - fetch details when entering edit mode - done
*     Command:Update - update details with changes and save them
*
*     Update
*     ------
*     get photo from gallery, encode it to base64
*     send string to server decode to base64 in php
*     upload photo to photos folder - $photoName = $username.".jpg"
*     insert link to the database photo column, where username.
*
 */

public class EditMyProfile extends Activity {
    private static final int GET_PHOTO_FROM_GALLERY = 1;
    ImageButton cancelBTN, photoBTN, saveBTN;
    private ImageView profilePhoto;
    String TASK_TO_EXECUTE, usernameString;
    String FIRST_FETCH = "Fetch", SAVE_DATA = "Save";
    String firstname_post, lastname_post, decodedImageString = "empty";
    EditText firstNameET, lastNameET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_profile);

        //KEEPING THE USERNAME FOR REFRESH WHEN GOING BACK TO PROFILE
        Bundle extras = getIntent().getExtras();
        final String PASSED_USERNAME = extras.getString("current_username");
        usernameString = PASSED_USERNAME;

        TASK_TO_EXECUTE = FIRST_FETCH;

        cancelBTN = (ImageButton) findViewById(R.id.cancelEdit_BTN_profileEditor);
        photoBTN = (ImageButton) findViewById(R.id.uploadphoto_profileEditor);
        saveBTN = (ImageButton) findViewById(R.id.save_profileEditor);
        profilePhoto = (ImageView) findViewById(R.id.photo_profileEditor);
        firstNameET = (EditText) findViewById(R.id.firstName_profileEditor);
        lastNameET = (EditText) findViewById(R.id.lastName_profileEditor);


        //button listeners
        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditMyProfile.this);
                builder.setCancelable(false)
                        .setIcon(R.drawable.cancel)
                        .setTitle(R.string.cancelEdit_EditMyProfile)
                        .setMessage(R.string.cancelEdit_dialogText_EditMyProfile)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent i = new Intent(EditMyProfile.this, MyProfile.class);
                                i.putExtra("username", PASSED_USERNAME);//BACK TO MYPROFILE WITH THE USERNAME
                                Log.d("Cancel Task","The username-->" + PASSED_USERNAME);
                                startActivity(i);
                                finish();
                            }
                        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(EditMyProfile.this, R.string.continueEdit_EditMyProfile, Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });
        photoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i, GET_PHOTO_FROM_GALLERY);
            }
        });
        new EditProfileTask().execute();
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstname_post = firstNameET.getText().toString();
                lastname_post = lastNameET.getText().toString();
                TASK_TO_EXECUTE = SAVE_DATA;
                new EditProfileTask().execute(firstname_post, lastname_post);//put after "save changes?" dialog
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_PHOTO_FROM_GALLERY && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String filepath[] = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filepath, null, null, null);
            cursor.moveToFirst();
            int columnIndext = cursor.getColumnIndex(filepath[0]);
            String picturePath = cursor.getString(columnIndext);
            cursor.close();
            Bitmap bmp = BitmapFactory.decodeFile(picturePath);
            Log.d("SOMETHING", "" + bmp.getByteCount());
            Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, 250, 250, true);
            profilePhoto.setImageBitmap(bmp2);
            decodedImageString = new UtilMethods().bitmapToBase64(bmp2);
        }


    }


    @Override
    public void onBackPressed() {
        new UtilMethods().exitMessage(this);
    }

    class EditProfileTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(EditMyProfile.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Fetching data");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            if (TASK_TO_EXECUTE.equals(SAVE_DATA)) {
                firstname_post = firstNameET.getText().toString();
                lastname_post = lastNameET.getText().toString();
                Log.d("Second Task", "First " + firstname_post + " last" + lastname_post);
            }

        }

        @Override
        protected String doInBackground(String... params) {
            if (TASK_TO_EXECUTE.equals(FIRST_FETCH)) {

                try {
                    String link = "http://www.ratedapp.net/nati/editprofile.php";
                    String command = FIRST_FETCH;
                    String username = usernameString;
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);

                    String data = URLEncoder.encode("command", "UTF-8") + "=" + URLEncoder.encode(command, "UTF-8");
                    data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                    Log.d("First Task", "data-->" + data);

                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(data);
                    Log.d("First Task", "data written");
                    writer.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;

                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        Log.d("First Task", "Line recieved-->" + line);
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (TASK_TO_EXECUTE.equals(SAVE_DATA)) {
                try {
                    String link = "http://www.ratedapp.net/nati/editprofile.php";
                    String command = SAVE_DATA;
                    String username = usernameString;

                    String thefirstname = params[0];
                    String thelastname = params[1];
                    String decodedImage = decodedImageString;

                    Log.d("Second Task", "The decoded bmp-->" + " " + decodedImage);
                    Log.d("Second Task", "First + Last name =" + " " + thefirstname + " " + thelastname);


                    String data = URLEncoder.encode("command", "UTF-8") + "=" + URLEncoder.encode(command, "UTF-8");
                    data += "&" + URLEncoder.encode("thefirstname", "UTF-8") + "=" + URLEncoder.encode(thefirstname, "UTF-8");
                    data += "&" + URLEncoder.encode("thelastname", "UTF-8") + "=" + URLEncoder.encode(thelastname, "UTF-8");
                    data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                    if (decodedImage != null) {
                        data += "&" + URLEncoder.encode("imagebase64", "UTF-8") + "=" + URLEncoder.encode(decodedImage, "UTF-8");
                    } else {
                        data += "&" + URLEncoder.encode("imagebase64", "UTF-8") + "=" + URLEncoder.encode("empty", "UTF-8");
                    }
                    Log.d("Second Task", "The DATA --=>" + " " + data);

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
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Log.d("First Task", "The 's' -->" + " " + s);
            if (s != null) {
                if (TASK_TO_EXECUTE.equals(FIRST_FETCH)) {
                    String[] fetchArray = s.split("\\s+");
                    if (!fetchArray[0].equals("empty")) {
                        Log.d("First Task", "First Name-->" + fetchArray[0]);
                        firstNameET.setText(fetchArray[0]);
                    }
                    if (!fetchArray[1].equals("empty")) {
                        lastNameET.setText(fetchArray[1]);
                        Log.d("First Task", "Last Name-->" + fetchArray[1]);
                    }
                    if (!fetchArray[2].equals("empty")) {
                        new ImageLoadTask(fetchArray[2], profilePhoto).execute();
                    }
                    Log.d("First Task", "PostExecute S-->" + s);

                }
                if (TASK_TO_EXECUTE.equals(SAVE_DATA)) {
                    Log.d("Second Task", "blA Bla");
                    Intent i = new Intent(EditMyProfile.this, MyProfile.class);
                    i.putExtra("username", usernameString);//BACK TO MYPROFILE WITH THE USERNAME
                    startActivity(i);
                    finish();
                    Log.d("Second Task", "SAVE_DATA S-->" + " " + s);
                }

            } else if (s == null) {
                Toast.makeText(getBaseContext(), "msg", Toast.LENGTH_SHORT).show();
            }
        }

    }

}

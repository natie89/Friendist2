package com.natimeital.friendist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegisterPage extends AppCompatActivity {

    EditText usernameET, passwordET, emailET, dayET, monthET, yearET;
    Button registerBTN, backBTN;
    Spinner genderSpinner;
    int genderNumber, day, month, year;
    String ageInString;
    CheckBox dateCB;
    boolean CAN_REGISTER = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //Edit texts
        usernameET = (EditText) findViewById(R.id.username_ET_register);
        passwordET = (EditText) findViewById(R.id.password_ET_register);
        emailET = (EditText) findViewById(R.id.email_ET_register);
        dayET = (EditText) findViewById(R.id.day_ET_register_page);
        monthET = (EditText) findViewById(R.id.month_ET_register_page);
        yearET = (EditText) findViewById(R.id.year_ET_register_page);
        dateCB = (CheckBox) findViewById(R.id.date_CB_register_page);


        //Buttons
        registerBTN = (Button) findViewById(R.id.register_BTN_register);
        backBTN = (Button) findViewById(R.id.back_BTN_register);

        //Spinner
        genderSpinner = (Spinner) findViewById(R.id.spinner_register);
        final List<String> genderList = new ArrayList<>();
        genderList.add(getString(R.string.male));
        genderList.add(getString(R.string.female));

        ArrayAdapter<String> spinnerData = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList);
        spinnerData.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerData);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (genderSpinner.getSelectedItem().toString().equals(getString(R.string.male))) {
                    genderNumber = 1;
                    Log.d("NATILOG", "gender number" + genderNumber);
                } else {
                    genderNumber = 2;
                    Log.d("NATILOG", "gender number" + genderNumber);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
        dateCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    if (Integer.parseInt(dayET.getText().toString()) > 31) {
                        Toast.makeText(getBaseContext(), R.string.register_dayError, Toast.LENGTH_LONG).show();
                        buttonView.setChecked(false);
                    } else if (Integer.parseInt(monthET.getText().toString()) > 12) {
                        Toast.makeText(getBaseContext(), R.string.register_month_error, Toast.LENGTH_LONG).show();
                        buttonView.setChecked(false);
                    } else if (Integer.parseInt(yearET.getText().toString()) > 2006) {
                        Log.d("YEAR", String.valueOf(year));
                        Toast.makeText(getBaseContext(), R.string.register_yearError, Toast.LENGTH_LONG).show();
                        buttonView.setChecked(false);
                    } else {
                        CAN_REGISTER = true;

                        //calculate age
                        day = Integer.parseInt(dayET.getText().toString());
                        month = Integer.parseInt(monthET.getText().toString());
                        year = Integer.parseInt(yearET.getText().toString());
                        ageInString = calculatedUserAge(day, month, year);
                    }
                }
            }
        }); //END OF  NEW DATE CHECK LISTENER
        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (usernameET.getText().toString().equals("")) {

                            new UtilMethods().neutralButtonMessages(usernameET, getString(R.string.register_emptyUsername),RegisterPage.this);

                } else if (passwordET.getText().toString().equals("")) {

                    new UtilMethods().neutralButtonMessages(passwordET, getString(R.string.register_emptyPassword), RegisterPage.this);

                } else if (emailET.getText().toString().equals("")) {

                    new UtilMethods().neutralButtonMessages(emailET, getString(R.string.register_emptyEmail), RegisterPage.this);

                } else if (dayET.getText().toString().equals("") || monthET.getText().toString().equals("") || yearET.getText().toString().equals("")) {

                    new UtilMethods().neutralButtonMessages(dayET, getString(R.string.register_emptyPartOfDate), RegisterPage.this);

                } else if (!dateCB.isChecked()) {
                    Toast t = Toast.makeText(getBaseContext(), R.string.register_approveDate_toast, Toast.LENGTH_LONG);
                    t.getView().setBackgroundColor(Color.RED);
                    t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    t.show();
                } else if(CAN_REGISTER == true){
                    String usernameTXT = usernameET.getText().toString();
                    String passwordTXT = passwordET.getText().toString();
                    String emailTXT = emailET.getText().toString();
                    int gender = genderNumber;
                    RegisterUser reguser = new RegisterUser();
                    reguser.execute(usernameTXT, passwordTXT, emailTXT);

                }
            }
        }); //END OF NEW REGISTER BUTTON LISTENER


    }

    public String calculatedUserAge(int day, int month, int year) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        Log.d("AGE", String.valueOf(age));
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        String ageString = Integer.toString(age);
        Log.d("AGE", ageString);


        return ageString;
    }

    public int getGender() {
        return genderNumber;
    }

    class RegisterUser extends AsyncTask<String, Void, String> {
        int gender = getGender();
        ProgressDialog pDialog = new ProgressDialog(RegisterPage.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setTitle(getString(R.string.register_regsitration_dialog_Title));
            pDialog.setMessage(getString(R.string.register_registration_Dialog_body));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String username = params[0];
                String password = params[1];
                String email = params[2];
                String age = ageInString;
                Log.d("AGE", age);
                Log.d("AGE", "ageInString-->" + ageInString);

                String link = "http://www.ratedapp.net/nati/register.php";

                String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                data += "&" + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(gender), "UTF-8");
                data += "&" + URLEncoder.encode("age", "UTF-8") + "=" + URLEncoder.encode(age, "UTF-8");

                Log.d("THEDATA", "-->" + data);
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    Log.d("THEDATA", "the Line -->" + line);
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
            pDialog.dismiss();
            Log.d("THEDATA", "Returned to post exectue" + s);
            switch (s) {
                case "success":
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterPage.this);
                    builder.setMessage(R.string.register_success_dialogBody).setTitle(R.string.register_success_dialogTitle).setCancelable(false).setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                            startActivity(intent);
                            finish();

                        }
                    }).show();
                    break;
                case "username_used":
                    alertUserRegistrationStatus(getString(R.string.register_usernameExists_dialogBody));
                    break;
                case "email_used":
                    alertUserRegistrationStatus(getString(R.string.register_emailExists_dialogBody));
                    break;
            }
        }
    }

    public void alertUserRegistrationStatus(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterPage.this);
        builder.setCancelable(false).setTitle(R.string.register_alreadyExists_dialogTitle).setMessage(msg).setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onBackPressed() {
        new UtilMethods().exitMessage(this);
    }
}

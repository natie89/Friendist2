package com.natimeital.friendist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SearchCategory extends Activity {
    final String TAG = "searchCategory";
    ListView searchCategory_LV;
    ImageButton back_BTN, addCat_BTN, clearSearch_BTN;
    EditText searchBar;
    ArrayAdapter<String> listAdapter;
    SharedPreferences sp;
    String loggedUsername, typedSearch;
    ArrayList<String> results = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_catergory);

        //instantiation
        searchCategory_LV = (ListView) findViewById(R.id.categories_LV_SearchCategory);
        back_BTN = (ImageButton) findViewById(R.id.back_BTN_searchCategory);
        addCat_BTN = (ImageButton) findViewById(R.id.addCategory_IB_searchCategory);
        clearSearch_BTN = (ImageButton)findViewById(R.id.clearSearch_IB_searchCategory);
        searchBar = (EditText) findViewById(R.id.search_ET_SearchCategory);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results);
        //get logged in user
        sp = getSharedPreferences("username", MODE_PRIVATE);
        String loggedInUsername = sp.getString("username2", "");
        loggedUsername = loggedInUsername;
        Log.d(TAG, "The logged in username is --> " + loggedInUsername);
        back_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchCategory.this, MyProfile.class);
                startActivity(i);
                finish();
            }
        });
        //clear search on click
        clearSearch_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setText("");
                findViewById(R.id.addorremove_LL_searchCategory).setVisibility(View.GONE);
                findViewById(R.id.noCategory_TV_searchCategory).setVisibility(View.GONE);
            }
        });

        //add categoryy on click
        addCat_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchCategory.this);
                builder.setTitle(R.string.add_category_dialog).setCancelable(false)
                        .setMessage(getString(R.string.wouldadd_dialog) + searchBar.getText().toString() +getString(R.string.as_new_Cat_dialog))
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
                        findViewById(R.id.addorremove_LL_searchCategory).setVisibility(View.GONE);
                        findViewById(R.id.noCategory_TV_searchCategory).setVisibility(View.GONE);
                    }
                }).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                       new AddCategoryTask().execute("add");
                    }
                }).show();
            }
        });

        //searchbar listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    typedSearch = searchBar.getText().toString();
                    new SearchCategoryTask().execute(typedSearch);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //listview lsitener
        searchCategory_LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String categoryTransfer = (searchCategory_LV.getItemAtPosition(position)).toString();
                Intent intent = new Intent(SearchCategory.this,CategoryProfile.class);
                intent.putExtra("category",categoryTransfer);
                startActivity(intent);

            }
        });


    }

    public class AddCategoryTask extends AsyncTask<String,Void,String>{
        String categoryToAddFromSearchBar = searchBar.getText().toString();
        @Override
        protected void onPreExecute() {
        findViewById(R.id.loadingAnimation_searchCategory).setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(String... params) {
            try{
                String link = "http://www.ratedapp.net/nati/addcategory.php";
                String command = params[0]; //add
                String categoryToAdd = categoryToAddFromSearchBar;
                String data = URLEncoder.encode("command","UTF-8") + "=" + URLEncoder.encode(command,"UTF-8");
                data += "&" + URLEncoder.encode("categorytoadd", "UTF-8") + "=" + URLEncoder.encode(categoryToAdd,"UTF-8");
                Log.d(TAG,"The data in add category-->"+data);

                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();

                while((line = reader.readLine())!= null){
                    Log.d(TAG,"the line-->"+ line);
                    sb.append(line);
                    break;
                }
                return sb.toString();

            }catch(Exception e){e.printStackTrace();}
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG,"the S-->"+ s);
            findViewById(R.id.loadingAnimation_searchCategory).setVisibility(View.GONE);
            searchBar.setText("");
            findViewById(R.id.addorremove_LL_searchCategory).setVisibility(View.GONE);
            findViewById(R.id.noCategory_TV_searchCategory).setVisibility(View.GONE);
            Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();

        }
    }


    public class SearchCategoryTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.loadingAnimation_searchCategory).setVisibility(View.VISIBLE);
            searchCategory_LV.setVisibility(View.GONE);
            findViewById(R.id.noCategory_TV_searchCategory).setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "in the search task");
            String thesearch = params[0];
            String link = "http://www.ratedapp.net/nati/searchcategory.php";
            try {
                String data = URLEncoder.encode("thesearch", "UTF-8") + "=" + URLEncoder.encode(thesearch, "UTF-8");
                Log.d(TAG, "the data-->" + data);

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
                    Log.d(TAG, "the line-->" + line);
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
            Log.d(TAG, "the s-->" + s);

            if (!s.equals("noresult")) {
                try {
                    listAdapter.clear();
                    JSONArray jsonArray = new JSONArray(s);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String lineresult = jsonObject.getString("category");
                        Log.d(TAG, "the search result JSON --> " + lineresult);
                        results.add(lineresult);


                    }
                    searchCategory_LV.setAdapter(listAdapter);
                    findViewById(R.id.addorremove_LL_searchCategory).setVisibility(View.GONE);
                    findViewById(R.id.loadingAnimation_searchCategory).setVisibility(View.GONE);

                    searchCategory_LV.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (s.equals("noresult")) {
                findViewById(R.id.loadingAnimation_searchCategory).setVisibility(View.GONE);
                findViewById(R.id.addorremove_LL_searchCategory).setVisibility(View.VISIBLE);

                findViewById(R.id.noCategory_TV_searchCategory).setVisibility(View.VISIBLE);
            }


        }
    }
}

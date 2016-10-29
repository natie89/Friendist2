package com.natimeital.friendist;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Moti on 23/05/2016.
 */
public class SendMessageFragment extends DialogFragment {
    public String sendingUsername,receivingUsername;
    ImageButton sendMessage,cancelMessage;
    EditText theMessageET;
    TextView usernameTV,messageToTitle;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater inflator = LayoutInflater.from(getActivity());

        View view = inflator.inflate(R.layout.send_message_layout,container,false);
        usernameTV = (TextView)view.findViewById(R.id.toUsername_TV_messageFragment);
        theMessageET = (EditText)view.findViewById(R.id.theMessage_ET_messageFragment);
        sendMessage = (ImageButton)view.findViewById(R.id.sendMessage_BTN_messageFragment);
        cancelMessage = (ImageButton)view.findViewById(R.id.cancelMessage_BTN_messageFragment);
        messageToTitle = (TextView)view.findViewById(R.id.sendmessagetoText_TV);

        getDialog().setTitle(R.string.sendmessage_fragment_Title);
        cancelMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Bundle mArgs = getArguments();
         sendingUsername = mArgs.getString("sendingUsername");
         receivingUsername = mArgs.getString("receivingUsername");
        usernameTV.setText(receivingUsername);

        Log.d("FragmentMan", mArgs.getString("replyfragment"));
        Log.d("FragmentMan", sendingUsername);
        if(mArgs.getString("replyfragment").equals("reply")){
            messageToTitle.setText(R.string.replyto_fragmentTitle);


        }
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendMessage().execute();
            }
        });

        return view;
    }
    class SendMessage extends AsyncTask<String,Void,String>{
        String theMessage = theMessageET.getText().toString();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(), R.string.sending_message_Fragment, Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... params) {
            String link = "http://www.ratedapp.net/nati/sendmessage.php";
            String fromUsername = sendingUsername;
            String toUsername = receivingUsername;
            String message = theMessage;
            try {
                String data = URLEncoder.encode("from", "UTF-8") + "=" + URLEncoder.encode(fromUsername, "UTF-8");
                data += "&" + URLEncoder.encode("to","UTF-8") + "=" + URLEncoder.encode(toUsername,"UTF-8");
                data += "&" + URLEncoder.encode("message","UTF-8") + "=" + URLEncoder.encode(message,"UTF-8");
                Log.d("MessageSent",data);
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(data);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();

                while((line = reader.readLine()) != null){
                    sb.append(line);
                    break;
                }
                return sb.toString();


            }catch (Exception e){e.printStackTrace();}

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("MessageSent",s);
            if(s!=null){
                if(s.equals("success")){
                    Toast.makeText(getActivity(), R.string.message_Sent_fragment, Toast.LENGTH_SHORT).show();
                    dismiss();
                }else {
                    Toast.makeText(getActivity(), R.string.message_notsent_fragment, Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

}

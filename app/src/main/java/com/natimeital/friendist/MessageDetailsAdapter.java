package com.natimeital.friendist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Moti on 23/05/2016.
 */
public class MessageDetailsAdapter extends ArrayAdapter<MessageObject> {
    TextView fromUsernameTV, toUsernameTV,theMessageTV,dateTV;

    public MessageDetailsAdapter(Context context, ArrayList<MessageObject> messegeObject){
        super(context,R.layout.messege_row, (ArrayList<MessageObject>) messegeObject);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        MessageObject theMessageObject =getItem(position);
        LayoutInflater inflator = LayoutInflater.from(getContext());
        View theview = inflator.inflate(R.layout.messege_row, parent, false);
        fromUsernameTV = (TextView)theview.findViewById(R.id.sendinguser_TV_inboxrowlayout);
        toUsernameTV = (TextView) theview.findViewById(R.id.recievinguser_TV_inboxrowlayout);
        theMessageTV = (TextView) theview.findViewById(R.id.themessege_TV_inboxrowlayout);
        dateTV = (TextView)theview.findViewById(R.id.datetime_TV_inboxrowlayout);

        fromUsernameTV.setText(theMessageObject.sendingUser);
        toUsernameTV.setText(theMessageObject.recievingUser);
        theMessageTV.setText(theMessageObject.theMessege);
        dateTV.setText(theMessageObject.thedate);


        return theview;
    }
}

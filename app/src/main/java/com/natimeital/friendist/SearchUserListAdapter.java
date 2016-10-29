package com.natimeital.friendist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 19/05/2016.
 */
public class SearchUserListAdapter extends ArrayAdapter<SearchableUserDetails> {

    public SearchUserListAdapter(Context context, ArrayList<SearchableUserDetails> theUser) {
        super(context,R.layout.user_row, (ArrayList<SearchableUserDetails>) theUser);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SearchableUserDetails user = getItem(position);
        LayoutInflater inflator = LayoutInflater.from(getContext());
        View theview = inflator.inflate(R.layout.user_row, parent, false);


        TextView usernameTV = (TextView)theview.findViewById(R.id.username_TV_LV_SearchUser);
        TextView firstnameTV = (TextView)theview.findViewById(R.id.firstname_TV_LV_SearchUser);
        TextView lastnameTV = (TextView)theview.findViewById(R.id.lastname_TV_LV_SearchUser);


        ImageView profilePhoto = (ImageView)theview.findViewById(R.id.userphoto_IV_LV_SearchUser);
        new ImageLoadTask(user.photoLink,profilePhoto).execute();

        usernameTV.setText(user.username);
        firstnameTV.setText(user.firstName);
        lastnameTV.setText(user.lastName);

        return theview;


    }
}

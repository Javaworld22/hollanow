package com.doxa360.android.betacaller.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.doxa360.android.betacaller.R;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Apple on 12/04/16.
 */

public class UsersListAdapter extends BaseAdapter {
    Context mContext;
    List<ParseUser> mUserList;
    private ViewHolder holder;

    public UsersListAdapter(Context context, List<ParseUser> userList) {
        mContext = context;
        mUserList = userList;
    }

    @Override
    public int getCount() {
        if (mUserList != null)
            return mUserList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int i) {
        if (mUserList != null)
            return mUserList.get(i);
        else
            return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.search_layout, viewGroup, false);
            holder = new ViewHolder();
            ParseUser user = mUserList.get(i);
            holder.username= (TextView) view.findViewById(R.id.user_full_name);
            holder.phone = (TextView) view.findViewById(R.id.phone);
            holder.photo = (ImageView) view.findViewById(R.id.profile_photo);

            holder.username.setText(user.getString("name"));
            holder.phone.setText(user.getString("phoneNumber"));
            if (user.getParseFile("photo")!=null) {
                Picasso.with(mContext)
                        .load(user.getParseFile("photo").getUrl())
                        .into(holder.photo);
                Log.e("photo", user.getParseFile("photo").getUrl());
            } else {
                Picasso.with(mContext)
                        .load(R.drawable.wil_profile)
                        .into(holder.photo);
            }
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        return view;

    }

    private static class ViewHolder{
        TextView username;
        TextView phone;
        ImageView photo;
    }


}

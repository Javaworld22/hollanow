//package com.doxa360.android.betacaller.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.doxa360.android.betacaller.LykeDetailActivity;
//import com.doxa360.android.betacaller.R;
//import com.doxa360.android.betacaller.helpers.MyToolBox;
//import com.doxa360.android.betacaller.model.Lyke;
//import com.parse.GetCallback;
//import com.parse.ParseException;
//import com.parse.ParseObject;
//import com.squareup.picasso.Callback;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
///**
// * Created by Apple on 09/01/16.
// */
//public class LykeAdapter extends RecyclerView.Adapter<LykeAdapter.LykeViewHolder> {
//
//    private List<Lyke> mLykeList;
//    LykeViewHolder mHolder;
//    private Context mContext;
//    private LayoutInflater mLayoutInflater;
//
//    public LykeAdapter(List<Lyke> lykeList, Context context) {
//        mLykeList = lykeList;
//        mContext = context;
//        mLayoutInflater = LayoutInflater.from(context);
//    }
//
//    public class LykeViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView mLykePhoto;
//        ImageView mUserPhoto;
//        TextView mUsername;
//        TextView mLykeName;
//        TextView mLykeCategory;
//        TextView mTimeAgo;
//
//        public LykeViewHolder(View itemView) {
//            super(itemView);
//
//            mLykePhoto = (ImageView) itemView.findViewById(R.id.lyke_photo);
//            mUserPhoto = (ImageView) itemView.findViewById(R.id.lyke_user_photo);
//            mUsername = (TextView) itemView.findViewById(R.id.lyke_user);
//            mLykeName = (TextView) itemView.findViewById(R.id.lyke_name);
//            mLykeCategory = (TextView) itemView.findViewById(R.id.lyke_category);
//            mTimeAgo = (TextView) itemView.findViewById(R.id.lyke_timeago);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(mContext, LykeDetailActivity.class);
//                    intent.putExtra("LYKE_ID", mLykeList.get(getPosition()).getObjectId());
//                    intent.putExtra("LYKE_NAME", mLykeList.get(getPosition()).getLyke());
//                    intent.putExtra("LYKE_OWNER", mLykeList.get(getPosition()).getUser().getString("name"));
//                    intent.putExtra("LYKE_OWNER_ID", mLykeList.get(getPosition()).getUser().getObjectId());
//                    if (mLykeList.get(getPosition()).getUser().getParseFile("photo") != null) {
//                        intent.putExtra("LYKE_OWNER_PHOTO", mLykeList.get(getPosition()).getUser().getParseFile("photo").getUrl());
//                    } else {
//                        intent.putExtra("LYKE_OWNER_PHOTO", "");
//                    }
//                    intent.putExtra("LYKE_PHOTO", mLykeList.get(getPosition()).getPhoto().getUrl());
//                    intent.putExtra("LYKE_GROUPS", MyToolBox.listToString(mLykeList.get(getPosition()).getGroupNames()));
//                    mContext.startActivity(intent);
//                }
//            });
//        }
//    }
//
//    @Override
//    public LykeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = mLayoutInflater.inflate(R.layout.lyke_layout, parent, false);
//        return new LykeViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final LykeViewHolder holder, int position) {
//        Lyke lyke = mLykeList.get(position);
//        mHolder = holder;
//
//        holder.mUsername.setText(lyke.getUser().getString("name"));
//        holder.mLykeName.setText(lyke.getLyke());
////        holder.mListName.setText(lyke.getCategory().getString("category"));
//        mHolder.mTimeAgo.setText(MyToolBox.getShortTimeAgo(lyke.getCreatedAt().getTime(), mContext));
//
//        if (lyke.getUser().getParseFile("photo") != null) {
//            Picasso.with(mContext)
//                    .load(lyke.getUser().getParseFile("photo").getUrl())
//                    .into(holder.mUserPhoto);
//        }
//        if (lyke.getPhoto() != null) {
//            Picasso.with(mContext)
//                    .load(lyke.getPhoto().getUrl())
//                    .into(holder.mLykePhoto);
//        }
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        if (mLykeList != null)
//            return mLykeList.size();
//        else
//            return 0;
//    }
//
//
//
//
//}

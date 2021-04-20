package com.example.administrator.share.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.entity.FollowsListItem;
import com.example.administrator.share.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewFansListAdapter extends RecyclerView.Adapter<NewFansListAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<FollowsListItem> mList;


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nicknameTv;
        TextView followTimeTv;
        ImageView faceIv;
        public ViewHolder(View view) {
            super(view);
            nicknameTv = view.findViewById(R.id.tv_msg_newfans_nickname);
            followTimeTv = view.findViewById(R.id.tv_msg_newfans_time);
            faceIv = view.findViewById(R.id.iv_msg_newfans_image);
        }
    }

    public NewFansListAdapter(Context mContext, List<FollowsListItem> mList) {
        this.mList = mList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = inflater.inflate(R.layout.item_new_fans, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowsListItem detail = mList.get(position);
        holder.nicknameTv.setText(detail.getNickName()+"关注了你");
        String time = detail.getFollowTime();
        try {
            if(isOldTime(time)){
                //设置为年月日
                holder.followTimeTv.setText(time.substring(0,11));
            }else{
                //设置为时分
                holder.followTimeTv.setText("今天 "+time.substring(11,16));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        getImage(detail.getFace(),holder);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    private void getImage(final String imageName, final ViewHolder holder) {
        Uri uri = Uri.parse(Constants.BASE_URL+"Download?method=getUserFaceImage&face="+imageName);
        Glide.with(mContext).load(uri).into(holder.faceIv);
    }

    public void updateList(List<FollowsListItem> newDatas) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        notifyDataSetChanged();
    }

    private boolean isOldTime(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = sdf.format(new Date());
        String standardTime = nowTime.substring(0,11)+"00:00:00";
        Date date1 = sdf.parse(standardTime);
        Date date2 = sdf.parse(time);
        return date1.getTime() > date2.getTime();
    }
}

package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.CommonListItem;
import com.example.administrator.share.util.Constants;
import com.startsmake.mainnavigatetabbar.widget.BadgeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class FavorListAdapter extends RecyclerView.Adapter<FavorListAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<CommonListItem> mList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nicknameTv;
        TextView badgeTv;
        ImageView faceIv;
        ImageView imageIv;
        TextView favorTimeTv;
        BadgeView badge;
        RelativeLayout messageLl;
        public ViewHolder(View view) {
            super(view);
            nicknameTv = view.findViewById(R.id.tv_favor_nickname);
            badgeTv = view.findViewById(R.id.tv_badge);
            faceIv = view.findViewById(R.id.iv_favor_face);
            imageIv = view.findViewById(R.id.iv_image);
            favorTimeTv = view.findViewById(R.id.tv_favor_time);
            messageLl = view.findViewById(R.id.ll_message);
        }
    }

    public FavorListAdapter(Context mContext, List<CommonListItem> mList) {
        this.mContext=mContext;
        this.mList = mList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = inflater.inflate(R.layout.item_favor, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.messageLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                CommonListItem commonListItem = mList.get(position);
                final int favorId = commonListItem.getFavorId();
                Intent intent = new Intent(mContext, CircleDetailActivity.class);
                intent.putExtra("newsId", commonListItem.getNewsId());
                intent.putExtra("be_focused_personId", mList.get(position).getUserId());
                mContext.startActivity(intent);
                if(commonListItem.getIsVisited() == 0){
                    new AsyncTask<Void, Void, Integer>(){
                        @Override
                        protected Integer doInBackground(Void... voids) {
                            String url = Constants.BASE_URL + "Message?method=updateFavorStatus";
                            OkHttpUtils
                                    .post()
                                    .url(url)
                                    .addParams("favorId", String.valueOf(favorId))
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                        }
                                    });
                            return 0;
                        }
                    }.execute();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final CommonListItem detail = mList.get(position);
        holder.nicknameTv.setText(detail.getNickname()+"赞了你");
        String time = detail.getFavorTime();
        try {
            if(isOldTime(time)){
                //设置为年月日
                holder.favorTimeTv.setText(time.substring(0,11));
            }else{
                //设置为时分
                holder.favorTimeTv.setText("今天 "+time.substring(11,16));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse(Constants.BASE_URL+"Download?method=getUserFaceImage&face="+detail.getFace());
        Glide.with(mContext).load(uri).into(((ViewHolder)holder).faceIv);
        uri = Uri.parse(Constants.BASE_URL+"Download?method=getNewsImage&imageName="+detail.getImage());
        Glide.with(mContext).load(uri).into(((ViewHolder)holder).imageIv);
        if(detail.getIsVisited()==0){
            holder.badge = new BadgeView(mContext);
            holder.badge.setTargetView(holder.badgeTv);
            holder.badge.setBadgeCount(1);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private boolean isOldTime(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = sdf.format(new Date());
        String standardTime = nowTime.substring(0,11)+"00:00:00";
        Date date1 = sdf.parse(standardTime);
        Date date2 = sdf.parse(time);
        return date1.getTime() > date2.getTime();
    }

    public void updateList(List<CommonListItem> newDatas) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        notifyDataSetChanged();
    }
}

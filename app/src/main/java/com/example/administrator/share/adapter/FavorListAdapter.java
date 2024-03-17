package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.FavorMsgListItem;
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
    private final LayoutInflater inflater;
    private final List<FavorMsgListItem> mList;

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

    public FavorListAdapter(Context mContext, List<FavorMsgListItem> mList) {
        this.mContext = mContext;
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
                FavorMsgListItem favorMsgListItem = mList.get(position);
                final int favorId = favorMsgListItem.getFavorId();
                Intent intent = new Intent(mContext, CircleDetailActivity.class);
                intent.putExtra("newsId", favorMsgListItem.getNewsId());
                intent.putExtra("authorId", mList.get(position).getAuthorId());
                mContext.startActivity(intent);
                if (favorMsgListItem.getIsVisited() == 0) {
                    String url = Constants.BASE_URL + "favors/updateFavorStatus";
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
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final FavorMsgListItem detail = mList.get(position);
        holder.nicknameTv.setText(detail.getNickname() + "赞了你");
        String time = detail.getFavorTime();
        try {
            if (isOldTime(time)) {
                //设置为年月日
                holder.favorTimeTv.setText(time.substring(0, 11));
            } else {
                //设置为时分
                holder.favorTimeTv.setText("今天 " + time.substring(11, 16));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse(Constants.BASE_URL + "download/getImage?face=" + detail.getFace());
        Glide.with(mContext).load(uri).into(((ViewHolder) holder).faceIv);
        uri = Uri.parse(Constants.BASE_URL + "download/getImage?imageName=" + detail.getImage());
        Glide.with(mContext).load(uri).into(((ViewHolder) holder).imageIv);
        if (detail.getIsVisited() == 0) {
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
        String standardTime = nowTime.substring(0, 11) + "00:00:00";
        Date date1 = sdf.parse(standardTime);
        Date date2 = sdf.parse(time);
        return date1.getTime() > date2.getTime();
    }

    public void updateList(List<FavorMsgListItem> newDatas) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        notifyDataSetChanged();
    }
}

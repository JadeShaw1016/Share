package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.CircleList;
import com.example.administrator.share.util.Constants;

import java.util.List;

public class FirstPageListAdapter0 extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "FirstPageListAdapter0";
    private Context mContext;
    private LayoutInflater mInflater;
    private List<CircleList> mList;
    private int normalType = 0;
    private int footType = 1;
    private boolean hasMore;
    private boolean fadeTips = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bgIv;
        TextView titleTv;
        TextView nicknameTv;
        public ViewHolder(View view) {
            super(view);
            bgIv = view.findViewById(R.id.found_list_icon);
            titleTv = view.findViewById(R.id.found_list_item_title);
            nicknameTv = view.findViewById(R.id.found_list_item_nickname);
        }
    }

    static class FootHolder extends RecyclerView.ViewHolder {
        private TextView tips;

        public FootHolder(View itemView) {
            super(itemView);
            tips = itemView.findViewById(R.id.tips);
        }
    }

    public FirstPageListAdapter0(Context mContext, List<CircleList> mList, boolean hasMore) {
        this.mContext=mContext;
        mInflater=LayoutInflater.from(mContext);
        this.mList = mList;
        this.hasMore = hasMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        final RecyclerView.ViewHolder holder;
        if (viewType == normalType) {
            View view = mInflater.inflate(R.layout.item_firstpage_image, parent, false);
            holder = new ViewHolder(view);
            ((ViewHolder)holder).bgIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    Intent intent = new Intent(mContext, CircleDetailActivity.class);
                    intent.putExtra("newsId", mList.get(position).getNewsId());
                    intent.putExtra("be_focused_personId", mList.get(position).getUserId());
                    mContext.startActivity(intent);
                }
            });
        } else {
            View view = mInflater.inflate(R.layout.footview, parent, false);
            holder = new FootHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            CircleList circleList = mList.get(position);
            final String imageName = circleList.getImage();
            ((ViewHolder)holder).titleTv.setText(circleList.getTitle());
            ((ViewHolder)holder).nicknameTv.setText(circleList.geNickName());
            Uri uri = Uri.parse(Constants.BASE_URL+"download/getImage?imageName="+imageName);
            Glide.with(mContext).load(uri).into(((ViewHolder)holder).bgIv);
        } else {
            ((FootHolder) holder).tips.setVisibility(View.VISIBLE);
            if (hasMore) {
                fadeTips = false;
                if (mList.size() > 0) {
                    ((FootHolder) holder).tips.setText("正在加载更多...");
                }
            } else {
                if (mList.size() > 0) {
                    ((FootHolder) holder).tips.setText("没有更多数据了");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((FootHolder) holder).tips.setVisibility(View.GONE);
                            fadeTips = true;
                            hasMore = true;
                        }
                    }, 500);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size()+1;
    }

    public void updateList(List<CircleList> newDatas, boolean hasMore) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public boolean isFadeTips() {
        return fadeTips;
    }

    public int getRealLastPosition() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }
}

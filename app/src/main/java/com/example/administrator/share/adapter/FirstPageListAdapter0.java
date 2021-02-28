package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.CircleListForFound;
import com.example.administrator.share.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class FirstPageListAdapter0 extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "FirstPageListAdapter0";
    private Context mContext;
    private LayoutInflater mInflater;
    private List<CircleListForFound> mList;
    private int normalType = 0;
    private int footType = 1;
    private boolean hasMore;
    private boolean fadeTips = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bgIv;
        TextView titleTv;
        TextView usernameTv;
        public ViewHolder(View view) {
            super(view);
            bgIv = view.findViewById(R.id.found_list_icon);
            titleTv = view.findViewById(R.id.found_list_item_title);
            usernameTv = view.findViewById(R.id.found_list_item_username);
        }
    }

    static class FootHolder extends RecyclerView.ViewHolder {
        private TextView tips;

        public FootHolder(View itemView) {
            super(itemView);
            tips = itemView.findViewById(R.id.tips);
        }
    }

    public FirstPageListAdapter0(Context mContext, List<CircleListForFound> mList, boolean hasMore) {
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
            View view = mInflater.inflate(R.layout.item_circle, parent, false);
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
            CircleListForFound news = mList.get(position);
            final String imageName = news.getImage();
            ((ViewHolder)holder).titleTv.setText(news.getTitle());
            ((ViewHolder)holder).usernameTv.setText(news.getUsername());
            new AsyncTask<Void, Void, Integer>(){

                @Override
                protected Integer doInBackground(Void... voids) {
                    String url = Constants.BASE_URL + "Download?method=getNewsImage";
                    OkHttpUtils
                            .get()//
                            .url(url)//
                            .addParams("imageName", imageName)
                            .build()//
                            .execute(new BitmapCallback() {
                                @Override
                                public void onError(Call call, Exception e, int i) {
                                }
                                @Override
                                public void onResponse(Bitmap bitmap, int i) {
                                    ((ViewHolder)holder).bgIv.setImageBitmap(bitmap);
                                }
                            });
                    return 0;
                }

            }.execute();
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

    public void updateList(List<CircleListForFound> newDatas,boolean hasMore) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public boolean isFadeTips() {
        return fadeTips;
    }

    public void resetDatas() {
        mList = new ArrayList<>();
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

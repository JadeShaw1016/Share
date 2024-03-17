package com.example.administrator.share.adapter;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.activity.FruitActivity;
import com.example.administrator.share.util.DownloadButton;

import java.util.List;
import java.util.Map;

public class FirstPageListAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final List<Map<String, String>> mapList;
    private boolean isDownloading;
    private final int normalType = 0;
    private boolean hasMore;
    private boolean fadeTips = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView pic;
        TextView text;
        TextView time;
        DownloadButton btn;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            pic = view.findViewById(R.id.item_pic);
            text = view.findViewById(R.id.item_text);
            time = view.findViewById(R.id.item_time);
            btn = view.findViewById(R.id.download_btn);
        }
    }

    static class FootHolder extends RecyclerView.ViewHolder {
        private final TextView tips;

        public FootHolder(View itemView) {
            super(itemView);
            tips = itemView.findViewById(R.id.tips);
        }
    }

    public FirstPageListAdapter2(Context mContext, List<Map<String, String>> mapList, boolean hasMore) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        this.mapList = mapList;
        this.hasMore = hasMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder;
        if (viewType == normalType) {
            View view = mInflater.inflate(R.layout.item_firstpage_ganhuo, parent, false);
            holder = new ViewHolder(view);
            ((ViewHolder) holder).mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    Intent intent = new Intent(mContext, FruitActivity.class);
                    intent.putExtra("text", (CharSequence) mapList.get(position).get("text"));
                    intent.putExtra("uri", Uri.parse(mapList.get(position).get("picUri")));
                    mContext.startActivity(intent);
                }
            });
            ((ViewHolder) holder).btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CompleteReceiver completeReceiver = new CompleteReceiver();
                    mContext.registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    if (isDownloading) {
                        Toast.makeText(mContext, "当前已在进行下载，请等待下载完成", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "开始下载...", Toast.LENGTH_SHORT).show();
                        isDownloading = true;
                        Uri uri = ((DownloadButton) view).getUri();
                        DownloadManager downloadManager;
                        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "BingPic" + ((DownloadButton) view).getTime() + ".jpg");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        downloadManager.enqueue(request);
                    }
                }
            });
        } else {
            View view = mInflater.inflate(R.layout.footview, parent, false);
            holder = new FootHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).text.setText(mapList.get(position).get("text"));
            ((ViewHolder) holder).time.setText(mapList.get(position).get("time"));
            Uri uri = Uri.parse(mapList.get(position).get("picUri"));
            Glide.with(mContext).load(uri).into(((ViewHolder) holder).pic);
            isDownloading = false;
            ((ViewHolder) holder).btn.setUri(uri);
            ((ViewHolder) holder).btn.setTime(mapList.get(position).get("time"));
        } else {
            ((FootHolder) holder).tips.setVisibility(View.VISIBLE);
            if (hasMore) {
                fadeTips = false;
                if (mapList.size() > 0) {
                    ((FootHolder) holder).tips.setText("正在加载更多...");
                }
            } else {
                if (mapList.size() > 0) {
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
        return mapList.size() + 1;
    }

    class CompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isDownloading) {
                Toast.makeText(mContext, "图片下载完成", Toast.LENGTH_SHORT).show();
                isDownloading = false;
            }
            context.unregisterReceiver(this);
        }
    }

    public void updateList(List<Map<String, String>> newDatas, boolean hasMore) {
        if (newDatas != null) {
            mapList.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public boolean isFadeTips() {
        return fadeTips;
    }

    public int getRealLastPosition() {
        return mapList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return 1;
        } else {
            return normalType;
        }
    }
}

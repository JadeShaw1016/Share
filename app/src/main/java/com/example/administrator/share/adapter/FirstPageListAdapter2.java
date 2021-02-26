package com.example.administrator.share.adapter;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.FruitActivity;
import com.example.administrator.share.activity.MainMenuActivity;
import com.example.administrator.share.util.DownloadButton;

import java.util.List;
import java.util.Map;

public class FirstPageListAdapter2 extends RecyclerView.Adapter<FirstPageListAdapter2.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Map<String,Object>> mapList;
    private boolean isDownloading;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View fruitView;
        ImageView pic;
        TextView text;
        TextView time;
        DownloadButton btn;

        public ViewHolder(View view) {
            super(view);
            fruitView = view;
            pic=view.findViewById(R.id.item_pic);
            text=view.findViewById(R.id.item_text);
            time=view.findViewById(R.id.item_time);
            btn=view.findViewById(R.id.download_btn);
        }
    }

    public FirstPageListAdapter2(Context mContext, List<Map<String,Object>> mapList) {
        this.mContext=mContext;
        mInflater=LayoutInflater.from(mContext);
        this.mapList=mapList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_firstpage, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.fruitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(mContext, FruitActivity.class);
                intent.putExtra("text",(CharSequence) mapList.get(position).get("text"));
                intent.putExtra("uri", Uri.parse((String)mapList.get(position).get("picUri")));
                mContext.startActivity(intent);
            }
        });
        holder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //大图所依附的dialog
                int position = holder.getAdapterPosition();
                final Dialog dialog = new Dialog(MainMenuActivity.mContext, R.style.MyDialogStyle_float_center);
                ImageView imageView = new ImageView(MainMenuActivity.mContext);
                imageView.setImageBitmap((Bitmap) mapList.get(position).get("pic"));
                dialog.setContentView(imageView);
                dialog.show();

                //大图的点击事件（点击让他消失）
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isDownloading){
                    Toast.makeText(mContext, "当前已在进行下载，请等待下载完成", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mContext, "开始下载...", Toast.LENGTH_SHORT).show();
                    isDownloading=true;
                    Uri uri = ((DownloadButton)view).getUri();
                    DownloadManager downloadManager;
                    downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "BingPic"+((DownloadButton)view).getTime()+".jpg");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    long reference = downloadManager.enqueue(request);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap bitmap = (Bitmap) mapList.get(position).get("pic");
        holder.pic.setImageBitmap(bitmap);
        holder.text.setText((CharSequence) mapList.get(position).get("text"));
        holder.time.setText((CharSequence) mapList.get(position).get("time"));

        Uri uri = Uri.parse((String)mapList.get(position).get("picUri"));
        isDownloading=false;
        CompleteReceiver completeReceiver = new CompleteReceiver();
        mContext.registerReceiver(completeReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        holder.btn.setUri(uri);
        holder.btn.setTime((String)mapList.get(position).get("time"));
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    class CompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isDownloading){
                Toast.makeText(mContext, "图片下载完成", Toast.LENGTH_SHORT).show();
                isDownloading=false;
            }
        }
    }
}

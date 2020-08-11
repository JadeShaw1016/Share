package com.example.administrator.share.adapter;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.share.R;
import com.example.administrator.share.util.DataResource;
import com.example.administrator.share.util.DownloadButton;

import java.util.List;
import java.util.Map;

public class FirstPageListAdapter extends BaseAdapter
{
    Context mContext;
    private LayoutInflater mInflater;
    private List<Map<String,Object>> mapList;
    private DataResource data;
    private boolean isDownloading;

    public FirstPageListAdapter(Context mContext, List<Map<String,Object>> mapList, DataResource data){
        this.mContext=mContext;
        mInflater=LayoutInflater.from(mContext);
        this.mapList=mapList;
        this.data=data;
    }

    @Override
    public int getCount() {
        return mapList.size();
    }

    @Override
    public Object getItem(int i) {
        return mapList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView=mInflater.inflate(R.layout.items_firstpage,null);
            viewHolder.pic=convertView.findViewById(R.id.item_pic);
            viewHolder.text=convertView.findViewById(R.id.item_text);
            viewHolder.time=convertView.findViewById(R.id.item_time);
            viewHolder.btn=convertView.findViewById(R.id.download_btn);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        Uri uri = Uri.parse((String)data.getList().get(position).get("picUri"));
//        viewHolder.pic.setImageURI(uri);
        Bitmap bitmap = (Bitmap) data.getList().get(position).get("pic");
        viewHolder.pic.setImageBitmap(bitmap);
        viewHolder.text.setText((CharSequence) data.getList().get(position).get("text"));
        viewHolder.time.setText((CharSequence) data.getList().get(position).get("time"));

        isDownloading=false;
        CompleteReceiver completeReceiver = new CompleteReceiver();
        mContext.registerReceiver(completeReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        viewHolder.btn.setUri(uri);
        viewHolder.btn.setTime((String)data.list.get(position).get("time"));
        viewHolder.btn.setOnClickListener(new View.OnClickListener() {
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
        return convertView;
    }

    private class ViewHolder{
        ImageView pic;
        TextView text;
        TextView time;
        DownloadButton btn;
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


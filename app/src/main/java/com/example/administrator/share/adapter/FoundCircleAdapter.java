package com.example.administrator.share.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.entity.CircleListForFound;
import com.example.administrator.share.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by djzhao on 17/04/30.
 */

public class FoundCircleAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<CircleListForFound> mList;
    private ListView mListView;

    public FoundCircleAdapter(Context mContext, List<CircleListForFound> mList) {
        this.inflater = LayoutInflater.from(mContext);
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(mListView == null){
            mListView = (ListView)parent;
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_circle, null);
            viewHolder = new ViewHolder();
            viewHolder.bgIv = (ImageView) convertView.findViewById(R.id.found_list_icon);
            viewHolder.titleTv = (TextView) convertView.findViewById(R.id.found_list_item_title);
            viewHolder.usernameTv = (TextView) convertView.findViewById(R.id.found_list_item_username);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CircleListForFound news = mList.get(position);
        String imageName = news.getImage();
        viewHolder.bgIv.setTag(imageName);
        viewHolder.titleTv.setText(news.getTitle());
        viewHolder.usernameTv.setText(news.getUsername());
        if(imageName!=null){
            BitmapWorkerTask task = new BitmapWorkerTask();
            task.execute(imageName);
        }

        return convertView;
    }

    private class  ViewHolder {
        public ImageView bgIv;
        public TextView titleTv;
        public TextView usernameTv;
    }


    class BitmapWorkerTask extends AsyncTask<String, Void, Integer> {

        private String imageName;

        @Override
        protected Integer doInBackground(String... params) {
            String url = Constants.BASE_URL + "Download?method=getNewsImage";
            imageName = params[0];
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
                            ImageView imageView = (ImageView) mListView.findViewWithTag(imageName);
                            if (imageView != null && bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    });
            return 0;
        }
    }

}

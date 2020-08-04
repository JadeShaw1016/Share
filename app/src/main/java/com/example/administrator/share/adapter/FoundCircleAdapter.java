package com.example.administrator.share.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

    private Context mContext;
    private LayoutInflater inflater;
    private List<CircleListForFound> mList;

    public FoundCircleAdapter(Context mContext, List<CircleListForFound> mList) {
        this.inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
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
    ViewHolder holder;
    if (convertView == null) {
        convertView = inflater.inflate(R.layout.item_circle, null);
        holder = new ViewHolder();
        holder.bgIv = (ImageView) convertView.findViewById(R.id.found_list_icon);
        holder.titleTv = (TextView) convertView.findViewById(R.id.found_list_item_title);
        holder.usernameTv = (TextView) convertView.findViewById(R.id.found_list_item_username);
        convertView.setTag(holder);
    } else {
        holder = (ViewHolder) convertView.getTag();
    }

    // fill data
    CircleListForFound news = mList.get(position);
    holder.titleTv.setText(news.getTitle());
    holder.usernameTv.setText(news.getUsername());

    if(news.getImage()!=null){
        getNewsImage(news.getImage(),holder.bgIv);
    }

    return convertView;
}

    private class  ViewHolder {
        public ImageView bgIv;
        public TextView titleTv;
        public TextView usernameTv;
    }

    private void getNewsImage(String imageName, final ImageView imageView) {
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
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }
}

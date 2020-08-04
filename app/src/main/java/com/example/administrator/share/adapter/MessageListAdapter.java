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
import com.example.administrator.share.entity.NewsListItem;
import com.example.administrator.share.util.Constants;
import com.startsmake.mainnavigatetabbar.widget.BadgeView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

/**
 * Created by djzhao on 17/05/04.
 */

public class MessageListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<NewsListItem> mList;

    public MessageListAdapter(Context mContext, List<NewsListItem> mList) {
        this.mList = mList;
        this.inflater = LayoutInflater.from(mContext);
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_message, null);
            viewHolder = new ViewHolder();
            viewHolder.usernameTv = (TextView) convertView.findViewById(R.id.tv_msg_username);
            viewHolder.commentTv = (TextView) convertView.findViewById(R.id.tv_msg_comment);
            viewHolder.badgeTv = (TextView)convertView.findViewById(R.id.tv_badge);
            viewHolder.imageTv = (ImageView)convertView.findViewById(R.id.iv_image);
            viewHolder.commentTimeTv = (TextView)convertView.findViewById(R.id.tv_comment_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // fill data
        NewsListItem detail = mList.get(position);
        viewHolder.usernameTv.setText(detail.getUsername()+"评论了你");
        viewHolder.commentTv.setText(detail.getComment());
        String time = detail.getCommentTime();
        try {
            if(isOldTime(time)){
                //设置为年月日
                viewHolder.commentTimeTv.setText(time.substring(0,11));
            }else{
                //设置为时分
                viewHolder.commentTimeTv.setText(time.substring(11,16));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(detail.getImage()!=null){
            getNewsImage(detail.getImage(),viewHolder.imageTv);
        }
//        System.out.println("第"+position+"条评论："+detail.getComment()+"的状态为"+detail.getStatus());
        if(detail.getStatus()==0){
            viewHolder.badge = new BadgeView(convertView.getContext());
            viewHolder.badge.setTargetView(viewHolder.badgeTv);
            viewHolder.badge.setBadgeCount(1);
        }
        return convertView;
    }


    private class ViewHolder {
        private TextView usernameTv;
        private TextView commentTv;
        private TextView badgeTv;
        private ImageView imageTv;
        private TextView commentTimeTv;
        private BadgeView badge;
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

    private boolean isOldTime(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = sdf.format(new Date());
        String standardTime = nowTime.substring(0,11)+"00:00:00";
        Date date1 = sdf.parse(standardTime);
        Date date2 = sdf.parse(time);
        if(date1.getTime()>date2.getTime()){
            return true;
        } else{
            return false;
        }
    }
}

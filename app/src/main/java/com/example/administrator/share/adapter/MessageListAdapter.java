package com.example.administrator.share.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.entity.NewsListItem;

import java.util.List;

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
            viewHolder.contentTv = (TextView) convertView.findViewById(R.id.tv_msg_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // fill data
        NewsListItem detail = mList.get(position);
        viewHolder.usernameTv.setText(detail.getTitle());
        viewHolder.contentTv.setText(detail.getUsername());

//        Comment comment = mList.get(position);
//        viewHolder.usernameTv.setText(comment.getUsername());
//        viewHolder.contentTv.setText(comment.getComment());
        return convertView;
    }

    private class ViewHolder {
        private TextView usernameTv;
        private TextView contentTv;
    }
}

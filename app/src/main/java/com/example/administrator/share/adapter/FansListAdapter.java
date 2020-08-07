package com.example.administrator.share.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.entity.FansListItem;

import java.util.List;

public class FansListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<FansListItem> mList;

    public FansListAdapter(Context mContext, List<FansListItem> mList) {
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
            convertView = inflater.inflate(R.layout.item_fans, null);
            viewHolder = new ViewHolder();
            viewHolder.username = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.description = (TextView) convertView.findViewById(R.id.tv_description);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // fill data
        FansListItem detail = mList.get(position);
        System.out.println("测试："+position);
        viewHolder.username.setText(detail.getFansName());
        return convertView;
    }

    private class ViewHolder {
        public TextView username;
        public TextView description;
    }
}

package com.example.administrator.share.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.entity.FansListItem;
import com.example.administrator.share.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

public class FansListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<FansListItem> mList;

    private int flag;

    public FansListAdapter(Context mContext, List<FansListItem> mList,int flag) {
        this.mList = mList;
        this.inflater = LayoutInflater.from(mContext);
        this.flag = flag;
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
            convertView = inflater.inflate(R.layout.item_fans_focus, null);
            viewHolder = new ViewHolder();
            viewHolder.usernameTv = convertView.findViewById(R.id.tv_username);
            viewHolder.descriptionTv = convertView.findViewById(R.id.tv_description);
            viewHolder.stateTv =convertView.findViewById(R.id.tv_state);
            viewHolder.stateIv = convertView.findViewById(R.id.iv_state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // fill data
        FansListItem detail = mList.get(position);
        viewHolder.usernameTv.setText(detail.getUserName());
        if(flag == 0){
            viewHolder.stateIv.setImageResource(R.drawable.icon_has_focused);
            viewHolder.stateTv.setText("\u0020\u0020已关注\u0020\u0020");
            isFocusedEachOther(detail.getUserId(),viewHolder);
        }else{
            viewHolder.stateIv.setImageResource(R.drawable.icon_add_focus);
            viewHolder.stateTv.setText("添加关注");
            isFocusedEachOther(detail.getFansId(),viewHolder);
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView usernameTv;
        public TextView descriptionTv;
        public TextView stateTv;
        public ImageView stateIv;
    }

    private void isFocusedEachOther(int fansId, final ViewHolder viewHolder) {

        String url = Constants.BASE_URL + "Follows?method=isFocusedEachOther";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("userId", Constants.USER.getUserId() + "")
                .addParams("fansId",fansId+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if(response.equals("true")){
                            viewHolder.stateIv.setImageResource(R.drawable.icon_focus_eachother);
                            viewHolder.stateTv.setText("互相关注");
                        }
                    }
                });
    }


}

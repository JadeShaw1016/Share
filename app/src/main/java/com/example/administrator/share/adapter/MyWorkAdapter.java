package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.CommonListItem;
import com.example.administrator.share.util.Constants;

import java.util.List;

public class MyWorkAdapter extends RecyclerView.Adapter<MyWorkAdapter.ViewHolder>{
    private static final String TAG = "MyWorkAdapter";
    private Context mContext;
    private LayoutInflater inflater;
    private List<CommonListItem> mList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView myworkImage;
        TextView myworkName;
        TextView clickTimes;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            myworkImage = view.findViewById(R.id.mywork_image);
            myworkName = view.findViewById(R.id.mywork_name);
            clickTimes = view.findViewById(R.id.mywork_click_times);
        }
    }

    public MyWorkAdapter(Context mContext, List<CommonListItem> mList) {
        this.mContext=mContext;
        this.mList = mList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = inflater.inflate(R.layout.item_mywork, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(mContext, CircleDetailActivity.class);
                intent.putExtra("newsId", mList.get(position).getNewsId());
                intent.putExtra("be_focused_personId", Constants.USER.getUserId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommonListItem detail = mList.get(position);
        holder.myworkName.setText(detail.getTitle());
        holder.clickTimes.setText(String.valueOf(detail.getClickTimes()));
        getImage(detail.getImage(),holder);
    }

    private void getImage(final String imageName, final ViewHolder holder) {
        Uri uri = Uri.parse(Constants.BASE_URL+"Download?method=getNewsImage&imageName="+imageName);
        Glide.with(mContext).load(uri).into(holder.myworkImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateList(List<CommonListItem> newDatas) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        notifyDataSetChanged();
    }
}

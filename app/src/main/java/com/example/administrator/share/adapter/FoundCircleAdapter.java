package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.CircleList;
import com.example.administrator.share.util.Constants;

import java.util.List;

public class FoundCircleAdapter extends RecyclerView.Adapter<FoundCircleAdapter.ViewHolder> {

    private Context mContext;
    private final LayoutInflater mInflater;
    private final List<CircleList> mList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bgIv;
        TextView topicTv;

        public ViewHolder(View view) {
            super(view);
            bgIv = view.findViewById(R.id.found_list_icon);
            topicTv = view.findViewById(R.id.tv_item_circle_topic);
        }
    }

    public FoundCircleAdapter(Context mContext, List<CircleList> mList) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = mInflater.inflate(R.layout.item_circle_image, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.bgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(mContext, CircleDetailActivity.class);
                intent.putExtra("newsId", mList.get(position).getNewsId());
                intent.putExtra("authorId", mList.get(position).getUserId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        CircleList circleList = mList.get(position);
        holder.topicTv.setText(circleList.getTopic());
        final String imageName = circleList.getImage();
        Uri uri = Uri.parse(Constants.BASE_URL + "download/getImage?imageName=" + imageName);
        Glide.with(mContext).load(uri).into((holder).bgIv);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateList(List<CircleList> newDatas) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        notifyDataSetChanged();
    }

}

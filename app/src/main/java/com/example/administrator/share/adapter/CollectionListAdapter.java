package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.NewsListItem;

import java.util.List;

/**
 * Created by djzhao on 17/05/04.
 */

public class CollectionListAdapter extends RecyclerView.Adapter<CollectionListAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<NewsListItem> mList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView username;
        LinearLayout mycollectionLl;
        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.item_normal_title);
            username = view.findViewById(R.id.item_normal_username);
            mycollectionLl = view.findViewById(R.id.ll_mycollection);
        }
    }

    public CollectionListAdapter(Context mContext, List<NewsListItem> mList) {
        this.mContext=mContext;
        this.mList = mList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = inflater.inflate(R.layout.item_mycollection, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mycollectionLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(mContext, CircleDetailActivity.class);
                intent.putExtra("newsId", mList.get(position).getNewsId());
                intent.putExtra("be_focused_personId", mList.get(position).getUserId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsListItem detail = mList.get(position);
        holder.title.setText(detail.getTitle());
        holder.username.setText("作者："+detail.getUsername());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}

package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.CommonListItem;
import com.example.administrator.share.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.List;

import okhttp3.Call;

public class CollectionListAdapter extends RecyclerView.Adapter<CollectionListAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<CommonListItem> mList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView nicknameTv;
        ImageView imageView;
        RelativeLayout mycollectionLl;
        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.item_normal_title);
            nicknameTv = view.findViewById(R.id.item_normal_nickname);
            imageView = view.findViewById(R.id.iv_mycollection);
            mycollectionLl = view.findViewById(R.id.ll_mycollection);
        }
    }

    public CollectionListAdapter(Context mContext, List<CommonListItem> mList) {
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
        CommonListItem detail = mList.get(position);
        holder.title.setText(detail.getTitle());
        holder.nicknameTv.setText("作者："+detail.getNickname());
        getImage(detail.getImage(),holder);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void getImage(final String imageName, final ViewHolder holder) {
        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
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
                                holder.imageView.setImageBitmap(bitmap);
                            }
                        });
                return 0;
            }
        }.execute();
    }

    public void updateList(List<CommonListItem> newDatas) {
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        notifyDataSetChanged();
    }
}

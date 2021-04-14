package com.example.administrator.share.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.share.R;
import com.example.administrator.share.activity.CircleDetailActivity;
import com.example.administrator.share.entity.CommonListItem;
import com.example.administrator.share.util.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.List;

import okhttp3.Call;

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
                                holder.myworkImage.setImageBitmap(bitmap);
                            }
                        });
                return 0;
            }
        }.execute();
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

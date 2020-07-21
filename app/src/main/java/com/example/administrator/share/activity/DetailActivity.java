package com.example.administrator.share.activity;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.share.R;

public class DetailActivity extends AppCompatActivity {

    private ImageView mIv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mIv1 = (ImageView) findViewById(R.id.title_back);
        mIv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AddnewsActivity.this, "返回上个界面", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}

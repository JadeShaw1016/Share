package com.example.administrator.share.fragment;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.administrator.share.R;
import com.example.administrator.share.view.DetailActivity;

import java.util.ArrayList;
import java.util.List;


public class FirstPageFragment extends Fragment {
    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;

    public static FirstPageFragment newInstance(String param1) {
        FirstPageFragment fragment = new FirstPageFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public FirstPageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firstpage, null);
        view.findViewById(R.id.iv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                startActivity(intent);
            }
        });
        spinner = view.findViewById(R.id.spinner);
        //数据
        data_list = new ArrayList<String>();
        data_list.add("   我的关注   ");
        data_list.add("   干货分享   ");

        //适配器
        arr_adapter= new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
//        spinner.getChildAt(-1).setVisibility(view.INVISIBLE);
        System.out.println(spinner.getSelectedItemPosition());
        spinner.setAdapter(arr_adapter);

        return view;
    }
}

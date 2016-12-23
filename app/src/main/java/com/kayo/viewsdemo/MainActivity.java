package com.kayo.viewsdemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kayo.develpeutils.SpannableStringUtils;
import com.kayo.motionlayout.IRefreshListener;
import com.kayo.motionlayout.MotionLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MotionLayout motion_layout;
    RecyclerView recycler_view;
    TextView  textView;

    List<String> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        motion_layout = (MotionLayout) findViewById(R.id.motion_layout);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        textView = (TextView) findViewById(R.id.text);
        addData(30);
        View view = LayoutInflater.from(this).inflate(R.layout.header_view, null);
        motion_layout.setHeaderView(view);
        motion_layout.setHeaderViewHeight(500);
        motion_layout.setAutoCheck(true);
        motion_layout.addRefreshListener(new IRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("MainActivity --> " + "onRefresh");
            }

            @Override
            public void onPullDistance(int distance) {

            }

            @Override
            public void onPullEnable(boolean enable) {
            }
        });
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(new DemoAdapter());

        settext();

    }

    class DemoAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.demo_holder_view, parent, false);
            return new DemoHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((DemoHolder) holder).bindData(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    class DemoHolder extends RecyclerView.ViewHolder {

        TextView title;

        public DemoHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text);
        }

        public void bindData(String data) {
            title.setText(data);
        }
    }

    private void addData(int count) {
        for (int i = 0; i < count; i++) {
            dataList.add("条目数据  " + i);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                motion_layout.setRefreshing(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void settext(){
        textView.setText(SpannableStringUtils.getBuilder(this,"测试 文字")
                .setRundBackground(Color.parseColor("#fe4070"),10)
                .setProportion(2).create());
    }
}

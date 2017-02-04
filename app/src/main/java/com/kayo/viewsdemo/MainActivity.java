package com.kayo.viewsdemo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kayo.animators.ItemAnimHelper;
import com.kayo.animators.adapters.SacaleAdapter;
import com.kayo.animators.animators.ShootItemAnimator;
import com.kayo.mutiadapter.ColumnRule;
import com.kayo.mutiadapter.MutiAdapter;
import com.kayo.mutiadapter.MutiData;
import com.kayo.mutiadapter.MutiHolder;
import com.kayo.mutiadapter.MutiListView;
import com.kayo.mutiadapter.rules.AbsRule;
import com.kayo.mutiadapter.rules.AdapterRulesManager;
import com.kayo.mutiadapter.footer.SimpleLoadMoreRule;
import com.kayo.mutiimageview.MutiImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by shilei on 17/1/19.
 * <pre>
 *     库测试类
 * </pre>
 */

public class MainActivity extends AppCompatActivity {

    MutiListView mutiListView;
    List<DemoData> dataList = new ArrayList<>();
    private DemoAdapter demoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        addData(20);

        mutiListView = (MutiListView) findViewById(R.id.muti_list_view);
//        mutiListView.setColumn(2);
        mutiListView.addColumnRule(new ColumnRule(R.layout.demo_holder_view2,1));
        mutiListView.addColumnRule(new ColumnRule(R.layout.demo_holder_view,2));
        final AdapterRulesManager manager = new AdapterRulesManager();
        manager.bindRecyclerView(mutiListView);
        manager.setDatas(dataList);
        manager.addRule(new com.kayo.mutiadapter.rules.Rule<DemoData,DemoHolder>(){
            @Override
            public int layoutId() {
                return R.layout.demo_holder_view;
            }

            @Override
            public DemoHolder holder(ViewGroup parent,int layoutId) {
                Context context = parent.getContext();
                return new DemoHolder(LayoutInflater.from(context).inflate(layoutId,parent,false));
            }

            @Override
            public void convert(DemoHolder holder, DemoData data) {
                holder.bindData(data);
            }
        });
        manager.addRule(new AbsRule<DemoData, DemoHolder2>(R.layout.demo_holder_view2) {
            @Override
            public DemoHolder2 holder(ViewGroup parent, int layoutId) {
                Context context = parent.getContext();
                return new DemoHolder2(LayoutInflater.from(context).inflate(layoutId,parent,false));
            }

            @Override
            public void convert(DemoHolder2 holder, DemoData data) {
                holder.bindData(data);
            }
        });
        manager.addFooter(new SimpleLoadMoreRule(){
            @Override
            public void loadMore() {
                System.out.println("MainActivity --> " + "loadmore....");
                final List<DemoData> datas = addDatas(10);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        manager.addDatas(datas);
                    }
                },1000);
            }
        });
//        manager.show();


//        demoAdapter = new DemoAdapter(this);
//        demoAdapter.setData(dataList);
//        mutiListView.setItemAnimator(new ScaleItemAnimator(Orientation.DOWN));
        //启用动画适配器

//        SlideAdapter slideAdapter = new SlideAdapter(demoAdapter,SlideAdapter.BOTTOM);
//        SacaleAdapter sacaleAdapter = new SacaleAdapter(demoAdapter);
//        slideAdapter.setDuration(2000);
//        mutiListView.setAdapter(slideAdapter);
//        ItemAnimHelper helper = ItemAnimHelper.getHelper();
//        helper.bindRecyclerView(mutiListView);
//        helper.bindDataAdapter(demoAdapter);
//        helper.bindItemAnimatior(new ScaleItemAnimator(Orientation.DOWN));
//        helper.setItemDuration(500);
//        helper.bindAnimationAdapter(new SlideAdapter());
//        helper.setAdapterDuration(500);
//        helper.showData();
        ItemAnimHelper.getHelper()
                .bindRecyclerView(mutiListView)
                .bindDataAdapter(manager.getAdapter())
                .bindItemAnimatior(new ShootItemAnimator())
                .setItemDuration(300)
                .bindAnimationAdapter(new SacaleAdapter(.2f))
                .setAdapterDuration(500)
                .showData();

    }


    public void add(View v){
        DemoData data = new DemoData("手动插入条目");
        data.setItemType(R.layout.demo_holder_view);
        demoAdapter.insertData(data,1);
    }

    public void del(View v){
        demoAdapter.removeData(1);
    }

    Random  random = new Random();
    int[] ids =  new int[]{R.layout.demo_holder_view,R.layout.demo_holder_view2};
    private void addData(int count) {
        for (int i = 0; i < count; i++) {
            DemoData demoData = new DemoData();
            demoData.setData("条目数据  " + i);
            demoData.setItemType(ids[random.nextInt(ids.length)]);
            dataList.add(demoData);
        }
    }

    private List<DemoData> addDatas(int count){
        List<DemoData> datas = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            DemoData demoData = new DemoData();
            demoData.setData("条目数据  " + i);
            demoData.setItemType(ids[random.nextInt(ids.length)]);
            datas.add(demoData);
        }
        return datas;
    }

//=============================================================
    class DemoAdapter extends MutiAdapter<DemoData>{

        private int layout_id = R.layout.demo_holder_view;
        private int layout_id2 = R.layout.demo_holder_view2;

        public DemoAdapter(Context context) {
            super(context);
        }

        @Override
        public MutiHolder onCreateMutiHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            if (viewType ==  layout_id){
                return new DemoHolder(inflater.inflate(layout_id,parent,false));
            }else if (viewType == layout_id2){
                return new DemoHolder2(inflater.inflate(layout_id2,parent,false));
            }
            return null;
        }

        @Override
        public void onBindMutiData(MutiHolder holder, DemoData data, int position) {
            holder.bindData(data);
        }
    }

    class DemoHolder extends MutiHolder<DemoData>{

        TextView title;
        MutiImageView image;

        public DemoHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text);
            image = (MutiImageView) itemView.findViewById(R.id.image);
        }

        @Override
        public void bindData(DemoData data) {
            title.setText(data.getData());
            Drawable drawable = getResources().getDrawable(R.drawable.abc);
            image.setBackgroundDrawable(drawable);
        }
    }

    class DemoHolder2 extends MutiHolder<DemoData>{

        TextView title;
        MutiImageView image;

        public DemoHolder2(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text);
            image = (MutiImageView) itemView.findViewById(R.id.image);
        }

        @Override
        public void bindData(DemoData data) {
            title.setText(data.getData());
            image.setBackgroundDrawable(getResources().getDrawable(R.drawable.cde));
        }
    }

    class DemoData extends MutiData{
        String data;
        public DemoData(){}

        public DemoData(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}

package com.lingxiao.mvp.huanxinmvp.view;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.skinlibrary.SkinLib;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.adapter.BaseRecyAdapter;
import com.lingxiao.mvp.huanxinmvp.adapter.SkinAdapter;
import com.lingxiao.mvp.huanxinmvp.event.SkinChangeEvent;
import com.lingxiao.mvp.huanxinmvp.utils.LogUtils;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SkinActivity extends BaseActivity {

    @BindView(R.id.toolbarSkin)
    Toolbar toolbarSkin;
    @BindView(R.id.recySkin)
    RecyclerView recySkin;

    private List<Integer> colorList = new ArrayList<>();
    private List<String> colorName = new ArrayList<>();
    private int[] svgId = new int[]{R.drawable.ic_img_photo
            ,R.drawable.ic_img_voice,R.drawable.ic_img_videocall
            ,R.drawable.ic_img_call,R.drawable.ic_img_go
            ,R.drawable.ic_img_setting,R.drawable.ic_img_theme,
            R.drawable.ic_msage,R.drawable.ic_phone
            ,R.drawable.ic_find,R.drawable.ic_mine};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
        ButterKnife.bind(this);
        toolbarSkin.setTitle("主题风格");
        setSupportActionBar(toolbarSkin);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        initData();

    }

    private void initData() {
        colorList.add(R.color.colorPrimary);
        colorList.add(R.color.red_300);
        colorList.add(R.color.blue300);
        colorList.add(R.color.indigo300);
        colorList.add(R.color.deepPurple300);

        colorName.add("red300");
        colorName.add("red300");
        colorName.add("blue300");
        colorName.add("indigo300");
        colorName.add("deepPurple300");

        SkinAdapter adapter = new SkinAdapter(colorList,this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recySkin.setLayoutManager(manager);
        recySkin.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseRecyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 0){
                    SkinLib.restoreDefaultTheme();
                }else {
                    SkinLib.changeSkinDef(colorName.get(position));
                }
                //发送换肤消息
                EventBus.getDefault().post(new SkinChangeEvent(colorList.get(position)));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.lingxiao.mvp.huanxinmvp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SkinAdapter extends BaseRecyAdapter{

    private CircleImageView imageView;
    private TextView textView;
    private Button button;
    private List<Integer> list;
    private String[] strings = {"默认绿","激情红","知乎蓝","颐缇蓝","基佬紫"};
    private Context mContext;
    public SkinAdapter(List<Integer> colorList, Context context) {
        super(colorList);
        mContext = context;
    }

    @Override
    public void bindData(BaseViewHolder holder, int position, List mList) {
        list = mList;
        imageView = (CircleImageView) holder.getView(R.id.img_skin);
        textView = (TextView) holder.getView(R.id.tv_skin);
        button = (Button) holder.getView(R.id.bt_skin_use);
        imageView.setBackgroundColor(mContext.getResources().getColor(list.get(position)));
        textView.setTextColor(mContext.getResources().getColor(list.get(position)));
        textView.setText(strings[position]);
    }

    @Override
    public int getLayoutId() {
        return R.layout.skip_card_view;
    }

    @Override
    public int getHeadLayoutId() {
        return 0;
    }
}

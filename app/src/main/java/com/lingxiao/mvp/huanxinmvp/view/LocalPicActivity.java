package com.lingxiao.mvp.huanxinmvp.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.google.encoding.EncodingHandler;
import com.lingxiao.mvp.huanxinmvp.utils.GlideHelper;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalPicActivity extends BaseActivity {

    @BindView(R.id.iv_local)
    ImageView ivLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_pic);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        boolean qrcode = intent.getBooleanExtra("qrcode",false);


        if (qrcode){
            //如果是二维码
            String username = intent.getStringExtra("username");
            try {
                Bitmap bitmap = EncodingHandler
                        .createQRCode(username, 300);
                ivLocal.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }else {
            if (null != path) {
                GlideHelper.loadImageView(path,ivLocal);
            } else {
                ToastUtils.showToast("无法查看原图");
            }
        }
        ivLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

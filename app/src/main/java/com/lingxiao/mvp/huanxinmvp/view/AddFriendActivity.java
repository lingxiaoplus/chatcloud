package com.lingxiao.mvp.huanxinmvp.view;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.adapter.AddFriendAdapter;
import com.lingxiao.mvp.huanxinmvp.presenter.AddFriendPresenter;
import com.lingxiao.mvp.huanxinmvp.presenter.Impl.AddFriendPresenterImpl;
import com.lingxiao.mvp.huanxinmvp.utils.ToastUtils;

import java.util.List;

public class AddFriendActivity extends BaseActivity implements AddFriendView{

    private Toolbar tbToolbar;
    //private TextView tv_search_title;
    private ImageView iv_nodata;
    private RecyclerView rv_addfriend;
    private AddFriendPresenter presenter;
    private SearchView searchView;
    private AddFriendAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initView();
        initToolbar();
        presenter = new AddFriendPresenterImpl(this);
    }

    private void initToolbar() {
        tbToolbar.setTitle("");
        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        tbToolbar = (Toolbar) findViewById(R.id.tb_search);
        searchView = (SearchView) findViewById(R.id.tv_search_title);
        iv_nodata = (ImageView) findViewById(R.id.iv_nodata);
        rv_addfriend = (RecyclerView) findViewById(R.id.rv_addfriend);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar,menu);
        //找到包含searchview的菜单项
        MenuItem item = menu.findItem(R.id.menu_search);
        //searchView = (SearchView) item.getActionView();
        //隐藏菜单项
        menu.findItem(R.id.menu_add).setVisible(false);
        menu.findItem(R.id.menu_photo).setVisible(false);
        menu.findItem(R.id.menu_suggest).setVisible(false);
        //设置搜索框提示
        searchView.setQueryHint("搜索好友");
        //给searchview添加搜索文字的变化监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //按了搜索按钮就会走这个方法
                if (adapter == null){
                    adapter = new AddFriendAdapter(null,null);
                    rv_addfriend.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rv_addfriend.setAdapter(adapter);
                    adapter.setAddFriendClickListener(new AddFriendAdapter.onAddFriendClickListener() {
                        @Override
                        public void onAddClick(View v, String username) {
                            presenter.addFriend(username);
                        }
                    });
                }
                //到服务器查询好友
                presenter.searchFriend(query);
                //隐藏软键盘
                InputMethodManager input = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(rv_addfriend.getWindowToken(),0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //搜索框文字有变化会走这个方法
                if (!TextUtils.isEmpty(newText)){
                    ToastUtils.showToast(newText);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public void onQuerySuccess(List<AVUser> avUsers, List<String> users, boolean isSuccess, String errormsg) {
        if (isSuccess){
            adapter.setContacts(users);
            adapter.setAvUsers(avUsers);
            adapter.notifyDataSetChanged();
            iv_nodata.setVisibility(View.GONE);
            rv_addfriend.setVisibility(View.VISIBLE);
        }else {
            ToastUtils.showToast(errormsg);
            iv_nodata.setVisibility(View.VISIBLE);
            rv_addfriend.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetAddFriendResult(boolean result, String messagre) {
        if (result){
            ToastUtils.showToast("添加好友成功");
        }else {
            ToastUtils.showToast(messagre);
        }
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

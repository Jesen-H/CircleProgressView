package com.hgeson.circleprogressview;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * @Describe：
 * @Date：2018/9/25
 * @Author：hgeson
 */

public abstract class BaseActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(layoutResource());
        ButterKnife.bind(this);
        initView();
    }

    protected abstract int layoutResource();

    protected abstract void initView();

}

package com.hgeson.circleprogressview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hgeson.circleprogressview.view.CircleProgressView;
import com.hgeson.circleprogressview.view.CircleWaterView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_progress_view)
    Button btnProgressView;
    @BindView(R.id.btn_water_view)
    Button btnWaterView;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_progress_view, R.id.btn_water_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_progress_view:
                startActivity(new Intent(this, CircleProgressActivity.class));
                break;
            case R.id.btn_water_view:
                startActivity(new Intent(this, CircleWaterActivity.class));
                break;
        }
    }
}

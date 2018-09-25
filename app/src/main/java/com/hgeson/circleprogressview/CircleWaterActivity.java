package com.hgeson.circleprogressview;

import android.os.Bundle;
import android.util.Log;

import com.hgeson.circleprogressview.view.CircleProgressView;
import com.hgeson.circleprogressview.view.CircleWaterView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Describe：
 * @Date：2018/9/25
 * @Author：hgeson
 */
public class CircleWaterActivity extends BaseActivity {

    @BindView(R.id.water_view)
    CircleWaterView waterView;

    private int progress = 0;
    private ExecutorService cachedThreadPool;

    @Override
    public int layoutResource() {
        return R.layout.activity_circle_water;
    }

    @Override
    protected void initView() {
        circleRun(waterView,66);
    }

    private void circleRun(final CircleWaterView circle, final int totalProgress) {
        cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (progress < totalProgress) {
                    progress++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            circle.setWaterProgress(progress);
                        }
                    });
                    try {
                        Thread.sleep(30);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}

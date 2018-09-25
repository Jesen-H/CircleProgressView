package com.hgeson.circleprogressview;

import android.graphics.Color;
import android.os.Bundle;

import com.hgeson.circleprogressview.view.CircleProgressView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Describe：
 * @Date：2018/9/25
 * @Author：hgeson
 */
public class CircleProgressActivity extends BaseActivity {

    @BindView(R.id.circle1)
    CircleProgressView circle1;
    @BindView(R.id.circle2)
    CircleProgressView circle2;
    @BindView(R.id.circle3)
    CircleProgressView circle3;
    @BindView(R.id.circle4)
    CircleProgressView circle4;

    private int progress = 0;
    private ExecutorService cachedThreadPool;

    @Override
    public int layoutResource() {
        return R.layout.activity_circle_progress;
    }

    @Override
    protected void initView() {
        circleRun(circle1, 59);

        circle1.setBeforeTxt("+");
        circle4.setCurrentProgress(73);
        circle4.setUnringColor(Color.BLUE);
    }

    private void circleRun(final CircleProgressView circle, final int totalProgress) {
        progress = 0;
        cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (progress < totalProgress) {
                    progress++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            circle.setProgress(false, progress);
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

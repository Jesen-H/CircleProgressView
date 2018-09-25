package com.hgeson.circleprogressview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.hgeson.circleprogressview.R;

/**
 * @Describe：CircleWaterBar
 * @Date：2018/9/6
 * @Author：hgeson
 */

public class CircleWaterView extends View {
    private Paint backgroundPaint;
    private Paint firstWavePaint;
    private Paint secondWavePaint;
    private Paint textPaint;
    private int backgroundColor;
    private int firstWaveColor;
    private int secondWaveColor;
    private Path firstPath;
    private Path secondPath;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;


    private String typeSymbol = "";     //符号设置
    private String beforeTxt = "";      //底部字体
    private String circleContent = "";
    private boolean isHidetxt = true;  //是否隐藏字体，默认显示
    private float txtWidth;
    private float bottomTextWidth;
    private int txtColor;
    private int bottomtxtColor;
    /* 水波的速度 */
    private int speed;

    /* 振幅 */
    private int amplitude;

    /* 角速度 */
    private static final float palstance = 0.5F;

    /* 最高水位 */
    private static final float waterProgressMax = 100;

    /* 水位高度 */
    private int waterProgress = 0;

    /* 控件尺寸 */
    private int waveSize;
    private int angle;

    /* 开始 波动水位 */
    private static final int whatStartWave = 100;
    private WaveHandler waveHandler;
    private WaveThread waveThread;
    private boolean canLoopWave;

    public CircleWaterView(Context context) {
        super(context);
        initView(null);
    }

    public CircleWaterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        angle = 360;
        canLoopWave = true;
        waterProgress = 0;
        Context context = getContext();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleWaterView);
        backgroundColor = typedArray.getColor(R.styleable.CircleWaterView_backgroundColor, Color.rgb(68,238,238));
        firstWaveColor = typedArray.getColor(R.styleable.CircleWaterView_firstWaveColor, Color.rgb(195,245,254));
        secondWaveColor = typedArray.getColor(R.styleable.CircleWaterView_secondWaveColor, Color.rgb(67,220,254));
        txtColor = typedArray.getColor(R.styleable.CircleWaterView_textColor, Color.rgb(48,48,48));
        bottomtxtColor = typedArray.getColor(R.styleable.CircleWaterView_btmtextColor, Color.rgb(144,144,144));
        waterProgress = typedArray.getInt(R.styleable.CircleWaterView_waterProgress, 0);
        circleContent = typedArray.getString(R.styleable.CircleWaterView_circleTxt);
        typeSymbol = typedArray.getString(R.styleable.CircleWaterView_typeSymbl);
        isHidetxt = typedArray.getBoolean(R.styleable.CircleWaterView_hidetxt,true);

        amplitude = typedArray.getInt(R.styleable.CircleWaterView_amplitude, dpToPx(20));
        speed = typedArray.getInt(R.styleable.CircleWaterView_speed, 1);
        waveSize = typedArray.getDimensionPixelSize(R.styleable.CircleWaterView_waveSize, 160);

        waveThread = new WaveThread();
        waveHandler = new WaveHandler();

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setAntiAlias(true);

        bitmap = Bitmap.createBitmap(waveSize, waveSize, Bitmap.Config.ARGB_8888);

        bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        /*初始化波浪画笔*/
        firstWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        firstWavePaint.setAntiAlias(true);
        firstWavePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        firstWavePaint.setColor(firstWaveColor);
        firstWavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        secondWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        secondWavePaint.setAntiAlias(true);
        secondWavePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        secondWavePaint.setColor(secondWaveColor);
        secondWavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        textPaint = new Paint();
        textPaint.setColor(txtColor);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize((waveSize / 2-15) / 2.5f);

        firstPath = new Path();
        secondPath = new Path();

        /*开启线程*/
        waveThread.start();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(waveSize, waveSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        firstPath.reset();
        secondPath.reset();
        bitmapCanvas.drawCircle(waveSize / 2, waveSize / 2, waveSize / 2-15, backgroundPaint);
        /**水位线*/
        float waterLine = (waterProgressMax - waterProgress) * waveSize * 0.01F;
        /* x、y*/
        firstPath.moveTo(0, waterLine);
        secondPath.moveTo(0, waterLine);

        int x1 = 0;
        int y1 = 0;
        int x2 = 0;
        int y2 = 0;
        for (int i = 0; i < waveSize; i++) {
            x1 = i;
            x2 = i;
            y1 = (int) (amplitude * Math.sin((i * palstance + angle) * Math.PI / 180) + waterLine);
            y2 = (int) (amplitude * Math.sin((i * palstance + angle - 90) * Math.PI / 180) + waterLine);
            firstPath.quadTo(x1, y1, x1 + 1, y1);
            secondPath.quadTo(x2, y2, x2 + 1, y2);
        }
        firstPath.lineTo(waveSize, waveSize);
        firstPath.lineTo(0, waveSize);
        firstPath.close();

        secondPath.lineTo(waveSize, waveSize);
        secondPath.lineTo(0, waveSize);
        secondPath.close();

        bitmapCanvas.drawPath(firstPath, firstWavePaint);
        bitmapCanvas.drawPath(secondPath, secondWavePaint);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(bitmap, 0, 0, null);

        if (!isHidetxt) {
            textPaint.setColor(txtColor);
            textPaint.setTextSize((waveSize / 2 - 15) / 2.5f);
            String text = beforeTxt + waterProgress + typeSymbol;
            txtWidth = textPaint.measureText(text, 0, text.length());
            if (!TextUtils.isEmpty(circleContent)) {
                canvas.drawText(text, getWidth() / 2f - txtWidth / 2f, getHeight() / 1.9f, textPaint);

                textPaint.setColor(bottomtxtColor);
                textPaint.setTextSize((waveSize / 2 - 15) / 4.5f);
                bottomTextWidth = textPaint.measureText(circleContent, 0, circleContent.length());
                canvas.drawText(circleContent, getWidth() / 2f - bottomTextWidth / 2f, getHeight() / 1.4f, textPaint);
            }else{
                canvas.drawText(text, getWidth() / 2f - txtWidth / 2f, getHeight() / 1.8f, textPaint);
            }
        }
    }

    /**
     * 设置 水位的进度 [0-100]
     *
     * @param waterProgress 水位进度
     */
    public CircleWaterView setWaterProgress(int waterProgress) {
        if (waterProgress <= 0) {
            waterProgress = 0;
        } else if (waterProgress >= 100) {
            waterProgress = 100;
        }
        this.waterProgress = waterProgress;
        postInvalidate();
        return this;
    }

    /**
     * 设置第一条波浪线的 颜色
     */
    public CircleWaterView setFirstWaveColor(int firstWaveColor) {
        this.firstWaveColor = firstWaveColor;
        firstWavePaint.setColor(firstWaveColor);
        invalidate();
        return this;
    }

    /**
     * 设置第二条波浪线的 颜色
     */
    public CircleWaterView setSecondWaveColor(int secondWaveColor) {
        this.secondWaveColor = secondWaveColor;
        secondWavePaint.setColor(secondWaveColor);
        invalidate();
        return this;
    }

    /**
     * 设置 波动速读
     */
    public CircleWaterView setSpeed(int speed) {
        if (speed < 1) {
            speed = 1;
        }
        if (speed > 10) {
            speed = 10;
        }
        this.speed = speed;
        return this;
    }

    /**
     * 波浪 波动的 线程
     */
    private final class WaveThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (canLoopWave) {
                angle = angle - 1 * speed;
                if (angle == 0) {
                    angle = 360;
                }
                waveHandler.sendEmptyMessage(whatStartWave);
                SystemClock.sleep(10);
            }
        }
    }

    /**
     * 波浪 波动的 操作者
     */
    private final class WaveHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (whatStartWave == msg.what) {
                invalidate();
            }
        }
    }

    /**
     * 设置 振幅
     *
     * @param amplitude 单位 dp
     */
    public CircleWaterView setAmplitude(int amplitude) {
        if (amplitude <= 0) {
            amplitude = 0;
        }
        this.amplitude = amplitude;
        invalidate();
        return this;
    }


    /**
     * 数据转换: dp---->px
     */
    private int dpToPx(float dp) {
        return (int) (dp * getContext().getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        canLoopWave = false;
        if (waveThread != null) {
            waveThread.interrupt();
            waveThread = null;
        }
        if (waveHandler != null) {
            waveHandler.removeMessages(whatStartWave);
            waveHandler = null;
        }
    }

    public void setTypeSymbol(String typeSymbol){
        this.typeSymbol = typeSymbol;
        invalidate();
    }

    public void setBeforeTxt(String beforeTxt){
        this.beforeTxt = beforeTxt;
        invalidate();
    }

    public void setCircleContent(String circleContent){
        this.circleContent = circleContent;
        invalidate();
    }
}

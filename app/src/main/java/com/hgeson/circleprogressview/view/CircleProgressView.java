package com.hgeson.circleprogressview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


import com.hgeson.circleprogressview.R;

import static android.R.attr.alpha;

/**
 * @Describe：CircleProgressBar
 * @Date：2018/9/6
 * @Author：hgeson
 */

public class CircleProgressView extends View {
    private RectF rectF = new RectF();

    private Paint ringPaint;            // 圆环画笔
    private Paint progressPaint;        // 进度字体画笔
    private Paint bottomtextPaint;      // 底部字体画笔
    private Paint unringPaint;          // 未完成圆环画笔

    private int ringColor;              // 圆环颜色
    private float strokeWidth;          // 圆环宽度

    private int unringColor;            // 未完成的圆环颜色
    private float unstrokeWidth;        // 未完成圆环宽度

    private int textColor;              // 字体颜色
    private int textStyle;
    private int bottomtextColor;        // 底部字体颜色

    private float radius;               // 半径
    private float txtWidth;             // 字的长度
    private float txtHeight;            // 字的高度
    private float bottomtxtWidth;       // 底部字体长度
    private float bottomtxtHeight;      // 底部字体高度
    private String bottomTxt = "";      // 底部字体

    private int totalProgress = 100;    // 总进度
    private int currentProgress;        // 当前进度
    private String typeSymbol = "";     // 符号
    private String beforeTxt = "";      // 字体前符号
    private boolean isFlag = false;     // 是否透明度
    private boolean isAbove = false;    // 文字是否于上方，默认false 处于下方

    /*default values*/
    private final int PROGRESS_CURRENT = 0;
    private final int PROGRESS_RADIUS = 150;
    private final int PROGRESS_STROKE_WIDTH = 10;
    private final int PROGRESS_UNSTROKE_WIDTH = 10;
    private final int PROGRESS_RING_COLOR = Color.rgb(255, 129, 171);
    private final int PROGRESS_UNRING_COLOR = Color.rgb(204, 204, 204);
    private final int PROGRESS_TXT_COLOR = Color.rgb(30, 144, 255);
    private final int PROGRESS_BOTTOM_TXT_COLOR = Color.rgb(48, 48, 48);
    private final boolean PROGRESS_ISABOVE = false;                        //默认文字下方

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
    }

    protected void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressView, defStyleAttr, 0);
        initTypeArray(typeArray);
        typeArray.recycle();
        initVariable();
    }

    protected void initTypeArray(TypedArray typeArray) {
        radius = typeArray.getDimensionPixelSize(R.styleable.CircleProgressView_radius, PROGRESS_RADIUS);
        strokeWidth = typeArray.getDimensionPixelSize(R.styleable.CircleProgressView_strokeWidth, PROGRESS_STROKE_WIDTH);
        unstrokeWidth = typeArray.getDimensionPixelSize(R.styleable.CircleProgressView_unstrokeWidth, PROGRESS_UNSTROKE_WIDTH);
        ringColor = typeArray.getColor(R.styleable.CircleProgressView_ringColor, PROGRESS_RING_COLOR);
        unringColor = typeArray.getColor(R.styleable.CircleProgressView_unringColor, PROGRESS_UNRING_COLOR);
        textColor = typeArray.getColor(R.styleable.CircleProgressView_txtColor, PROGRESS_TXT_COLOR);
        bottomtextColor = typeArray.getColor(R.styleable.CircleProgressView_bottomtxtColor, PROGRESS_BOTTOM_TXT_COLOR);
        bottomTxt = typeArray.getString(R.styleable.CircleProgressView_bottomtxt);
        typeSymbol = typeArray.getString(R.styleable.CircleProgressView_typeSymbol);
        currentProgress = typeArray.getInt(R.styleable.CircleProgressView_currentProgress,PROGRESS_CURRENT);
        isAbove = typeArray.getBoolean(R.styleable.CircleProgressView_isabove,PROGRESS_ISABOVE);

        //Only allowed completed ring width >= incomplete ring width otherwise equal
        if (strokeWidth < unstrokeWidth){
            unstrokeWidth = strokeWidth;
        }
    }

    protected void initPaint() {
        ringPaint = new Paint();
        unringPaint = new Paint();
        progressPaint = new Paint();
        bottomtextPaint = new Paint();
    }

    protected void initVariable() {
        initPaint();
        ringPaint.setAntiAlias(true);
        ringPaint.setDither(true);
        ringPaint.setColor(ringColor);
        ringPaint.setStyle(Paint.Style.STROKE);
//        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setStrokeWidth(strokeWidth);

        unringPaint.setAntiAlias(true);
        unringPaint.setDither(true);
        unringPaint.setColor(unringColor);
        unringPaint.setStyle(Paint.Style.STROKE);
        unringPaint.setStrokeWidth(unstrokeWidth);

        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setColor(textColor);
        progressPaint.setTextSize(radius / 2.5f);
        Paint.FontMetrics fm = progressPaint.getFontMetrics();
        txtHeight = fm.descent + Math.abs(fm.ascent);

        bottomtextPaint.setAntiAlias(true);
        bottomtextPaint.setStyle(Paint.Style.FILL);
        bottomtextPaint.setColor(bottomtextColor);
        bottomtextPaint.setTextSize(radius / 4.5f);
        Paint.FontMetrics bottomFm = bottomtextPaint.getFontMetrics();
        bottomtxtHeight = bottomFm.descent + Math.abs(bottomFm.ascent);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (currentProgress >= 0) {
            float delta = Math.max(this.strokeWidth, this.unstrokeWidth);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ringPaint.setAlpha(isFlag ? (int) (alpha + ((float) currentProgress / totalProgress)*220) : 255);
            }

            rectF.set(delta, delta, getWidth() - delta, getHeight() - delta);

            canvas.drawArc(rectF, -90, ((float) currentProgress / totalProgress) * 360 - 360, false, unringPaint);
            canvas.drawArc(rectF,   -90, ((float) currentProgress / totalProgress) * 360,       false, ringPaint);

            if (typeSymbol == null){
                typeSymbol = "";
            }
            String txt = beforeTxt + currentProgress + typeSymbol;
            txtWidth = progressPaint.measureText(txt, 0, txt.length());

            //set bottom content
            if (!TextUtils.isEmpty(bottomTxt)) {
                bottomtxtWidth = bottomtextPaint.measureText(bottomTxt, 0, bottomTxt.length());
                if (isAbove) {
                    canvas.drawText(txt, getWidth() / 2 - txtWidth / 2, getHeight() / 1.5f, progressPaint);
                    canvas.drawText(bottomTxt, getWidth() / 2 - bottomtxtWidth / 2, getHeight() / 2.8f, bottomtextPaint);
                }else{
                    canvas.drawText(txt, getWidth() / 2 - txtWidth / 2, getHeight() / 2, progressPaint);
                    canvas.drawText(bottomTxt, getWidth() / 2 - bottomtxtWidth / 2, getHeight() / 2 + bottomtxtHeight * 1.5f, bottomtextPaint);
                }
            }else{
                canvas.drawText(txt, getWidth() / 2 - txtWidth / 2, getHeight() / 2 + txtHeight / 4, progressPaint);
            }
        }
    }

    @Override
    public void invalidate() {
        this.initVariable();
        super.invalidate();
    }

    public void setProgress(boolean flag, int progress) {
        isFlag = flag;
        currentProgress = progress;
        postInvalidate();
    }

    public int getRingColor() {
        return ringColor;
    }

    public void setRingColor(int ringColor) {
        this.ringColor = ringColor;
        this.invalidate();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        this.invalidate();
    }

    public int getUnringColor() {
        return unringColor;
    }

    public void setUnringColor(int unringColor) {
        this.unringColor = unringColor;
        this.invalidate();
    }

    public float getUnstrokeWidth() {
        return unstrokeWidth;
    }

    public void setUnstrokeWidth(float unstrokeWidth) {
        this.unstrokeWidth = unstrokeWidth;
        this.invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.invalidate();
    }

    public int getBottomtextColor() {
        return bottomtextColor;
    }

    public void setBottomtextColor(int bottomtextColor) {
        this.bottomtextColor = bottomtextColor;
        this.invalidate();
    }

    public String getBottomTxt() {
        return bottomTxt;
    }

    public void setBottomTxt(String bottomTxt) {
        this.bottomTxt = bottomTxt;
        this.invalidate();
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        this.invalidate();
    }

    public String getTypeSymbol() {
        return typeSymbol;
    }

    public void setTypeSymbol(String typeSymbol) {
        this.typeSymbol = typeSymbol;
        this.invalidate();
    }

    public String getBeforeTxt() {
        return beforeTxt;
    }

    public void setBeforeTxt(String beforeTxt) {
        this.beforeTxt = beforeTxt;
        this.invalidate();
    }
}

package com.example.widget;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.widget.util.LogUtils;
import com.example.widget.util.SizeUtils;

/**
 * author : daiwenbo
 * e-mail : daiwwenb@163.com
 * date   : 2018/8/3
 * description   : 性别选择
 */

public class GenderSwitchView extends View {
    public static final int DEFAULT_ANIMATION_DURATION = 250;

    private Paint bitmapPaint;
    private Paint selectTextPaint;
    private Paint defaultTextPaint;

    private int height;
    private int width;
    private ShapeDrawable backgroundDrawable;
    private ShapeDrawable genderDrawable;


    private int drawBitmapY;
    private int drawBitmapX;
    private float drawTextX;
    private float drawTextY;

    private Bitmap girlSign;
    private Bitmap boySign;

    private Bitmap whiteGirlSign;
    private Bitmap whiteBoySign;


    private float mProgress;
    private int mTouchSlop;
    private int mClickTimeout;
    private ValueAnimator mProgressAnimator;
    private long mAnimationDuration = DEFAULT_ANIMATION_DURATION;
    private Rect bounds;
    private int boundsWidth;
    private int bundsX;
    private Bitmap grayGirlSign;
    private Bitmap grayBoySign;
    private int grayText;
    //渐变色计算类
    private ArgbEvaluator argbEvaluator;
    private int girlStartColor;
    private int girlEndColor;
    private int boyStartColor;
    private int boyEndColor;
    private LinearGradient linearGradient;

    public GenderSwitchView(Context context) {
        this(context, null);
    }

    public GenderSwitchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GenderSwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        int testSize = SizeUtils.sp2px(16);
        height = SizeUtils.dp2px(45);
        width = SizeUtils.dp2px(200);
        int radiis = SizeUtils.dp2px(80);

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mClickTimeout = ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout();

        grayText = ContextCompat.getColor(context, R.color.col_aaabb3);
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        selectTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectTextPaint.setTextAlign(Paint.Align.CENTER);
        selectTextPaint.setTextSize(testSize);
        selectTextPaint.setColor(Color.WHITE);

        defaultTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultTextPaint.setTextAlign(Paint.Align.CENTER);
        defaultTextPaint.setTextSize(testSize);
        defaultTextPaint.setColor(grayText);


        mProgressAnimator = new ValueAnimator();


        grayGirlSign = BitmapFactory.decodeResource(context.getResources(), R.drawable.gender_girl_sign);
        grayBoySign = BitmapFactory.decodeResource(context.getResources(), R.drawable.gender_boy_sign);

        whiteGirlSign = BitmapFactory.decodeResource(context.getResources(), R.drawable.white_gender_girl_sign);
        whiteBoySign = BitmapFactory.decodeResource(context.getResources(), R.drawable.white_gender_boy_sign);

        girlSign = whiteGirlSign;
        boySign = grayBoySign;

        float[] outerRadii = {radiis, radiis, radiis, radiis, radiis, radiis, radiis, radiis};//外矩形 左上、右上、右下、左下的圆角半径
        RectF inset = new RectF(0, 0, 0, 0);//内矩形距外矩形，左上角x,y距离， 右下角x,y距离
        float[] innerRadii = {0, 0, 0, 0, 0, 0, 0, 0};//内矩形 圆角半径
        RoundRectShape roundRectShape = new RoundRectShape(outerRadii, inset, innerRadii);
        backgroundDrawable = new ShapeDrawable(roundRectShape);
        int back_color = ContextCompat.getColor(context, R.color.col_f3f3f3);
        backgroundDrawable.getPaint().setColor(back_color);
        backgroundDrawable.setBounds(0, 0, width, height);

        girlStartColor = ContextCompat.getColor(context, R.color.col_ff719e);
        girlEndColor = ContextCompat.getColor(context, R.color.col_ffae9b);

        boyStartColor = ContextCompat.getColor(context, R.color.col_55a8ff);
        boyEndColor = ContextCompat.getColor(context, R.color.col_8998ff);
        //渐变色计算类
        argbEvaluator = new ArgbEvaluator();

        RoundRectShape shape = new RoundRectShape(outerRadii, inset, innerRadii);
        linearGradient = new LinearGradient(0, 0, boundsWidth, height, girlStartColor, girlEndColor, Shader.TileMode.REPEAT);
        genderDrawable = new ShapeDrawable(shape);
        genderDrawable.getPaint().setShader(linearGradient);
        genderDrawable.getPaint().setStyle(Paint.Style.FILL);
        boundsWidth = width / 2;
        bundsX = (int) (mProgress * boundsWidth);
        bounds = new Rect(bundsX, 0, boundsWidth + bundsX, height);
        genderDrawable.setBounds(bounds);




    }

    public float getProgress() {
        return mProgress;
    }

    public void setProcess(float progress) {
        LogUtils.e("setProcess(GenderSwitchView.java:141)进度" + progress);
        float tp = progress;
        if (tp > 1) {
            tp = 1;
        } else if (tp < 0) {
            tp = 0;
        }
        updatePaintStyle(tp);
        this.mProgress = tp;
        bundsX = (int) (mProgress * boundsWidth);
        bounds.left = bundsX;
        bounds.right = boundsWidth + bundsX;
        genderDrawable.setBounds(bounds);
        invalidate();
    }

    private void updatePaintStyle(float tp) {
        int startColor;
        int endColor;
        if (tp > 0.5f) {
            girlSign = grayGirlSign;
            boySign = whiteBoySign;
            selectTextPaint.setColor(grayText);
            defaultTextPaint.setColor(Color.WHITE);
        } else {
            girlSign = whiteGirlSign;
            boySign = grayBoySign;
            selectTextPaint.setColor(Color.WHITE);
            defaultTextPaint.setColor(grayText);
        }
        startColor = (int) (argbEvaluator.evaluate(tp, girlStartColor, boyStartColor));
        endColor = (int) (argbEvaluator.evaluate(tp, girlEndColor, boyEndColor));
        LinearGradient linearGradient = new LinearGradient(0, 0, boundsWidth, height, startColor, endColor, Shader.TileMode.REPEAT);
        genderDrawable.getPaint().setShader(linearGradient);

    }

    float mStartX;
    float mStartY;
    float mLastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float deltaX = event.getX() - mStartX;
        float deltaY = event.getY() - mStartY;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                mLastX = mStartX;
                setPressed(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                //计算滑动的比例 boundsWidth为整个宽度的一半
                setProcess(getProgress() + (x - mLastX) / boundsWidth);
                //这里比较x轴方向的滑动 和y轴方向的滑动 如果y轴大于x轴方向的滑动 事件就不在往下传递
                if ((Math.abs(deltaX) > mTouchSlop / 2 || Math.abs(deltaY) > mTouchSlop / 2)) {
                    if (Math.abs(deltaY) > Math.abs(deltaX)) {
                        return false;
                    }
                }
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                //计算从手指触摸到手指抬起时的时间
                float time = event.getEventTime() - event.getDownTime();
                //如果x轴和y轴滑动距离小于系统所能识别的最小距离 切从手指按下到抬起时间 小于系统默认的点击事件触发的时间  整个行为将被视为触发点击事件
                if (Math.abs(deltaX) < mTouchSlop && Math.abs(deltaY) < mTouchSlop && time < mClickTimeout) {
                    //获取事件触发的x轴区域 主要用于区分是左边还是右边
                    float clickX = event.getX();

                    //如果是在左边
                    if (clickX > boundsWidth) {
                        if (mProgress == 1.0f) {
                            return false;
                        } else {
                            animateToState(true);
                        }
                    } else {
                        if (mProgress == 0.0f) {
                            return false;
                        } else {
                            animateToState(false);
                        }
                    }
                    return false;
                } else {
                    boolean nextStatus = getProgress() > 0.5f;
                    animateToState(nextStatus);
                }
                break;
        }
        return true;
    }

    protected void animateToState(boolean checked) {
        float progress = mProgress;
        if (mProgressAnimator == null) {
            return;
        }
        if (mProgressAnimator.isRunning()) {
            mProgressAnimator.cancel();
            mProgressAnimator.removeAllUpdateListeners();
        }
        mProgressAnimator.setDuration(mAnimationDuration);
        if (checked) {
            mProgressAnimator.setFloatValues(progress, 1f);
        } else {
            mProgressAnimator.setFloatValues(progress, 0.0f);
        }
        mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();

                updatePaintStyle(mProgress);
                bundsX = (int) (mProgress * boundsWidth);
                bounds.left = bundsX;
                bounds.right = boundsWidth + bundsX;
                genderDrawable.setBounds(bounds);
                postInvalidate();
            }
        });
        mProgressAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawBitmapX = SizeUtils.dp2px(22);
        int textMargin = SizeUtils.dp2px(5);
        drawBitmapY = (height - girlSign.getHeight()) / 2;
        String mText = "男士";
        Rect bounds = new Rect();
        selectTextPaint.getTextBounds(mText, 0, mText.length(), bounds);
        int textHeight = bounds.height();
        drawTextX = drawBitmapX + girlSign.getWidth() + textMargin + bounds.width() / 2;
        drawTextY = height / 2 + textHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        backgroundDrawable.draw(canvas);
        genderDrawable.draw(canvas);
        canvas.drawBitmap(girlSign, drawBitmapX, drawBitmapY, bitmapPaint);
        canvas.drawBitmap(boySign, width / 2 + drawBitmapX, drawBitmapY, bitmapPaint);
        canvas.drawText("女士", drawTextX, drawTextY, selectTextPaint);
        canvas.drawText("男士", width / 2 + drawTextX, drawTextY, defaultTextPaint);
    }
}

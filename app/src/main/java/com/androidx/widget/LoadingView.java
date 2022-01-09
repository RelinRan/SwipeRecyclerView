package com.androidx.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Relin
 * Description:数据加载
 * Date:2020/12/14 22:01
 */
public class LoadingView extends View implements ValueAnimator.AnimatorUpdateListener {

    /**
     * 线条画笔
     */
    private Paint paint;
    /**
     * 线条颜色
     */
    private int lineColor = Color.parseColor("#5AF1A6");
    /**
     * 中心点
     */
    private float centerX, centerY;
    /**
     * 等边边长
     */
    private float sideLength = 1;
    /**
     * 半径
     */
    private float radius = 20;
    /**
     * 线段宽度
     */
    private float lineWidth = dip(2);
    /**
     * 线段长度
     */
    private float lineLength = dip(1);
    /**
     * 透明度位置
     */
    private int alphaPosition = 0;
    /**
     * 旋转的单位角度
     */
    private float angle = 90f / 4f;
    /**
     * 开始角度
     */
    private int minAlpha = 50;
    /**
     * 结束角度
     */
    private int maxAlpha = 255;
    /**
     * 持续时间
     */
    private int duration = 350;
    /**
     * 透明度值
     */
    private List<Integer> alphas;
    /**
     * 动画值
     */
    private ValueAnimator animator;
    /**
     * 是否开始
     */
    private boolean loading;

    public LoadingView(Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs!=null){
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
            radius = array.getDimension(R.styleable.LoadingView_android_radius, radius);
            lineColor = array.getColor(R.styleable.LoadingView_lineColor, lineColor);
            minAlpha = array.getInt(R.styleable.LoadingView_lineAlpha, minAlpha);
            duration = array.getInteger(R.styleable.LoadingView_android_duration, duration);
            angle = array.getFloat(R.styleable.LoadingView_android_angle, angle);
            lineWidth = array.getDimension(R.styleable.LoadingView_lineWidth, lineWidth);
            lineLength = array.getDimension(R.styleable.LoadingView_lineLength, lineLength);
            array.recycle();
        }
        //初始化透明度数据
        alphas = buildList(minAlpha, maxAlpha, (int) (360 / angle - 1));
        int alphaSize = alphas.size();
        initAnimator(alphaSize - 1);
        start();
        //初始化线条画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerX = getMeasuredWidth() / 2F;
        centerY = getMeasuredHeight() / 2F;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float diameter;
        if (height >= width) {
            diameter = width - getPaddingLeft() - getPaddingRight();
        } else {
            diameter = height - getPaddingTop() - getPaddingBottom();
        }
        radius = diameter * 0.90F / 2F;
    }

    protected float dip(float value) {
        return value * Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * 初始化动画值
     *
     * @param value 透明值数据Size
     */
    protected void initAnimator(int value) {
        animator = ValueAnimator.ofInt(value - 1);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(this);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                loading = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                loading = false;
            }
        });
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        alphaPosition = (int) animation.getAnimatedValue();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLoading(canvas);
    }

    /**
     * 绘制Loading
     *
     * @param canvas 画布
     */
    protected void drawLoading(Canvas canvas) {
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineWidth);
        int index = -1;
        for (float value = 0; value < 360; value += getAngle()) {
            index++;
            setLineAlpha(index);
            double radians = Math.toRadians(value - 2 * Math.PI);
            float startX = (float) (Math.sin(radians) * (radius - lineLength));
            float startY = (float) (Math.cos(radians) * (radius - lineLength));
            float endX = (float) (Math.sin(radians) * radius);
            float endY = (float) (Math.cos(radians) * radius);
            canvas.drawLine(centerX + startX, centerY + startY, centerX + endX, centerY + endY, paint);
        }
    }

    /**
     * 构建列表
     *
     * @param start 开始值
     * @param end   结束值
     * @param count 需要的个数
     * @return
     */
    protected List<Integer> buildList(int start, int end, int count) {
        List<Integer> alphas = new ArrayList<>();
        int value = (end - start) / count;
        for (int i = start; i < end; i += value) {
            alphas.add(i);
        }
        return alphas;
    }

    /**
     * 重组列表
     *
     * @param list     透明值列表
     * @param position 位置
     * @return
     */
    protected List<Integer> regroupList(List<Integer> list, int position) {
        List<Integer> groups = new ArrayList<>();
        if (position > 0) {
            groups.addAll(list.subList(position, list.size() - 1));
            List<Integer> remaining = list.subList(0, position);
            groups.addAll(remaining);
        } else {
            groups.addAll(list);
        }
        Collections.reverse(groups);
        return groups;
    }

    /**
     * 设置透明度
     *
     * @param index
     */
    private void setLineAlpha(int index) {
        List<Integer> list = regroupList(alphas, alphaPosition);
        if (index < list.size()) {
            paint.setAlpha(list.get(index));
        }
    }

    /**
     * 是否正在加载
     *
     * @return
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * 开始
     */
    public void start() {
        if (animator != null && !isLoading()) {
            animator.start();
        }
    }

    /**
     * 取消
     */
    public void cancel() {
        if (animator != null) {
            animator.cancel();
        }
        loading = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();
    }

    /**
     * 获取线段画笔
     *
     * @return
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * 设置线段画笔
     *
     * @param paint
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
        invalidate();
    }

    /**
     * 获取线段颜色
     *
     * @return
     */
    public int getLineColor() {
        return lineColor;
    }

    /**
     * 设置线段颜色
     *
     * @param lineColor
     */
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        invalidate();
    }


    /**
     * 获取中心点x
     *
     * @return
     */
    public float getCenterX() {
        return centerX;
    }

    /**
     * 设置中心x
     *
     * @param centerX
     */
    public void setCenterX(float centerX) {
        this.centerX = centerX;
        invalidate();
    }

    /**
     * 获取中心点Y
     *
     * @return
     */
    public float getCenterY() {
        return centerY;
    }

    /**
     * 设置中心点Y
     *
     * @param centerY
     */
    public void setCenterY(float centerY) {
        this.centerY = centerY;
        invalidate();
    }

    /**
     * 获取边长，按正方形算
     *
     * @return
     */
    public float getSideLength() {
        return sideLength;
    }

    /**
     * 设置边长
     *
     * @param sideLength
     */
    public void setSideLength(float sideLength) {
        this.sideLength = sideLength;
        invalidate();
    }

    /**
     * 获取半径
     *
     * @return
     */
    public float getRadius() {
        return radius;
    }

    /**
     * 设置半径
     *
     * @param radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    /**
     * 获取线段宽度
     *
     * @return
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     * 设置线段宽度
     *
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }

    /**
     * 获取线段长度
     *
     * @return
     */
    public float getLineLength() {
        return lineLength;
    }

    /**
     * 设置线段长度
     *
     * @param lineLength
     */
    public void setLineLength(float lineLength) {
        this.lineLength = lineLength;
        invalidate();
    }

    /**
     * 获取透明度位置
     *
     * @return
     */
    public int getAlphaPosition() {
        return alphaPosition;
    }

    /**
     * 设置透明度位置
     *
     * @param alphaPosition
     */
    public void setAlphaPosition(int alphaPosition) {
        this.alphaPosition = alphaPosition;
        invalidate();
    }

    /**
     * 获取单位角度
     *
     * @return
     */
    public float getAngle() {
        return angle;
    }

    /**
     * 设置单位角度
     *
     * @param angle
     */
    public void setAngle(float angle) {
        this.angle = angle;
        invalidate();
    }

    /**
     * 获取开始得透明度
     *
     * @return
     */
    public int getMinAlpha() {
        return minAlpha;
    }

    /**
     * 设置开始透明度
     *
     * @param minAlpha
     */
    public void setMinAlpha(int minAlpha) {
        this.minAlpha = minAlpha;
        invalidate();
    }

    /**
     * 获取结束透明度
     *
     * @return
     */
    public int getMaxAlpha() {
        return maxAlpha;
    }

    /**
     * 设置结束透明度
     *
     * @param maxAlpha
     */
    public void setMaxAlpha(int maxAlpha) {
        this.maxAlpha = maxAlpha;
        invalidate();
    }

    /**
     * 获取动画持续时间
     *
     * @return
     */
    public long getDuration() {
        return duration;
    }

    /**
     * 设置动画持续时间
     *
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
        invalidate();
    }

    /**
     * 获取透明度数据
     *
     * @return
     */
    public List<Integer> getAlphas() {
        return alphas;
    }

    /**
     * 设置透明度数据
     *
     * @param alphas
     */
    public void setAlphas(List<Integer> alphas) {
        this.alphas = alphas;
        invalidate();
    }

    /**
     * 获取动画值
     *
     * @return
     */
    public ValueAnimator getAnimator() {
        return animator;
    }

    /**
     * 设置动画值
     *
     * @param animator
     */
    public void setAnimator(ValueAnimator animator) {
        this.animator = animator;
        invalidate();
    }

}

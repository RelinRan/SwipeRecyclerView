/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ViewCompat;


/**
 * 创建私有类以解决 AnimationListeners 的问题
 * 在动画真正完成之前调用并支持旧版本的阴影
 * 平台。
 */
public class SwipeRefreshImageView extends AppCompatImageView {

    private static final int DEFAULT_BACKGROUND_COLOR = 0xFFFAFAFA;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    private static final int KEY_SHADOW_COLOR = 0x1E000000;

    // PX
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;
    private static final int SHADOW_ELEVATION = 4;

    private final float density = getContext().getResources().getDisplayMetrics().density;
    private final int shadowYOffset = (int) (density * Y_OFFSET);
    private final int shadowXOffset = (int) (density * X_OFFSET);

    private Animation.AnimationListener mListener;
    private int mShadowRadius;
    private int mBackgroundColor;
    private ShapeDrawable circle;

    public SwipeRefreshImageView(Context context) {
        super(context);
        initAttributeSet(context,null);
    }

    public SwipeRefreshImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context,attrs);
    }

    public SwipeRefreshImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context,attrs);
    }

    private void initAttributeSet(Context context,AttributeSet attrs){
        mShadowRadius = (int) (density * SHADOW_RADIUS);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwipeRefreshLayout);
        mBackgroundColor = array.getColor(R.styleable.SwipeRefreshLayout_swipeRefreshCircleBackgroundColor, DEFAULT_BACKGROUND_COLOR);
        array.recycle();
        if (elevationSupported()) {
            circle = new ShapeDrawable(new OvalShape());
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density);
        } else {
            circle = new ShapeDrawable(new OvalShadow(this, mShadowRadius));
            setLayerType(View.LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset, KEY_SHADOW_COLOR);
            final int padding = mShadowRadius;
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding);
        }
        circle.getPaint().setColor(mBackgroundColor);
        ViewCompat.setBackground(this, circle);
    }

    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!elevationSupported()) {
            setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight()
                    + mShadowRadius * 2);
        }
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mListener != null) {
            mListener.onAnimationStart(getAnimation());
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mListener != null) {
            mListener.onAnimationEnd(getAnimation());
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
            mBackgroundColor = color;
        }
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    private static class OvalShadow extends OvalShape {
        private Paint mShadowPaint;
        private int mShadowRadius;
        private SwipeRefreshImageView mCircleImageView;

        OvalShadow(SwipeRefreshImageView circleImageView, int shadowRadius) {
            super();
            mCircleImageView = circleImageView;
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            updateRadialGradient((int) rect().width());
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            updateRadialGradient((int) width);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int x = mCircleImageView.getWidth() / 2;
            final int y = mCircleImageView.getHeight() / 2;
            canvas.drawCircle(x, y, x, mShadowPaint);
            canvas.drawCircle(x, y, x - mShadowRadius, paint);
        }

        private void updateRadialGradient(int diameter) {
            mShadowPaint.setShader(new RadialGradient(
                    diameter / 2,
                    diameter / 2,
                    mShadowRadius,
                    new int[]{FILL_SHADOW_COLOR, Color.TRANSPARENT},
                    null,
                    Shader.TileMode.CLAMP));
        }
    }
}

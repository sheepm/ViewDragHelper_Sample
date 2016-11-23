package org.paomo.viewdraghelper;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import static android.support.v4.widget.ViewDragHelper.EDGE_LEFT;

/**
 * Created by Administrator on 2016/11/23 0023.
 */

public class NavigationView extends LinearLayout {

    private static final String TAG = "NavigationView";
    private static final int RIGHT = 100;
    private static final int MIN_VELOCITY = 300;
    private static float density;
    private ViewDragHelper mDragHelper;
    private View mContent;
    private View mMenu;

    public NavigationView(Context context) {
        this(context, null);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        mDragHelper = ViewDragHelper.create(this, new CustomCallBack());
        mDragHelper.setEdgeTrackingEnabled(EDGE_LEFT);
        density  = getResources().getDisplayMetrics().density;
    }

    private class CustomCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mMenu;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mDragHelper.captureChildView(mMenu,pointerId);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int newLeft = Math.max(-child.getWidth(),Math.min(left,0));
            return newLeft;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (xvel > MIN_VELOCITY || releasedChild.getLeft()  >-releasedChild.getWidth() * 0.5) {
                mDragHelper.settleCapturedViewAt(0, releasedChild.getTop());
            }else {
                mDragHelper.settleCapturedViewAt(-releasedChild.getWidth(), releasedChild.getTop());
            }
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)){
            invalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        if(count >= 2){
            //简单写了  直接写死
            mMenu = getChildAt(1);
            mContent = getChildAt(0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //如果menu的宽度是match_parent或者超过限制 那么就需要重新设置
        int width = (int) (density * RIGHT);
        if (mMenu.getMeasuredWidth() + width > getWidth()){
            int menuWidthSpec = MeasureSpec.makeMeasureSpec(getWidth() -width,MeasureSpec.EXACTLY);
            mMenu.measure(menuWidthSpec,heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mMenu != null){
            mMenu.layout(-mMenu.getMeasuredWidth(),t,0,mMenu.getMeasuredHeight());
        }
        if (mContent != null){
            mContent.layout(0,0,mContent.getMeasuredWidth(),mContent.getMeasuredHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }
}

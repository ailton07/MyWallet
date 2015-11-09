package br.edu.ufam.ceteli.mywallet.classes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by rodrigo on 04/11/15.
 */
public abstract class RecyclerScrollListener extends RecyclerView.OnScrollListener {
    private static final float HIDE_THRESHOLD = 10;
    private static final float SHOW_THRESHOLD = 70;
    private static RecyclerScrollListener scrollListener = null;
    private static boolean mControlsVisible = true;
    private static int mToolbarHeight;
    private static View view = null;
    private static int mToolbarOffset = 0;
    private static int mTotalScrolledDistance;

    public static RecyclerScrollListener getInstance(final View toolbarView, final View otherView){
        if(scrollListener == null){
            view = toolbarView;
            scrollListener = new RecyclerScrollListener(toolbarView.getContext()) {
                @Override
                public void onMoved(int distance) {
                    view.setTranslationY(-distance);
                    if(otherView != null){
                        otherView.setTranslationY(-distance);
                    }
                }

                @Override
                public void onShow() {
                    view.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                    if(otherView != null){
                        otherView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                    }
                }

                @Override
                public void onHide() {
                    view.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(2)).start();
                    if(otherView != null){
                        otherView.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(2)).start();
                    }
                }
            };
        }
        return scrollListener;
    }

    public static void resetScrollingView(){
        mToolbarOffset = 0;
        mTotalScrolledDistance = 0;
        mControlsVisible = true;
        view.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    public static void removeObjectReference(){
        scrollListener = null;
    }

    private RecyclerScrollListener(Context context) {
        mToolbarHeight = DesignUtils.getToolbarHeight(context);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if(newState == RecyclerView.SCROLL_STATE_IDLE) {
            if(mTotalScrolledDistance < mToolbarHeight) {
                setVisible();
            } else {
                if (mControlsVisible) {
                    if (mToolbarOffset > HIDE_THRESHOLD) {
                        setInvisible();
                    } else {
                        setVisible();
                    }
                } else {
                    if ((mToolbarHeight - mToolbarOffset) > SHOW_THRESHOLD) {
                        setVisible();
                    } else {
                        setInvisible();
                    }
                }
            }
        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        clipToolbarOffset();
        onMoved(mToolbarOffset);

        if((mToolbarOffset <mToolbarHeight && dy>0) || (mToolbarOffset >0 && dy<0)) {
            mToolbarOffset += dy;
        }
        if (mTotalScrolledDistance < 0) {
            mTotalScrolledDistance = 0;
        } else {
            mTotalScrolledDistance += dy;
        }
    }

    private void clipToolbarOffset() {
        if(mToolbarOffset > mToolbarHeight) {
            mToolbarOffset = mToolbarHeight;
        } else if(mToolbarOffset < 0) {
            mToolbarOffset = 0;
        }
    }

    private void setVisible() {
        if(mToolbarOffset > 0) {
            onShow();
            mToolbarOffset = 0;
        }
        mControlsVisible = true;
    }

    private void setInvisible() {
        if(mToolbarOffset < mToolbarHeight) {
            onHide();
            mToolbarOffset = mToolbarHeight;
        }
        mControlsVisible = false;
    }

    public abstract void onMoved(int distance);
    public abstract void onShow();
    public abstract void onHide();
}

package com.hitomi.smlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.IdRes;
import androidx.core.view.GestureDetectorCompat;
import androidx.viewpager.widget.PagerAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by hitomi on 2016/9/18. <br/>
 * <p>
 * github : https://github.com/Hitomis <br/>
 * <p>
 * email : 196425254@qq.com
 */
public class SpinMenu extends FrameLayout {

    public static final String TAG = SpinMenu.class.getSimpleName();

    static final String TAG_ITEM_CONTAINER = "tag_item_container";

    static final String TAG_ITEM_PAGER = "tag_item_pager";

    static final String TAG_ITEM_HINT = "tag_item_hint";

    static final int MENU_STATE_CLOSE = -2;

    static final int MENU_STATE_CLOSED = -1;

    static final int MENU_STATE_OPEN = 1;

    static final int MENU_STATE_OPENED = 2;

    /**
     * 左右菜单 Item 移动动画的距离
     */
    static final float TRAN_SKNEW_VALUE = 160;

    /**
     * Hint 相对 页面的上外边距
     */
    static final int HINT_TOP_MARGIN = 15;

    /**
     * 可旋转、转动布局
     */
    private SpinMenuLayout spinMenuLayout;

    /**
     * 菜单打开关闭动画帮助类
     */
    private SpinMenuAnimator spinMenuAnimator;

    /**
     * 页面适配器
     */
    private PagerAdapter pagerAdapter;
    //    private SMFragmentAdapter pagerAdapter;

    /**
     * 手势识别器
     */
    private GestureDetectorCompat menuDetector;

    /**
     * 菜单状态改变监听器
     */
    private OnSpinMenuStateChangeListener onSpinMenuStateChangeListener;

    /**
     * 缓存 Fragment 的集合，供 {@link #pagerAdapter} 回收使用
     */
    private List pagerObjects;

    /**
     * 菜单项集合
     */
    private List<SMItemLayout> smItemLayoutList;

    /**
     * 页面标题字符集合
     */
    private List<String> hintStrList;

    /**
     * 页面标题字符尺寸
     */
    private int hintTextSize = 14;

    /**
     * 页面标题字符颜色
     */
    private int hintTextColor = Color.parseColor("#666666");

    /**
     * 默认打开菜单时页面缩小的比率
     */
    private float scaleRatio = 0.624f;//0.36f

    /**
     * 默认书本页面长宽的比率
     */
    private float heightWidthScale = 1.0f;
    /**
     * 控件是否初始化的标记变量
     */
    private boolean init = true;

    /**
     * 是否启用手势识别
     */
    private boolean enableGesture;

    /**
     * 当前菜单状态，默认为已关闭
     */
    private int menuState = MENU_STATE_CLOSED;

    /**
     * 滑动与触摸之间的阀值
     */
    private int touchSlop = 8;

    /**
     * 点击监听
     */
    private OnSpinSelectedListener mSpinSelectedListener;
    private OnMenuSelectedListener mMenuSelectedListener;
    private OnMenuLongClickListener mMenuLongClickListener;

    private OnSpinSelectedListener onSpinSelectedListener = new OnSpinSelectedListener() {
        @Override
        public void onSpinSelected(int position) {
            log("SpinMenu onSpinSelected position:" + position);
            if (null != mSpinSelectedListener) {
                mSpinSelectedListener.onSpinSelected(position);
            }
        }
    };

    private OnMenuSelectedListener onMenuSelectedListener = new OnMenuSelectedListener() {
        @Override
        public void onMenuSelected(SMItemLayout smItemLayout, int position) {
            log("SpinMenu onMenuSelected position:" + position);
            if (position < smItemLayoutList.size() - 1) {
                closeMenu(smItemLayout);
            }
            if (null != mSpinSelectedListener) {
                mMenuSelectedListener.onMenuSelected(smItemLayout, position);
            }
        }
    };

    private OnMenuLongClickListener onMenuLongClickListener = new OnMenuLongClickListener() {
        @Override
        public void onMenuLongClick(int position) {
            if (null != mMenuLongClickListener) {
                mMenuLongClickListener.onMenuLongClick(position);
            }
        }
    };

    /**
     * 手势识别监听
     */
    private GestureDetector.SimpleOnGestureListener menuGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) < touchSlop && distanceY < -touchSlop * 3) {
                //                openMenu();
            }
            return true;
        }
    };

    public SpinMenu(Context context) {
        this(context, null);
    }

    public SpinMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpinMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "SpinMenu()");
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpinMenu);
        scaleRatio = typedArray.getFloat(R.styleable.SpinMenu_scale_ratio, scaleRatio);
        heightWidthScale = typedArray.getFloat(R.styleable.SpinMenu_height_width_scale, heightWidthScale);

        hintTextSize = typedArray.getDimensionPixelSize(R.styleable.SpinMenu_hint_text_size, hintTextSize);
        hintTextSize = px2Sp(hintTextSize);
        hintTextColor = typedArray.getColor(R.styleable.SpinMenu_hint_text_color, hintTextColor);
        typedArray.recycle();

        pagerObjects = new ArrayList();
        smItemLayoutList = new ArrayList<>();
//        menuDetector = new GestureDetectorCompat(context, menuGestureListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            ViewConfiguration conf = ViewConfiguration.get(getContext());
            touchSlop = conf.getScaledTouchSlop();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate()");

        @IdRes final int smLayoutId = 0x6F060505;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        spinMenuLayout = new SpinMenuLayout(getContext());
        spinMenuLayout.setId(smLayoutId);
        spinMenuLayout.setLayoutParams(layoutParams);
        spinMenuLayout.setOnSpinSelectedListener(onSpinSelectedListener);
        spinMenuLayout.setOnMenuSelectedListener(onMenuSelectedListener);
        spinMenuLayout.setOnMenuLongClickListener(onMenuLongClickListener);
        addView(spinMenuLayout);
    }

    private int mPagerWidth;//笔记本宽度
    private int mPagerHeight;//笔记本高度

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout() " + init + ":" + smItemLayoutList.size() + ":" + getMeasuredWidth());

        if (init && smItemLayoutList.size() > 0) {
            // 根据 scaleRatio 去调整菜单中 item 视图的整体大小
            mPagerWidth = (int) (getMeasuredWidth() * scaleRatio);//根据scaleRatio比例取页面宽度
            //            mPagerHeight = (int) (getMeasuredHeight() * scaleRatio);
            mPagerHeight = (int) (mPagerWidth * heightWidthScale);//根据页面宽度按比例取高度
            SMItemLayout.LayoutParams containerLayoutParams = new SMItemLayout.LayoutParams(mPagerWidth, mPagerHeight);
            SMItemLayout smItemLayout;
            FrameLayout frameContainer;
            TextView tvHint;
            for (int i = 0; i < smItemLayoutList.size(); i++) {
                smItemLayout = smItemLayoutList.get(i);
                frameContainer = (FrameLayout) smItemLayout.findViewWithTag(TAG_ITEM_CONTAINER);
                frameContainer.setLayoutParams(containerLayoutParams);
                if (i == 0) { // 初始菜单的时候，默认显示第一个 Fragment
                    FrameLayout pagerLayout = (FrameLayout) smItemLayout.findViewWithTag(TAG_ITEM_PAGER);
                    // 先移除第一个包含 Fragment 的布局
                    if (null != pagerLayout) {
                        frameContainer.removeView(pagerLayout);
                    }

                    // 创建一个用来占位的 FrameLayout
                    FrameLayout holderLayout = new FrameLayout(getContext());
                    LinearLayout.LayoutParams pagerLinLayParams = new LinearLayout.LayoutParams(mPagerWidth, mPagerHeight);
                    holderLayout.setLayoutParams(pagerLinLayParams);

                    // 将占位的 FrameLayout 添加到布局中的 frameContainer 中
                    frameContainer.addView(holderLayout, 0);

                    // 添加 第一个包含 Fragment 的布局添加到 SpinMenu 中
                    FrameLayout.LayoutParams pagerFrameParams = new FrameLayout.LayoutParams(mPagerWidth, mPagerHeight);
                    if (null != pagerLayout) {
                        pagerLayout.setLayoutParams(pagerFrameParams);
                        addView(pagerLayout);
                    }
                }

                // 显示标题
                if (hintStrList != null && !hintStrList.isEmpty() && i < hintStrList.size()) {
                    tvHint = (TextView) smItemLayout.findViewWithTag(TAG_ITEM_HINT);
                    tvHint.setText(hintStrList.get(i));
                    tvHint.setTextSize(hintTextSize);
                    tvHint.setTextColor(hintTextColor);
                }

                // 位于菜单中当前显示 Fragment 两边的 SMItemlayout 左右移动 TRAN_SKNEW_VALUE 个距离
                if (spinMenuLayout.getSelectedPosition() + 1 == i
                        || (spinMenuLayout.isCyclic()
                        && spinMenuLayout.getMenuItemCount() - i == spinMenuLayout.getSelectedPosition() + 1)) { // 右侧 ItemMenu
                    smItemLayout.setTranslationX(TRAN_SKNEW_VALUE);
                } else if (spinMenuLayout.getSelectedPosition() - 1 == i
                        || (spinMenuLayout.isCyclic()
                        && spinMenuLayout.getMenuItemCount() - i == 1)) { // 左侧 ItemMenu
                    smItemLayout.setTranslationX(-TRAN_SKNEW_VALUE);
                } else {
                    smItemLayout.setTranslationX(0);
                }
            }
            if (null == spinMenuAnimator) {
                spinMenuAnimator = new SpinMenuAnimator(this, spinMenuLayout, onSpinMenuStateChangeListener);
            }
            init = false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (enableGesture)
//            menuDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (enableGesture) {
//            menuDetector.onTouchEvent(event);
//            return true;
//        } else {
        return super.onTouchEvent(event);
//        }
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位转成为 sp
     *
     * @param pxValue
     * @return
     */
    private int px2Sp(float pxValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dip(float pxValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private void log(String log) {
        Log.d(TAG, log);
    }

    public void setFragmentAdapter(PagerAdapter adapter) {
        Log.d(TAG, "setFragmentAdapter()");
        if (adapter == null){
            return;
        }
        int pagerCount = adapter.getCount();
        if (pagerCount > spinMenuLayout.getMaxMenuItemCount())
            throw new RuntimeException(String.format("Fragment number can't be more than %d", spinMenuLayout.getMaxMenuItemCount()));

        pagerAdapter = adapter;
        SMItemLayout.LayoutParams itemLinLayParams = new SMItemLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        LinearLayout.LayoutParams containerLinlayParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        FrameLayout.LayoutParams pagerFrameParams = new FrameLayout.LayoutParams(mPagerWidth, mPagerHeight);
        LinearLayout.LayoutParams hintLinLayParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        hintLinLayParams.topMargin = HINT_TOP_MARGIN;

        if (null != smItemLayoutList && smItemLayoutList.size() > 0) {
            smItemLayoutList.clear();
        }

        pagerAdapter.startUpdate(spinMenuLayout);
        for (int i = 0; i < pagerCount; i++) {
            // 创建菜单父容器布局
            SMItemLayout smItemLayout = new SMItemLayout(getContext());
            smItemLayout.setId(i + 1);
            smItemLayout.setGravity(Gravity.CENTER);
            smItemLayout.setLayoutParams(itemLinLayParams);

            // 创建包裹FrameLayout
            FrameLayout frameContainer = new FrameLayout(getContext());
            frameContainer.setId(pagerCount + i + 1);
            frameContainer.setTag(TAG_ITEM_CONTAINER);
            frameContainer.setLayoutParams(containerLinlayParams);

            // 创建 Fragment 容器
            FrameLayout framePager = new FrameLayout(getContext());
            framePager.setId(pagerCount * 2 + i + 1);
            framePager.setTag(TAG_ITEM_PAGER);
            framePager.setLayoutParams(pagerFrameParams);
            Object object = pagerAdapter.instantiateItem(framePager, i);

            // 创建菜单标题 TextView
            //            TextView tvHint = new TextView(getContext());
            //            tvHint.setId(pagerCount * 3 + i + 1);
            //            tvHint.setTag(TAG_ITEM_HINT);
            //            tvHint.setLayoutParams(hintLinLayParams);

            frameContainer.addView(framePager);
            smItemLayout.addView(frameContainer);
            //            smItemLayout.addView(tvHint);
            spinMenuLayout.addView(smItemLayout);
            pagerObjects.add(object);
            smItemLayoutList.add(smItemLayout);
        }
        pagerAdapter.finishUpdate(spinMenuLayout);
    }

    public void pagerDestroyItem(PagerAdapter adapter) {
        Log.d(TAG, "pagerDestroyItem()");
        if (pagerAdapter != null) {
            pagerAdapter.startUpdate(spinMenuLayout);
            View view;
            for (int i = 0; i < adapter.getCount(); i++) {
                view = spinMenuLayout.getChildAt(i);
                if (null != view) {

                    ViewGroup pager = (ViewGroup) view.findViewWithTag(TAG_ITEM_PAGER);
                    if (null != pagerObjects && null != pagerObjects.get(i)) {
                        pagerAdapter.destroyItem(pager, i, pagerObjects.get(i));
                    }
                }
            }
            pagerObjects.clear();
            smItemLayoutList.clear();
            spinMenuLayout.removeAllViews();
            pagerAdapter.finishUpdate(spinMenuLayout);
        }
    }


    public void removeItem(int position) {
        pagerAdapter.startUpdate(spinMenuLayout);
        if (position < pagerAdapter.getCount()) {
            View view = spinMenuLayout.getChildAt(position);
            if (null != view) {
                ViewGroup pager = (ViewGroup) view.findViewWithTag(TAG_ITEM_PAGER);
                pagerAdapter.destroyItem(pager, position, pagerObjects.get(position));
            }
        }
        pagerAdapter.finishUpdate(spinMenuLayout);
    }


    public void adapterDestroyView(PagerAdapter adapter) {
        if (pagerAdapter != null) {
            pagerAdapter.startUpdate(spinMenuLayout);
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = spinMenuLayout.getChildAt(i);
                if (null != view) {

                    ViewGroup pager = (ViewGroup) view.findViewWithTag(TAG_ITEM_PAGER);
                    pagerAdapter.destroyItem(pager, i, pagerObjects.get(i));
                }
            }
            pagerAdapter.finishUpdate(spinMenuLayout);
        }
    }

    public void scroll(int startX, int startY, int clickIndex, int currSelPos) {
        spinMenuLayout.scroll(startX, startY, clickIndex, currSelPos);
    }

    public void openMenu() {
        if (menuState == MENU_STATE_CLOSED) {
            spinMenuAnimator.openMenuAnimator();
        }
    }

    public void closeMenu(SMItemLayout chooseItemLayout) {
        if (menuState == MENU_STATE_OPENED) {
            spinMenuAnimator.closeMenuAnimator(chooseItemLayout);
        }
    }

    public int getMenuState() {
        return menuState;
    }

    public void updateMenuState(int state) {
        menuState = state;
    }

    public void setEnableGesture(boolean enable) {
        enableGesture = enable;
    }

    public void setMenuItemScaleValue(float scaleValue) {
        scaleRatio = scaleValue;
    }

    public void setHintTextSize(int textSize) {
        hintTextSize = textSize;
    }

    public void setHintTextColor(int textColor) {
        hintTextColor = textColor;
    }

    public void setHintTextStrList(List<String> hintTextList) {
        hintStrList = hintTextList;
    }

    public void setOnSpinMenuStateChangeListener(OnSpinMenuStateChangeListener listener) {
        onSpinMenuStateChangeListener = listener;
    }

    public float getScaleRatio() {
        return scaleRatio;
    }

    public void setSpinSelectedListener(OnSpinSelectedListener spinSelectedListener) {
        this.mSpinSelectedListener = spinSelectedListener;
    }

    public void setOnMenuLongClickListener(OnMenuLongClickListener onMenuLongClickListener) {
        this.mMenuLongClickListener = onMenuLongClickListener;
    }

    public void setMenuSelectedListener(OnMenuSelectedListener mMenuSelectedListener) {
        this.mMenuSelectedListener = mMenuSelectedListener;
    }
}

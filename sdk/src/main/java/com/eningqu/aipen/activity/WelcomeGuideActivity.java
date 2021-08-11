//package com.eningqu.aipen.activity;
//
//import android.graphics.Paint;
//import androidx.viewpager.widget.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.eningqu.aipen.R;
//import com.eningqu.aipen.adapter.GuideViewPagerAdapter;
//import com.eningqu.aipen.base.ui.BaseActivity;
//import com.eningqu.aipen.common.AppCommon;
//import com.eningqu.aipen.common.utils.SpUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
//import butterknife.BindView;
//
///**
// * 说明：
// * 作者：WangYabin
// * 邮箱：wyb@eningqu.com
// * 时间：13:46
// */
//public class WelcomeGuideActivity extends BaseActivity implements View.OnClickListener {
//    ViewPager vp;
//    TextView startBtn;
//    LinearLayout ll;
//
//    private GuideViewPagerAdapter adapter;
//    private List<View> views;
//
//    // 引导页图片资源
//    private static  int[] pics = new int[3];
//
//    // 底部小点图片
//    private ImageView[] dots;
//
//    // 记录当前选中位置
//    private int currentIndex;
//
//    @Override
//    protected void setLayout() {
//        // 判断是否是第一次开启应用
//        boolean isFirstOpen = SpUtils.getBoolean(this, SpUtils.FIRST_OPEN);
//        // 如果是第一次启动，则先进入功能引导页
//        if (isFirstOpen) {
////            gotoActivity(WelcomeActivity.class,true);
//        }
//        setContentView(R.layout.activity_guide);
//        String local = Locale.getDefault().getLanguage();
//        if (local.equals("zh")) {
//            pics = new int[]{R.layout.guid_view1,
//                    R.layout.guid_view2, R.layout.guid_view3};
//        }else {
//            pics = new int[]{R.layout.guid_view_one,
//                    R.layout.guid_view_two, R.layout.guid_view_three};
//        }
//    }
//
//    @Override
//    protected void initView() {
//        vp = findViewById(R.id.guide_vp);
//        startBtn = findViewById(R.id.btn_login);
//        ll =findViewById(R.id.ll);
//
//        views = new ArrayList<View>();
//        startBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//        View view;
//        // 初始化引导页视图列表
//        for (int i = 0; i < pics.length; i++) {
//            view = LayoutInflater.from(this).inflate(pics[i], null);
//
//            if (i == pics.length - 1) {
//                startBtn.setTag("enter");
//                startBtn.setOnClickListener(this);
//            }
//
//            views.add(view);
//
//        }
//
//        // 初始化adapter
//        adapter = new GuideViewPagerAdapter(views);
//        vp.setAdapter(adapter);
//        vp.setOnPageChangeListener(new PageChangeListener());
//
//        initDots();
//
//    }
//
//    @Override
//    protected void initData() {
//
//    }
//
//    @Override
//    protected void initEvent() {
//
//    }
//
//    @Override
//    protected void onPause() {
//        SpUtils.putBoolean(WelcomeGuideActivity.this, SpUtils.FIRST_OPEN, true);
//        finish();
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        SpUtils.putBoolean(WelcomeGuideActivity.this, SpUtils.FIRST_OPEN, true);
//        super.onDestroy();
//    }
//    private void initDots() {
//        dots = new ImageView[pics.length];
//
//        // 循环取得小点图片
//        for (int i = 0; i < pics.length; i++) {
//            // 得到一个LinearLayout下面的每一个子元素
//            dots[i] = (ImageView) ll.getChildAt(i);
//            dots[i].setEnabled(false);// 都设为灰色
//            dots[i].setOnClickListener(this);
//            dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
//        }
//
//        currentIndex = 0;
//        dots[currentIndex].setEnabled(true); // 设置为白色，即选中状态
//
//    }
//
//    /**
//     * 设置当前view
//     *
//     * @param position
//     */
//    private void setCurView(int position) {
//        if (position < 0 || position >= pics.length) {
//            return;
//        }
//        vp.setCurrentItem(position);
//    }
//
//    /**
//     * 设置当前指示点
//     *
//     * @param position
//     */
//    private void setCurDot(int position) {
//        if (position < 0 || position > pics.length || currentIndex == position) {
//            return;
//        }
//        dots[position].setEnabled(true);
//        dots[currentIndex].setEnabled(false);
//        currentIndex = position;
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v.getTag().equals("enter")) {
//            enterMainActivity();
//            return;
//        }
//
//        int position = (Integer) v.getTag();
//        setCurView(position);
//        setCurDot(position);
//    }
//
//
//    private void enterMainActivity() {
//        if (!AppCommon.checkLogin()) {
//            gotoActivity(LoginActivity.class,true);
//        }else {
//            gotoActivity(MainActivity.class,true);
//        }
//        SpUtils.putBoolean(WelcomeGuideActivity.this, SpUtils.FIRST_OPEN, true);
//        finish();
//    }
//
//    private class PageChangeListener implements ViewPager.OnPageChangeListener {
//        // 当滑动状态改变时调用
//        @Override
//        public void onPageScrollStateChanged(int position) {
//            // arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。
//
//        }
//
//        // 当前页面被滑动时调用
//        @Override
//        public void onPageScrolled(int position, float arg1, int arg2) {
//            // arg0 :当前页面，及你点击滑动的页面
//            // arg1:当前页面偏移的百分比
//            // arg2:当前页面偏移的像素位置
//
//        }
//
//        // 当新的页面被选中时调用
//        @Override
//        public void onPageSelected(int position) {
//            // 设置底部小点选中状态
//            setCurDot(position);
//            if (position == 2){
//                startBtn.setVisibility(View.VISIBLE);
//            }else {
//                startBtn.setVisibility(View.GONE);
//            }
//        }
//
//    }
//}

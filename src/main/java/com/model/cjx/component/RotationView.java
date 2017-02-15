package com.model.cjx.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.model.cjx.MyApplication;
import com.model.cjx.R;
import com.model.cjx.bean.AdvertiseBean;
import com.model.cjx.util.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cjx on 2016/8/20.
 * 循环广告控件
 */
public class RotationView extends RelativeLayout {

    private MyViewPager pager;
    ImageRepeatPagerAdapter adapter;
    private PagerPointView pointView;

    private int dotSize = 0, spaceSize = 0;

    private boolean pause = false;
    private int DOT_UNSELECT = 0, DOT_SELECT = 0;

    private Timer timer = null;
    private MyTimerTask timerTask = null;
    private List<AdvertiseBean> poster;
    private View[] views;
    private View firstView, lastView;

    OnSingleTouchListener onSingleTouchListener;
    FacePageChangeListener pageChangeListener;

    public RotationView(Context context) {
        super(context);
        initView(context);
    }

    public RotationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RotationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        int screenWidth = MyApplication.getInstance().getScreen_width();
        dotSize = (int) (25 * screenWidth / 720f);
        spaceSize = dotSize;

        pointView = new PagerPointView(context);
        LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.bottomMargin = (int) (dotSize / 2.5f);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        pointView.setLayoutParams(lp);

        pager = new MyViewPager(context);
        pager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        setPause(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        setPause(false);
                        break;
                }
                return false;
            }
        });
        addView(pager);
        addView(pointView);
    }

    /**
     * 设置一个适配器和标记页数的点
     *
     * @param poster
     */
    public void setData(Activity activity, ArrayList<AdvertiseBean> poster) {
        if (poster == null) {
            return;
        }
        this.poster = poster;
        if (this.poster.size() == 0) {
            pager.setAdapter(null);
            pointView.setVisibility(GONE);
            return;
        }
        pointView.setVisibility(VISIBLE);
        int length = poster.size();
        if (length > 1) { // 给链表的头部和尾部添加多一条数据,实现无限循环
            AdvertiseBean first = poster.get(length - 1);
            AdvertiseBean last = poster.get(0);
            poster.add(last);
            poster.add(0, first);
            if (firstView == null) {
                firstView = getView();
            }
            int viewCount = length == 2 ? 2 : 3; // 当只有2个广告时, 只需额外两个缓存view, 大于2个广告时, 需要三个缓存view
            if (views == null) {
                views = new View[viewCount];
                for (int i = 0; i < viewCount; i++) {
                    views[i] = getView();
                }
            }else{ // 当缓存view 已经存在, 并且当前需要三个缓存view,且 之前不是三个缓存view时:
                if(viewCount == 3 && views.length != 3){
                    View[] newViews = new View[viewCount];
                    newViews[0] = views[0];
                    newViews[1] = views[1];
                    newViews[2] = getView();
                    views = newViews;
                }
            }
        }
        if (lastView == null) {
            lastView = getView();
        }
        if (adapter == null) {
            adapter = new ImageRepeatPagerAdapter(activity, firstView, lastView, views, poster);
            DOT_SELECT = R.drawable.dot_selected;
            DOT_UNSELECT = R.drawable.dot_unselected;
            pager.setAdapter(adapter);
            pointView.setPoint(dotSize, spaceSize, DOT_SELECT, DOT_UNSELECT, length);
            pageChangeListener = new FacePageChangeListener(activity, poster.size(), pointView);
            pager.setOnPageChangeListener(pageChangeListener);
            if (length != 1) {
                pager.setCurrentItem(1);
            }
        } else {
            adapter.notifyDataSetChanged(firstView, lastView, views, poster);
            pointView.setPoint(dotSize, spaceSize, DOT_SELECT, DOT_UNSELECT, length);
            pager.setCurrentItem(length == 1 ? 0 : 1);
            pageChangeListener.setPageCount(poster.size());
        }
        if (length != 1) {
            startScroll();
        } else {
            stopScroll();
        }
    }

    private View getView() {
        TouchImageView tiv = new TouchImageView(getContext());
        tiv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tiv.setImageGestureListener(listener);
        tiv.setBackgroundColor(Color.BLACK);
        tiv.setScaleTouch(false);
        return tiv;
    }

    public void setPause(boolean pause) {
        if (this.pause == pause) {
            return;
        }
        this.pause = pause;
        if (!pause) {
            if (timer == null) {
                startScroll();
            }
        } else if (pause && timerTask != null) {
            timerTask.initSecond();
        }
    }

    /**
     * 开始翻页
     */
    public void startScroll() {
        try {
            if (poster == null) {
                return;
            }
            if (timer != null) {
                timer.cancel();
                timerTask.cancel();
            }
            timer = new Timer(true);
            timerTask = new MyTimerTask();
            timer.schedule(timerTask, 0, 1000);
            if (pager.getTag() == null) {
                pager.setTag(new Runnable() {
                    @Override
                    public void run() {
                        int position = pager.getCurrentItem();
                        pager.setCurrentItem(position + 1, true);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopScroll() {
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动翻转的计时器
     */
    class MyTimerTask extends TimerTask {
        int second = 0;
        /**
         * 默认翻转的时间间隔, 单位/s
         */
        private final int SCROLL_PERIOD = 5;

        public void initSecond() {
            second = 0;
        }

        @Override
        public void run() {
            while (pause) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            second++;
            if (second == SCROLL_PERIOD) {
                second = 0;
                pager.post((Runnable) pager.getTag());
            }
        }
    }

    TouchImageView.ImageGestureListener listener = new TouchImageView.ImageGestureListener() {
        @Override
        public void onSingleTapConfirmed(MotionEvent e) {
            int count = pager.getCurrentItem();
            int size = poster.size();
            if (size > 1) {
                if (count == 0) {
                    count = size - 1;
                } else if (count == size + 1) {
                    count = 0;
                } else {
                    count = count - 1;
                }
            }
            if(onSingleTouchListener != null){
                onSingleTouchListener.onSingleTouch(poster.get(count));
            }
        }
    };

    /**
     * 创建点击事件接口
     */
    public interface OnSingleTouchListener {
        void onSingleTouch(AdvertiseBean m);
    }

    class FacePageChangeListener implements ViewPager.OnPageChangeListener {
        Context c;
        PagerPointView pv;
        int pageCount;

        public FacePageChangeListener(Context c, int pageCount, PagerPointView pv) {
            this.pv = pv;
            this.c = c;
            this.pageCount = pageCount;
        }

        public void setPageCount(int count) {
            pageCount = count;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            if (arg0 == 0) {
                if (pageCount > 1) {
                    if (pager.getCurrentItem() == pageCount - 1) { // 停在最后一个, 就将界面刷到第二个
                        pager.setCurrentItem(1, false);
                    } else if (pager.getCurrentItem() == 0) { // 停在第一个, 就刷到倒数第二个
                        pager.setCurrentItem(pageCount - 2, false);
                    }
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            if (pageCount == 1) {
                pv.setPosition(0);
            } else {
                if (arg0 == pageCount - 1) {
                    pv.setPosition(0);
                } else if (arg0 == 0) {
                    pv.setPosition(pageCount - 3);
                } else {
                    pv.setPosition(arg0 - 1);
                }
            }

        }
    }

    public interface CanScrollListener {
        void closeScroll();
    }


    class ImageRepeatPagerAdapter extends PagerAdapter {
        View[] views;
        int photoCount = 0;
        ArrayList<AdvertiseBean> photos;
        Activity context;
        View first, last;

        public ImageRepeatPagerAdapter(Activity context, View first, View last, View[] l, ArrayList<AdvertiseBean> photos) {
            this.context = context;
            this.photoCount = photos == null ? 0 : photos.size();
            this.photos = photos;
            views = l;
            this.first = first;
            this.last = last;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v;
            if (photoCount == 4) {
                if (position == 2) {
                    v = last;
                } else if (position == 3) {
                    v = first;
                } else {
                    v = views[position];
                }
            } else {
                if (position == 0 || position == photoCount - 2) {
                    v = last;
                } else if (position == 1 || position == photoCount - 1) {
                    v = first;
                } else {
                    v = views[(position - 2) % 3];
                }
            }

            container.removeView(v);
            TouchImageView image = (TouchImageView) v;
            container.addView(v);
            AdvertiseBean ib = photos.get(position);
            Tools.setImage(context, image, ib.image);
            return v;
        }

        @Override
        public int getCount() {
            return photoCount;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void notifyDataSetChanged(View firstView, View lastView, View[] views, ArrayList<AdvertiseBean> photos) {
            this.first = firstView;
            this.last = lastView;
            this.views = views;
            this.photoCount = photos == null ? 0 : photos.size();
            this.photos = photos;
            notifyDataSetChanged();
        }
    }
}

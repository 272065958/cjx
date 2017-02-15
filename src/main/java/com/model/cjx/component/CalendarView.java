package com.model.cjx.component;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.model.cjx.R;
import com.model.cjx.bean.DayBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

/**
 * Created by cjx on 2015/9/14. 显示一个月份的view
 */
public class CalendarView extends LinearLayout {
    List<DayBean> l;
    LayoutInflater inflate;
    TextView[] vs;
    Context c;
    int firstIndex = 0;
    int currentDay, currentMonth, currentYear;
    TextView yearMonth;
    int grayTextColor, blackTextColor, currentColor, grayBgColor, sunColor;
    HashSet<String> select;

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        c = context;
        l = new ArrayList<>();
        vs = new TextView[42];
        inflate = LayoutInflater.from(context);

        Resources res = getResources();
        grayTextColor = ContextCompat.getColor(context, R.color.cjx_text_secondary_color);
        blackTextColor = ContextCompat.getColor(context, R.color.cjx_text_deep_color);
        currentColor = ContextCompat.getColor(context, R.color.cjx_colorPrimary);
        grayBgColor = ContextCompat.getColor(context, R.color.cjx_divider_color);
        sunColor = ContextCompat.getColor(context, R.color.cjx_sun_color);
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH) + 1;
        currentDay = c.get(Calendar.DAY_OF_MONTH);
        yearMonth = new TextView(getContext());
        yearMonth.setTextColor(blackTextColor);
        yearMonth.setGravity(Gravity.CENTER);
        yearMonth.setTextSize(22);
        int padding = res.getDimensionPixelOffset(R.dimen.bigger_margin);
        yearMonth.setPadding(padding, padding, padding, padding);
        addView(yearMonth);
        View line = new View(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        line.setLayoutParams(lp);
        line.setBackgroundResource(R.color.cjx_divider_color);
        addView(line);
        addView(getCalendarHead(inflate));

        initDateView();
    }

    /**
     * 设置顶部显示的日期
     */
    public void setTopData(int year, int month) {
        if (month > 9) {
            yearMonth.setText(year + "-" + month);
        } else {
            yearMonth.setText(year + "-0" + month);
        }
        refreshData(year, month);
    }

    public void bind(HashSet<String> select) {
        this.select = select;
    }

    private void refreshData(int year, int month) {
        firstIndex = getWeekdayOfMonth(year, month);
        if (firstIndex > 0) {
            int prev;    //前一个月的总天数
            if (month == 1) {
                prev = getDaysOfMonth(year - 1, 12);
            } else {
                prev = getDaysOfMonth(year, month - 1);
            }
            for (int i = 0; i < firstIndex; i++) {
                int c = firstIndex - 1 - i;
                DayBean db = l.get(i);
                db.setDay(prev - c);
                db.setMonth(month == 1 ? 12 : month - 1);
                db.setYear(month == 1 ? year - 1 : year);
                db.setCurrentMonth(false);
            }
        }
        int count = getDaysOfMonth(year, month);
        for (int i = 0; i < count; i++) {
            DayBean db = l.get(firstIndex + i);
            db.setDay(i + 1);
            db.setCurrentMonth(true);
            db.setMonth(month);
            db.setYear(year);
        }
        int size = count + firstIndex;
        // 每月的日历有42个日期格子
        if (size < 42) {
            for (int i = size; i < 42; i++) {
                DayBean db = l.get(i);
                db.setCurrentMonth(false);
                db.setYear(month == 12 ? year + 1 : year);
                db.setMonth(month == 12 ? 1 : month + 1);
                db.setDay(i + 1 - size);
            }
        }
        for (int i = 0; i < l.size(); i++) {
            setDayStyle(vs[i], l.get(i));
        }
    }

    /**
     * 创建一行显示时间的界面
     */
    private void initDateView() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.widthPixels / 7;
        for (int i = 0; i < 6; i++) {
            View child = inflate.inflate(R.layout.calendar_component, null);
            TextView v = (TextView) child.findViewById(R.id.data_tv_1);
            vs[(i * 7)] = v;
            l.add(new DayBean());
            v.setHeight(height);
            v.setOnClickListener(clickListener);
            v = (TextView) child.findViewById(R.id.data_tv_2);
            vs[i * 7 + 1] = v;
            l.add(new DayBean());
            v.setHeight(height);
            v.setOnClickListener(clickListener);
            v = (TextView) child.findViewById(R.id.data_tv_3);
            vs[i * 7 + 2] = v;
            l.add(new DayBean());
            v.setHeight(height);
            v.setOnClickListener(clickListener);
            v = (TextView) child.findViewById(R.id.data_tv_4);
            vs[i * 7 + 3] = v;
            l.add(new DayBean());
            v.setHeight(height);
            v.setOnClickListener(clickListener);
            v = (TextView) child.findViewById(R.id.data_tv_5);
            vs[i * 7 + 4] = v;
            l.add(new DayBean());
            v.setHeight(height);
            v.setOnClickListener(clickListener);
            v = (TextView) child.findViewById(R.id.data_tv_6);
            vs[i * 7 + 5] = v;
            l.add(new DayBean());
            v.setHeight(height);
            v.setOnClickListener(clickListener);
            v = (TextView) child.findViewById(R.id.data_tv_7);
            vs[i * 7 + 6] = v;
            l.add(new DayBean());
            v.setHeight(height);
            v.setOnClickListener(clickListener);
            addView(child);
        }
    }

    /**
     * 设置单个日期的布局
     */
    private void setDayStyle(TextView v, DayBean db) {
        if (db.isCurrentMonth()) {
            if (db.getYear() == currentYear && db.getMonth() == currentMonth) {
                if (db.getDay() < currentDay) {
                    v.setTextColor(grayTextColor);
                    v.setBackgroundColor(Color.WHITE);
                    v.setClickable(false);
                } else {
                    v.setClickable(true);
                    db.setTime();
                    if (select.contains(db.getTime())) {
                        v.setTextColor(Color.WHITE);
                        v.setBackgroundColor(currentColor);
                    } else {
                        v.setBackgroundColor(Color.WHITE);
                        if (db.getDay() == currentDay) {
                            v.setTextColor(currentColor);
                        } else {
                            v.setTextColor(blackTextColor);
                        }
                    }
                }

            } else {
                v.setClickable(true);
                db.setTime();
                if (select.contains(db.getTime())) {
                    v.setTextColor(Color.WHITE);
                    v.setBackgroundColor(currentColor);
                } else {
                    v.setTextColor(blackTextColor);
                    v.setBackgroundColor(Color.WHITE);
                }
            }

        } else {
            v.setClickable(false);
            v.setTextColor(grayTextColor);
            v.setBackgroundColor(grayBgColor);
        }
        v.setTag(db);
        v.setText(String.valueOf(db.getDay()));
    }

    /**
     * 得到某月有多少天数
     */
    private int getDaysOfMonth(int year, int month) {
        int daysOfMonth = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                daysOfMonth = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                daysOfMonth = 30;
                break;
            case 2:
                if (isLeapYear(year)) {
                    daysOfMonth = 29;
                } else {
                    daysOfMonth = 28;
                }

        }
        return daysOfMonth;
    }

    /**
     * 判断是否为闰年
     */
    private boolean isLeapYear(int year) {
        if (year % 100 == 0 && year % 400 == 0) {
            return true;
        } else if (year % 100 != 0 && year % 4 == 0) {
            return true;
        }
        return false;
    }

    /**
     * 指定某年中的某月的第一天是星期几
     */
    private int getWeekdayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 获取一个显示星期行的view
     */
    private View getCalendarHead(LayoutInflater inflate) {
        View head = inflate.inflate(R.layout.calendar_component, null);
        TextView v = (TextView) head.findViewById(R.id.data_tv_1);
        setWeekStyle(v, true);
        v.setText("日");
        v = (TextView) head.findViewById(R.id.data_tv_2);
        setWeekStyle(v, false);
        v.setText("一");
        v = (TextView) head.findViewById(R.id.data_tv_3);
        setWeekStyle(v, false);
        v.setText("二");
        v = (TextView) head.findViewById(R.id.data_tv_4);
        setWeekStyle(v, false);
        v.setText("三");
        v = (TextView) head.findViewById(R.id.data_tv_5);
        setWeekStyle(v, false);
        v.setText("四");
        v = (TextView) head.findViewById(R.id.data_tv_6);
        setWeekStyle(v, false);
        v.setText("五");
        v = (TextView) head.findViewById(R.id.data_tv_7);
        setWeekStyle(v, false);
        v.setText("六");
        return head;
    }

    /**
     * 设置星期行的布局
     */
    private void setWeekStyle(TextView v, boolean isSunDay) {
        v.setTextColor(isSunDay ? sunColor : grayTextColor);
        v.setTextSize(14);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            DayBean db = (DayBean) v.getTag();
            if(select.contains(db.getTime())){
                select.remove(db.getTime());
                if(db.getYear() == currentYear && db.getMonth() == currentMonth && db.getDay() == currentDay){
                    ((TextView)v).setTextColor(currentColor);
                }else{
                    ((TextView)v).setTextColor(blackTextColor);
                }
                v.setBackgroundColor(Color.WHITE);
            } else {
                select.add(db.getTime());
                v.setBackgroundColor(currentColor);
                ((TextView)v).setTextColor(Color.WHITE);
            }
        }
    };

    public void destroy() {
        inflate = null;
        if(l != null){
            l.clear();
            l = null;
        }
        vs = null;
        c = null;
        select = null;
    }

}

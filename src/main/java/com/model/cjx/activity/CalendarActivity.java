package com.model.cjx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.model.cjx.R;
import com.model.cjx.component.CalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by cjx. 选择日期界面
 */
public class CalendarActivity extends BaseActivity {
    CalendarAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Intent intent = getIntent();
        setToolBar(true, null, "");

        listView = (ListView) findViewById(R.id.list_view);
        // 默认选中的日期数组
        String[] defaultTime = intent.getStringArrayExtra("time");
        HashSet<String> select = null;
        if (defaultTime != null) {
            select = new HashSet<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date(System.currentTimeMillis()));
            try {
                Date todayDate = sdf.parse(today);
                // 判断默认要显示的时间如果早于当前时间, 则不选中
                for (String aDefaultTime : defaultTime) {
                    try {
                        Date d = sdf.parse(aDefaultTime);
                        if (!d.before(todayDate)) {
                            select.add(aDefaultTime);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        adapter = new CalendarAdapter(select);
        listView.setAdapter(adapter);
    }

    public void comfirm() {
        Intent data = new Intent();
        int size = adapter.select.size();
        String[] times = new String[size];
        Iterator<String> it = adapter.select.iterator();
        int i = 0;
        while (it.hasNext()) {
            times[i] = it.next();
            i++;
        }
        if (size > 1) {
            Arrays.sort(times);
        }
        data.putExtra("time", times);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comfirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        comfirm();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int c = listView.getChildCount();
        for (int i = 0; i < c; i++) {
            View v = listView.getChildAt(i);
            if (v instanceof CalendarView) {
                ((CalendarView) v).destroy();
            }
        }
        listView.setAdapter(null);
        adapter.destroy();

    }

    class CalendarAdapter extends BaseAdapter {
        ArrayList<YearMonth> list;
        HashSet<String> select;
        int count = 12;

        public CalendarAdapter(HashSet<String> select) {
            Calendar c = Calendar.getInstance();
            int currendYear = c.get(Calendar.YEAR);
            int currendMonth = c.get(Calendar.MONTH);
            list = new ArrayList<>();
            if (select != null) {
                this.select = select;
            } else {
                this.select = new HashSet<>();
            }
            for (int i = 0; i < count; i++) {
                YearMonth my = new YearMonth();
                currendMonth++;
                if (currendMonth == 13) {
                    currendMonth = 1;
                    currendYear++;
                }
                my.year = currendYear;
                my.month = currendMonth;
                list.add(my);
            }
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public YearMonth getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) {
                v = new CalendarView(CalendarActivity.this);
                ((CalendarView) v).bind(select);
            }
            YearMonth ym = getItem(position);
            ((CalendarView) v).setTopData(ym.year, ym.month);
            return v;
        }

        void destroy() {
            list.clear();
            select.clear();
        }
    }

    class YearMonth {
        int month;
        int year;
    }
}
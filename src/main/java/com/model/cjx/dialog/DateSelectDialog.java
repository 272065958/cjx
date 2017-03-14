package com.model.cjx.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.model.cjx.R;
import com.model.cjx.component.NumberPickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cjx on 2016/2/24.
 * 时间选择器
 */
public class DateSelectDialog extends CustomDialog implements View.OnClickListener, NumberPickerView.OnValueChangeListenerRelativeToRaw {

    private DateType dateType = DateType.NORMAL;
    private DateType currentType;
    private Date currentDate;
    private String splite = "-";

    public enum ViewType {
        MONTH_SELECT, DATE_SELECT, TIME_SELECT, DATE_TO_HOUR_SELECT, DATE_TO_MINUTE_SELECT
    }

    public enum DateType {
        FETURE, PAST, NORMAL
    }

    private NumberPickerView yearView, monthView, dayView, hourView, minuteView;

    public DateSelectDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_date_select);
        findViewById(R.id.pick_comfirm).setOnClickListener(this);
    }

    public DateSelectDialog setDateType(DateType dateType) {
        this.dateType = dateType;
        return this;
    }

    public DateSelectDialog setViewType(ViewType viewType, String splite) {
        this.splite = splite;
        Calendar c = Calendar.getInstance();
        switch (viewType) {
            case MONTH_SELECT:
                setDate(c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH) + 1, -1);
                break;
            case DATE_SELECT:
                setDate(c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
                break;
            case DATE_TO_HOUR_SELECT:
                setDate(c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY));
                break;
            case TIME_SELECT:
                setDate(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                break;
            case DATE_TO_MINUTE_SELECT:
                setDate(c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                break;
        }
        return this;
    }

    private void setDate(int year, int month, int day) {
        setDate(year, month, day, -1);
    }

    private void setDate(int hour, int minute) {
        setDate(-1, -1, -1, hour, minute);
    }

    private void setDate(int year, int month, int day, int hour) {
        setDate(year, month, day, hour, -1);
    }

    private void setDate(int year, int month, int day, int hour, int minute) {
        if (year != -1) {
            // year
            yearView = (NumberPickerView) findViewById(R.id.year_picker);
            yearView.setOnValueChangedListenerRelativeToRaw(this);
            yearView.setVisibility(View.VISIBLE);
            String[] displayedValues = new String[20];
            for (int i = 0; i < 20; i++) {
                displayedValues[i] = String.valueOf(year + i - 9);
            }
            yearView.setDisplayedValues(displayedValues);
            yearView.setHintText("年");
            yearView.setValue(9);
            // month
            monthView = (NumberPickerView) findViewById(R.id.month_picker);
            monthView.setOnValueChangedListenerRelativeToRaw(this);
            monthView.setVisibility(View.VISIBLE);
            displayedValues = new String[12];
            for (int i = 0; i < 12; i++) {
                displayedValues[i] = String.valueOf(i + 1);
            }
            monthView.setDisplayedValues(displayedValues);
            monthView.setHintText("月");
            monthView.setValue(month - 1);
            if (day != -1) {
                // day
                dayView = (NumberPickerView) findViewById(R.id.day_picker);
                dayView.setVisibility(View.VISIBLE);
                int dayCount = getDayCount(year, month);
                displayedValues = new String[dayCount];
                for (int i = 0; i < dayCount; i++) {
                    displayedValues[i] = String.valueOf(i + 1);
                }
                dayView.setDisplayedValues(displayedValues);
                dayView.setHintText("日");
                dayView.setValue(day - 1);
            }
        }
        if (hour != -1) {
            hourView = (NumberPickerView) findViewById(R.id.hour_picker);
            hourView.setVisibility(View.VISIBLE);
            String[] displayedValues = new String[24];
            for (int i = 0; i < 24; i++) {
                displayedValues[i] = String.valueOf(i);
            }
            hourView.setDisplayedValues(displayedValues);
            hourView.setHintText("时");
            hourView.setValue(hour - 1);
        }
        if (minute != -1) {
            minuteView = (NumberPickerView) findViewById(R.id.minute_picker);
            minuteView.setVisibility(View.VISIBLE);
            String[] displayedValues = new String[60];
            for (int i = 0; i < 60; i++) {
                displayedValues[i] = String.valueOf(i);
            }
            minuteView.setDisplayedValues(displayedValues);
            minuteView.setHintText("分");
            minuteView.setValue(minute);
        }
    }

    public DateSelectDialog bind(DateSelectListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void show() {
        this.currentType = null;
        this.currentDate = null;
        super.show();
    }

    public void show(@NonNull DateType currentType, @NonNull Date currentDate) {
        this.currentType = currentType;
        this.currentDate = currentDate;
        super.show();
    }

    @Override
    public void onClick(View v) {
        String time = getTime(splite);
        if (time != null) {
            if (listener != null) {
                listener.select(time);
            }
            dismiss();
        }
    }

    @Override
    public void onValueChangeRelativeToRaw(NumberPickerView picker, int oldPickedIndex, int newPickedIndex,
                                           String[] displayedValues) {
        int month;
        if (picker == monthView) {
            month = Integer.parseInt(displayedValues[newPickedIndex]);
        } else {
            month = Integer.parseInt(monthView.getContentByCurrValue());
            if (month != 2) { // 如果是2月的话, 需要计算是否闺年
                return;
            }
        }
        if (dayView != null) {
            int dayCount = getDayCount(Integer.parseInt(yearView.getContentByCurrValue()), month);
            if (dayView.getDisplayedValues().length != dayCount) {
                String[] newValues = new String[dayCount];
                for (int i = 0; i < dayCount; i++) {
                    newValues[i] = String.valueOf(i + 1);
                }
                dayView.setDisplayedValues(newValues);
            }
        }
    }

    DateSelectListener listener;

    public interface DateSelectListener {
        void select(String date);
    }

    // 获取本月的总天数
    private int getDayCount(int year, int month) {
        int dayCount = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                dayCount = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                dayCount = 30;
                break;
            case 2:
                if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                    dayCount = 29;
                } else {
                    dayCount = 28;
                }
                break;
        }
        return dayCount;
    }

    /**
     * 获取当前设置的时间
     *
     * @return
     */
    private String getTime(String split) {
        StringBuffer formatBuffer = new StringBuffer();
        StringBuilder sb = new StringBuilder();
        if (yearView != null) {
            sb.append(yearView.getContentByCurrValue());
            sb.append(split);
            String month = monthView.getContentByCurrValue();
            if (month.length() < 2) {
                sb.append("0");
            }
            sb.append(month);
            formatBuffer.append("yyyy").append(split).append("MM");
            if (dayView != null) {
                sb.append(split);
                String day = dayView.getContentByCurrValue();
                if (day.length() < 2) {
                    sb.append("0");
                }
                sb.append(day);
                formatBuffer.append(split).append("dd");
            }
        }
        if (hourView != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            String hour = hourView.getContentByCurrValue();
            if (hour.length() < 2) {
                sb.append("0");
            }
            sb.append(hour);
            sb.append(":");
            if (minuteView != null) {
                String minute = minuteView.getContentByCurrValue();
                if (minute.length() < 2) {
                    sb.append("0");
                }
                sb.append(minute);
            } else {
                sb.append("00");
            }
            formatBuffer.append(" hh:mm");
        }
        String time = sb.toString();
        if(currentType == null && dateType == DateType.NORMAL){
            return time;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formatBuffer.toString());
        Date selectDate = null;
        try {
            selectDate = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(selectDate == null){
            Toast.makeText(getContext(), "time dialog error", Toast.LENGTH_SHORT).show();
            return null;
        }
        if(currentType != null){
            switch (currentType) {
                case FETURE:
                    if (selectDate.before(currentDate)) {
                        time = null;
                        Toast.makeText(getContext(), "选择时间不能早于开始时间", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PAST:
                    if (selectDate.after(currentDate)) {
                        time = null;
                        Toast.makeText(getContext(), "选择时间不能晚于结束时间", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
        if (dateType != DateType.NORMAL && time != null) {
            switch (dateType) {
                case FETURE:
                    if (selectDate.before(new Date())) {
                        time = null;
                        Toast.makeText(getContext(), "选择时间不能早于当前时间", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PAST:
                    if (selectDate.after(new Date())) {
                        time = null;
                        Toast.makeText(getContext(), "选择时间不能晚于当前时间", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
        return time;
    }
}

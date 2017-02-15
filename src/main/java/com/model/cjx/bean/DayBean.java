package com.model.cjx.bean;

public class DayBean {
	int year;
	int month;
	int day;
	boolean currentMonth;
	String time;
	public boolean isCurrentMonth() {
		return currentMonth;
	}
	public void setCurrentMonth(boolean currentMonth) {
		this.currentMonth = currentMonth;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public void setTime(){
		time = year+"-"+(month > 9 ? month : "0"+month)+"-"+(day > 9 ? day : "0"+day);
	}
	public String getTime(){
		return time;
	}
}

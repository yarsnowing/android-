package com.guo.CityWeather;

import android.graphics.Bitmap;
public class WeatherForecastCondition {
	
	private String day_of_week;		//����
	private String low;				//����¶�
	private String high;			//����¶�
	private String icon;			//ͼ����ַ
	private String condition;		//��ʾ
	private Bitmap bm; //ͼ��
	//����������ʼ��������
	public WeatherForecastCondition()
	{

	}
	//��ȡ��������
	public String getCondition()
	{
		return condition;
	}

	//��������Ԥ��
	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	//��ȡ����
	public String getDay_of_week()
	{
		return day_of_week;
	}

	//��������
	public void setDay_of_week(String day_of_week)
	{
		this.day_of_week = day_of_week;
	}

	//��ȡ����¶�
	public String getLow()
	{
		return low;
	}

	//��������¶�
	public void setLow(String low)
	{
		this.low = low;
	}

	//��������¶�
	public String getHigh()
	{
		return high;
	}

	//��������¶�
	public void setHigh(String high)
	{
		this.high = high;
	}

	//ȡ��ͼ��
	public String getIcon()
	{
		return icon;
	}

	//����ͼ��
	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	//����ͼ��
	public void setBm(Bitmap bm)
	{
		this.bm = bm;
	}

	//�õ�ͼ��
	public Bitmap getBm()
	{
		return bm;
	}
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(" ").append(day_of_week);
		sb.append(" : ").append(high);
		sb.append("/").append(low).append(" ��C");
		sb.append(" ").append(condition);
		return sb.toString();
	}
}
package com.guo.CityWeather;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
//�½�һ����ͼ�̳�LinearLayout��������ʾ����Ԥ����Ϣ
public class SingleWeatherInfoView extends LinearLayout
{
	//������ʾ����״��ͼƬ
	private ImageView	myWeatherImageView	= null;
	//������ʾ������ϸ��Ϣ
	private TextView	myTempTextView		= null;

	public SingleWeatherInfoView(Context context)
	{
		super(context);
	}
	public SingleWeatherInfoView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		//����ͼ��λ�õ���Ϣ
		this.myWeatherImageView = new ImageView(context);
		this.myWeatherImageView.setPadding(10, 5, 5, 5);
		//�����ı���ɫ�������С
		this.myTempTextView = new TextView(context);
		this.myTempTextView.setTextColor(R.color.black);
		this.myTempTextView.setTextSize(16);
		//��ImageViewԪ����ӵ���ǰ��LinearLayout
		this.addView(this.myWeatherImageView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//��TextViewԪ����ӵ���ǰ��LinearLayout
		this.addView(this.myTempTextView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
	//�����ı�����
	public void setWeatherString(String aWeatherString)
	{
		this.myTempTextView.setText(aWeatherString);
	}
	//����ͼƬ
	public void setWeatherIcon(Bitmap bm)
	{
		this.myWeatherImageView.setImageBitmap(bm);
	}
}

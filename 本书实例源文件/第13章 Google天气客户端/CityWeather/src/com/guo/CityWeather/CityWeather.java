package com.guo.CityWeather;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CityWeather extends Activity
{
	//������ʾ������Ϣ
	private String cityNow="";
	//google����Ԥ���Ļ�׼��ַ
	private static String GOOGLE="http://www.google.com.hk";
	private URL url;
	final int DIALOG_YES_NO_MESSAGE=1;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//��ʼ������
		init();
	}
	//�����ʼ��
	private void init()
	{
		Spinner city_spr = (Spinner) findViewById(R.id.citySpinner);
		//�½����������󶨳�������
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ConstData.city);
		//���������˵��Ĳ���
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Ϊspinner��������
		city_spr.setAdapter(adapter);

		Button submit = (Button) findViewById(R.id.btn1);
		//Ϊ��ť�󶨰���������
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Spinner spr = (Spinner) findViewById(R.id.citySpinner);
				//ȡ��ѡ��item��idֵ
				Long l = spr.getSelectedItemId();
				//��long��ת��Ϊint��
				int index = l.intValue();
				//ͨ�����е�idֵȡ�þ�γ����Ϣ
				String cityParamString = ConstData.cityCode[index];
				//ȡ�õ�ǰѡ�еĳ��е�����
				cityNow=(String) spr.getSelectedItem();
				try
				{
					//ȡ������Ԥ����url��ַ
					url = new URL(ConstData.queryString + cityParamString);
					new Thread(){
						public void run()
						{
							//��ȡ������Ϣ
							getCityWeather(url);
						}
					}.start();
				}
				catch (Exception e)
				{
					//�����������ʾ�Ի�����ʾ�û�
					showDialog1(DIALOG_YES_NO_MESSAGE);
				}			
			}
		});	
		Button submit_input = (Button) findViewById(R.id.btn2);
		//Ϊ��ť�󶨰���������
		submit_input.setOnClickListener(new OnClickListener()
        {
			public void onClick(View v) 
			{
				//�����
				EditText inputcity = (EditText) findViewById(R.id.cityEt);
				//ȡ������������
				final String tmp = inputcity.getText().toString();
				//���������ƴ洢��cityNow������ȥ����ʾ��
				cityNow=tmp;
				new Thread(){
					public void run()
					{
						URL url;
						try {
							//ȡ������Ԥ����url��ע�����������ַ���Ҫת����utf-8����������
							url = new URL(ConstData.queryString_intput + to_Chanese(tmp));
							getCityWeather(url);
						} catch (MalformedURLException e) {
							// ����������ʾ����Ի���
							showDialog1(DIALOG_YES_NO_MESSAGE);
						}						
					}
				}.start();
			}      	
        });
	}
	//��ʾ�Ի�����Ϣ
	void showDialog1(int id) {
		CreateDialog(id).show();
	}
	//���ɶԻ���
	protected Dialog CreateDialog(int id) {
		switch (id) {
		case DIALOG_YES_NO_MESSAGE:
			return new AlertDialog.Builder(this).setIcon(
					//���ñ���:�Բ������ݣ�û���ҵ������Ϣ
					R.drawable.alert_dialog_icon).setTitle(R.string.sorry)  
					.setMessage(R.string.find_nothing).setPositiveButton(
							R.string.conform,
							new DialogInterface.OnClickListener() {
								//���ð�ť�����κβ�����ֱ�ӹص��Ի���
								public void onClick(DialogInterface dialog,  
										int whichButton) {
								}
							}).create();
		}
		return null;
	}
	  //�ж��Ƿ��к���
	  public boolean vd(String str)
	  {   
		  //ȡ���ַ������ֽ���
		  byte[] bytes=str.getBytes();
		  //����ֽ�������1˵���Ǻ��֣�������
		  if (bytes.length>1)
			  return true;
		  else 
			  return false;
	  }
	  //������ת��UTF8��ʽ
	  public String to_Chanese(String str)
	  {
		  String s_chin = "";  
		  String s_ch_one;
		  for (int i=0;i<str.length();i++)
		  {
			  //���ν�ȡÿһ���ַ�
			  s_ch_one=str.substring(i,i+1);
			  //����ÿһ�ַ�
			  if (vd(s_ch_one))
			  {
				  try  
				  {
					  //����Ǻ�����ת����utf-8�ĸ�ʽ
					  s_chin=s_chin+URLEncoder.encode(s_ch_one,"utf8");
				  }
				  catch (UnsupportedEncodingException e) {
					  // TODO Auto-generated catch block
					  e.printStackTrace();
				  }
			  }
			  else
				  s_chin=s_chin+s_ch_one;
	  }
		  //���ؾ���ת�����ַ���
		  return s_chin;
	  }
	
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg)
		{
			//��ǰ������Ϣ
			if(msg.what ==1)
			{
				//���õ�ǰ��������
				((TextView)findViewById(R.id.currentCity)).setText(cityNow);
				int aResourceID=msg.arg1;
				WeatherCurrentCondition aWCC=(WeatherCurrentCondition)msg.obj;		
				//����ͼƬ
				((SingleWeatherInfoView) findViewById(aResourceID)).setWeatherIcon(aWCC.getBm());
				//��������Ԥ����ϸ��Ϣ
				((SingleWeatherInfoView) findViewById(aResourceID)).setWeatherString(aWCC.toString());
			}
			//����Ԥ����Ϣ
			else if(msg.what == 2)
			{
				int aResourceID=msg.arg1;
				WeatherForecastCondition aWCC=(WeatherForecastCondition)msg.obj;	
				//����ͼƬ
				((SingleWeatherInfoView) findViewById(aResourceID)).setWeatherIcon(aWCC.getBm());
				//��������Ԥ����ϸ��Ϣ
				((SingleWeatherInfoView) findViewById(aResourceID)).setWeatherString(aWCC.toString());
			}
		}
	};
	// ������ʾʵʱ������Ϣ
	private void updateWeatherInfoView(int aResourceID, WeatherCurrentCondition aWCC) throws MalformedURLException
	{
		//ͨ��url��ַ��ȡλͼ��Ϣ
		URL imgURL = new URL(GOOGLE + aWCC.getIcon());
		Bitmap bm=getBm(imgURL);
		//��λͼ�洢����Ӧ������
		aWCC.setBm(bm);
		Message msg=new Message();
		msg.what=1;
		//����󶨵���Ϣ��
		msg.obj=aWCC;
		//����Ҫ���µĽ���Ԫ�ص�id�󶨵���Ϣ��
		msg.arg1=aResourceID;
		mHandler.sendMessage(msg);		
	}
	// ������ʾ����Ԥ��
	private void updateWeatherInfoView(int aResourceID, WeatherForecastCondition aWFC) throws MalformedURLException
	{
		//ͨ��url��ַ��ȡλͼ��Ϣ
		URL imgURL = new URL(GOOGLE + aWFC.getIcon());
		Bitmap bm=getBm(imgURL);
		//��λͼ�洢����Ӧ������
		aWFC.setBm(bm);
		Message msg=new Message();
		msg.what=2;
		//����󶨵���Ϣ��
		msg.obj=aWFC;
		//����Ҫ���µĽ���Ԫ�ص�id�󶨵���Ϣ��
		msg.arg1=aResourceID;
		mHandler.sendMessage(msg);
	}
	
	//��ȡ������Ϣ
	//ͨ�������ȡ����
	//���ݸ�XMLReader����
	public void getCityWeather(URL url)
	{
		try
		{
			//�½�һ��sax������
			SAXParserFactory spf = SAXParserFactory.newInstance();
			//ʵ����sax������
			SAXParser sp = spf.newSAXParser();
			//����sax��ȡ��
			XMLReader xr = sp.getXMLReader();
			//���ý��������õĴ�����
			GoogleWeatherHandler gwh = new GoogleWeatherHandler();
			xr.setContentHandler(gwh);
			//��ȡ��ҳ��������
			InputStreamReader isr = new InputStreamReader(url.openStream(), "GBK");
			//����������װ��InputSource
			InputSource is = new InputSource(isr);
			//����������
			xr.parse(is);
			//�������������Ϣ
			WeatherSet ws = gwh.getMyWeatherSet();
			//ͨ������������Ϣȡ�õ�ǰ������Ϣ
			updateWeatherInfoView(R.id.weather_0, ws.getMyCurrentCondition());
			//ͨ������������Ϣ�ֱ�ȡ�ý���ȥ�����������Ϣ
			updateWeatherInfoView(R.id.weather_1, ws.getMyForecastConditions().get(0));
			updateWeatherInfoView(R.id.weather_2, ws.getMyForecastConditions().get(1));
			updateWeatherInfoView(R.id.weather_3, ws.getMyForecastConditions().get(2));
			updateWeatherInfoView(R.id.weather_4, ws.getMyForecastConditions().get(3));
		}
		catch (Exception e)
		{
			Log.e("CityWeather", e.toString());
		}
	}
	//ͨ��url��ַȡ������״��ͼ��
	public Bitmap getBm(URL aURL)
	{
		URLConnection conn;
		Bitmap bm=null;
		try {
			//��url����
			conn = aURL.openConnection();
			conn.connect();
			//�����������浽is
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			//��λͼ������������������������ת����λͼ
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		//����λͼ
		return bm;		
	}

}

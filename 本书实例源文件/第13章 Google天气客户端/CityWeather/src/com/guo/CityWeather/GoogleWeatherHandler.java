package com.guo.CityWeather;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GoogleWeatherHandler extends DefaultHandler
{
	//������Ϣ
	private WeatherSet		myWeatherSet			= null;

	//ʵʱ������Ϣ
	private boolean			is_Current_Conditions	= false;
	//Ԥ��������Ϣ
	private boolean			is_Forecast_Conditions	= false;

	private final String	CURRENT_CONDITIONS		= "current_conditions";
	private final String	FORECAST_CONDITIONS		= "forecast_conditions";

	//��������ʵ������
	public GoogleWeatherHandler()
	{

	}
	//����������Ϣ����
	public WeatherSet getMyWeatherSet()
	{
		return myWeatherSet;
	}
	//�ĵ���β
	@Override
	public void endDocument() throws SAXException
	{
		// TODO Auto-generated method stub
		super.endDocument();
	}
	//Ԫ�ؽ�β
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		//���������ǰ������Ϣ��ǩ������Ӧ��־λ��Ϊfalse	
		if (localName.equals(CURRENT_CONDITIONS))
		{
			this.is_Current_Conditions = false;
		}
		//�����������Ԥ����Ϣ��ǩ������Ӧ��־λ��Ϊfalse
		else if (localName.equals(FORECAST_CONDITIONS))
		{
			this.is_Forecast_Conditions = false;
		}
	}

	//��ʼ�����ĵ�
	@Override
	public void startDocument() throws SAXException
	{
		this.myWeatherSet = new WeatherSet();
	}

	//��ʼ����Ԫ��
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{	
		if (localName.equals(CURRENT_CONDITIONS))
		{	//ʵʱ����
			this.myWeatherSet.setMyCurrentCondition(new WeatherCurrentCondition());
			this.is_Current_Conditions = true;
		}
		else if (localName.equals(FORECAST_CONDITIONS))
		{	//Ԥ������
			this.myWeatherSet.getMyForecastConditions().add(new WeatherForecastCondition());
			this.is_Forecast_Conditions = true;
		}
		else
		{
			//��ȡ���ԡ�data����ֵ
			String dataAttribute = attributes.getValue("data");
			//�����icon�����ж��ǵ�ǰ������icon��������Ԥ����icon
			if (localName.equals("icon"))
			{
				if (this.is_Current_Conditions)
				{
					this.myWeatherSet.getMyCurrentCondition().setIcon(dataAttribute);
				}
				else if (this.is_Forecast_Conditions)
				{
					this.myWeatherSet.getLastForecastCondition().setIcon(dataAttribute);
				}
			}
			//�����condition�����ж��ǵ�ǰ������condition��������Ԥ����condition
			else if (localName.equals("condition"))
			{
				if (this.is_Current_Conditions)
				{
					this.myWeatherSet.getMyCurrentCondition().setCondition(dataAttribute);
				}
				else if (this.is_Forecast_Conditions)
				{
					this.myWeatherSet.getLastForecastCondition().setCondition(dataAttribute);
				}
			}
			else if (localName.equals("temp_c"))
			{
				this.myWeatherSet.getMyCurrentCondition().setTemp_celcius(dataAttribute);
			}
			else if (localName.equals("temp_f"))
			{
				this.myWeatherSet.getMyCurrentCondition().setTemp_fahrenheit(dataAttribute);
			}
			else if (localName.equals("humidity"))
			{
				this.myWeatherSet.getMyCurrentCondition().setHumidity(dataAttribute);
			}
			else if (localName.equals("wind_condition"))
			{
				this.myWeatherSet.getMyCurrentCondition().setWind_condition(dataAttribute);
			}// Tags is forecast_conditions
			else if (localName.equals("day_of_week"))
			{
				this.myWeatherSet.getLastForecastCondition().setDay_of_week(dataAttribute);
			}
			else if (localName.equals("low"))
			{
				this.myWeatherSet.getLastForecastCondition().setLow(dataAttribute);
			}
			else if (localName.equals("high"))
			{
				this.myWeatherSet.getLastForecastCondition().setHigh(dataAttribute);
			}
		}
	}
	//�������Ԫ�ؽڵ��ı�
	@Override
	public void characters(char ch[], int start, int length)
	{
	}
}

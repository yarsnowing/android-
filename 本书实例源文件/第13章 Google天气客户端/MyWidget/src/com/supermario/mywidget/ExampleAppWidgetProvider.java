package com.supermario.mywidget;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ExampleAppWidgetProvider extends AppWidgetProvider{
	private String TAG="MyWidget";
	//widget��ɾ��ʱ����
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		Log.i(TAG,"onDeleted");
	}
	//�����һ��widgetʵ����ɾ��ʱ����
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		Log.i(TAG,"onDisabled");
	}
	//��widget������ʱ����
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Log.i(TAG,"onEnabled");
	}
	//��Ҫ���ڵ��ȸ�ExampleAppWidgetProvider���е���������
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		Log.i(TAG,"onReceive");
	}
	//����Ҫ�ṩRemoteViews����
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.i(TAG,"onUpdate");
	}
}

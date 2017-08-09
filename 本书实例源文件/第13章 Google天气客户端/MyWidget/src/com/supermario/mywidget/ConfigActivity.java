package com.supermario.mywidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

//��activity��ϵͳ����widgetʱ ������
public class ConfigActivity extends Activity{
	private int mAppWidgetId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//���õ�ǰ����layoutΪmain.xml
		setContentView(R.layout.main);	
		Log.i("ConfigActivity","onCreate!");
		//ȡ���������Activity��Intent
		Intent intent = getIntent();
		//ȡ�ø�intent����չ����
		Bundle extras = intent.getExtras();
		//�õ�widget��������id ÿһ��widget����һ��id������ͬ
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		Log.i("ConfigActivity",mAppWidgetId+"");
		//���û�д���AppWidgetId����ֱ�ӽ���
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}
		//������Ǳ��뷵��һ��RESULT_OK��Intent����������ǰActivity��ϵͳ�Ż���Ϊ���óɹ����������Ϸ������widget{
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				mAppWidgetId);

		setResult(RESULT_OK, resultValue);
		finish();
	}
}

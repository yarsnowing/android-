package com.guo.startAFR;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class StartAFRActivity extends Activity {
	TextView show;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    //��ʼ������Ԫ��
	    show=(TextView)findViewById(R.id.show);
	    Button btnOpen=(Button)this.findViewById(R.id.open);
	    btnOpen.setOnClickListener(new View.OnClickListener(){
	        public void onClick(View v) {
	            //�õ��´�Activity�رպ󷵻ص�����
	            //�ڶ�������Ϊ�����룬���Ը���ҵ�������Լ����
	            startActivityForResult(new Intent(StartAFRActivity.this, AnotherActivity.class), 1);
	        }
	    });
	}	
	/**
	 * Ϊ�˵õ����ص����ݣ�������ǰ���Activity�У�ָMainActivity�ࣩ��дonActivityResult����
	 * requestCode �����룬������startActivityForResult()���ݹ�ȥ��ֵ
	 * resultCode ����룬��������ڱ�ʶ�������������ĸ���Activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    String result = data.getExtras().getString("result");//�õ���Activity �رպ󷵻ص�����
	    show.setText(result);
	}
}
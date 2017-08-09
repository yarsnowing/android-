package com.guo.layoutinflate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LayoutInflateActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button bt1=(Button)findViewById(R.id.btn1);
        //Ϊ�����󶨼�����
        bt1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//��ʾ�Ի���
				showMyDialog();
			}   	
        });
    }
    //��ʾ�Ի���
    public void showMyDialog()    
    {    
        AlertDialog.Builder builder;    
        AlertDialog alertDialog;  //�½�һ���Ի���  
        Context mContext = LayoutInflateActivity.this;    //ȡ�õ�ǰ�����������
            
        //�������ַ���������    
        //LayoutInflater inflater = getLayoutInflater();    
        LayoutInflater inflater = (LayoutInflater)     
        mContext.getSystemService(LAYOUT_INFLATER_SERVICE);    
        View layout = inflater.inflate(R.layout.my_dialog,null);   //Ѱ�������Զ����layout 
        TextView text = (TextView) layout.findViewById(R.id.text);    
        text.setText("Hello, Welcome to Read my Book!");    //������i���ı�
        ImageView image = (ImageView) layout.findViewById(R.id.image);    
        image.setImageResource(R.drawable.phone);    //������ʾ��ͼƬ
        builder = new AlertDialog.Builder(mContext);    
        builder.setView(layout);    //������my_dialog�ĸ�ʽ��ʾ�Ի���
        alertDialog = builder.create();    
        alertDialog.show();    //��ʾ�Ի���
    }    
}
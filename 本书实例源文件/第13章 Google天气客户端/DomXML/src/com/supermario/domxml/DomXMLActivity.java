package com.supermario.domxml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DomXMLActivity extends Activity {
	//�½�һ����ť������Ӧ�û�����
	private Button start;
	//�½�һ��TextView���ڴ�Ž��
	private TextView show;
	//Assets�е�xml�ļ�����
	private String fileName="fruit.xml";
    InputStream inStream=null;
    /** �״δ�������ʱ����*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        show=(TextView)findViewById(R.id.show);
        start=(Button)findViewById(R.id.start);

		try {
			//��Assets�л�ȡ�ļ�
			inStream = this.getAssets().open(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Ϊ�������¼�
        start.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//���ڴ�Ž���ַ���
				String result="";
				//�����ַ���
				result=parse(inStream);
				//�������ʾ��������
				show.setText(result);
			}	
        });
    }
    //�����ַ���
    public String parse(InputStream inStream)
    {
    	String result="";
    	//ʵ����һ��DocumentBuilderFactory��
    	DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
    	DocumentBuilder builder=null;
    	Document doc=null;
    	try {
    		//ʵ����һ��DocumentBuilder���ڽ����ַ���
			builder=dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//�����ַ���
			doc=builder.parse(inStream);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element ele=doc.getDocumentElement();
		//��ȡ���еġ�fruit���ڵ�
		NodeList nl=ele.getElementsByTagName("fruit");
		if(nl != null && nl.getLength() != 0)
		{
			for(int i=0;i<nl.getLength();i++)
			{
				Element entry=(Element)nl.item(i);
				//���ڻ�ȡ����
				result += "name:"+entry.getAttribute("name")+"-->";
				//���ڻ�ȡ�ı�����
				result += entry.getTextContent()+"\n";
			}
		}
    	return result;
    }
}
package xom.supermario.pullxml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class PullXMLActivity extends Activity {
	//�½�һ������
	private Button button;  
	//�½�һ���б�
	private ListView listView;
	//�½�һ�������б����ڴ���ַ�������
	private ArrayList<String> list=new ArrayList<String>();  
	public void onCreate(Bundle savedInstanceState) {  
	    super.onCreate(savedInstanceState);  
	    setContentView(R.layout.main); 
	    button=(Button)findViewById(R.id.btn1);  
	    listView=(ListView) findViewById(R.id.listView1);
	    //Ϊ�����󶨼�����
	    button.setOnClickListener(new ButtonListener());  
	}  
	
	class ButtonListener implements OnClickListener{  
	
	    @Override  
	    public void onClick(View v) {  
		  //��������Ľ���洢��students��   
		   List<Student> students=parserXMl();
	    //	List<Student> students=null;
		   //ö�������е�Ԫ��
		   for (Iterator iterator = students.iterator(); iterator.hasNext();) {  
			   Student student = (Student) iterator.next();  
			   //���������ת�����ַ��������δ洢��list��
			   list.add(String.valueOf(student.getId())+" "+student.getName()+" "+student.getSpeciality()+" "+String.valueOf((student.getQQ())));  
		}  
		   //�½�һ��������daapter���ڸ�listview�ṩ����
		   ArrayAdapter<String> adapter=new ArrayAdapter<String>(PullXMLActivity.this, android.R.layout.simple_list_item_1, list);  
		  //Ϊlistview��������
		   listView.setAdapter(adapter);  
	    }  
	   
	      
	}  
	
	  //����xml�ļ�
	private List<Student> parserXMl()  
	{
		//��ʼ��һ��List<student>���������ڽ�����student��Ա
		List<Student> students=null;
		//��ʼ��һ��student���������ڴ洢ÿһ���ڵ����Ϣ
		Student stu=null;
		try{
			//����Դ�ļ�student.xml
			InputStream inputstream=PullXMLActivity.this.getResources().getAssets().open("student.xml");
			//����XmlParser�����ַ�ʽ
			//��ʽһ��ʹ�ù�����XmlPullParserFactory
			XmlPullParserFactory pullFactory=XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser=pullFactory.newPullParser();
			//��ʽ����ʹ��Android�ṩ��ʵ�ù�����android.util.Xml
			//XmlPullParser xmlPullParser=Xml.newPullParser();
			//���������ֽ���Ϊinputstream�������ñ��뷽ʽΪ��UTF-8��
			xmlPullParser.setInput(inputstream, "UTF-8");
			//ȡ���¼����ͣ����ڿ�ʼ����ʱ���ж�
			int eventType=xmlPullParser.getEventType();
			//ѭ�����������ļ�ֱ���������
			while(eventType != XmlPullParser.END_DOCUMENT)
			{
				/*��ӡlog��ʾ�¼�����
				*START_DOCUMENT:0
				*END_DOCUMENT:1
				*START_TAG:2
				*END_TAG:3
				*TEXT:4
				*/
				Log.e("guojs--->event",eventType+"");
				//���ڴ洢�ڵ�����
				String localName=null;
				switch(eventType)
				{
				case XmlPullParser.START_DOCUMENT:
					//�����ĵ���ͷ��ʵ����students����������ӡlog
					students=new ArrayList<Student>();
					Log.e("guojs","start document!");
					break;
				case XmlPullParser.START_TAG:
				{
					localName=xmlPullParser.getName();
					if ("student".equals(xmlPullParser.getName())) {  
			            stu=new Student();  //ʵ����һ��student��
			            //��ID��Ϣ���浽stu��
			            stu.setId(Long.parseLong(xmlPullParser.getAttributeValue(0)));  
			            Log.e("guojs",stu.getId()+"");
					}
					else if(stu != null)
					{
						//����һ���������ڴ洢�ڵ��ı�
						String currentData=null;
						if("name".equals(xmlPullParser.getName()))
						{
							/*ע������nextText()��ʹ�ã���ǰ�¼�ΪSTART_TAG��
							 * �������ȥ���ı����ͻ᷵�ص�ǰ���ı����ݣ������һ���¼���END_TAG
							 * �ͻ᷵�ؿ��ַ����������׳�һ���쳣��
							 */
							currentData=xmlPullParser.nextText();
							//�洢��name������Ϣ
							stu.setName(currentData);
						}
						else if("speciality".equals(xmlPullParser.getName()))
						{
							currentData=xmlPullParser.nextText();
							//�洢רҵ��Ϣ
							stu.setSpeciality(currentData);
						}else if("qq".equals(xmlPullParser.getName()))
						{
							currentData=xmlPullParser.nextText();
							//�洢QQ��Ϣ
							stu.setQQ(Long.parseLong(currentData));
						}
					}
				}
				break;
				case XmlPullParser.END_TAG:
				{
					localName=xmlPullParser.getName();
					Log.e("guojs--end tag",localName);
					if("student".equals(localName) && stu != null)
					{
						//��stu��ӽ�students�����б���
						students.add(stu);
						//����stuΪ��
						stu = null;
					}
				}
				break;
				default:
					break;
				}
				//������һ���¼�
				eventType=xmlPullParser.next();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return students;
	}  
}
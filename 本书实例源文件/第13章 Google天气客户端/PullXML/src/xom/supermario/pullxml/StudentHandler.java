package xom.supermario.pullxml;

import java.util.List;   
import org.xml.sax.Attributes;  
import org.xml.sax.SAXException;  
import org.xml.sax.helpers.DefaultHandler;  
import android.util.Log;
  
public class StudentHandler extends DefaultHandler {  
    private String preTAG;    //���ڴ洢xml�ڵ������
    private List<Student> ListStudent;  
    private Student stu;  
    //�޲���ʵ������
    public StudentHandler() {  
        super();  
    }
    //������ʵ������
    public StudentHandler(List<Student> listStudent) {  
        super();  
        ListStudent = listStudent;  
    }
    //��ʼ�����ĵ�
    public void startDocument() throws SAXException {  
        // TODO Auto-generated method stub   
    Log.i("------>", "�ĵ���ʼ");  
        super.startDocument();  
    }
    //��ʼ�����ĵ���Ԫ��
    public void startElement(String uri, String localName, String qName,  
            Attributes attributes) throws SAXException {  
        Log.i("localName-------->", localName);  
        preTAG=localName;  //����ǰԪ�ص����Ʊ��浽preTAG
        if ("student".equals(localName)) {  
            stu=new Student();  //ʵ����һ��student��
            //��ID��Ϣ���浽stu��
            stu.setId(Long.parseLong(attributes.getValue(0)));  
              
        for (int i = 0; i < attributes.getLength(); i++) {    
            Log.i("attributes-------->",String.valueOf(stu.getId()));  
        	}  
        }  
        //��仰�ǵ�Ҫִ��
        super.startElement(uri, localName, qName, attributes);  
    }  
  
    public void endDocument() throws SAXException {  
      
        Log.i("------>", "�ĵ�����");  
        super.endDocument();  
    }  
    public void endElement(String uri, String localName, String qName)  
            throws SAXException {  
        preTAG="";  
        if ("student".equals(localName)) {  
        ListStudent.add(stu);  
        Log.i("-------->", "һ��Ԫ�ؽ������");  
        }  
        super.endElement(uri, localName, qName);  
    }     
    //�����ڵ��ı�����
    public void characters(char[] ch, int start, int length)  
        throws SAXException {  
      
        String str; 
        //�ҳ�Ԫ���еġ�name���ڵ�
       if ("name".equals(preTAG)) {  
    	   str=new String(ch,start,length);  
            stu.setName(str);  
            Log.i("name=", stu.getName());  
        //�ҳ�Ԫ���еġ�speciality���ڵ�
        }else if ("speciality".equals(preTAG)) {  
        	str=new String(ch,start,length);  
            stu.setSpeciality(str);  
            Log.i("speciality=", stu.getSpeciality());
        //�ҳ�Ԫ���еġ�qq���ڵ�
        }else if ("qq".equals(preTAG)) {  
        	str=new String(ch,start,length);  
            stu.setQQ(Long.parseLong((str)));  
            Log.i("QQ=", String.valueOf(stu.getQQ()));  
        } 
        super.characters(ch, start, length);  
    }                
	public List<Student> getListStudent() {  
	    return ListStudent;  
	}

	public void setListStudent(List<Student> listStudent) {  
	    ListStudent = listStudent;  
	}   
}  
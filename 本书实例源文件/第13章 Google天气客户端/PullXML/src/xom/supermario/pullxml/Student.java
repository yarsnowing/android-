package xom.supermario.pullxml;

public class Student {  
	long Id;  //���ڴ��id��Ϣ
	String Name;  //���ڴ��Name��Ϣ
	String Speciality;  //���ڴ��רҵ��Ϣ
	long QQ;  	//���ڴ��QQ��Ϣ
	//���������캯�������ڳ�ʼ����
	public Student(long id, String name, String speciality, long qQ) {  
	    super();  
	    Id = id;  
	    Name = name;  
	    Speciality = speciality;  
	    QQ = qQ;  
	}  
	//�����������캯��
	public Student() {  
	    super();  
	}  
	//ȡ��id
	public long getId() {  
	    return Id;  
	} 
	//ȡ��Name
	public String getName() {  
	    return Name;  
	}  
	//ȡ��QQ
	public long getQQ() {  
	    return QQ;  
	}  
	//ȡ��רҵ��Ϣ
	public String getSpeciality() {  
	    return Speciality;  
	}  
	//����id
	public void setId(long id) {  
	    Id = id;  
	}  
	//��������
	public void setName(String name) {  
	    Name = name;  
	}  
	//����QQ
	public void setQQ(long qQ) {  
	    QQ = qQ;  
	}  
	//����רҵ
	public void setSpeciality(String speciality) {  
	    Speciality = speciality;  
	}  
	}  
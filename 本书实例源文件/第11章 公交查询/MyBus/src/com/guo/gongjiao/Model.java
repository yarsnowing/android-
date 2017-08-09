package com.guo.gongjiao;

//import java.util.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

//import java.lang.*;

public class Model implements Runnable {

	// ���hash��洢��Ϣ��ʽ����:
	// line name => line stations eg.
	// 1 => a-b-c-d-e-
	final  Hashtable<String, String> road = new Hashtable<String, String>();
	// ���hash��洢��Ϣ��ʽ���£�
	// line name => line time eg.
	// 1 =>{8:00 12:00} ��ʼվ {8:00 12:00} �յ�վ
	final  Hashtable<String, String> road_time = new Hashtable<String, String>();
	public final static int MAX_NUM = 50;

	private String file;
	private Traffic parent;

	public Model(Traffic parent, String file) {
		this.parent = parent;
		this.file = file;
		return;
	}

	@Override
	public void run() {
		initRoadHash();
		class CallBack implements Runnable {
			public void run() {
				parent.mDialog.dismiss();
			}
		}
		CallBack cb = new CallBack();
		parent.runOnUiThread(cb);
	}
	//������·���ƻ�õ�ǰ��·������������վ�㣬���ַ�����ʾ
	public String getSationsForOneRoad(String lineName) {
		return (String) (road.get((Object) lineName));

	}
	//ȡ����·��ʱ����Ϣ
	public String getTimeforOneRoad(String lineName) {
		String time = (String) (road_time.get((Object) lineName));
		//����Ҳ���������Ѱ�Ҵ���a��������
		if (time == null) {
			time = (String) (road_time.get(lineName + "a"));
		}
		return time;
	}
	//����·�����ƻ�ȡ���н���·��
	public Vector getSuggestRoads(String s) {
		//���ڴ��·�߼���
		Enumeration key_of_roads;
		String road;
		int i = 0;
		Vector temp = new Vector();	//���ڲ�ѯ��Ž��

		String mmm = s.trim();
		key_of_roads = this.road.keys();
		//���������е�Ԫ��
		while (key_of_roads.hasMoreElements()) {
			road = (String) key_of_roads.nextElement();
			// �����ԡ�b����β��·��
			if (road.endsWith("b") == true)
				continue;
			//����ͷ��ָ���ַ�����������·�洢��Ԫ��temp��
			if (road.startsWith(mmm) == true) {
				if (road.endsWith("a") == true) {
					//ȥ����·����ġ�a����ֻ�洢��·����
					temp.addElement((Object) road.substring(0,
							road.length() - 1));
				} else {
					temp.addElement((Object) road.toString());
				}
				i++;
			}
		}

		return temp;
	}

	/**
	 * @param sa
	 *            :the name of road
	 * @raturn a vector array vector[0]is the sequnce of sation for start to
	 *         end,vector[1] is a stack ,whitch is the sequnce of station from
	 *         start to end, you can you popup() to get sequnce from end to
	 *         start
	 */
	//������·���ƻ��������·
	public String[] get_stations_for_one_road(String s) {
		//��ʼ���ַ������飬���ڴ��ȥ�̺ͻس�
		String[] vv = new String[2];
		String stations = null;

		s = s.trim();
		if (s.equals(""))
			return null;

		stations = this.getSationsForOneRoad(s);

		if (stations != null) {
			// ˵����·������'a' ���� 'b'��β
			int index;
			String x;
			//�½�һ��ջ���ڴ����·����������վ��
			Stack down_road = new Stack();

			vv[0] = stations.toString();
			//������վ������ӵ�ջ��
			while ((index = stations.indexOf("-")) > 0) {
				x = stations.substring(0, index);
				down_road.addElement(x);
				stations = stations.substring(index + 1);
			}
			
			String st = "";
			//��ջ��Ԫ��pop���Եõ�һ�����෴˳����ʾվ����Ϣ���ַ���
			while (!down_road.empty()) {
				if (st.trim().equals("")) {
					st = (String) down_road.pop();
				} else {
					st = st + "-" + (String) down_road.pop();
				}
			}
			vv[1] = st;
			return vv;

		} else {
			// ���е�����˵����·ȥ�̺ͻس̵�·�߲�һ��
			// һ���ԡ�a����β��һ���ԡ�b����β
			vv[0] = getSationsForOneRoad(s + "a");
			if (vv[0] == null)
				return null;
			vv[1] = getSationsForOneRoad(s + "b");
			if (vv[1] == null)
				return null;
			return vv;
		}

	}

	/*
	 * just like get_stations_for_one_road function but just return all the
	 * stations this road have
	 */
	//�������������У���ȡ��ǰ·�ߵľ���������վ��
	public Vector get_stations_for_one_road_not_care_direction(String s) {
		String stations = null;

		s = s.trim();
		if (s.equals(""))
			return null;
		//ȡ�õ�ǰ·�ߵ�����վ��
		stations = this.getSationsForOneRoad(s);
		//���û�ҵ�������Ѱ���Ƿ��и�·�����з����·��
		if (stations == null) {
			// go here mean that hashtable has two entry for this road
			// one is end with a,another is end with b
			stations = getSationsForOneRoad(s + "a");

		}
		int index;
		String x;
		//��·�����δ洢��down_load
		Vector down_road = new Vector();
		//ͨ����-���ж�ÿ��վ�������
		while ((index = stations.indexOf("-")) > 0) {
			x = stations.substring(0, index);
			down_road.addElement(x);
			stations = stations.substring(index + 1);
		}
		//���ظ�·�߾���������վ�����Ϣ
		return down_road;

	}

	/**
	 * check two vector whitch contain string object, check if a string is both
	 * in the first vector and the second vector
	 * 
	 * @param v1
	 *            the vector containning strings
	 * @param v2
	 *            the vector containning strings
	 */
	//���2���������Ƿ������ͬԪ�أ������򷵻���ͬ��Ԫ�أ����򷵻ؿ�
	private String is_there_has_a_same_string(Vector v1, Vector v2) {

		Enumeration seq1 = v1.elements();
		Enumeration seq2;
		String s1, s2;
		//����seq1��Ԫ��
		while (seq1.hasMoreElements()) {
			s1 = (String) seq1.nextElement();
			//����seq2��Ҫÿ�ζ����³�ʼ��������ǰ��ָ��Ԫ������
			seq2 = v2.elements();
			//����seq2
			while (seq2.hasMoreElements()) {
				s2 = (String) seq2.nextElement();
				//���ҵ���ͬ��Ԫ�أ��򷵻���ͬ��Ԫ��
				if (s1.compareTo(s2) == 0) {
					return s1;
				}
			}
		}
		return null;
	}
	//�������ֱ�﷽��
	private Vector get_all_the_same_string(Vector v1,Vector v2)
	{
		//���ڴ��ֱ���·��
		Vector temp=new Vector();
		Enumeration seq1 = v1.elements();
		Enumeration seq2;
		String s1, s2;
		//����seq1��Ԫ��
		while (seq1.hasMoreElements()) {
			s1 = (String) seq1.nextElement();
			//����seq2��Ҫÿ�ζ����³�ʼ��������ǰ��ָ��Ԫ������
			seq2 = v2.elements();
			//����seq2
			while (seq2.hasMoreElements()) {
				s2 = (String) seq2.nextElement();
				//���ҵ���ͬ��Ԫ�أ�����Ӹ�Ԫ��
				if (s1.compareTo(s2) == 0) {
					temp.add((Object)s1);
				}
			}
		}
		return temp;
	}
	/**
	 * according the abbriave string ,get a full name of one station
	 * 
	 * @param v1
	 *            the vector containning stations on one road
	 * @param s
	 *            the station abrrave
	 * @return the full name of this station
	 */
	//�����û���������ƻ��վ���ʵ������
	private String get_station_full_name(Vector v1, String s) {
		//�����·��վ�㼯��
		Enumeration seq1 = v1.elements();

		String s1, s2;
		s = s.trim();
		//����Ԫ�أ��ж��Ƿ���Ԫ�ذ���ָ���ַ���
		while (seq1.hasMoreElements()) {
			s1 = (String) seq1.nextElement();
			//���ذ���ָ���ַ�����Ԫ��
			if (s1.indexOf(s) != -1) {
				return s1;
			}

		}
		//���򷵻ؿ�
		return "";

	}

	/**
	 * @param sa
	 *            :the name of start station
	 * @param sb
	 *            :the name of end station
	 * @raturn a String array Vector[0]is the first road you should take
	 *         Vector[1] is the second road you should take Vector[2] is the
	 *         middle station you should take off from the first road and take
	 *         on the second road,if it's "same" means just need one road
	 */
	//�������վ���յ�վ��ó˳�·��
	public Vector[] get_road_for_inter_station(String sa, String sb) {
		//���ڴ������Ҫ������·��
		Vector road1 = new Vector();
		//���ڴ�����Ҫ������·��
		Vector road2 = new Vector();
		//���ڴ���м�Ҫ������·��
		Vector road3 = new Vector();
		Vector[] vv = new Vector[5];
		Vector sta1 = new Vector();
		Vector sta2 = new Vector();
		String x = null;
		Enumeration  direct;
		Vector direct_vector=new Vector();
		int index;
		int j = 0;
		//�����ʼվ���յ�վ��ͬ��ֱ�ӷ��ؿ�
		if (sa.trim().equals("") || sa.trim().equals("")) {
			return null;
		}
		vv[0] = road1;
		vv[1] = road2;
		vv[2] = road3;
		vv[3] = sta1;
		vv[4] = sta2;
		// ȡ�ð�����ʼվ������·����Ϣ
		Vector tmpa = this.get_road_from_station_key(sa);

		// ȡ�ð����յ�վ������·����Ϣ
		Vector tmpb = this.get_road_from_station_key(sb);
		//ȡ������ֱ��·�߱��浽direct_vector��
		direct_vector = this.get_all_the_same_string(tmpa, tmpb);
		if(direct_vector.size() > 0)
		{
			//������direct_vector�е�Ԫ��ȡ���ŵ�direct��
			direct=direct_vector.elements();
			//������ѯdirect�е�Ԫ��
			while(direct.hasMoreElements())
			{
				String a=(String)direct.nextElement();
				road1.addElement((Object) a.toString());
				//�����������������road3�����Ԫ�ء�same��������ֱ��·��
				road3.addElement((Object) "same");
			}
			return vv;
		}
		//���������Ҫת�ˣ�seq1�洢���а���������ʼվ�Ĺ���·��
		Enumeration seq1 = tmpa.elements();
		//seq2�洢���а��������յ�վ�Ĺ���·��
		Enumeration seq2;
		// s1 the first road ,s2:the second road
		String s1, s2;
		Vector stations1, stations2;
		while (seq1.hasMoreElements()) {
			//���λ�ȡseq1��ÿ������·��
			s1 = (String) seq1.nextElement();
			seq2 = tmpb.elements();
			//����Ƿ���s1վ�㵽�յ�վ��ֱ�﷽����Ҳ����ת�˷���
			while (seq2.hasMoreElements()) {
				s2 = (String) seq2.nextElement();
				stations1 = get_stations_for_one_road_not_care_direction(s1);
				stations2 = get_stations_for_one_road_not_care_direction(s2);
				//�ж���2�������Ƿ��н���������˵����ת�˷���
				x = is_there_has_a_same_string(stations1, stations2);
				if (x != null) {
					j++;
					//��෵��5��ת�˷�����ѡ��
					if (j > 3) { 
						return vv;
					}
					//����ʱ�ĳ˳������洢����
					road1.addElement((Object) s1.toString());//road1�洢��ʼ��·
					road2.addElement((Object) s2.toString());//road2�洢ת����·
					road3.addElement((Object) x);//road3�洢��תվ����
					sta1.addElement((Object) get_station_full_name(stations1,
							sa.trim()));//sta1�洢��ʼվ���ȫ���������еĻ�
					sta2.addElement((Object) get_station_full_name(stations2,
							sb.trim()));//sta2�洢�յ�վ��ȫ��������еĻ�
				}
			}

		}
		//���raod3��Ԫ�ش���һ���������е��﷽��������ת�˺�ֱ���
		if (road3.size() > 0) {
			return vv;
		} else {
			return null;
		}
	}

	private static final int MAX_STATION = 20;
	//����վ���þ�����վ�������·��
	public Vector get_road_from_station_key(String s) {
		//���ڴ��·������
		Enumeration key_of_roads;
		String roads;
		int i = 0;
		//���ڴ�Ű���ָ��վ���·������
		Vector temp = new Vector();

		String mmm = s.trim();
		//ȡ����·���Ƶļ���
		key_of_roads = this.road.keys();
		//��������
		while (key_of_roads.hasMoreElements()) {
			roads = (String) key_of_roads.nextElement();
			//�����������·������
			if (roads.endsWith("b") == true)
				continue;
			//�������ִ����·��洢��temp��
			if (getSationsForOneRoad(roads).indexOf(mmm) != -1) {
				if (roads.endsWith("a") == true) {
					//ֻ�洢·�����ƣ�����������������Ϣ
					temp.addElement((Object) roads.substring(0,
							roads.length() - 1));
				} else {
					temp.addElement((Object) roads.toString());
				}
				i++;
			}
		}
		return temp;
	}

	//��ʼ���ض����еĹ�����·��Ϣ
	private void initRoadHash() {
		try {
			String b = null;
			String name = null; //���ڴ�Ź�����·�����硰121·��
			String line = null;	//���ڴ�Ź���·��
			String time = "";	//���ڴ��ʱ���������Ϣ
			String encode = CharacterEnding.getFileEncode(new FileInputStream(file));//����ļ��ı��뷽ʽ
			Log.v(Globals.TAG, "encode is:" + encode);
			if(encode.equals("GB18030"))
				encode = "GBK";
			Log.v(Globals.TAG, "1");
			//��InputStreamReader��װ��Bufferedreader
			BufferedReader buf = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), encode));
			int i=0;
			Log.v(Globals.TAG, "2");
			//ÿ�ζ�ȡһ��
			while ((b = buf.readLine()) != null) {
				// System.out.println(b);
				Log.v(Globals.TAG, ""+i);
				b.trim();
				//���õ�������Ϊ�գ�������ȡ��һ��
				if (b.length() == 0) {
					continue;
				}
				//����ַ����ԡ�.����ͷ����������һ��ע�ͣ�������ȡ��һ��
				if (b.startsWith(".")) {
					continue;
				}
				//���������ո��������ȡ��һ��
				final int spaceIndex = b.indexOf(' ');
				if (spaceIndex == -1)
					continue;
					
				// ��·��Ϊ��ʼ����һ���ո�֮����ַ���
				name = b.substring(0, spaceIndex);
				if (name.trim().length() == 0)
					continue;
				//�� ���� a���� ���� b�����ڱ�ʾ������·��������
				name = name.replace(Character.toChars(8593)[0], 'a');
				name = name.replace(Character.toChars(8595)[0], 'b');

				// ���û�С��������ߡ�������λ�ò��ԣ��������ȡ��һ��
				final int colonIndex = b.indexOf(':');
//				Log.v(Globals.TAG, "line" + colonIndex);
				if (colonIndex == -1 || spaceIndex > colonIndex)
					continue;
				//��·Ϊ��һ���ո񵽡�����֮����ַ���
				line = b.substring(spaceIndex + 1, colonIndex);
				if (line.trim().length() == 0)
					continue;
				line = line + "-";// ���һ����-�����������������Ĺ���ʵ��


				time = b.substring(colonIndex + 1, b.length());
				//��·��վ����Ϣ�ŵ�road
				road.put(name, line);
				//����·ʱ����Ϣ�ŵ�road_time��
				road_time.put(name, time);
				i++;
				//����������0xFFF��4096�����˳�
				if(i > 0xfff)
					break;
			}
			//�ر��ļ�
			buf.close();
			Log.v(Globals.TAG, "total count:"+i);
			// road.put("aa","fff-gg-aa-");//·��վ����Ϣ������������ʽ
		} catch (Exception e) {
			Log.v(Globals.TAG, "exception"+e.getMessage());
			e.printStackTrace();
		}
	}

}

package com.guo.gongjiao;

import java.util.Enumeration;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;



public class Traffic extends TabActivity {
	/** Called when the activity is first created. */
	TabHost.TabSpec tab4;
	String mFile;
	ProgressDialog mDialog;
/*	static {
		AdManager.init("4ef94353630d245b", "1fc8b2d8877c7eee", 30, false, "1.0");
	}*/

	private static final int DIALOG_YES_NO_MESSAGE = 1;//��ʾ������ʾ
	private static final int DIALOG_LIST = 2;//��ʾ����·�����
	private static final int DIALOG_LIST1 = 3;//��ʾת�˷����б�
	private static final int DIALOG_LIST_FOR_ROAD = 4;//��ʾ����վ���·���б�
	private static final int DIALOG_LIST_BUS = 5;//��ʾֱ�﷽���б�

	private TabHost tabHost;
	private TableLayout table;
	private ImageButton interSearchButton;
	private ImageButton lineSearchButton;
	private ImageButton stationSearchButton;
	private EditText start, end;
	private EditText line, station;
	Model m;
	String[] road_list;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//ȡ�ô���һ�����ݹ����Ĳ���
		mFile = this.getIntent().getStringExtra(Globals.FILENAME);//��������ļ���
		setTitle(getIntent().getStringExtra(Globals.Title));//���õ�ǰ�ı���Ϊ��������
		
		tabHost = getTabHost();//ȡ�õ�ǰ��tabHost�����ڹ����ǩ

		LayoutInflater.from(this).inflate(R.layout.tab,
				tabHost.getTabContentView(), true);//�������ļ�tab��������չ����ǰ������
		//��ӡ����ˡ���ǩ
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(
				this.getString(R.string.interchage)).setContent(R.id.tab1));
		//��ӡ���·����ǩ
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(
				this.getString(R.string.line)).setContent(R.id.tab2));
		//��ӡ�վ�㡱��ǩ
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator(
				this.getString(R.string.station)).setContent(R.id.tab3));
		tab4 = tabHost.newTabSpec("tab4").setIndicator(
				this.getString(R.string.result)).setContent(R.id.tab4);
		//��ӽ����ǩ
		tabHost.addTab(tab4);
		//ȡ�á����ˡ���ǩ�İ�ť�����󶨰���������
		interSearchButton = (ImageButton) findViewById(R.id.tab1_b1);
		interSearchButton.setOnClickListener(mGoListener);
		//ȡ�á���·����ǩ�İ�ť�����󶨰���������
		lineSearchButton = (ImageButton) findViewById(R.id.tab2_b1);
		lineSearchButton.setOnClickListener(mGoListener);
		//ȡ�á�վ�㡱��ǩ�İ�ť�����󶨰���������
		stationSearchButton = (ImageButton) findViewById(R.id.tab3_b1);
		stationSearchButton.setOnClickListener(mGoListener);
		//��ʼ��������ǩ�Ľ���Ԫ��
		start = (EditText) findViewById(R.id.tab1_et1);
		end = (EditText) findViewById(R.id.tab1_et2);
		line = (EditText) findViewById(R.id.tab2_et1);
		station = (EditText) findViewById(R.id.tab3_et1);
		//��ʾ�Ի�����ʾ������������
		mDialog = CreateDialog();
		mDialog.show();
		//�������ļ����д���
		m = new Model(this, mFile);
		Thread t = new Thread(m);
		t.start();
	}

	protected ProgressDialog CreateDialog() {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(this.getResources().getString(
						R.string.pregress_diag));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		return dialog;
	}
    

	//���û����������ʱ��ִ����Ӧ����
	private OnClickListener mGoListener = new OnClickListener() {
		public void onClick(View v) {
			String s1, s2;
			if (v == (View) interSearchButton) {

				s1 = start.getText().toString();//���ڴ洢�û�������ʼվ
				s2 = end.getText().toString();//���ڴ洢�û������յ�վ
				if(s1.trim().length() == 0 ||
						s2.trim().length() == 0)
					return;
				//��֤������Ϣ����Ч��
				if (!checkTextValid(s1) || !checkTextValid(s2)
						|| (s1.indexOf(s2) != -1) || (s2.indexOf(s1) != -1)) {
					//����Ч����ʾ��ʾ��
					showDialog1(DIALOG_YES_NO_MESSAGE);
					return;
				}
				//������Ϸ�����в�ѯҵ��
				processinterSearch(s1, s2);

			} else if (v == (View) lineSearchButton) {

				s1 = line.getText().toString();//���ڴ洢����������·
				if(s1.trim().length() == 0)
					return;
				//��֤�������ݵ���Ч��
				if (!checkTextValid(s1)) {
					//���������Ч����ʾ��ʾ��
					showDialog1(DIALOG_YES_NO_MESSAGE);
					return;
				}
				//������Ϸ����в�ѯҵ��
				processRoadSearch(s1);
			} else if (v == (View) stationSearchButton) {
				//�洢�û�����վ����Ϣ
				s1 = station.getText().toString();
				if(s1.trim().length() == 0)
					return;
				//��֤�û�������Ϣ����Ч��
				if (!checkTextValid(s1)) {
					//�����Ч����ʾ��ʾ��
					showDialog1(DIALOG_YES_NO_MESSAGE);
					return;
				}
				//������Ϸ�����в�ѯҵ��
				processStationSearch(s1);
			} else {
				Log.e(Globals.TAG, "error");
			}
		}
	};

	void showDialog1(int id) {
		CreateDialog(id).show();
	}
	//�Ի���
	protected Dialog CreateDialog(int id) {
		switch (id) {
		case DIALOG_YES_NO_MESSAGE:
			return new AlertDialog.Builder(this).setIcon(
					//���ñ���:�Բ������ݣ�û���ҵ������Ϣ
					R.drawable.alert_dialog_icon).setTitle(R.string.sorry)  
					.setMessage(R.string.find_nothing).setPositiveButton(
							R.string.conform,
							new DialogInterface.OnClickListener() {
								//���ð�ť�����κβ�����ֱ�ӹص��Ի���
								public void onClick(DialogInterface dialog,  
										int whichButton) {
								}
							}).create();
		case DIALOG_LIST:
			return new AlertDialog.Builder(Traffic.this).setTitle(//���ñ���
					Html.fromHtml(list_title)).setItems(road_list,//�����б���ʽ��ʾ·������
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//��ʾ�û�ѡ����·�ߵ���ϸ��Ϣ
							draw_road_table(road_list[which], false);
							//�л���ǰ�����ǵı�ǩΪ�����ǩ
							tabHost.setCurrentTabByTag("tab4");
						}
					}).create();
		case DIALOG_LIST1:
			return new AlertDialog.Builder(Traffic.this).setTitle(
					R.string.suggst_road).setItems(road_list, 	//���ñ��⣺������·
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//����Ϣ��ʾ�������ǩ
							draw_road_table1(which);
							//�л���ǰ��ʾ�ı�ǩΪ�����ǩ
							tabHost.setCurrentTabByTag("tab4");
							dialog.cancel();
						}
					}).create();
		case DIALOG_LIST_FOR_ROAD:
			return new AlertDialog.Builder(Traffic.this).setTitle(
					Html.fromHtml(list_title)).setItems(station_list,//���ñ���
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//��ʾ��ѯ��������������ǩ
							draw_road_table(station_list[which], false);
							//�л������������ǩ
							tabHost.setCurrentTabByTag("tab4");
						}
					}).create();
		case DIALOG_LIST_BUS:
			return new AlertDialog.Builder(Traffic.this).setTitle(
					R.string.suggst_road).setItems(road_list,//���ñ��⣺������·
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//����Ϣ��ʾ�������ǩ
							draw_road_table(road_list[which], false);
							//�л���ǰ��ʾ�ı�ǩΪ�����ǩ
							tabHost.setCurrentTabByTag("tab4");
						}
					}).create();
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// call the parent to attach any system level menus
		super.onCreateOptionsMenu(menu);
		int base = Menu.FIRST; // value is 1
		// menu.add(base, base, base, this.getString(R.string.exit));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == Menu.FIRST) {
			this.finish();
		}
		// should return true if the menu item
		// is handled
		return true;
	}

	String list_title;
	//��ʾ·����ϸ��Ϣ
	private void draw_road_table(String road, boolean append) {
		TextView line, upload, download;

		table = (TableLayout) findViewById(R.id.menu);
		if (table == null) {
			Log.e(Globals.TAG, "tab is null");
		}
		//���õ�һ��Ϊ������
		table.setColumnShrinkable(1, true);

		((TextView) findViewById(R.id.tab4_h2))
				.setText(getString(R.string.line));//��·
		((TextView) findViewById(R.id.tab4_h3))
				.setText(getString(R.string.upload));//ȥ��
		((TextView) findViewById(R.id.tab4_h4))
				.setText(getString(R.string.download));//�س�

		line = (TextView) findViewById(R.id.tab4_et2);
		upload = (TextView) findViewById(R.id.tab4_et3);
		download = (TextView) findViewById(R.id.tab4_et4);

		line.setText(road);//��ʾ��·����
		final String[] ss = m.get_stations_for_one_road(road);//ȡ����·������·��
		if (ss != null) {
			upload.setText(ss[0]);//��ʾȥ��
			download.setText(ss[1]);//��ʾ�س�
		}
		//ȡ����·ʱ����Ϣ
		final String time = m.getTimeforOneRoad(road);
		if (time != null) {
			((TextView) findViewById(R.id.tab4_h5))
					.setText(getString(R.string.time));//ʱ��
			((TextView) findViewById(R.id.tab4_et5)).setText(time);//��ʾʱ����Ϣ
		}
	}

	private boolean checkTextValid(String s) {
		if (s.trim().length() == 0)
			return false;
		if (s.trim().equals("") || !validateString(s)) {
			return false;
		}
		return true;
	}

	private void draw_station_road_table(String road) {
		String st = "";
		Enumeration seq;

		Vector vv = m.get_stations_for_one_road_not_care_direction(road);
		if (vv == null)
			return;
		seq = vv.elements();
		// get station for forward
		while (seq.hasMoreElements()) {
			if (st.trim().equals("")) {
				st = (String) seq.nextElement();
			} else {
				st = st + "-" + (String) seq.nextElement();
			}
		}
		// #style roaditem
		/*
		 * StringItem rr = new StringItem(road, st); this.tabbedForm.append(3,
		 * rr);
		 */
	}
	//����·���б���ʽ��ʾ
	private void draw_filter_list(Vector v) {
		Enumeration seq = v.elements();
		int j = 0;
		road_list = new String[v.size()];
		//������ѯ���ϵ�Ԫ��
		while (seq.hasMoreElements()) {
			road_list[j++] = (String) seq.nextElement();
		}
		//��ʾ�Ի���
		showDialog1(DIALOG_LIST);
	}

	/*
	 * show a roads dialog which pass the station
	 */
	//�û����վ���б�
	String[] station_list;
	//��վ���б���ʾ���Ի�����
	private void draw_filter_list3(Vector v) {
		//�½�һ���������ڴ��վ��
		Enumeration seq = v.elements();
		int j = 0;
		// ChoiceItem ci;
		station_list = new String[v.size()];
		//�������ϣ�������վ���ŵ�station_list��
		while (seq.hasMoreElements()) {
			station_list[j++] = (String) seq.nextElement();
		}
		//��ʾ���ѡ��Ի���
		showDialog1(DIALOG_LIST_FOR_ROAD);
	}
	//��ѯ��·
	private void processRoadSearch(String s) {
		//���ڴ�Ž���·��
		Vector v;
		//ȡ�ý���·��
		v = m.getSuggestRoads(s);
		//�����ѯ�������һ��������ʾһ���б��û�ѡ��
		if (v.size() > 1) {
			this.list_title = this.getString(R.string.select_one_road);
			draw_filter_list(v);
		} else if (v.size() == 1) {//����պ���һ������ֱ�ӽ���ѯ�͹���ʾ�������ǩ��
			draw_road_table((String) v.elementAt(0), false);
			this.tabHost.setCurrentTabByTag("tab4");
		} else {
			// ���û�н������ʾ�û�û�ҵ������Ϣ
			showDialog1(DIALOG_YES_NO_MESSAGE);
		}
	}

	/**
	 * @param s
	 *            the station name
	 */
	//����վ�������ȡ�þ�����վ���������·
	private void processStationSearch(String s) {
		//���ڴ����·����
		Vector mm;
		//ȥ����β�Ŀո�
		s = s.trim();
		//��þ�����վ�������·��
		mm = m.get_road_from_station_key(s);
		//��û�ҵ�������ʾû�ҵ�
		if ((mm.size() == 0)) {
			this.showDialog1(DIALOG_YES_NO_MESSAGE);
			return;
		}
		//����·��Ϣ�����ַ���������station_from_line��
		String resultsTextFormat = getString(R.string.station_from_line);
		this.list_title = String.format(resultsTextFormat, s);
		//��ʾ��ѯ���
		draw_filter_list3(mm);

		return;
	}

	private boolean validateString(String s) {
		if (s.trim().equals(""))
			return false;
		if (s.indexOf('?') != -1) {
			return false;
		}
		return true;
	}

	/**
	 * @param sa
	 *            the first station name
	 * @param sb
	 *            the second station name
	 */
	Vector[] vv;
	//������ʼվ���յ�վ��ó˳�·��
	private void processinterSearch(String sa, String sb) {
		//ȡ�ó˳�������������Ϣ
		vv = m.get_road_for_inter_station(sa, sb);
		//���û�г˳��������򷵻ؿգ�����ʾ�û�û���ҵ�
		if (vv == null) {
			this.showDialog1(DIALOG_YES_NO_MESSAGE);
			return;
		}
		//ת��վ����Ϣ�������ֱ��ģ���洢���ǡ�same��
		Enumeration seq = vv[2].elements();
		//��ʼվ����Ϣ
		Enumeration seq1 = vv[0].elements(); 
		//ת��·����Ϣ
		Enumeration seq2 = vv[1].elements(); 
		String road;
		String road2;
		String middleStation;
		//��ʼ��road_list
		road_list = new String[vv[0].size()];
		//���ڼ�¼Ԫ�صĸ���
		int j = 0;
		int direct=0;
		// ȡ��·�ߵ���Ϣ
		while (seq.hasMoreElements()) {
			middleStation = (String) seq.nextElement();
			if (middleStation.compareTo("same") == 0) {
				// ����־λ��Ϊ1��������ֱ��
				direct=1;
				road = (String) seq1.nextElement();
				Log.e("guojs",road);
				road_list[j++]=road;
			}else{
				// ����־λ��Ϊ0��������ת��
				direct=0;
				road = (String) seq1.nextElement();
				road2 = (String) seq2.nextElement();
				road_list[j++] = road + " -> " + road2;
			}
		}
		//direct =0 ���� ��ֱ�﷽����=1��ʾ��ֱ�﷽��
		if(direct == 0)
			this.showDialog1(DIALOG_LIST1);//��ʾת�˷����б�
		else
			this.showDialog1(DIALOG_LIST_BUS);//��ʾֱ�﷽���б�
	}
	//��ʾת�˷�����·��
	private void draw_road_table1(int which) {

		TextView line, upload, download;
		
		table = (TableLayout) findViewById(R.id.menu);
		if (table == null) {
			Log.e(Globals.TAG, "tab is null");
		}
		//���õ�һ��Ϊ������
		table.setColumnShrinkable(1, true);
		((TextView) findViewById(R.id.tab4_h2))
				.setText(getString(R.string.start_end));//��ĩվ
		((TextView) findViewById(R.id.tab4_h3))
				.setText(getString(R.string.chufa1));//�ȳ���
		((TextView) findViewById(R.id.tab4_h4))
				.setText(getString(R.string.daoda1));//�ڳ���
		((TextView) findViewById(R.id.tab4_h5)).setText("");//���ò���Ҫ��ʾ����Ϊ��
		((TextView) findViewById(R.id.tab4_et5)).setText("");//���ò���Ҫ��ʾ����Ϊ��
		line = (TextView) findViewById(R.id.tab4_et2);
		upload = (TextView) findViewById(R.id.tab4_et3);
		download = (TextView) findViewById(R.id.tab4_et4);

		String line0 = (String) vv[0].elementAt(which);	//ȡ�õ�ǰ����ʼ��·
		String line1 = (String) vv[1].elementAt(which);	//ȡ�õ�ǰ��ת����·
		String start = (String) vv[3].elementAt(which);//ȡ�õ�ǰ��ת��վ������
		String middle = (String) vv[2].elementAt(which);//ȡ��ʵ����ʼվ����
		String end = (String) vv[4].elementAt(which);//ȡ��ʵ���յ�վ����

		line.setText(start + " -> " + end);
		CharSequence styledResults = Html.fromHtml(line0 + "\n\n"
				+ getString(R.string.chufa) + ":  " + start + "\n"
				+ getString(R.string.daoda) + ":  " + middle);
		upload.setText(styledResults);//��html��ʽ��ʾ��ʼվ��ת��վ·��
		styledResults = Html.fromHtml(line1 + "\n\n"
				+ getString(R.string.chufa) + ":  " + middle + "\n"
				+ getString(R.string.daoda) + ":  " + end);
		download.setText(styledResults);//��html��ʽ��ʾת��վ���յ�վ·��

	}
}
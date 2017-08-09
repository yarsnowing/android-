package com.guo.apkmanager;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MainViewActivity extends Activity implements Runnable {

	private GridView gridView = null;
	// ����ȡ��ϵͳ�����а�����Ϣ
	private List<PackageInfo> packageInfos = null; 
	private ImageButton changeCategoryBtn = null;
	// �û��Լ���װ�ĳ������Ϣ
	private List<PackageInfo> userPackageInfos = null;
	//����ʵ��ϵͳӦ�����Լ�Ӧ�õ��л�
	private boolean isUserApp = true; 
	private ListView listView = null;
	private ImageButton changeViewBtn = null;
	//����ListView��GridView���л�
	private boolean isListView = true; 
	// ��ǰ��ʾ�İ�װ����
	private List<PackageInfo> showPackageInfos = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��title������ʾ���ȵĹ���
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// ȫ����ʾ
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		//���г������û������л���ť
		changeCategoryBtn = (ImageButton) findViewById(R.id.ib_change_category);
		changeCategoryBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isUserApp){
					//�ı䰴ť����ͼƬ
					changeCategoryBtn.setImageResource(R.drawable.user);
					//�û��Լ���Ӧ�ó���
					showPackageInfos =userPackageInfos;
					//���ñ�־λ
					isUserApp = false;
					Toast.makeText(MainViewActivity.this, "�û��Լ��ĳ���", 2000).show();
				}else{
					changeCategoryBtn.setImageResource(R.drawable.all);
					//����Ӧ�ó���
					showPackageInfos = packageInfos;
					//���ñ�־λ
					isUserApp = true;
					Toast.makeText(MainViewActivity.this, "���еĳ���", 2000).show();
				}
				gridView.setAdapter(new GridViewAdapter(showPackageInfos, MainViewActivity.this));
				listView.setAdapter(new ListViewAdapter(showPackageInfos, MainViewActivity.this));
			}
		});
		listView = (ListView) findViewById(R.id.lv_apps);
		changeViewBtn = (ImageButton) findViewById(R.id.ib_change_view);
		//�б���ͼ��������ͼ�л�
		changeViewBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isListView == true){
					listView.setAdapter(new ListViewAdapter(showPackageInfos, MainViewActivity.this));
					//��ʾ�б���ͼ
					listView.setVisibility(View.VISIBLE);
					gridView.setVisibility(View.GONE);
					//���ñ�־λ
					isListView = false;
					Toast.makeText(MainViewActivity.this, "��ǰ���б���ͼ", 2000).show();
					changeViewBtn.setImageResource(R.drawable.list);
				}else {
					//��ʾ������ͼ
					listView.setVisibility(View.GONE);
					gridView.setVisibility(View.VISIBLE);
					//���ñ�־λ
					isListView = true;
					Toast.makeText(MainViewActivity.this, "��ǰ��������ͼ", 2000).show();
					changeViewBtn.setImageResource(R.drawable.grids);
				}
			}
		});
		gridView = (GridView) findViewById(R.id.gridView);
		gridView.setOnItemClickListener(listener);
		listView.setOnItemClickListener(listener);
		
		Thread thread = new Thread(this);
		thread.start();
		//���ñ���������ɼ�
		setProgressBarIndeterminateVisibility(true);
	}
	OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//ͨ��positionȡ����Ӧapk��packageInfo
			final PackageInfo packageInfo = showPackageInfos.get(position);
			//����һ��Dialog��������ѡ��
			AlertDialog.Builder builder = new AlertDialog.Builder(MainViewActivity.this);
			builder.setTitle("ѡ��");
			//����һ����Դ��ID
			builder.setItems(R.array.choice,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						String packageName = packageInfo.packageName;
						ActivityInfo activityInfo = packageInfo.activities[0];
						//activities����ֻ����������PackageManager.GET_ACTIVITIES��Żᱻ���
						//���ڻ�ȡpackageInfoʱҪ�ں������һ�����������
						if(activityInfo == null) {
							Toast.makeText(MainViewActivity.this, "û���κ�activity", Toast.LENGTH_SHORT).show();
							return;
						}
						String activityName = activityInfo.name;
						Intent intent = new Intent();
						//ͨ������������������Ӧ�ó���
						intent.setComponent(new ComponentName(packageName,activityName));
						//����apk
						startActivity(intent);
						break;
					case 1:
						//��ʾapk��ϸ��Ϣ
						showAppDetail(packageInfo);
						break;
					case 2:
						Uri packageUri = Uri.parse("package:" + packageInfo.packageName);
						Intent deleteIntent = new Intent();
						deleteIntent.setAction(Intent.ACTION_DELETE);
						deleteIntent.setData(packageUri);
						//������仰��Ϊ�ˣ����ɾ����Ӧ�ú󣬳���ͼ����Ȼ���ڵ�Bug���������onActivityResult����
						startActivityForResult(deleteIntent, 0);
						break;
					}
				}
			});
			//�˴���Ϊnull����ΪĬ�Ͼ�ʵ���˹رչ���
			builder.setNegativeButton("ȡ��", null);
			builder.create().show();
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//�������apk
		packageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
		userPackageInfos = new ArrayList<PackageInfo>();
		for(int i=0;i<packageInfos.size();i++) {
			
			PackageInfo temp = packageInfos.get(i);
			ApplicationInfo appInfo = temp.applicationInfo;
			boolean flag = false;
			if((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				flag = true;
				//FLAG_SYSTEM������ϵͳapk
			} else if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// �û�apk
				flag = true;
			}
			if(flag) {
				//��ӵ�ϵͳapk������
				userPackageInfos.add(temp);
			}
		}	
		if(isUserApp) {
			showPackageInfos = packageInfos;
		} else {
			showPackageInfos = userPackageInfos;
		}
		gridView.setAdapter(new GridViewAdapter(showPackageInfos,MainViewActivity.this));
		listView.setAdapter(new ListViewAdapter(showPackageInfos,MainViewActivity.this));		
	}
	//��ʾapk����ϸ��Ϣ
	private void showAppDetail(PackageInfo packageInfo) {	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��ϸ��Ϣ");
		StringBuffer message = new StringBuffer();
		message.append("��������:" + packageInfo.applicationInfo.loadLabel(getPackageManager()));
		message.append("\n ����:" + packageInfo.packageName);//����
		message.append("\n �汾��:" + packageInfo.versionCode);//�汾��
		message.append("\n �汾��:" + packageInfo.versionName);//�汾��		
		builder.setMessage(message.toString());
		builder.setIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
		builder.setPositiveButton("ȷ��", null);//��������Dialog��ʧ
		builder.create().show();
	}

	private final int SEARCH_APP = 0 ;
	private Handler handler = new Handler() {
		// ����Ϣ���͹�����ʱ���ִ�������������
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if(msg.what == SEARCH_APP){
				showPackageInfos = packageInfos;
				gridView.setAdapter(new GridViewAdapter(showPackageInfos, MainViewActivity.this));
				listView.setAdapter(new ListViewAdapter(showPackageInfos, MainViewActivity.this));
				//���ñ�����������ɼ�
				setProgressBarIndeterminateVisibility(false);
			}
		};
	};
	// ����¿��ٵ��߳���Ҫ������ListView����������Ա������������߳�
	@Override
	public void run() {
		// ���ϵͳ�����а�
		packageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
		// ʵ�����û��Լ���װ�ĳ���
		userPackageInfos = new ArrayList<PackageInfo>();
		for (PackageInfo temp : packageInfos) {
			boolean flag = false;
			ApplicationInfo appInfo = temp.applicationInfo;
			if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				// ���¹���ϵͳӦ�ó���
				flag = true;
			} else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// �û��Լ���Ӧ�ó���
				flag = true;
			}
			if (flag) {
				userPackageInfos.add(temp);
			}
		}
		// ����һ����Ϣ�����̣߳������̰߳�ProgressDialog��ȡ����
		handler.sendEmptyMessage(SEARCH_APP);
		// ��ͬ�Ĳ����ͻ��в�ͬ�Ĳ���ֵ���ò�����Ҫ�������ֲ�ͬ�Ĳ���
		//���ǿ��������ֵ�����û���ͬ�Ĳ�����������
		
		try {// Ϊ�˿�����ʾЧ��������������仰
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
}
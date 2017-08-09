package com.guo.gongjiao;

import java.io.File;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private ArrayList<RowModel> directoryEntries = new ArrayList<RowModel>();
	private DataDownloader downloader = null;
	private File currentDirectory;
	private ProgressDialog mDialog;
	

	//��Ϊzip lib��֧�����ģ��������Ҫ����һ��hashtable������Ӣ�Ľ���һ��ӳ��
	final  Hashtable<String, String> mFileName = new Hashtable<String, String>();		
	private void initFileNameMap() {
		mFileName.put("shanghai", this.getResources().getString(R.string.shanghai));//�Ϻ�
		mFileName.put("beijing", this.getResources().getString(R.string.beijing));//����
		mFileName.put("guangzhou", this.getResources().getString(R.string.guangzhou));//����
		mFileName.put("shenzhen", this.getResources().getString(R.string.shenzhen));//����
		mFileName.put("chengdu", this.getResources().getString(R.string.chengdu));//�ɶ�
		mFileName.put("fuzhou", this.getResources().getString(R.string.fuzhou));//����
		mFileName.put("hefei", this.getResources().getString(R.string.hefei));//�Ϸ�
		mFileName.put("wuhan", this.getResources().getString(R.string.wuhan));//�人
		mFileName.put("zhixiashi", this.getResources().getString(R.string.zhixiashi));//ֱϽ��
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		initFileNameMap();
		unzip();
	}

	protected void OnPause() {
		super.onPause();
		if (downloader != null) {
			synchronized (downloader) {
				downloader.setStatusField(null);
			}
		}
	}

	protected void OnResume() {
		super.onResume();
		if (downloader != null) {
			synchronized (downloader) {
				downloader.setStatusField(mDialog);
			}
		}
	}
	//����ʱ��Ӧ
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	boolean result = true;
    	//�ж�������µ���back��
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		//�����ǰĿ¼��mybus���˳�����
    		if(currentDirectory.getName().equals("mybus")) {
    			finish();
    		} else {
    			//������ʾ�ϼ�Ŀ¼������
    			browseTo(currentDirectory.getParentFile(),0);
    		}
    	} 
        return result;
    }
	

	/**
	 * move the file in asset to directory /sdcard/mybus
	 */
	private void unzip() {
		Log.v(Globals.TAG, "start unzip file");
		class CallBack implements Runnable {
			public MainActivity mParent;

			public void run() {
				if (mParent.downloader == null)
					mParent.downloader = new DataDownloader(mParent, mDialog);
			}
		}
		CallBack cb = new CallBack();
		cb.mParent = this;
		mDialog = CreateDialog();
		mDialog.show();
		this.runOnUiThread(cb);	
	}
	
	protected ProgressDialog CreateDialog() {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog
				.setMessage(this.getResources().getString(
						R.string.pregress_diag));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		return dialog;
	}
	//��ʾ�ļ��б�
	private void browseTo(final File aDirectory, final long id) {
		//�������Ĳ����Ǹ�Ŀ¼����ʾĿ¼�µ���������
		if (aDirectory.isDirectory()) {
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		} else {
			//�����������ļ���Ϊ�ļ���Ӱ�ť
			DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
			   @Override
				public void onClick(DialogInterface arg0, int arg1) {
					try {
						try {//����û�ѡ��ȷ����ť��������ѯ����
							Intent in = new Intent(MainActivity.this,
									Traffic.class);
							//�������ļ���·����Ϊ�������ݹ�ȥ
							in.putExtra(Globals.FILENAME, aDirectory.getPath());
							//�������ļ���Ӧ�����ĳ��������ݹ�ȥ
							in.putExtra(Globals.Title, directoryEntries.get((int)id).mChineseName);
							//�򿪲�ѯ�����Activity
							MainActivity.this.startActivity(in);
						} catch (Exception e) {
							Context context = getApplicationContext();
							//�����������Toast��ʽ��ʾ�û�
							CharSequence text = MainActivity.this
									.getResources().getString(
											R.string.diag_err);
							int duration = Toast.LENGTH_SHORT;

							Toast toast = Toast.makeText(context, text,
									duration);
							toast.show();
						}
						;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			//���ȡ����ť�Ĺ���
			DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener() {
				 @Override
				 //���ѡ��ȡ������dialog���أ�������������
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			};
			//����һ��AlertDialog�����û�������е�ʱ��ᵯ����ʾ�����û���һ��ѡ��
			AlertDialog ad = new AlertDialog.Builder(this).setMessage(
					R.string.diag_msg).setPositiveButton(android.R.string.ok,
					okButtonListener).setNegativeButton(
					android.R.string.cancel, cancelButtonListener).create();
			ad.show();
		}
	}

	private void fill(File[] files) {
		//��������е�����Ԫ��
		this.directoryEntries.clear();	
		int type = 0;
		for (File file : files) {
			//���������ļ�Ҳ����Ŀ¼������
			if (!file.getName().endsWith(".txt") && !file.isDirectory())
				continue;
			final String name;
			//������ļ����ļ���ȥ����չ��֮�󱣴���������
			if (!file.isDirectory()) {
				name = file.getName().substring(0,
						file.getName().lastIndexOf('.'));
				type = 0;
			} else {
				//�����Ŀ¼ֱ�ӱ���Ŀ¼��
				type = 1;
				name = file.getName();
			}
			this.directoryEntries.add(new RowModel(type, name));
		}
		//�½�һ�������ַ��Ƚ���
		Comparator<RowModel> cmp = new ChinsesCharComp();
		//�������ļ���������
		Collections.sort(directoryEntries, cmp);
		//�½�һ��IconAdapter�������ļ����
		IconAdapter directoryList = new IconAdapter(directoryEntries);
		//����ListAdapter
		this.setListAdapter(directoryList);
	}

	//Ϊѡ��󶨼�����
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		File clickedFile = null;
		//�����һ��Ŀ¼���ͽ�һ����ʾĿ¼������
		if(this.directoryEntries.get(position).mRowtype == 1) {
			
			Log.v(Globals.TAG, "is a directory");
			//ͨ��directoryEntries��������ݻ�ȡ�ļ�·����Ϣ
			clickedFile = new File(this.currentDirectory.getAbsolutePath()
					+ File.separator + this.directoryEntries.get(position).mLabel);
			this.browseTo(clickedFile, id);
			return;
		} 
		//����Ǹ��ļ���Ҫ�Ȳ�ȫ�ļ�����·�����ڴ���Ӧ�����ļ�
		clickedFile = new File(this.currentDirectory.getAbsolutePath()
				+ File.separator + this.directoryEntries.get(position).mLabel + ".txt");
		//�ٴ�ȷ���ļ���������
		try {
			if (clickedFile != null && clickedFile.isFile())
				this.browseTo(clickedFile, id);
				
		} catch (Exception e) {
			//don't throw
		}
		
	}
	//����һ��ListView��������
	class IconAdapter extends ArrayAdapter<RowModel> {
		//��������ʼ���������趨������Դ�ͽ����ļ�
		IconAdapter(List<RowModel> _items) {
		    super(MainActivity.this, R.layout.row, _items);  
	    }
		//�Խ�������ӵ�ÿһ�У�������ͼ��
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row, parent, false);
			}
			//�����ı�
			TextView tv= (TextView)row.findViewById(R.id.label);
			tv.setText(directoryEntries.get(position).mChineseName);
			ImageView iv = (ImageView)row.findViewById(R.id.icon);
			//����ICON
			iv.setImageResource(R.drawable.icon);
			return row;
		}
		
	}	
	class ChinsesCharComp implements Comparator<RowModel> {
		//����һ���ȽϷ�����������Ҫ�Ƚϵ�2��ֵ
		public int compare(RowModel o1, RowModel o2) {
			String c1 = (String) o1.mChineseName;
			String c2 = (String) o2.mChineseName;
			//��ʼ��һ�������ַ�����
			Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);
			//�����׸����ӵ��ֵ�˳������
			if (myCollator.compare(c1, c2) < 0)
				return -1;
			else if (myCollator.compare(c1, c2) > 0)
				return 1;
			else
				return 0;
		}
	}

	public void getFileList() {
		mDialog.dismiss();//�������̶Ի���
		browseTo(new File(Globals.DataDir),0);//��ʾ�ļ��б�
	}
	
	class RowModel {
		int mRowtype; //0:file 1:directory
		String mLabel;//Ӣ������
		String mChineseName;//��������
		
		RowModel(int type, String label) {
			mRowtype = type;
			mChineseName = mLabel = label;
			String temp = mFileName.get(label);
			//���������������������ƴ洢��mChineseName
			if (temp != null) {
				mChineseName = temp;
			}
		}
		//������������
		public String toString() {
			return mChineseName;
		}	
	}



}

package com.guo.apkmanager;
import java.util.List;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
//ListView��������
class ListViewAdapter extends BaseAdapter {
	//���ڴ��Ӧ�ó�����Ϣ
	private List<PackageInfo> packageInfos = null;
	private LayoutInflater inflater = null;
	private  Context context = null;
	//���캯������ʼ������
	public ListViewAdapter(List<PackageInfo>  packageInfos , Context context){
		this.packageInfos = packageInfos; 
		this.context = context ; 
		inflater = LayoutInflater.from(context);
	}
	//���Ӧ�ó���ĸ���
	@Override
	public int getCount() {
		return packageInfos.size();
	}
	//���Ӧ�ó���
	@Override
	public Object getItem(int arg0) {
		return packageInfos.get(arg0);
	}
	//���Ӧ�ó����ID
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	//����listView����ͼ
	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.listviewitem, null);
		TextView appName = (TextView) view.findViewById(R.id.lv_item_appname);
		TextView packageName = (TextView) view.findViewById(R.id.lv_item_packagename);
		ImageView iv = (ImageView) view.findViewById(R.id.lv_icon);
		//����Ӧ�ó�������
		appName.setText(packageInfos.get(position).applicationInfo.loadLabel(context.getPackageManager()));
		//���ð���
		packageName.setText(packageInfos.get(position).packageName);
		//����icon
		iv.setImageDrawable(packageInfos.get(position).applicationInfo.loadIcon(context.getPackageManager()));	
		return view;
	}	
}

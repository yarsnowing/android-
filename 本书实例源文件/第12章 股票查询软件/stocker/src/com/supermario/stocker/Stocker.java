package com.supermario.stocker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
//������ʵ����
public class Stocker extends ListActivity implements View.OnClickListener, KeyEvent.Callback {
	//��Ʊ����������
	private QuoteAdaptor quoteAdaptor;
	//��Ʊ���������
	private EditText symbolText;
	//��Ʊ�������밴ť
	private Button addButton;
	//���ذ�ť
	private Button cancelButton;
	//ɾ����ť
	private Button deleteButton;
	//�Ի���
	private Dialog dialog = null;
	//��Ʊ��ϸ��Ϣ
	private TextView currentTextView,noTextView, openTextView, closeTextView, dayLowTextView, dayHighTextView;
	//��K��ͼ
	private ImageView chartView;
	//��Ʊ���ݴ�����
	DataHandler mDataHandler;
	//��ǰActivityʵ��
	Stocker mContext;
	//��ǰѡ�еĹ�Ʊ�����
	int currentSelectedIndex;
	//��ʼ������
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		mContext=this;
		//��֤��ǰ��Ź�Ʊ������ļ��Ƿ����
		File mFile =new File("/data/data/com.supermario.stocker/files/symbols.txt");
		if(mFile.exists())
		{
			Log.e("guojs","file exist");
		}else{
			try {
				//�½���Ź�Ʊ������ļ�
				FileOutputStream outputStream=openFileOutput("symbols.txt",MODE_PRIVATE);
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("guojs","file no exist");
		}
		//��ʼ����Ʊ���봦����
		mDataHandler = new DataHandler(mContext);
		//���adapter����Ϊ����ʾ������
		getListView().setEmptyView(findViewById(R.id.empty));
		quoteAdaptor = new QuoteAdaptor(this, this,mDataHandler);
		//Ϊ�б�����������
		this.setListAdapter(quoteAdaptor);
		//��ӹ�Ʊ��ť
		addButton = (Button) findViewById(R.id.add_symbols_button);
		//������Ӱ�ť������
		addButton.setOnClickListener(this);
		//��Ʊ�����ı���
		symbolText= (EditText) findViewById(R.id.stock_symbols);
	}
	//��������onCreate->onStart->onResume
	protected void onResume(){
		super.onResume();
		if(quoteAdaptor != null)
		{
			//��ʼ���½���
			quoteAdaptor.startRefresh();
		}
	}
	//���治�ɼ�ʱ��ֹͣ����
	protected void onStop(){
		super.onStop();
		//ֹͣ���½���
		quoteAdaptor.stopRefresh();
	}
	//�б�Ԫ�ر����֮�󴥷�
	protected void onListItemClick(ListView l, View v, int position, long id){
		super.onListItemClick(l,v, position, id);
		//ȡ�õ��λ�õĹ�Ʊ
		StockInfo quote = quoteAdaptor.getItem(position);
		//ȡ�õ�ǰλ�õ����
		currentSelectedIndex=position;
		if(dialog == null){
			dialog = new Dialog(mContext);
			dialog.setContentView(R.layout.quote_detail);
			//ɾ����ť
			deleteButton = (Button) dialog.findViewById(R.id.delete);
			//����ɾ����ť������
			deleteButton.setOnClickListener(this);
			//���������水ť
			cancelButton = (Button) dialog.findViewById(R.id.close);
			//���÷��ذ�ť������
			cancelButton.setOnClickListener(this);
			//��ǰ��Ʊ�۸�
			currentTextView = (TextView) dialog.findViewById(R.id.current);
			//��ǰ��Ʊ����
			noTextView = (TextView) dialog.findViewById(R.id.no);
			//�������̼�
			openTextView = (TextView) dialog.findViewById(R.id.opening_price);
			//�������̼�
			closeTextView = (TextView) dialog.findViewById(R.id.closing_price);
			//������ͼ�
			dayLowTextView = (TextView) dialog.findViewById(R.id.day_low);
			//������߼�
			dayHighTextView = (TextView) dialog.findViewById(R.id.day_high);
			//��ƱK��ͼ
			chartView = (ImageView)dialog.findViewById(R.id.chart_view);
		}
		//���öԻ������
		dialog.setTitle(quote.getName());
		//���ù�Ʊ��ǰ�۸�
		double current=Double.parseDouble(quote.getCurrent_price());
		double closing_price=Double.parseDouble(quote.getClosing_price());
		//������λС��
		DecimalFormat df=new DecimalFormat("#0.00"); 
		String percent=df.format(((current-closing_price)*100/closing_price))+"%";
		//����Ʊ�۸񳬹��������̼�
		if(current > closing_price)
		{
			//����������ɫΪ��ɫ
			currentTextView.setTextColor(0xffEE3B3B);			
		}
		else 
		{
			//����������ɫΪ��ɫ
			currentTextView.setTextColor(0xff2e8b57);
		}
		//����TextView����
		currentTextView.setText(df.format(current)+"  ("+percent+")");
		openTextView.setText(quote.opening_price);
		closeTextView.setText(quote.closing_price);
		dayLowTextView.setText(quote.min_price);
		dayHighTextView.setText(quote.max_price);
		noTextView.setText(quote.no);
		//����K��ͼ
		chartView.setImageBitmap(mDataHandler.getChartForSymbol(quote.no));
		dialog.show();
	}
	//�жϻس�������ʱ��ӹ�Ʊ
	public boolean onKeyUp(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_ENTER){
			//���ͺư
			addSymbol();
			return true;
		}
		return false;
	}
	//��ӹ�Ʊ���룬�Կո���߻س��ָ������Ʊ
	private void addSymbol(){
		//����ı������������
		String symbolsStr = symbolText.getText().toString();
		//���س����滻�ɿո�
		symbolsStr = symbolsStr.replace("\n", " ");
		//�Կո�ָ��ַ���
		String symbolArray[] = symbolsStr.split(" ");
		int index, count = symbolArray.length;
		ArrayList<String> symbolList = new ArrayList<String>();
		for(index = 0; index < count; index++){
			symbolList.add(symbolArray[index]);
		}
		//����Ʊ������ӽ��ļ���
		quoteAdaptor.addSymbolsToFile(symbolList);
		//�����ı���Ϊ��
		symbolText.setText(null);
	}
	//���ð����ص�����
	public void onClick(View view) {
		if(view == addButton){
			//��ӹ�Ʊ���ļ���
			addSymbol();
		} else if(view == cancelButton){
			//�رնԻ���
			dialog.dismiss();
		} else if(view == deleteButton){
			//ɾ����ǰ��Ʊ
			quoteAdaptor.removeQuoteAtIndex(currentSelectedIndex);
			dialog.dismiss();
		} else if(view.getParent() instanceof RelativeLayout){
			RelativeLayout rl = (RelativeLayout)view.getParent();
			this.onListItemClick(getListView(), rl, rl.getId()-33, rl.getId());
		} else if(view instanceof RelativeLayout){
			this.onListItemClick(getListView(), view, view.getId()-33, view.getId());
		}
	}    
	//��Ʊ����������
	public class QuoteAdaptor extends BaseAdapter implements ListAdapter, Runnable {
		//��ǰ��ʾ���������Ϊ10
		private static final int DISPLAY_COUNT = 10;
		public DataHandler dataHandler;
		//ǿ�Ƹ��±�־
		private boolean forceUpdate = false;
		//����������
		Context context;
		//����Activityʵ��
		Stocker stocker;
		LayoutInflater inflater;

		QuoteRefreshTask quoteRefreshTask = null;
		int progressInterval;
		//��Ϣ������
		Handler messageHandler = new Handler();


		public QuoteAdaptor(Stocker aController, Context mContext,DataHandler mdataHandler) {
			//���浱ǰ�������ĺ�Activityʵ��
			context = mContext;
			stocker = aController;
			dataHandler = mdataHandler;
		}
		//ȡ�ù�Ʊ����Ĵ�С
		public int getCount() {
			return dataHandler.stocksSize();
		}
		//ȡ�õ�ǰλ�ù�Ʊ�Ķ���
		public StockInfo getItem(int position) {
			return dataHandler.getQuoteForIndex(position);
		}
		//ȡ�õ�ǰ��λ��
		public long getItemId(int position) {
			return position;
		}
		//������ͼ
		public View getView(int position, View convertView, ViewGroup parent) {
			StockInfo quote;
			inflater = LayoutInflater.from(context);
			RelativeLayout cellLayout = (RelativeLayout)inflater.inflate(R.layout.quote_cell, null);
			cellLayout.setMinimumWidth(parent.getWidth());
			int color;
			stocker.setProgress(progressInterval*(position + 1));
			if(position % 2 > 0)
				color = Color.rgb(48,92,131);
			else 
				color = Color.rgb(119,138,170);
			cellLayout.setBackgroundColor(color);
			quote = dataHandler.getQuoteForIndex(position);
			TextView field = (TextView)cellLayout.findViewById(R.id.symbol);
			//���ù�Ʊ�Ĵ���
			field.setText(quote.getNo());
			field.setClickable(true);
			field.setOnClickListener(stocker);

			//��Ʊ����
			field = (TextView)cellLayout.findViewById(R.id.name);
			field.setClickable(true);
			field.setOnClickListener(stocker);
			field.setText(quote.getName());
			
			field = (TextView)cellLayout.findViewById(R.id.current);
			//���ù�Ʊ��ǰ�۸�
			double current=Double.parseDouble(quote.getCurrent_price());
			double closing_price=Double.parseDouble(quote.getClosing_price());
			//������λС��
			DecimalFormat df=new DecimalFormat("#0.00"); 
			String percent=df.format(((current-closing_price)*100/closing_price))+"%";
			field.setText(df.format(current));
			field.setClickable(true);
			field.setOnClickListener(stocker);		
			
			field = (TextView)cellLayout.findViewById(R.id.percent);
			//����Ʊ�۸񳬹��������̼�
			if(current > closing_price)
			{
				//����������ɫΪ��ɫ
				field.setTextColor(0xffEE3B3B);			
			}
			else 
			{
				//����������ɫΪ��ɫ
				field.setTextColor(0xff2e8b57);
			}
			field.setText(percent);
			cellLayout.setId(position + 33);
			cellLayout.setClickable(true);
			cellLayout.setOnClickListener(stocker);
			return cellLayout;
		}
		//����Ԫ�ؾ���ѡ��
		public boolean areAllItemsSelectable() {
			return true;
		}
		public boolean isSelectable(int arg0) {
			return true;
		}
		//ֹͣ���¹�Ʊ
		public void stopRefresh(){
			quoteRefreshTask.cancelTimer();
			quoteRefreshTask = null;
		}
		//��ʼ���¹�Ʊ
		public void startRefresh(){
			if(quoteRefreshTask == null)
				quoteRefreshTask = new QuoteRefreshTask(this);
		}
		//����������
		public void refreshQuotes(){
			messageHandler.post(this);		
		}
		//��������������
		public void run(){
			if(mDataHandler.stocksSize() > 0){
				if(forceUpdate ){
					forceUpdate = false;
					progressInterval = 10000/DISPLAY_COUNT;
					stocker.setProgressBarVisibility(true);
					stocker.setProgress(progressInterval);
					mDataHandler.refreshStocks();
				}
				//֪ͨ���ݸ���
				this.notifyDataSetChanged();
			}
		}
		//��ӹ�Ʊ���뵽�ļ���
		public void addSymbolsToFile(ArrayList<String> symbols){
			//ǿ�и���ҳ������
			forceUpdate = true;
			//��ӹ�Ʊ���ļ���
			mDataHandler.addSymbolsToFile(symbols);
			//�����Ϣ����Ϣ����
			messageHandler.post(this);
		}
		//�Ƴ��б��е�����
		public void removeQuoteAtIndex(int index){
			forceUpdate = true;
			mDataHandler.removeQuoteByIndex(index);
			messageHandler.post(this);
		}
		//��Ʊ���¶�ʱ��
		public class QuoteRefreshTask extends TimerTask {
			QuoteAdaptor quoteAdaptor;
			Timer        refreshTimer;
			final static int  TENSECONDS = 10000;
			public QuoteRefreshTask(QuoteAdaptor anAdaptor){
				refreshTimer = new Timer("Quote Refresh Timer");
				refreshTimer.schedule(this, TENSECONDS, TENSECONDS);
				quoteAdaptor = anAdaptor;
			}

			public void run(){
				messageHandler.post(quoteAdaptor);
			}

			public void startTimer(){
				if(refreshTimer == null){
					refreshTimer = new Timer("Quote Refresh Timer");
					refreshTimer.schedule(this, TENSECONDS, TENSECONDS);
				}
			}
			//ȡ����ʱ��
			public void cancelTimer(){
				this.cancel();
				refreshTimer.cancel();
				refreshTimer = null;
			}
		}
	}
}
package com.guo.gongjiao;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.util.Log;

class CountingInputStream extends BufferedInputStream {

	private long bytesReadMark = 0;  //���ڴ洢�ļ�λ�ñ�ʶ
	private long bytesRead = 0;	     //��ǰ��ȡ�ļ��ֽ���
	//����һ��BufferedInputStream�������СΪsize
	public CountingInputStream(InputStream in, int size) {
		super(in, size);
	}
	//����һ��BufferedInputStream�������СΪ8192bytes
	public CountingInputStream(InputStream in) {
		super(in);
	}
	//�õ���ǰ��ȡ�ļ����ֽ���
	public long getBytesRead() {
		return bytesRead;
	}
	//ÿ�ζ�ȡһ���ֽڣ�������ȡ�ֽ�����1
	//ʹ��synchronized�ؼ��֣�ʹ�ú���ͬʱֻ�ܱ�ͬһ��ʵ������һ��
	public synchronized int read() throws IOException {

		int read = super.read();
		if (read >= 0) {
			bytesRead++;
		}
		return read;
	}
	//��ȡ������len���ȵ��ֽڣ��洢��buffer b�д�ƫ����Ϊoff��ʼ��λ�ã�
	//readΪʵ�ʶ�ȡ�����ֽ�����������ǰ��ȡ�Լ�������read
	public synchronized int read(byte[] b, int off, int len) throws IOException {

		int read = super.read(b, off, len);
		if (read >= 0) {
			bytesRead += read;
		}
		return read;
	}
	//�������n�ֽڣ�����ʵ���������ֽ�����������ǰ�ֽ�����skipped
	public synchronized long skip(long n) throws IOException {

		long skipped = super.skip(n);
		if (skipped >= 0) {
			bytesRead += skipped;
		}
		return skipped;
	}
	//����ǰλ�ñ��浽bytesReadMark���������
	//����ȡƫ��������readlimit�����mark��λ��ʧЧ
	public synchronized void mark(int readlimit) {
		super.mark(readlimit);
		bytesReadMark = bytesRead;
	}
	//����ǰ�ļ�λ�ûָ���bytesReadMark��ָ��λ��
	//����ļ��رգ�����û��markһ��λ�ã�����֮ǰmark��λ���Ѿ�ʧЧ�������׳�һ��IOException
	public synchronized void reset() throws IOException {
		super.reset();
		bytesRead = bytesReadMark;
	}
}
//�����ļ����أ���Ҫ�Ǹ����ѹ��data.zip�ļ�
class DataDownloader extends Thread
{
	public StatusWriter Status;//������ʾ���ȣ�������Ϣ��
	public boolean DownloadComplete = false;
	public boolean DownloadFailed = false;
	private MainActivity Parent;//���ڴ����������࣬�Խ��н�����ز���
	private String outFilesDir = null;//����ļ�Ŀ¼
	class StatusWriter
	{
		private ProgressDialog Status;	//������ʾ��Ϣ
		private MainActivity Parent;//�����������潻��
		private String oldText = "";
		//ʵ������Ա��������������Ԫ�ش��ݹ�����������и���
		public StatusWriter( ProgressDialog _Status, MainActivity _Parent )
		{
			Status = _Status;
			Parent = _Parent;
		}
		public void setParent( ProgressDialog _Status, MainActivity _Parent )
		{
			//����һ�����󣬵���ͬһ��DataDownloaderʵ��ʱ���߳�ͬ����
			//���ǲ�ͬʵ�����ǲ�ͬ������Ҫ��ͬ����Ҳͬ���������������Ǿ�̬����
			synchronized(DataDownloader.this) {
				Status = _Status;
				Parent = _Parent;
				setText( oldText );//��ʼ����ʱ��TextView��ʾ��
			}
		}
		
		public void setText(final String str)
		{
			//���ڸ���TextView�е�����
			class Callback implements Runnable
			{
				public ProgressDialog Status;
				public String text;
				public void run()
				{
					Status.setMessage(text);
				}
			}
			synchronized(DataDownloader.this) {
				Callback cb = new Callback();
				oldText = new String(str);
				cb.text = new String(str);
				cb.Status = Status;
				//Ϊ�˷�ֹ����������۸��ж���ֵ�õ�
				if( Parent != null && Status != null )
					Parent.runOnUiThread(cb);//������UI�߳��У��Ը��½���Ԫ��
			}
		}
		
	}
	//��DataDownloader�Ĺ��캯����������������Ӧ��Ԫ�أ�Ϊ���½�����Ϣ��׼��
	public DataDownloader( MainActivity _Parent, ProgressDialog _Status )
	{
		Parent = _Parent;
		Status = new StatusWriter( _Status, _Parent );//����StatusWriter�࣬ר�����ڽ��н���ĸ��²���
		//Status.setText( "Connecting to " + Globals.DataDownloadUrl );
		outFilesDir = Globals.DataDir;//��ʼ��Ŀ��Ŀ¼��·��
		DownloadComplete = false;	//��ʼ��DownloadComplete
		this.start();		//���и���ĺ��ĺ���
	}
	
	public void setStatusField(ProgressDialog _Status)
	{
		synchronized(this) {
			Status.setParent( _Status, Parent );
		}
	}
	//���ĺ���
	@Override
	public void run()
	{	
		//���Ŀ��Ŀ¼���ļ��Ƿ���������ȷ������ѹ���ļ�����Ҫ���ı�ʶ�ļ���
		if( ! DownloadDataFile(Globals.DataDownloadUrl, "DownloadFinished.flag") )
		{
			DownloadFailed = true;
			return;
		}
		//������е������˵����������ȷ��
		DownloadComplete = true;
		//��ʼ��
		initParent();
	}

	public boolean DownloadDataFile(final String DataDownloadUrl, final String DownloadFlagFileName)
	{	
		//��ʼ����Դʵ��
		Resources res = Parent.getResources();
		//���Ŀ���ļ��Ƿ����ָ��������
		String path = getOutFilePath(DownloadFlagFileName);
		InputStream checkFile = null;
		try {
			checkFile = new FileInputStream( path );
		} catch( FileNotFoundException e ) {
		} catch( SecurityException e ) { };
		if( checkFile != null )
		{
			try {
				//����һ���ȱ�׼�����Դ��buffer�����ڴ洢�ļ�����
				byte b[] = new byte[ Globals.DataDownloadUrl.getBytes("UTF-8").length + 1 ];
				int readed = checkFile.read(b);
				String compare = new String( b, 0, readed, "UTF-8" );  //DataDownloadUrl=data.zip
				boolean matched = false;
				//��compare=data.zip
				if( compare.compareTo(DataDownloadUrl) == 0 )
					matched = true;
				//�����ƥ�䣬�׳��쳣��ֱ����ת��1���ڵ�λ��
				if( ! matched )
					throw new IOException();
				Status.setText( res.getString(R.string.download_unneeded) );
				return true;
			} catch ( IOException e ) {};
		}
		checkFile = null;  //----1
		//mkdirs ������������м��Ҫ��Ŀ¼
		try {
			(new File( outFilesDir )).mkdirs();
			OutputStream out = new FileOutputStream( getOutFilePath(".nomedia") );
			out.flush();
			out.close();
		}
		catch( SecurityException e ) {}
		catch( FileNotFoundException e ) {}
		catch( IOException e ) {};

		long totalLen = 0;
		CountingInputStream stream;
		byte[] buf = new byte[16384];

		String url = DataDownloadUrl;
		System.out.println("Unpacking from assets: '" + url + "'");
		try {
			//��assets�µ��ļ�
			stream = new CountingInputStream(Parent.getAssets().open(url), 8192);
			//�����ļ�ĩβ��ȷ���ļ���С
			while( stream.skip(65536) > 0 ) { };
			//���ļ���С�洢��totalLen
			totalLen = stream.getBytesRead();
			stream.close();
			//���´��ļ����������ļ���ǰ��ȡλ��
			stream = new CountingInputStream(Parent.getAssets().open(url), 8192);
		} catch( IOException e ) {
			System.out.println("Unpacking from assets '" + url + "' - error: " + e.toString());
			Status.setText( res.getString(R.string.error_dl_from, url) );//�������
			return false;
		}
		ZipInputStream zip = new ZipInputStream(stream);
		
		while(true)
		{
			ZipEntry entry = null;
			try {
				//ȡ��ѹ���ļ���һ������Ԫ��
				entry = zip.getNextEntry();
				if( entry != null )
					System.out.println("Reading from zip file '" + url + "' entry '" + entry.getName() + "'");
			} catch( java.io.IOException e ) {
				Status.setText( res.getString(R.string.error_dl_from, url) );
				System.out.println("Error reading from zip file '" + url + "': " + e.toString());
				return false;
			}
			//���Ԫ��Ϊ�գ�����ѹ���ļ��Ѿ���ȡ���
			if( entry == null )
			{
				System.out.println("Reading from zip file '" + url + "' finished");
				break;
			}
			//�����Ŀ¼���򴴽���ӦĿ¼�ṹ������ֱ�Ӷ�ȡ��һ��Ԫ��
			if( entry.isDirectory() )
			{
				System.out.println("Creating dir '" + getOutFilePath(entry.getName()) + "'");
				try {
					(new File( getOutFilePath(entry.getName()) )).mkdirs();
				} catch( SecurityException e ) { };
				continue;
			}

			OutputStream out = null;
			path = getOutFilePath(entry.getName());
			//��ȫ���������Ŀ���ļ�����Ҫ��Ŀ¼�ṹ����Ȼ���ﲻ��Ҫ�����Ժ���ܻ���Ҫ
			try {
				(new File( path.substring(0, path.lastIndexOf("/") ))).mkdirs();
			} catch( SecurityException e ) { };
			
			try {
				//ʹ��CRC32У��Ŀ���ļ�
				CheckedInputStream check = new CheckedInputStream( new FileInputStream(path), new CRC32() );
				while( check.read(buf, 0, buf.length) > 0 ) {};
				check.close();
				//���У����Ƿ���ȷ
				if( check.getChecksum().getValue() != entry.getCrc() )
				{
					File ff = new File(path);
					ff.delete();//У��Ͳ���ȷ��ɾ���ļ������´����ļ�
					throw new Exception(); //��ת��catch
				}
				System.out.println("File '" + path + "' exists and passed CRC check - not overwriting it");
				//��У��ɹ��������У����һ���ļ�
				continue;
			} catch( Exception e )
			{
			}
			try {
				out = new FileOutputStream( path );
			} catch( FileNotFoundException e ) {
				System.out.println("Saving file '" + path + "' - cannot create file: " + e.toString());
			} catch( SecurityException e ) {
				System.out.println("Saving file '" + path + "' - cannot create file: " + e.toString());
			};
			//��������ļ�ʧ��
			if( out == null )
			{
				Status.setText( res.getString(R.string.error_write, path) );
				System.out.println("Saving file '" + path + "' - cannot create file");
				return false;
			}
			//��������԰ٷֱ���ʾ��progress dialog��
			float percent = 0.0f;
			if( totalLen > 0 )
				percent = stream.getBytesRead() * 100.0f / totalLen;
			Status.setText( res.getString(R.string.dl_progress, percent, path) );
			
			try {
				//��ȡ��ǰԪ��
				int len = zip.read(buf);
				while (len >= 0)
				{
					if(len > 0)
						out.write(buf, 0, len);//����ǰԪ�ص����ݿ�����Ŀ���ļ�
					len = zip.read(buf);

					percent = 0.0f;
					if( totalLen > 0 )
						percent = stream.getBytesRead() * 100.0f / totalLen;
					Status.setText( res.getString(R.string.dl_progress, percent, path) );
				}
				out.flush();
				out.close();
				out = null;
			} catch( java.io.IOException e ) {
				Status.setText( res.getString(R.string.error_write, path) );
				System.out.println("Saving file '" + path + "' - error writing or downloading: " + e.toString());
				return false;
			}
			
			try {
				//������֮��ͬ����ҪУ��һ��
				CheckedInputStream check = new CheckedInputStream( new FileInputStream(path), new CRC32() );
				while( check.read(buf, 0, buf.length) > 0 ) {};
				check.close();
				if( check.getChecksum().getValue() != entry.getCrc() )
				{
					File ff = new File(path);
					ff.delete();
					throw new Exception();
				}
			} catch( Exception e )
			{
				Status.setText( res.getString(R.string.error_write, path) );
				System.out.println("Saving file '" + path + "' - CRC check failed");
				return false;
			}
			System.out.println("Saving file '" + path + "' done");
		}

		OutputStream out = null;
		//ȫ�����֮���ƶ���Ϣд��У���ļ���
		path = getOutFilePath(DownloadFlagFileName);
		try {
			out = new FileOutputStream( path );
			out.write(DataDownloadUrl.getBytes("UTF-8"));
			out.flush();
			out.close();
		} catch( FileNotFoundException e ) {
		} catch( SecurityException e ) {
		} catch( java.io.IOException e ) {
			Status.setText( res.getString(R.string.error_write, path) );
			return false;
		};
		Status.setText( res.getString(R.string.dl_finished) );

		try {
			stream.close();
		} catch( java.io.IOException e ) {
		};

		return true;
	};
	//��ǰ������ݶ���ȷ����ִ�н���ĳ�ʼ�����������ļ����б����ʽ���ֳ���
	private void initParent()
	{
		class Callback implements Runnable
		{
			public MainActivity Parent;
			public void run()
			{
				Parent.getFileList();//���Ŀ��Ŀ¼���ļ��б�
				Log.e("guojs","initParent!");
			}
		}
		Callback cb = new Callback();
		synchronized(this) {
			cb.Parent = Parent;//�������������ʵ��
			if(Parent != null)
				Parent.runOnUiThread(cb);//��Ϊ��Ҫ���½��棬�����Ҫ������UI�߳�
		}
	}
	//����Ŀ������ļ��ľ���·��
	private String getOutFilePath(final String filename)
	{
		return outFilesDir + "/" + filename;
	};
	

}


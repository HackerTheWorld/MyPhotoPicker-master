package com.wgm.scaneqinfo.common;

import android.os.Message;

import com.wgm.scaneqinfo.conf.Setting;
import com.wgm.scaneqinfo.operate.BaseOperate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.system.Os.read;

public class HttpFileUpTool {
	private Handler handler;
	private String msg = null;
	private static String JSESSIONID;
	private JSONObject response;
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10*10000000; //超时时间
	private static final String CHARSET = "utf-8"; //设置编码
	private static final String PREFIX = "--";
	private static final String LINE_END = "";
	/**
	 * ͨ��ƴ�ӵķ�ʽ�����������ݣ�ʵ�ֲ��������Լ��ļ�����
	 * 
	 * @param actionUrl
	 * @param params
	 * @param files
	 * @return
	 * @throws IOException
	 */
	private static void post(String actionUrl, Map<String, String> params,
			Map<String, File> files) throws IOException, JSONException {

		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(5 * 1000); // ������ʱ��
		conn.setDoInput(true);// ��������
		conn.setDoOutput(true);// �������
		conn.setUseCaches(false); // ������ʹ�û���
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		// ������ƴ�ı����͵Ĳ���
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(conn
				.getOutputStream());
		outStream.write(sb.toString().getBytes());
		InputStream in = null;
		// �����ļ�����
		if (files != null){
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\""+file.getKey()+"\"; filename=\""
								+ "car_id\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}

		// ���������־
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		// �õ���Ӧ��
		int res = conn.getResponseCode();
		if (res == 200) {
			in = conn.getInputStream();
			int ch;
			StringBuilder sb2 = new StringBuilder();
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
			System.out.println(sb2.toString());
		}
		outStream.close();
		conn.disconnect();
		}
	}

	public  void asyncRequestImg(final String actionUrl, final Map<String, String> params,
										  final Map<String,File> files, final AsyncRequestCallBack callBack){
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(callBack != null)
					callBack.callBack();
			}
		};
		new Thread() {
			@Override
			public void run() {

				try {
					post(actionUrl,params,files);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);

			}
		}.start();

	}




	public interface AsyncRequestCallBack{
		public void callBack();
	}

}

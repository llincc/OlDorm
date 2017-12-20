package com.app.linch.oldorm.service;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.app.linch.oldorm.activity.ActivityInterface;
import com.app.linch.oldorm.activity.ChooseActivity;
import com.app.linch.oldorm.activity.LoginActivity;
import com.app.linch.oldorm.util.JSONparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class FetchDataService implements Runnable
{
    private static final  String TAG = "FetchData";
    public static  final  int LOGIN_RESPONSE = 0;
    public static  final  int PERNENNEL_INFO = 1;
    public static  final  int ROOM_DATA = 2;
    public static  final  int CHOOSE_RESULT = 3;
    public static  final  String REQUEST_GET =  "GET";
    public static  final  String REQUEST_POST = "POST";
    private String address;
    private HttpURLConnection connection;
    private ActivityInterface context;
    private int ResultCode;
    private String requestType;
    public FetchDataService(String address,int ResultCode, String requestType, ActivityInterface context)
    {
        this.address = address;
        this.connection = null;
        this.requestType = requestType;
        this.context = context;
        this.ResultCode = ResultCode;
        Log.d(TAG, "Address:"+address);
        Log.d(TAG, "ResultCode:"+ResultCode);
    }

    /**
     * Https安全验证
     */
    private static class TrustAnyTrustManager implements X509TrustManager
    {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
        }

        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[] {};
        }
    }
    private static class TrustAnyHostnameVerifier implements HostnameVerifier
    {
        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }
    }

    /**
     * 创建网络连接
     * @throws IOException
     */
    private void initConnection() throws IOException,Exception
    {
        connection = (HttpURLConnection) new URL(address).openConnection();

        //https安全验证
        if (connection instanceof HttpsURLConnection)
        {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] {new TrustAnyTrustManager()}, new java.security.SecureRandom());
            ((HttpsURLConnection) connection).setSSLSocketFactory(sc.getSocketFactory());
            ((HttpsURLConnection) connection).setHostnameVerifier(new TrustAnyHostnameVerifier());
//            connection.connect();
        }
        connection.setRequestMethod(requestType);
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);

    }

    /**
     * 从网络获取数据
     * @return
     * @throws IOException
     */
    private String fetchData() throws IOException
    {
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();

        for (String str; (str = reader.readLine()) != null;)
        {
            response.append(str);
        }
        System.out.println(response.toString());
        return response.toString();
    }

    /**
     * 数据解析
     * @param jsondata
     * @throws IOException
     */
    private void parseData(String jsondata) throws IOException{
        Message message = new Message();
        switch (ResultCode){
            case LOGIN_RESPONSE:
                message.what = LOGIN_RESPONSE;
                message.obj = JSONparser.parseLoginResponse(jsondata);
                context.getHandler().sendMessage(message);
                break;
            case PERNENNEL_INFO:
                message.what = PERNENNEL_INFO;
                message.obj = JSONparser.parsePersonnelInfo(jsondata);
                context.getHandler().sendMessage(message);
                break;
            case ROOM_DATA:
                message.what = ROOM_DATA;
                message.obj = JSONparser.parseRoomData(jsondata);
                context.getHandler().sendMessage(message);
                break;
            case CHOOSE_RESULT:
                message.what = CHOOSE_RESULT;
                message.obj = JSONparser.parseChooseResponse(jsondata);
                context.getHandler().sendMessage(message);
                break;
            default:
                break;
        }
    }

    /**
     * 关闭网络连接
     * @throws IOException
     */
    private void close() throws IOException
    {
        if (connection != null)
        {
            connection.disconnect();
        }
    }

    /**
     * 主调用程序
     * @return TodayWeather
     * @throws IOException
     */
    private void runWithException() throws Exception
    {
        initConnection();
        String data = fetchData();
        close();
        parseData(data);
    }

    @Override
    public void run() {
        try
        {
            runWithException();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e){
            Log.d("FetchData","网络连接错误");
            e.printStackTrace();
        }
    }
}


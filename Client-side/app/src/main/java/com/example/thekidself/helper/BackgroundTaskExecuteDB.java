package com.example.thekidself.helper;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundTaskExecuteDB extends AsyncTask<String,Void,String> {

    Context ctx;
    String sql;

    public BackgroundTaskExecuteDB(Context ctx) { this.ctx=ctx; }

    public String doInBackground(String... params) {
        String IP = params[0];
        String reg_url1="http://" + IP + "/TheKidSelfPHP/TheKidSelfExecute.php";
        String method = params[1];
        if(method.equals("Register")) {
            sql = params[2];
        }
        try {
            URL url = new URL(reg_url1);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream os=httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            String data = URLEncoder.encode("sql","UTF-8")+"="+URLEncoder.encode(sql,"UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            os.close();
            InputStream IS = httpURLConnection.getInputStream();
            IS.close();
            return null;}
            catch (MalformedURLException e) {e.printStackTrace();}
            catch (ProtocolException e) {e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}

        return null; }

    @Override
    public void onPreExecute() {super.onPreExecute();}
    @Override
    public void onPostExecute(String result) {}
    @Override
    public void onProgressUpdate(Void... values) {super.onProgressUpdate(values);}
}

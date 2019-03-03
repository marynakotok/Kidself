package com.example.thekidself.helper;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackgroundTaskSelectDB extends AsyncTask<String,Void,String> {
    Context ctx; String urlWebService;
    public BackgroundTaskSelectDB(Context ctx) {
        this.ctx = ctx;
    }
    public String doInBackground(String... params)
    {
        String IP = params[0]; String method = params[1];
        if (method.equals("Selection")) {
            String sql = params[2]; String table = params[3];
            String sqlReplaced = sql.replaceAll(" ", "%20");
            sqlReplaced = sqlReplaced.replaceAll("'", "%22");
            sqlReplaced = sqlReplaced.replaceAll(";", "%3B");
            urlWebService = "http://" + IP + "/TheKidSelfPHP/TheKidSelfSelect.php?table=" + table + "&sql=" + sqlReplaced;
        }
        if (method.equals("CheckEmail")) {
            String emailToCheck = params[2];
            urlWebService = "http://" + IP + "/TheKidSelfPHP/TheKidSelfCheckEmailExist.php?emailToCheck=" + emailToCheck;
        }
        if (method.equals("SendEmail")) {
            String url = params[2]; urlWebService = url;
        }
        if (method.equals("CheckPassword")) {
            String codeToCheck = params[2];
            urlWebService = "http://" + IP + "/TheKidSelfPHP/TheKidSelfCheckPasswordExist.php?codeToCheck=" + codeToCheck;
        }
        if (method.equals("CheckCodeChild")) {
            String codeChild = params[2];
            urlWebService = "http://" + IP + "/TheKidSelfPHP/TheKidSelfCheckCodeChild.php?codeChild=" + codeChild;
        }
        if (method.equals("CheckCodeUser")) {
            String codeUser = params[2];
            urlWebService = "http://" + IP + "/TheKidSelfPHP/TheKidSelfCheckCodeUser.php?codeUser=" + codeUser;
        }
        if (method.equals("CheckCodeParent")) {
            String codeParent = params[2];
            urlWebService = "http://" + IP + "/TheKidSelfPHP/TheKidSelfCheckCodeParent.php?codeParent=" + codeParent;
        }
        try {
            URL url = new URL(urlWebService);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String dataStream;
            while ((dataStream = bufferedReader.readLine()) != null) { sb.append(dataStream + "\n"); }
            return sb.toString().trim(); } catch (Exception e) { return null;} }
    @Override
    public void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    public void onPostExecute(String string) {
        super.onPostExecute(string);
    }
}


package org.nhitc.NetworkTask;

import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class Web_Crawler {
    public String Download(String str_URL) {
        String str_Output = "";
        try {
            /*Network connection.*/
            HttpsURLConnection WebConnection = getConnection(str_URL, "GET"); /*Get connection.*/
            WebConnection.connect();

            /*Get data stream.*/
            InputStream NetworkInput = WebConnection.getInputStream();
            InputStreamReader StreamReader;

            /*Read data stream.*/
            try {
                StreamReader = new InputStreamReader(new GZIPInputStream(NetworkInput), "UTF-8");
            }
            catch(Exception GZIP_Err) {
                StreamReader = new InputStreamReader(NetworkInput, "UTF-8");
            }
            BufferedReader DataReader = new BufferedReader(StreamReader);

            /*Read strings.*/
            String str_Buffer;
            do {
                str_Buffer = DataReader.readLine();
                str_Output += str_Buffer;
            }
            while(str_Buffer != null);            

            WebConnection.disconnect();
        }
        catch(Exception Err) {
            System.out.println(Err.getMessage());
        }
        return str_Output;
    }

    public String Download(String str_URL, boolean Auth) {
        String str_Output = "";
        try {
            /*Network connection.*/
            HttpsURLConnection WebConnection = getConnection(str_URL, "GET"); /*Get connection.*/
            String AppID = "";
            String AppKey = "";
            String AuthorizationText = String.format("hmac username=\"%s\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"%s\"", AppID, getAuthorization_String(AppKey));
            WebConnection.setRequestProperty("Authorization", AuthorizationText);
            WebConnection.connect();

            /*Get data stream.*/
            InputStream NetworkInput = WebConnection.getInputStream();
            InputStreamReader StreamReader;

            /*Read data stream.*/
            try {
                StreamReader = new InputStreamReader(new GZIPInputStream(NetworkInput), "UTF-8");
            }
            catch(Exception GZIP_Err) {
                StreamReader = new InputStreamReader(NetworkInput, "UTF-8");
            }
            BufferedReader DataReader = new BufferedReader(StreamReader);

            /*Read strings.*/
            String str_Buffer;
            do {
                str_Buffer = DataReader.readLine();
                str_Output += str_Buffer;
            }
            while(str_Buffer != null);            

            WebConnection.disconnect();
        }
        catch(Exception Err) {
            System.out.println(Err.getMessage());
        }
        return str_Output;
    }

    public void Download(String str_URL, String FilePath, String FileName) {
        try {
            /*Network connection.*/
            HttpsURLConnection WebConnection = getConnection(str_URL, "GET"); /*Get connection.*/
            WebConnection.connect();

            /*Prepare file location and target directory.*/
            File TargetDir = new File(FilePath);
            if(TargetDir.exists() != true) {
                TargetDir.mkdir();
            }
            File TargetFile = new File(FilePath + FileName);

            /*Get file stream.*/
            InputStream NetworkInput = WebConnection.getInputStream();
            FileOutputStream SaveFileStream = new FileOutputStream(TargetFile);

            /*Save as a file.*/
            try {
                SaveFileStream.write(new GZIPInputStream(NetworkInput).readAllBytes());
            }
            catch(Exception GZIP_Err) {
                SaveFileStream.write(NetworkInput.readAllBytes());
            }

            /*Close data stream.*/
            if(SaveFileStream != null) {
                SaveFileStream.flush();
                SaveFileStream.close();
            }
            if(NetworkInput != null) {
                NetworkInput.close();
            }
            WebConnection.disconnect();
            System.out.println("Download success.");
        }
        catch(Exception Err) {
            /*Https connection's error*/
            System.out.println(Err.getMessage());
            System.out.println("Download failed.");
        }
    }

    private HttpsURLConnection getConnection(String str_URL, String Method) throws IOException {
        HttpsURLConnection WebConnection = (HttpsURLConnection)new URL(str_URL).openConnection();
        /*Set waiting time.*/
        WebConnection.setConnectTimeout(15000);
        WebConnection.setReadTimeout(10000);
        /*Set http request's header.*/
        WebConnection.setRequestProperty("Content-Type", "charset=utf-8"); /*Charset*/
        WebConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        WebConnection.setRequestProperty("User-Agent", "Mozilla/5.0"); /*User agent*/
        WebConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        WebConnection.setRequestProperty("Accept-Encoding", "gzip,deflate");
        WebConnection.setRequestMethod(Method);
        WebConnection.setDoInput(true);
        return WebConnection;
    }

    private String getAuthorization_String(String AppKey) {
        return "";
    }
}

package cn.cruder.tools.http;


import cn.hutool.log.Log;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HttpClient 请求
 *
 * @author dousx
 */
public class HttpClientUtils {
    private static final Log log = Log.get(HttpClientUtils.class);

    private HttpClientUtils() {
    }

    public static String get(String url) throws IOException {
        HttpGet httpGet = new HttpGet();
        httpGet.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
        httpGet.setHeader("Connection", "keep-alive");
        String responseResult = null;
        try {
            httpGet.setURI(URI.create(url));
            HttpResponse response = HttpClientHelper.getSingleton().execute(httpGet);
            responseResult = getHttpEntityContent(response);
            httpGet.abort();
            return responseResult;
        } catch (IOException e) {
            log.error("Error", e);
            responseResult = e.getMessage();
            throw e;
        } finally {
            log.info("GET - url:{} ,responseResult:{}", java.net.URLDecoder.decode(url, String.valueOf(Consts.UTF_8)), responseResult);
        }
    }


    public static String get(String url, Map<String, String> urlParamMap) throws IOException {
        HttpGet httpGet = new HttpGet();
        httpGet.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
        httpGet.setHeader("Connection", "keep-alive");
        List<NameValuePair> formparams = setHttpParams(urlParamMap);
        String param = URLEncodedUtils.format(formparams, StandardCharsets.UTF_8);
        URI uri = URI.create(url + "?" + param);
        httpGet.setURI(uri);
        String responseResult = null;
        try {
            HttpResponse response = HttpClientHelper.getSingleton().execute(httpGet);
            responseResult = getHttpEntityContent(response);
            httpGet.abort();
            return responseResult;
        } catch (IOException e) {
            log.error("Error", e);
            responseResult = e.getMessage();
            throw e;
        } finally {
            log.info("GET - url:{} ,responseResult:{}", java.net.URLDecoder.decode(uri.toString(), String.valueOf(Consts.UTF_8)), responseResult);
        }
    }


    public static String post(String url, Map<String, String> urlParamMap) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> formparams = setHttpParams(urlParamMap);
        UrlEncodedFormEntity param = new UrlEncodedFormEntity(formparams, StandardCharsets.UTF_8);
        httpPost.setEntity(param);
        String responseResult = null;
        try {
            HttpResponse response = HttpClientHelper.getSingleton().execute(httpPost);
            responseResult = getHttpEntityContent(response);
            httpPost.abort();
            return responseResult;
        } catch (IOException e) {
            log.error("Error", e);
            responseResult = e.getMessage();
            throw e;
        } finally {
            log.info("POST -  url:{} ,responseResult:{}", java.net.URLDecoder.decode(url, String.valueOf(Consts.UTF_8)), responseResult);
        }

    }


    public static String post(String url, String reqBody) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "text/json; charset=utf-8");
        httpPost.setEntity(new StringEntity(URLEncoder.encode(reqBody, String.valueOf(StandardCharsets.UTF_8))));
        String responseResult = null;
        try {
            HttpResponse response = HttpClientHelper.getSingleton().execute(httpPost);
            responseResult = getHttpEntityContent(response);
            httpPost.abort();
            return responseResult;
        } catch (IOException e) {
            log.error("Error", e);
            responseResult = e.getMessage();
            throw e;
        } finally {
            log.info("POST -  url:{} ,responseResult:{}", java.net.URLDecoder.decode(url, String.valueOf(Consts.UTF_8)), responseResult);
        }
    }


    /**
     * 设置请求参数
     *
     * @param paramMap 参数
     * @return NameValuePair
     */
    private static List<NameValuePair> setHttpParams(Map<String, String> paramMap) {
        List<NameValuePair> valuePairs = new ArrayList<>();
        Set<Map.Entry<String, String>> set = paramMap.entrySet();
        for (Map.Entry<String, String> entry : set) {
            valuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return valuePairs;
    }

    /**
     * 获得响应HTTP实体内容
     *
     * @param response {@link  HttpResponse}
     * @return json
     */
    private static String getHttpEntityContent(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line + " ");
                line = br.readLine();
            }
            return sb.toString();
        }
        return "";
    }
}
package cn.cruder.tools.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @Author: cruder
 * @Date: 2022/04/06/14:19
 */
public class HttpClientHelper {
    /**
     * 单例
     */
    private static HttpClient SINGLETON;

    public HttpClientHelper() {
    }

    public static HttpClient getSingleton() {
        if (SINGLETON == null) {
            synchronized (HttpClientHelper.class) {
                if (SINGLETON == null) {
                    SINGLETON = buildDefaultHttpClient();
                }
            }
        }
        return SINGLETON;
    }

    /**
     * 构建默认
     *
     * @return
     */
    private static HttpClient buildDefaultHttpClient() {

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(3000);
        connectionManager.setDefaultMaxPerRoute(1000);

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }

}

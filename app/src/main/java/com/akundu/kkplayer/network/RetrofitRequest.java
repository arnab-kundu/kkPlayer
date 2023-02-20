package com.akundu.kkplayer.network;

import static org.apache.http.conn.ssl.AbstractVerifier.getDNSSubjectAlts;
import static java.util.regex.Pattern.matches;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequest {

    private static Retrofit retrofit;
    // BASE_URL was https://pwdown.com/11981/ its changed on 19/11/2022
    private final static String BASE_URL = "https://pwdown.info/11981/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
            okHttpClient.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Certificate[] certs;
                    try {
                        certs = session.getPeerCertificates();
                    } catch (SSLException e) {
                        return false;
                    }
                    X509Certificate x509 = (X509Certificate) certs[0];
                    // We can be case-insensitive when comparing the host we used to
                    // establish the socket to the hostname in the certificate.
                    String hostName = hostname.trim().toLowerCase(Locale.ENGLISH);
                    // Verify the first CN provided. Other CNs are ignored. Firefox, wget,
                    // curl, and Sun Java work this way.
                    String firstCn = getFirstCn(x509);
                    if (matches(hostName, firstCn)) {
                        return true;
                    }
                    for (String cn : getDNSSubjectAlts(x509)) {
                        if (matches(hostName, cn)) {
                            return true;
                        }
                    }
                    return false;

                }
            });
            //okHttpClient.build().hostnameVerifier().verify("pwdown.info",null);

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.level(HttpLoggingInterceptor.Level.BODY);
            //okHttpClient.connectionPool();
            //okHttpClient.readTimeout(3000, TimeUnit.MILLISECONDS);
            //okHttpClient.connectTimeout(3000, TimeUnit.MILLISECONDS);
            okHttpClient.addInterceptor(interceptor);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static String getFirstCn(X509Certificate cert) {
        String subjectPrincipal = cert.getSubjectX500Principal().toString();
        for (String token : subjectPrincipal.split(",")) {
            int x = token.indexOf("CN=");
            if (x >= 0) {
                return token.substring(x + 3);
            }
        }
        return null;
    }
}
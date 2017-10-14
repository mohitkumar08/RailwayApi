package com.example.mylibrary;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

/**
 * Created by bit on 10/10/17.
 */

class GzipInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.body() == null) {
            return response;
        }
        boolean isResponseGZipped = false;
        if (response.headers() != null && response.headers().names() != null && response.headers().size() > 0) {
            Headers header = response.headers();
            if (header.get("Content-Encoding") != null && header.get("Content-Encoding").equalsIgnoreCase("gzip")) {
                isResponseGZipped = true;
            }
        }
        if (isResponseGZipped) {

            Headers strippedHeaders = response.headers().newBuilder()
                    .removeAll("Content-Encoding")
                    .removeAll("Content-Length")
                    .build();

            GzipSource responseBody = new GzipSource(response.body().source());
            return response.newBuilder()
                    .headers(strippedHeaders)
                    .body(new RealResponseBody(strippedHeaders, Okio.buffer(responseBody)))
                    .build();
        } else {
            return response;
        }

    }
}

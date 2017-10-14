package com.example.mylibrary;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bit on 10/10/17.
 */


final class RestClient extends AsyncTask {
    private final WeakReference<Callback> callbackReference;
    private final String message;
    private final OkHttpClient okHttpClient;
    private final WeakReference<Context> contextReference;
    private ProgressDialog progressDialog;
    private final int request;
    private final String queryUrl;

    public RestClient(Context context, OkHttpClient okHttpClient, String queryUrl, int request, WeakReference<Callback> callbackReference, String message) {
        this.contextReference = new WeakReference<Context>(context);
        this.okHttpClient = okHttpClient;
        this.queryUrl = queryUrl;
        this.request = request;
        this.callbackReference = callbackReference;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (message != null) {
            setProgressDialog();
        }
    }

    @Deprecated
    private void setProgressDialog() {
        this.progressDialog = new ProgressDialog(contextReference.get());
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setMessage(message);
        this.progressDialog.setCancelable(false);
        this.progressDialog.onStart();
        this.progressDialog.show();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Object responseObject = null;
        try {
            if (HttpMethods.GET.ordinal() == request) {
                HttpUrl.Builder builder = new HttpUrl.Builder();
                Headers.Builder headers = new Headers.Builder();

                Request request = null;
                if (queryUrl.contains("http") || queryUrl.contains("https")) {
                    request = new Request.Builder().url(queryUrl).headers(headers.build()).get().build();
                } else {
                    HttpUrl url = builder.scheme("http").host(queryUrl).build();
                    request = new Request.Builder().url(url).headers(headers.build()).get().build();
                }

                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    responseObject = response.body().string();
                    response.body().close();
                    return responseObject;
                } else {
                    responseObject = response.body().string();
                    response.body().close();
                    return responseObject;
                }
            }
        } catch (Throwable e) {
            return e;
        }
        return responseObject;
    }

    @Override
    protected void onPostExecute(Object response) {
        super.onPostExecute(response);
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
            this.progressDialog.hide();
        }
        if (response != null && response instanceof Throwable && callbackReference.get() != null) {
            callbackReference.get().onError((Throwable) response);
            return;
        } else if (response != null && callbackReference.get() != null) {
            callbackReference.get().onSuccess(response);
            return;
        }

    }
}

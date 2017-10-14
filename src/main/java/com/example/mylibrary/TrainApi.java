package com.example.mylibrary;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public final class TrainApi {
    private static final String WEB_HOST_BASE_URL = "http://api.railwayapi.com/v2";
    private static final String LIVE_TRAIN_STATUS = "/live/train";
    private static final String API_KEY = "apikey";
    private static final String DATE_KEY = "date";
    private static final String FORWARD_SLASH = "/";
    private String mApiKey;
    private String mTrainNumber;
    private WeakReference<Callback> responseCallback;
    private boolean debug;
    private static final int TIMEOUT_TIME = 30;
    private OkHttpClient okHttpClientInstance;
    private static Application sAppContext;
    private static TrainApi sTrainAdiReference;
    private static Activity sCurrentActivity;


    synchronized public final static void initialize(Application context) {
        sAppContext = context;
        sAppContext.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        if (sTrainAdiReference == null) {
            sTrainAdiReference = new TrainApi();
        }
        YumSharedPref.initialize(context);

    }

    private static final Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            sCurrentActivity = activity;

            Log.e("onActivityCreated", "onActivityCreated" + activity.getLocalClassName());
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.e("onActivityStarted", "onActivityCreated" + activity.getLocalClassName());
        }

        @Override
        public void onActivityResumed(Activity activity) {
            sCurrentActivity = activity;
            Log.e("onActivityResumed", "onActivityCreated" + activity.getLocalClassName());
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    private TrainApi() {

    }

    private TrainApi(Builder builder) {
        this.mApiKey = builder.apiKey;
        this.mTrainNumber = builder.trainNumber;
        this.debug = builder.debug;
    }

    public String getmTrainNumber() {
        return mTrainNumber;
    }

    public boolean isDebug() {
        return debug;
    }

    public static class Builder {
        private String apiKey;
        private String trainNumber;
        private boolean debug;

        public Builder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder setTrainNumber(String trainNumber) {
            this.trainNumber = trainNumber;
            return this;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public TrainApi build() {
            return new TrainApi(this);
        }
    }

    public void setOkHttpClientInstance(OkHttpClient okHttpClientInstance) {
        this.okHttpClientInstance = okHttpClientInstance;
    }

    public void getLiveTrainStatus(String message, Callback callback) {

        this.responseCallback = new WeakReference(callback);
        if (okHttpClientInstance == null) {
            okHttpClientInstance = setWebClient();
        }
        String queryUrl = new StringBuilder().append(WEB_HOST_BASE_URL).append(LIVE_TRAIN_STATUS).append(FORWARD_SLASH).append(getmTrainNumber()).append(FORWARD_SLASH).append(DATE_KEY).append(FORWARD_SLASH).append("10-08-2017").append(FORWARD_SLASH).append(API_KEY).append(FORWARD_SLASH).append(mApiKey).toString();
        new RestClient(sCurrentActivity, okHttpClientInstance, queryUrl, HttpMethods.GET.ordinal(),  responseCallback, message).executeOnExecutor(Executors.newCachedThreadPool());

    }

    private OkHttpClient setWebClient() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(TIMEOUT_TIME, TimeUnit.SECONDS);
        builder.connectTimeout(TIMEOUT_TIME, TimeUnit.SECONDS);
        builder.writeTimeout(TIMEOUT_TIME, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        if (isDebug()) {
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        builder.addInterceptor(new GzipInterceptor());
        return builder.build();
    }
}

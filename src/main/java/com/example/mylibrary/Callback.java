package com.example.mylibrary;

/**
 * Created by bit on 10/10/17.
 */

public interface Callback {
    void onSuccess(Object data);

    void onError(Throwable e);
}

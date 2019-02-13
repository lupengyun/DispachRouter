package com.router.demo;

import android.app.Application;
import android.util.Log;

import com.router.annotation.Dispatcher;

/**
 * @author Lupy Create on 2019/2/13
 * @Description
 */
@Dispatcher
public class DemoApp implements com.router.dispatchapi.Dispatcher {
    @Override
    public void onCreate(Application application) {
        Log.e("demo", "demoapp init success");
    }
}

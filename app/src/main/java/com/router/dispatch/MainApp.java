package com.router.dispatch;

import android.app.Application;

import com.router.dispatchapi.DRouter;

/**
 * @author Lupy Create on 2019/2/13
 * @Description
 */
public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DRouter.init(this);
        DRouter.getInstance().onCreate(this);
    }
}

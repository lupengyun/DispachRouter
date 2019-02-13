package com.router.dispatchapi;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import com.router.annotation.RouterLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author Lupy Create on
 * @since 2019/2/13
 *
 */
public class DRouter {

    private static final String TAG = "router";

    private ArrayList<Dispatcher> dispatchers;

    private DRouter() {
        dispatchers = new ArrayList<>();
    }

    private static class Holder {
        public static DRouter instance = new DRouter();
    }

    public static DRouter getInstance() {
        return Holder.instance;
    }

    public static void init(Application application) {
        DRouter instance = getInstance();
        try {
            Log.d(TAG, "init start");
            instance.loadDispatcher(application);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "init fails");
        }
    }

    private void loadDispatcher(Application application) throws InterruptedException, IOException, PackageManager.NameNotFoundException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Set<String> generatorNames = ClassUtils.getFileNameByPackageName(application, "com.router.generator");
        if (generatorNames == null || generatorNames.isEmpty()) {
            Log.d(TAG, "no generator");
            return;
        }

        ArrayList<Class> classLoader = new ArrayList<>();
        for (String generator : generatorNames) {
            Log.d(TAG, "find class name is " + generator);
            if (generator.startsWith("com.router.generator.Dispatcher")) {
                Object generatorObject = Class.forName(generator).getConstructor().newInstance();
                if (generatorObject instanceof RouterLoader) {
                    RouterLoader loader = (RouterLoader) generatorObject;
                    loader.loadInto(classLoader);
                }
            }
        }

        for (Class aClass : classLoader) {
            Object loader = aClass.getConstructor().newInstance();
            if (loader instanceof Dispatcher) {
                dispatchers.add((Dispatcher) loader);
            }
        }

    }


    public void onCreate(Application application) {
        for (Dispatcher dispatcher : dispatchers) {
            dispatcher.onCreate(application);
        }
    }

}

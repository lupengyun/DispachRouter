package com.router.generator;

import com.router.annotation.RouterLoader;
import com.router.demo.DemoApp;
import java.lang.Class;
import java.lang.Override;
import java.util.ArrayList;

/**
 * do not edit 
 */
public class Dispatcher$demo implements RouterLoader {
  @Override
  public void loadInto(ArrayList<Class> dispatchers) {
    dispatchers.add(DemoApp.class);
  }
}

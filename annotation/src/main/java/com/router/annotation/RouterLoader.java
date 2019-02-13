package com.router.annotation;

import java.util.ArrayList;

/**
 * @author Lupy Create on 2019/2/13
 * @Description
 */
public interface RouterLoader {

    void loadInto(ArrayList<Class> dispatcher);

}

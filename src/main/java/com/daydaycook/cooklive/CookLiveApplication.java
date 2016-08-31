package com.daydaycook.cooklive;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by creekhan on 7/6/16.
 */
@Component(value = "liveApplication")
@Lazy(value = false)
public class CookLiveApplication implements ApplicationContextAware {


    private static ApplicationContext cookApplicationContext;


    public static <T> T getBean(Class<T> clas) {
        return cookApplicationContext.getBean(clas);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        cookApplicationContext = applicationContext;
    }
}

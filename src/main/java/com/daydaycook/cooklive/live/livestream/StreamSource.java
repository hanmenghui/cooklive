package com.daydaycook.cooklive.live.livestream;

import com.daydaycook.cooklive.live.livestream.qiniu.QiNiuStream;

/**
 * Created by creekhan on 7/7/16.
 */
public enum StreamSource {

    qiniu(QiNiuStream.class);

    private Class<?> sourceClass;

    StreamSource(Class<? extends AbstractLiveStream> sclass) {
        this.sourceClass = sclass;
    }

    public Class getSourceClass() {
        return this.sourceClass;
    }
}

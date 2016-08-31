package com.daydaycook.cooklive.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by creekhan on 7/13/16.
 */
public class URLEncoderHelper {

    private static Logger LOGGER = LoggerFactory.getLogger(URLEncoderHelper.class);

    public static String encode(String s, EncodeType encodeType) {
        try {
            return URLEncoder.encode(s, encodeType.typeName());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
        }
        return s;
    }


    public enum EncodeType {
        UTF8("UTF-8"), GB2312("GB2312"), GBK("GBK");

        private String typeName;

        EncodeType(String typeName) {
            this.typeName = typeName;
        }

        public String typeName() {
            return typeName;
        }

    }

}

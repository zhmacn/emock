package com.mzh.emock.log;

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class Logger {
    private static final String LOG_PREFIX="*************************============================================================*************************";
    private static final String LOG_SUFFIX="*************************============================================================*************************";
    private org.slf4j.Logger logger;
    public static Logger get(Class<?> clz){
        Logger support=new Logger();
        support.logger=LoggerFactory.getLogger(clz);
        return support;
    }
    private String format(String msg){
        return "\r\n"+LOG_PREFIX
                +"\r\n\t"
                +msg.replace("\r","\t\r")
                +"\r\n"
                +LOG_SUFFIX;
    }


    public void debug(String s) {
        logger.debug(format(s));
    }

    public void debug(String s, Object o) {
        logger.debug(format(s), o);
    }

    public void debug(Marker marker, String s) {
        logger.debug(marker, format(s));
    }

    public void debug(Marker marker, String s, Object o) {
        logger.debug(marker, format(s), o);
    }

    public void info(String s) {
        logger.info(format(s));
    }

    public void info(String s, Object o) {
        logger.info(format(s), o);
    }

    public void info(Marker marker, String s) {
        logger.info(marker, format(s));
    }

    public void info(Marker marker, String s, Object o) {
        logger.info(marker, format(s), o);
    }

    public void warn(String s) {
        logger.warn(format(s));
    }

    public void warn(String s, Object o) {
        logger.warn(format(s), o);
    }

    public void warn(Marker marker, String s) {
        logger.warn(marker, format(s));
    }

    public void warn(Marker marker, String s, Object o) {
        logger.warn(marker, format(s), o);
    }

    public void error(String s) {
        logger.error(format(s));
    }

    public void error(String s, Throwable o) {
        logger.error(format(s), o);
    }

    public void error(Marker marker, String s) {
        logger.error(marker, format(s));
    }

    public void error(Marker marker, String s, Object o) {
        logger.error(marker, format(s), o);
    }
}

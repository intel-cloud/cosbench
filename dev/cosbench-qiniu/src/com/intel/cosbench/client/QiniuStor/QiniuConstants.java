package com.intel.cosbench.client.QiniuStor;

public interface QiniuConstants {
    String AUTH_USERNAME_KEY = "accesskey";
    String AUTH_USERNAME_DEFAULT = "";
    
    String AUTH_PASSWORD_KEY = "secretkey";
    String AUTH_PASSWORD_DEFAULT = "";
    
    String QINIU_BUCKET_NAME = "bucket";
    String QINIU_BUCKET_NAME_DEFAULT = "";
    
    String QINIU_BUCKET_DOMAIN = "domain";
    String QINIU_BUCKET_DOMAIN_DEFAULT = "";
    
    String QINIU_UP_HOST = "up";
    String QINIU_UP_HOST_DEFAULT = "http://up.qiniu.com";
    
    String QINIU_IO_HOST = "io";
    String QINIU_IO_HOST_DEFAULT = "http://iovip.qbox.me";
    
    String QINIU_RS_HOST = "rs";
    String QINIU_RS_HOST_DEFAULT = "http://rs.qiniu.com";
    
    String QINIU_RSF_HOST = "rsf";
    String QINIU_RSF_HOST_DEFAULT = "http://rsf.qiniu.com";
    
    String QINIU_API_HOST = "api";
    String QINIU_API_HOST_DEFAULT = "http://api.qiniu.com";
}

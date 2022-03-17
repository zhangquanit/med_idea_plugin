package com.medlinker.idea.plugin.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 蒲公英上传结果
 *
 * @autho zhangquan
 */
public class PgyUploadResult {
    public int code;
    public String message;
    public UploadData data;

    public static class UploadData {
        @SerializedName("buildName")
        public String appName;
        @SerializedName("buildVersion")
        public String appVersion;
        @SerializedName("buildUpdateDescription")
        public String description;
        @SerializedName("buildQRCodeURL")
        public String qrcode;
        @SerializedName("buildUpdated")
        public String buildTime;
        @SerializedName("buildBuildVersion")
        public String buildBuildVersion;
    }
}

package com.opitzconsulting.soa.mdsviewer.pojo;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class File {
    private String path;
    private String type;
    private String fullpath;
    private long versionCount;
    private String lastModified;

    private static DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public static final String FOLDER = "folder";
    public static final String FILE = "file";

    public File(String path, String type, String fullpath, long versionCount, Date lastModified) {
        this.path = path;
        this.type = type;
        this.fullpath = fullpath;
        this.versionCount = versionCount;
        this.lastModified = format.format(lastModified);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullpath() {
        return fullpath;
    }

    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }

    public long getVersionCount() {
        return versionCount;
    }

    public void setVersionCount(long versionCount) {
        this.versionCount = versionCount;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = format.format(lastModified);
    }
}

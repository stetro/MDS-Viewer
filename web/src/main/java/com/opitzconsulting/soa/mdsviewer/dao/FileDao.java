package com.opitzconsulting.soa.mdsviewer.dao;

import com.opitzconsulting.soa.mdsviewer.pojo.File;
import com.opitzconsulting.soa.mdsviewer.util.Node;
import difflib.Delta;
import oracle.mds.naming.InvalidReferenceException;
import oracle.mds.naming.InvalidReferenceTypeException;

import java.util.List;
import java.util.Map;

public interface FileDao {
    Node<File> getAllFilesAsTreeFrom(String root) throws Exception;

    List<File> getAllFilesAsListFrom(String root) throws Exception;

    String getFileByPath(String path) throws Exception;

    void deleteFile(String path) throws InvalidReferenceTypeException, InvalidReferenceException, Exception;

    void updateFile(String path, String code) throws Exception;

    Map<Long, String> getFileVersions(String path) throws Exception;

    String getFileByPath(String path, Long version) throws Exception;

    void createFile(String path, String code);

    void deleteFolder(String path) throws Exception;

    void createFolder(String path) throws Exception;

    List<Delta> getListOfDeltas(String path, long first, long second) throws Exception;
}

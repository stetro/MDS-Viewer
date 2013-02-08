package com.opitzconsulting.soa.mdsviewer.controller;

import com.opitzconsulting.soa.mdsviewer.dao.MdsExtractorFileDao;
import com.opitzconsulting.soa.mdsviewer.pojo.File;
import com.opitzconsulting.soa.mdsviewer.util.Node;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileViewerControllerTest {
    private MdsExtractorFileDao dao;
    private FileViewerController controller;

    @Before
    public void setupMocksAndController() {
        dao = mock(MdsExtractorFileDao.class);
        controller = new FileViewerController(dao);
    }

    @Test
    public void fileRequestAsDownload() throws Exception {
        when(dao.getFileByPath(anyString())).thenReturn("OK");
        Assert.assertEquals("OK", controller.downloadFile("Foo"));
    }

    @Test
    public void fileRequestAsHtml() throws Exception {
        when(dao.getFileByPath(anyString())).thenReturn("OK");
        Assert.assertEquals("OK", controller.getFile("Foo"));
    }

    @Test
    public void fileTreeFromRoot() throws Exception {
        Node<File> node = new Node<File>(new File("/", "folder", "/", 0, new Date()));
        when(dao.getAllFilesAsTreeFrom(anyString())).thenReturn(node);
        Assert.assertEquals(controller.getAllFilesAsTree("/"), node);
    }
}

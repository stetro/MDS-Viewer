package com.opitzconsulting.soa.mdsviewer.controller;


import com.opitzconsulting.soa.mdsviewer.dao.FileDao;
import com.opitzconsulting.soa.mdsviewer.pojo.File;
import com.opitzconsulting.soa.mdsviewer.util.Node;
import difflib.Delta;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class FileViewerController {

    private static Logger logger = Logger.getLogger(FileViewerController.class);

    private FileDao fileDao;

    @Autowired
    public FileViewerController(FileDao fileDao) {
        this.fileDao = fileDao;
    }

    @RequestMapping(value = "/tree/", method = RequestMethod.GET)
    public
    @ResponseBody
    Node<File> getAllFilesAsTree(@RequestParam(value = "root") String root) throws Exception {
        return fileDao.getAllFilesAsTreeFrom(root);
    }

    @RequestMapping(value = "/file/", method = RequestMethod.GET)
    public
    @ResponseBody
    String getFile(@RequestParam(value = "path") String path) throws Exception {
        return fileDao.getFileByPath(path);
    }

    @RequestMapping(value = "/file/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getFileWithVersion(@RequestParam(value = "path") String path, @PathVariable String id) throws Exception {
        return fileDao.getFileByPath(path, Long.parseLong(id));
    }

    @RequestMapping(value = "/file/versions/", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<Long, String> getFileVersions(@RequestParam(value = "path") String path) throws Exception {
        return fileDao.getFileVersions(path);
    }

    @RequestMapping(value = "/file/download/", method = RequestMethod.GET, produces = "application/xml")
    public
    @ResponseBody
    String downloadFile(@RequestParam(value = "path") String path) throws Exception {
        return fileDao.getFileByPath(path);
    }

    @RequestMapping(value = "/file/download/{id}", method = RequestMethod.GET, produces = "application/xml")
    public
    @ResponseBody
    String downloadFileWithVersion(@RequestParam(value = "path") String path, @PathVariable String id) throws Exception {
        return fileDao.getFileByPath(path, Long.parseLong(id));
    }

    @RequestMapping(value = "/file/", method = RequestMethod.DELETE)
    public
    @ResponseBody
    String deleteFile(@RequestParam(value = "path") String path) throws Exception {
        fileDao.deleteFile(path);
        return "{\"status\":\"success\"}";
    }

    @RequestMapping(value = "/folder/", method = RequestMethod.DELETE)
    public
    @ResponseBody
    String deleteFolder(@RequestParam(value = "path") String path) throws Exception {
        fileDao.deleteFolder(path);
        return "{\"status\":\"success\"}";
    }

    @RequestMapping(value = "/file/", method = RequestMethod.POST)
    public
    @ResponseBody
    String updateFile(@RequestParam(value = "path") String path, @RequestParam(value = "data") String code) throws Exception {
        fileDao.updateFile(path, code);
        return "{\"status\":\"success\"}";
    }

    @RequestMapping(value = "/file/new/", method = RequestMethod.POST)
    public
    @ResponseBody
    String createFile(@RequestParam(value = "path") String path, @RequestParam(value = "data") String code) throws Exception {
        fileDao.createFile(path, code);
        return "{\"status\":\"success\"}";
    }

    @RequestMapping(value = "/folder/new/", method = RequestMethod.POST)
    public
    @ResponseBody
    String createFolder(@RequestParam(value = "path") String path) throws Exception {
        fileDao.createFolder(path);
        return "{\"status\":\"success\"}";
    }

    @RequestMapping(value = "/file/diff/", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Delta> getDiff(@RequestParam(value = "path") String path, @RequestParam(value = "firstVersion") String firstVersion, @RequestParam(value = "secondVersion") String secondVersion) throws Exception {
        return fileDao.getListOfDeltas(path, Long.parseLong(firstVersion), Long.parseLong(secondVersion));
    }


    @ExceptionHandler(Exception.class)
    public
    @ResponseBody
    String handleException(Exception e) {
        return "{\"error\":\"" + e.getMessage() + "\"}";
    }
}

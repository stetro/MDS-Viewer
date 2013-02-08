package com.opitzconsulting.soa.mdsviewer.dao;

import com.opitzconsulting.soa.mdsviewer.pojo.File;
import com.opitzconsulting.soa.mdsviewer.util.ApplicationProperties;
import com.opitzconsulting.soa.mdsviewer.util.DiffGenerator;
import com.opitzconsulting.soa.mdsviewer.util.MdsExtractor;
import com.opitzconsulting.soa.mdsviewer.util.Node;
import difflib.Delta;
import oracle.mds.naming.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.*;

@Component
public class MdsExtractorFileDao implements FileDao {

    private static Logger logger = Logger.getLogger(MdsExtractorFileDao.class);
    private static DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    @Override
    public String getFileByPath(String path) throws Exception {
        MdsExtractor mdsExtractor = getMdsExtractor();
        logger.info("Datei " + path + " wird abgerufen ...");
        return mdsExtractor.readFile(DocumentName.create(path));
    }

    @Override
    public void deleteFile(String path) throws Exception {
        MdsExtractor mdsExtractor = getMdsExtractor();
        logger.info("Datei " + path + " wird gelöscht ....");
        mdsExtractor.deleteFile(DocumentName.create(path));
    }

    @Override
    public void updateFile(String path, String code) throws Exception {
        MdsExtractor mdsExtractor = getMdsExtractor();
        logger.info("Datei " + path + " wird bearbeitet ....");
        mdsExtractor.updateFile(DocumentName.create(path), code);
    }

    @Override
    public Map<Long, String> getFileVersions(String path) throws Exception {
        MdsExtractor mdsExtractor = getMdsExtractor();
        logger.info("Versionen von " + path + " werden geladen ....");
        return mdsVersionsToString(mdsExtractor.getVersions(DocumentName.create(path)));
    }

    @Override
    public String getFileByPath(String path, Long version) throws Exception {
        MdsExtractor mdsExtractor = getMdsExtractor();
        logger.info("Datei " + path + " wird abgerufen mit Version " + version + " ...");
        return mdsExtractor.readFile(DocumentName.create(path), version);
    }

    @Override
    public void createFile(String path, String code) {
        MdsExtractor mdsExtractor = getMdsExtractor();
        logger.info("Datei " + path + " wird erzeugt ...");
        mdsExtractor.createFile(path, code);
    }

    @Override
    public void deleteFolder(String path) throws Exception {
        MdsExtractor mdsExtractor = getMdsExtractor();
        logger.info("Ordner " + path + " wird gelöscht ...");
        mdsExtractor.deleteFolder(PackageName.createPackageName(path));
    }

    @Override
    public void createFolder(String path) throws Exception {
        MdsExtractor mdsExtractor = getMdsExtractor();
        logger.info("Ordner " + path + " wird erzeugt ...");
        mdsExtractor.createFolder(PackageName.createPackageName(path));
    }

    @Override
    public List<Delta> getListOfDeltas(String path, long first, long second) throws Exception {
        String firstString = getFileByPath(path, first);
        String secondString = getFileByPath(path, second);
        DiffGenerator diffGenerator = new DiffGenerator();
        return diffGenerator.getDeltasBetween(firstString, secondString);
    }

    @Override
    public Node<File> getAllFilesAsTreeFrom(String root) throws Exception {
        MdsExtractor mdsExtractor = getMdsExtractor();
        logger.info("Daten von " + root + " werden abgerufen ...");
        PackageName packageName = PackageName.createPackageName(root);
        Node<ResourceName> resourceTree = mdsExtractor.queryMDSAsTree(packageName);
        return resourceTreeToFileTree(resourceTree, mdsExtractor);
    }

    @Override
    public List<File> getAllFilesAsListFrom(String root) throws Exception {
        MdsExtractor mdsExtractor = getMdsExtractor();
        List<File> fileList = new ArrayList<File>();

        logger.info("Daten von " + root + " werden als Liste abgerufen ...");
        List<ResourceName> resourceNames = mdsExtractor.queryMDS(PackageName.createPackageName("/"));
        for (ResourceName r : resourceNames) {
            fileList.add(resourceNameToFile(r, mdsExtractor));
        }

        return fileList;
    }

    private Map<Long, String> mdsVersionsToString(HashMap<Long, Date> versions) {
        Map<Long, String> longStingMap = new HashMap<Long, String>();
        for (Map.Entry<Long, Date> e : versions.entrySet()) {
            longStingMap.put(e.getKey(), format.format(e.getValue()));
        }
        return longStingMap;
    }

    private File resourceNameToFile(ResourceName resourceName, MdsExtractor mdsExtractor) throws InvalidReferenceTypeException, InvalidReferenceException {
        File file;
        String absoluteName = resourceName.getAbsoluteName();
        String localName = resourceName.getLocalName();
        if (resourceName instanceof PackageName) {
            file = new File(localName, File.FOLDER, absoluteName, 0, new Date());
        } else {
            DocumentName documentName = DocumentName.create(absoluteName);
            long versionCount = mdsExtractor.getVersionCount(documentName);
            Date lastModified = mdsExtractor.getLastModified(documentName);
            file = new File(localName, File.FILE, absoluteName, versionCount, lastModified);
        }
        return file;
    }

    private MdsExtractor getMdsExtractor() {
        String user = ApplicationProperties.getProperty("mds.user");
        String password = ApplicationProperties.getProperty("mds.password");
        String jdbc = ApplicationProperties.getProperty("mds.jdbc");
        String schema = ApplicationProperties.getProperty("mds.schema");
        MdsExtractor mdsExtractor = new MdsExtractor(user, password, jdbc, schema);
        logger.info("MdsExtractor erfolgreich erstellt !");
        return mdsExtractor;
    }

    private Node<File> resourceTreeToFileTree(Node<ResourceName> resourceTree, MdsExtractor mdsExtractor) throws InvalidReferenceTypeException, InvalidReferenceException {
        Node<File> fileNode = new Node<File>(resourceNameToFile(resourceTree.getData(), mdsExtractor));
        for (Node<ResourceName> resourceNameNode : resourceTree.getChildren()) {
            Node<File> newNode = new Node<File>(resourceNameToFile(resourceNameNode.getData(), mdsExtractor));
            if (resourceNameNode.getData() instanceof PackageName) {
                newNode.addChildrenOf(resourceTreeToFileTree(resourceNameNode, mdsExtractor));
            }
            fileNode.addChild(newNode);
        }
        return fileNode;
    }
}

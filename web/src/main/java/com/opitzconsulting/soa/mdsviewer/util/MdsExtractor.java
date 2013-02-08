package com.opitzconsulting.soa.mdsviewer.util;

import oracle.mds.config.CustConfig;
import oracle.mds.core.*;
import oracle.mds.exception.InvalidNamespaceException;
import oracle.mds.naming.*;
import oracle.mds.persistence.*;
import oracle.mds.query.*;
import oracle.mds.versioning.Version;
import oracle.mds.versioning.VersionInfo;
import oracle.mds.versioning.persistence.PDocumentVersionSupport;
import oracle.wsm.mds.MDSAccessException;
import oracle.wsm.mds.MDSDBStoreInitializer;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.*;

/**
 * Little helper class to extract files out of the MDS.
 * To do so a connection to the mds database has to be acquired, so the db user and password are needed.
 */
public class MdsExtractor {

    private final MDSDBStoreInitializer storeInitializer;

    public MdsExtractor(String user, String password, String jdbc, String partition) {
        storeInitializer = new MDSDBStoreInitializer(user, password, jdbc, partition);

    }

    public Node<ResourceName> queryMDSAsTree(PackageName packageName) {
        Node<ResourceName> tree = new Node<ResourceName>(packageName);
        List<ResourceName> resourceNames = queryMDS(packageName);
        for (ResourceName r : resourceNames) {
            Node<ResourceName> newNode = new Node<ResourceName>(r);
            if (r instanceof PackageName) {
                newNode.addChildrenOf(queryMDSAsTree((PackageName) r));
            }
            tree.addChild(newNode);
        }
        return tree;
    }

    public List<ResourceName> queryMDS(PackageName packageName) {
        MDSInstance mdsInstance;
        List<ResourceName> resources = new ArrayList<ResourceName>();
        try {
            mdsInstance = storeInitializer.getMDSInstance();
            packageName = (packageName == null) ? PackageName.createPackageName("/") : packageName;
            NameCondition condition = ConditionFactory.createNameCondition(packageName.getAbsoluteName(), "%");
            ResourceQuery query = QueryFactory.createResourceQuery(mdsInstance, condition);

            Iterator<QueryResult> contents = query.execute();
            if (contents == null) {
                return resources;
            }
            while (contents.hasNext()) {
                QueryResult result = contents.next();
                if (result.getResultType() == QueryResult.ResultType.PACKAGE_RESULT) {
                    PackageResult pack = (PackageResult) result;
                    resources.add(pack.getPackageName());
                } else {
                    DocumentResult doc = (DocumentResult) result;
                    resources.add(doc.getDocumentName());
                }
            }
            return resources;
        } catch (MDSAccessException e) {
            throw new IllegalArgumentException("Could not connect to MDS, check login data", e);
        } catch (InvalidReferenceException e) {
            throw new IllegalArgumentException("Could not find/read " + packageName, e);
        } catch (InvalidReferenceTypeException e) {
            throw new IllegalArgumentException("No correct type!", e);
        }
    }

    public String readFile(DocumentName documentName) {
        try {
            PDocument pdocument = getPDocument(documentName, (long) 0);
            InputSource source = pdocument.read();
            Reader reader = source.getCharacterStream();
            if (reader != null) {
                return read(reader);
            } else {
                InputStream inputStream = source.getByteStream();
                return stream(inputStream);
            }
        } catch (MDSIOException e) {
            throw new IllegalArgumentException("Could not connect to MDS, check login data", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read ", e);
        }
    }

    public void deleteFile(ResourceName path) {
        try {
            MDSSession session = getMdsSession();
            PTransaction transaction = getpTransaction(session);
            PManager pManager = storeInitializer.getMDSInstance().getPersistenceManager();
            PContext pContext = session.getPContext();
            if (path instanceof PackageName) {
                PPackage ppackage = pManager.getPackage(pContext, (PackageName) path);
                transaction.deletePackage(ppackage, true);
            } else {
                PDocument pdocument = pManager.getDocument(pContext, (DocumentName) path);
                transaction.deleteDocument(pdocument, true);
            }
            session.flushChanges();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not remove file ", e);
        }
    }

    public boolean isAvailable(ResourceName documentName) {
        try {
            MDSSession session = getMdsSession();
            PManager pManager = storeInitializer.getMDSInstance().getPersistenceManager();
            PContext pContext = session.getPContext();
            if (documentName instanceof PackageName) {
                PPackage ppackage = pManager.getPackage(pContext, (PackageName) documentName);
                if (ppackage == null) return false;
            } else {
                PDocument pdocument = pManager.getDocument(pContext, (DocumentName) documentName);
                if (pdocument == null) return false;
            }
        } catch (InvalidNamespaceException e) {
            return false;
        } catch (MDSAccessException e) {
            return false;
        }
        return true;
    }

    public void updateFile(DocumentName documentName, String content) {
        try {
            MDSSession session = getMdsSession();
            PTransaction transaction = getpTransaction(session);
            PManager pManager = storeInitializer.getMDSInstance().getPersistenceManager();
            PContext pContext = session.getPContext();
            PDocument pdocument = pManager.getDocument(pContext, documentName);
            InputSource is = new InputSource(new StringReader(content));
            transaction.saveDocument(pdocument, true, is);
            session.flushChanges();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not edit file ", e);
        }
    }

    public void createFile(String path, String content) {
        try {
            MDSSession session = getMdsSession();
            PTransaction transaction = getpTransaction(session);
            DocumentName mdsDocumentName = DocumentName.create(path);
            InputSource is = new InputSource(new StringReader(content));
            transaction.createDocument(mdsDocumentName, is);
            session.flushChanges();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not add new file ", e);
        }
    }

    public HashMap<Long, Date> getVersions(DocumentName documentName) {
        HashMap<Long, Date> hashMap = new HashMap<Long, Date>();
        try {
            PDocument pdocument = getpDocument(documentName);
            for (Version v : pdocument.getVersionSupport().getVersionInfo().listVersions()) {
                hashMap.put(v.getVersionNumber(), new Date(v.getCreationTime()));
            }
        } catch (MDSAccessException e) {
            throw new IllegalArgumentException("Could not list Versions !" + e.getMessage());
        }
        return hashMap;
    }

    public long getVersionCount(DocumentName documentName) {
        try {
            PDocument pdocument = getpDocument(documentName);
            return pdocument.getVersionSupport().getVersionInfo().listVersions().size();
        } catch (MDSAccessException e) {
            return 0;
        }
    }

    public void createFolder(PackageName packageName) {
        try {
            MDSSession mdsSession = getMdsSession();
            PTransaction transaction = getpTransaction(mdsSession);
            transaction.createPackage(packageName);
            mdsSession.flushChanges();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create Folder ! " + e.getMessage());
        }
    }

    public String readFile(DocumentName documentName, Long key) {
        try {
            PDocument pdocument = getPDocument(documentName, key);
            InputSource source = pdocument.read();
            Reader reader = source.getCharacterStream();
            if (reader != null) {
                return read(reader);
            } else {
                InputStream inputStream = source.getByteStream();
                return stream(inputStream);
            }
        } catch (MDSIOException e) {
            throw new IllegalArgumentException("Could not connect to MDS, check login data", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read ", e);
        }
    }

    private MDSSession getMdsSession() throws MDSAccessException {
        return createSession(storeInitializer.getMDSInstance());
    }

    private PTransaction getpTransaction(MDSSession session) {
        return session.getPTransaction();
    }

    private PDocument getpDocument(DocumentName documentName) throws MDSAccessException {
        MDSSession session = getMdsSession();
        PManager pManager = storeInitializer.getMDSInstance().getPersistenceManager();
        PContext pContext = session.getPContext();
        return pManager.getDocument(pContext, documentName);
    }

    private String stream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    private String read(Reader in) throws IOException {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[1048];
        while (true) {
            int nBytes = in.read(buffer);
            if (nBytes == -1)
                break;
            builder.append(new String(buffer, 0, nBytes));
        }
        return builder.toString();
    }

    private MDSSession createSession(MDSInstance instance) {
        SessionOptions sessionOptions =
                new SessionOptions(IsolationLevel.READ_COMMITTED, Locale.getDefault(), CustConfig.NO_CUSTOMIZATIONS);
        UserStateHandler userStateHandler = new UserStateHandler() {
            public Object getAttribute(String string) {
                return null;
            }

            public void setAttribute(String string, Object object) {
            }
        };
        return instance.createSession(sessionOptions, userStateHandler);
    }

    private PDocument getPDocument(DocumentName documentName, Long key) {
        try {
            MDSInstance instance = storeInitializer.getMDSInstance();
            MDSSession session = createSession(instance);
            PManager pManager = instance.getPersistenceManager();
            PContext pContext = session.getPContext();
            if (key > 0) {
                return pManager.getVersionSupport().getDocument(pContext, documentName, key);
            } else {
                return pManager.getDocument(pContext, documentName);
            }
        } catch (MDSAccessException e) {
            throw new IllegalArgumentException("Could not read ", e);
        }
    }

    public void deleteFolder(PackageName packageName) {
        deleteFile(packageName);
    }

    public Date getLastModified(DocumentName documentName) {
        try {
            PDocument pdocument = getpDocument(documentName);
            PDocumentVersionSupport versionSupport = pdocument.getVersionSupport();
            VersionInfo versionInfo = versionSupport.getVersionInfo();
            Version version = versionInfo.getVersion();
            return new Date(version.getCreationTime());
        } catch (MDSAccessException e) {
            return new Date();
        }
    }
}

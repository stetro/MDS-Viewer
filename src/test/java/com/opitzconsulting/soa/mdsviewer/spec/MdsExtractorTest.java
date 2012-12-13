package com.opitzconsulting.soa.mdsviewer.spec;

import com.opitzconsulting.soa.mdsviewer.util.ApplicationProperties;
import com.opitzconsulting.soa.mdsviewer.util.MdsExtractor;
import com.opitzconsulting.soa.mdsviewer.util.Node;
import junit.framework.Assert;
import oracle.mds.naming.*;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Arrays.asList;

public class MdsExtractorTest extends AbstractMdsSpec {

    public static final String b2bAck = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<Types xmlns=\"http://xmlns.oracle.com/integration/b2b/profile\">\n" +
            "<IdentificationTypes>\n" +
            "<IdentificationType exchangeIdentifier=\"true\" id=\"itype-Name\" name=\"Name\" nameType=\"true\"/>\n" +
            "<IdentificationType exchangeIdentifier=\"true\" id=\"itype-Generic\" name=\"Generic Identifier\" nameType=\"false\"/>\n" +
            "<IdentificationType exchangeIdentifier=\"true\" id=\"itype-DUNS\" name=\"DUNS\" nameType=\"false\"/>\n" +
            "<IdentificationType exchangeIdentifier=\"true\" id=\"itype-ebMS\" name=\"ebMS Identifier\" nameType=\"false\"/>\n" +
            "<IdentificationType exchangeIdentifier=\"true\" id=\"itype-AS2\" name=\"AS2 Identifier\" nameType=\"false\"/>\n" +
            "<IdentificationType exchangeIdentifier=\"true\" id=\"itype-MLLP\" name=\"MLLP ID\" nameType=\"false\"/>\n" +
            "<IdentificationType exchangeIdentifier=\"true\" id=\"itype-AS1\" name=\"AS1 Identifier\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-InterchangeID\" name=\"EDI Interchange ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-InterchangeIDQualifier\" name=\"EDI Interchange ID Qualifier\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-GroupID\" name=\"EDI Group ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-InterchangeInternalID\" name=\"EDI Interchange Internal ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-InterchangeInternalSubID\" name=\"EDI Interchange Internal Sub ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-GroupIDQualifier\" name=\"EDI Group ID Qualifier\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Message-ApplicationID\" name=\"HL7 Message Application ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Message-ApplicationUniversalID\" name=\"HL7 Message Application Universal ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Message-ApplicationUniversalIDType\" name=\"HL7 Message Application Universal ID Type\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Message-FacilityID\" name=\"HL7 Message Facility ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Message-FacilityUniversalID\" name=\"HL7 Message Facility Universal ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Message-FacilityUniversalIDType\" name=\"HL7 Message Facility Universal ID Type\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Batch-ApplicationID\" name=\"HL7 Batch Application ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Batch-ApplicationUniversalID\" name=\"HL7 Batch Application Universal ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Batch-ApplicationUniversalIDType\" name=\"HL7 Batch Application Universal ID Type\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Batch-FacilityID\" name=\"HL7 Batch Facility ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Batch-FacilityUniversalID\" name=\"HL7 Batch Facility Universal ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-Batch-FacilityUniversalIDType\" name=\"HL7 Batch Facility Universal ID Type\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-File-ApplicationID\" name=\"HL7 File Application ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-File-ApplicationUniversalID\" name=\"HL7 File Application Universal ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-File-ApplicationUniversalIDType\" name=\"HL7 File Application Universal ID Type\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-File-FacilityID\" name=\"HL7 File Facility ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-File-FacilityUniversalID\" name=\"HL7 File Facility Universal ID\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-File-FacilityUniversalIDType\" name=\"HL7 File Facility Universal ID Type\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-TradacomsName\" name=\"TRADACOMS Name\" nameType=\"false\"/>\n" +
            "<IdentificationType id=\"itype-TradacomsCode\" name=\"TRADACOMS Code\" nameType=\"false\"/>\n" +
            "</IdentificationTypes>\n" +
            "<ContactTypes>\n" +
            "<ContactType description=\"Contact Name\" id=\"ctype-ContactName\" name=\"Contact Name\" notifyType=\"false\"/>\n" +
            "<ContactType description=\"Phone Number\" id=\"ctype-Phone\" name=\"Phone\" notifyType=\"false\"/>\n" +
            "<ContactType description=\"Email Address\" id=\"ctype-Email\" name=\"Email\" notifyType=\"true\"/>\n" +
            "<ContactType description=\"Fax Number\" id=\"ctype-Fax\" name=\"Fax\" notifyType=\"false\"/>\n" +
            "<ContactType description=\"SMS Number\" id=\"ctype-SMS\" name=\"SMS\" notifyType=\"true\"/>\n" +
            "</ContactTypes>\n" +
            "</Types>\n";

    private MdsExtractor extractor;


    @Before
    public void buildUpExtractorFromProperties() {
        String user = ApplicationProperties.getProperty("mds.user");
        String password = ApplicationProperties.getProperty("mds.password");
        String jdbc = ApplicationProperties.getProperty("mds.jdbc");
        String schema = ApplicationProperties.getProperty("mds.schema");
        extractor = new MdsExtractor(user, password, jdbc, schema);
    }

    @Test
    public void testRemovingFolder() throws InvalidReferenceTypeException, InvalidReferenceException {
        String path = "/newFolder";
        PackageName packageName = PackageName.createPackageName(path);
        extractor.createFolder(packageName);
        extractor.deleteFolder(packageName);
        Assert.assertFalse(extractor.isAvailable(packageName));
    }

    @Test
    public void testAddingFolder() throws InvalidReferenceTypeException, InvalidReferenceException {
        String path = "/newFolder";
        PackageName packageName = PackageName.createPackageName(path);
        extractor.createFolder(packageName);
        Assert.assertTrue(extractor.isAvailable(packageName));
        extractor.deleteFolder(packageName);
    }

    @Test
    public void testGetFileWithVersion() throws InvalidReferenceTypeException, InvalidReferenceException {
        String path = "/test7.xml";
        String content = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<head/>\n";
        String content2 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<head>foo</head>\n";
        List<String> readContent = new ArrayList<String>();
        DocumentName documentName = DocumentName.create(path);

        extractor.createFile(path, content);
        extractor.updateFile(documentName, content2);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        HashMap<Long, Date> versions = extractor.getVersions(documentName);
        for (Map.Entry e : versions.entrySet()) {
            readContent.add(extractor.readFile(documentName, (Long) e.getKey()));
        }
        Assert.assertEquals(versions.size(), 2);
        Assert.assertEquals(readContent, asList(content, content2));
        extractor.deleteFile(documentName);
    }

    @Test
    public void testGetVersions() throws InvalidReferenceTypeException, InvalidReferenceException {
        String path = "/test6.xml";
        extractor.createFile(path, "<head/>");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        DocumentName documentName = DocumentName.create(path);
        extractor.updateFile(documentName, "<head>foo</head>");
        HashMap<Long, Date> versionHashMap = extractor.getVersions(documentName);
        Assert.assertEquals(extractor.getVersions(documentName).size(), 2);
        extractor.deleteFile(documentName);
    }

    @Test
    public void testGetVersionCount() throws InvalidReferenceTypeException, InvalidReferenceException {
        String path = "/test5.xml";
        extractor.createFile(path, "<head/>");
        DocumentName documentName = DocumentName.create(path);
        extractor.updateFile(documentName, "<head>foo</head>");
        Assert.assertEquals(extractor.getVersionCount(documentName), 2);
        extractor.deleteFile(documentName);
    }

    @Test
    public void extractSomething() throws Exception {
        String b2bMdsPath = "/soa/b2b/types.xml";
        String content = extractor.readFile(DocumentName.create(b2bMdsPath));
        Assert.assertEquals(b2bAck, content);
    }

    @Test
    public void getRootListOfMDS() throws InvalidReferenceTypeException, InvalidReferenceException {
        List<ResourceName> resourceNames = extractor.queryMDS(PackageName.createPackageName("/"));
        Assert.assertNotNull(resourceNames);
        Assert.assertEquals(2, resourceNames.size());
    }

    @Test
    public void getTreeOfAnPackage() throws InvalidReferenceTypeException, InvalidReferenceException {
        Node<ResourceName> resourceNameTree = extractor.queryMDSAsTree(PackageName.createPackageName("/soa/shared/mediator/"));
        Assert.assertEquals(2, resourceNameTree.getChildren().size());
        Assert.assertEquals(9, resourceNameTree.depth());
    }


    @Test(expected = InvalidReferenceTypeException.class)
    public void extractingNotExistingContentShouldThrowException() throws InvalidReferenceTypeException, InvalidReferenceException {
        extractor.readFile(DocumentName.create("ABC"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongLoginDataShouldThrowExceptionDuringRead() throws InvalidReferenceTypeException, InvalidReferenceException {
        MdsExtractor mdsExtractor = new MdsExtractor("", "", "", "");
        mdsExtractor.readFile(DocumentName.create("/apps/OAIMigration/xsd/XXOC_OPC.ASD.xsd"));
    }

    @Test
    public void testAddingNewFile() throws InvalidReferenceTypeException, InvalidReferenceException {
        String path = "/unittest1.xsd";
        String content = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<test/>\n";
        extractor.createFile(path, content);
        Assert.assertEquals(extractor.readFile(DocumentName.create(path)), content);
        extractor.deleteFile(DocumentName.create(path));
    }

    @Test
    public void testRemovingFile() throws InvalidReferenceTypeException, InvalidReferenceException {
        String path = "/unittest2.xsd";
        extractor.createFile(path, "<foo></foo>");
        org.junit.Assert.assertTrue(extractor.isAvailable(DocumentName.create(path)));
        extractor.deleteFile(DocumentName.create(path));
        org.junit.Assert.assertFalse(extractor.isAvailable(DocumentName.create(path)));
    }

    @Test
    public void testEditingFile() throws InvalidReferenceTypeException, InvalidReferenceException {
        String path = "/unittest3.xsd";
        extractor.createFile(path, "<foo></foo>");
        String content = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<test/>\n";
        DocumentName documentName = DocumentName.create(path);
        extractor.updateFile(documentName, content);
        Assert.assertEquals(extractor.readFile(documentName), content);
        extractor.deleteFile(documentName);
    }

    @Test(expected = Exception.class)
    public void testEditingFileWithNotValidXml() throws InvalidReferenceTypeException, InvalidReferenceException {
        String path = "/unittest4.xsd";
        extractor.createFile(path, "<foo>asd</foo>asd");
    }
}

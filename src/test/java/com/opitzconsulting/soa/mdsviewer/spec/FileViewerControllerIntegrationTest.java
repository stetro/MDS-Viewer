package com.opitzconsulting.soa.mdsviewer.spec;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: str
 * Date: 29.11.12
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public class FileViewerControllerIntegrationTest {
    @Test
    public void testSimplePostRequest() throws IOException {
        HttpClient client = new HttpClient();
        client.getParams().setParameter("http.useragent", "Test Client");

        BufferedReader br = null;

        PostMethod method = new PostMethod("http://10.1.20.71:8080/api/file/");
        method.addParameter("path", "<fooobar/>");
        method.addParameter("data", "test");

        try {
            int returnCode = client.executeMethod(method);

            if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Post method is not implemented by this URI");
                method.getResponseBodyAsString();
            } else {
                System.out.println(method.getResponseBodyAsString());
            }
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            method.releaseConnection();
            if (br != null) try {
                br.close();
            } catch (Exception fe) {
            }
        }
    }
}

package com.opitzconsulting.soa.mdsviewer.util;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Steffen Troester
 * Date: 08.02.13
 * Time: 10:32
 * ----------------------
 */
public class LogoutServlet extends HttpServlet {

    /**
     * To do a proper logout one has to invalidate the session.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath());
    }

}

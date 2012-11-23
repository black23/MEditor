/*
 * Metadata Editor
 * @author Jiri Kremser
 * 
 * 
 * 
 * Metadata Editor - Rich internet application for editing metadata.
 * Copyright (C) 2011  Jiri Kremser (kremser@mzk.cz)
 * Moravian Library in Brno
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * 
 */

package cz.mzk.editor.server;

import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;

import org.apache.log4j.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cz.mzk.editor.client.ConnectionException;
import cz.mzk.editor.server.DAO.DatabaseException;
import cz.mzk.editor.server.DAO.LogInOutDAO;
import cz.mzk.editor.server.DAO.UserDAO;
import cz.mzk.editor.server.config.EditorConfiguration;
import cz.mzk.editor.server.config.EditorConfiguration.ServerConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class AuthenticationServlet.
 */
public class AuthenticationServlet
        extends HttpServlet {

    private static final long serialVersionUID = -146532693218441119L;

    /** The Constant LOGGER. */
    private static final Logger ACCESS_LOGGER = Logger.getLogger(ServerConstants.ACCESS_LOG_ID);

    /** The user dao. */
    @Inject
    private static UserDAO userDAO;

    /** The configuration. */
    @Inject
    private static EditorConfiguration configuration;

    @Inject
    private static LogInOutDAO logInOutDAO;

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final String ALLOWED_PREFIX = "administrator:";

    /*
     * (non-Javadoc)
     * @see
     * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        HttpSession session = req.getSession(true);
        String url = (String) session.getAttribute(HttpCookies.TARGET_URL);
        String root =
                (URLS.LOCALHOST() ? "http://" : "https://")
                        + req.getServerName()
                        + (URLS.LOCALHOST() ? (req.getServerPort() == 80 || req.getServerPort() == 443 ? ""
                                : (":" + req.getServerPort())) : "") + URLS.ROOT()
                        + (URLS.LOCALHOST() ? "?gwt.codesvr=127.0.0.1:9997" : "");
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null) {
            String decodedHeader = decode(req, authHeader);
            String pass = configuration.getHttpBasicPass();
            if (pass == null || "".equals(pass.trim()) || pass.length() < 4) {
                requrireAuthentication(resp);
            }
            if (decodedHeader.equals(ALLOWED_PREFIX + pass)) {
                session.setAttribute(HttpCookies.TARGET_URL, null);
                session.setAttribute(HttpCookies.SESSION_ID_KEY,
                                     "https://www.google.com/profiles/109255519115168093543");
                session.setAttribute(HttpCookies.NAME_KEY, "admin");
                session.setAttribute(HttpCookies.ADMIN, HttpCookies.ADMIN_YES);
                ACCESS_LOGGER.info("LOG IN: [" + FORMATTER.format(new Date()) + "] User "
                        + decodedHeader.substring(0, decodedHeader.indexOf(":"))
                        + " with openID BASIC_AUTH and IP " + req.getRemoteAddr());
                URLS.redirect(resp, url == null ? root : url);
                return;
            } else {
                requrireAuthentication(resp);
                return;
            }
        }
        session.setAttribute(HttpCookies.TARGET_URL, null);
        String token = req.getParameter("token");

        String appID = configuration.getOpenIDApiKey();
        String openIdurl = configuration.getOpenIDApiURL();
        RPX rpx = new RPX(appID, openIdurl);
        Element e = null;
        try {
            e = rpx.authInfo(token);
        } catch (ConnectionException connEx) {
            requrireAuthentication(resp);
            return;
        }
        String idXPath = "//identifier";
        String nameXPath = "//displayName";
        XPathFactory xpfactory = XPathFactory.newInstance();
        XPath xpath = xpfactory.newXPath();
        String identifier = null;
        String name = null;
        try {
            XPathExpression expr1 = xpath.compile(idXPath);
            XPathExpression expr2 = xpath.compile(nameXPath);
            NodeList nodes1 = (NodeList) expr1.evaluate(e.getOwnerDocument(), XPathConstants.NODESET);
            NodeList nodes2 = (NodeList) expr2.evaluate(e.getOwnerDocument(), XPathConstants.NODESET);
            Element el = null;
            if (nodes1.getLength() != 0) {
                el = (Element) nodes1.item(0);
            }
            if (el != null) {
                identifier = el.getTextContent();
            }
            if (nodes2.getLength() != 0) {
                el = (Element) nodes2.item(0);
            }
            if (el != null) {
                name = el.getTextContent();
            }
        } catch (XPathExpressionException e1) {
            e1.printStackTrace();
        }
        if (identifier != null && !"".equals(identifier)) {
            ACCESS_LOGGER.info("LOG IN: [" + FORMATTER.format(new Date()) + "] User " + name
                    + " with openID " + identifier + " and IP " + req.getRemoteAddr());
            int userStatus = UserDAO.UNKNOWN;
            try {
                userStatus = userDAO.isSupported(identifier);
                //                userStatus = userDAO.isSupported(identifier, true);
            } catch (DatabaseException ex) {
                ex.printStackTrace();
            }
            switch (userStatus) {
                case UserDAO.UNKNOWN:
                    // TODO handle DB error (inform user)
                    break;
                case UserDAO.USER:
                    // HttpCookies.setCookie(req, resp, HttpCookies.SESSION_ID_KEY,
                    // identifier);
                    session.setAttribute(HttpCookies.SESSION_ID_KEY, identifier);
                    session.setAttribute(HttpCookies.NAME_KEY, name);
                    try {
                        logInOutDAO.logInOut(true, new Long(0));
                    } catch (DatabaseException e1) {
                        ACCESS_LOGGER.error(e1.getMessage());
                        e1.printStackTrace();
                    }
                    URLS.redirect(resp, url == null ? root : url);
                    break;
                case UserDAO.ADMIN:
                    // HttpCookies.setCookie(req, resp, HttpCookies.SESSION_ID_KEY,
                    // identifier);
                    // HttpCookies.setCookie(req, resp, HttpCookies.ADMIN,
                    // HttpCookies.ADMIN_YES);
                    session.setAttribute(HttpCookies.SESSION_ID_KEY, identifier);
                    session.setAttribute(HttpCookies.NAME_KEY, name);
                    session.setAttribute(HttpCookies.ADMIN, HttpCookies.ADMIN_YES);
                    try {
                        logInOutDAO.logInOut(true, new Long(0));
                    } catch (DatabaseException e1) {
                        ACCESS_LOGGER.error(e1.getMessage());
                        e1.printStackTrace();
                    }
                    URLS.redirect(resp, url == null ? root : url);
                    break;
                case UserDAO.NOT_PRESENT:
                default:
                    session.setAttribute(HttpCookies.UNKNOWN_ID_KEY, identifier);
                    session.setAttribute(HttpCookies.NAME_KEY, name);
                    URLS.redirect(resp, URLS.LOCALHOST() ? root.substring(0, root.indexOf("?"))
                            + URLS.INFO_PAGE + root.substring(root.indexOf("?")) : root + URLS.INFO_PAGE);
                    break;
            }
        } else {
            URLS.redirect(resp, root + (URLS.LOCALHOST() ? URLS.LOGIN_LOCAL_PAGE : URLS.LOGIN_PAGE));
        }

        // System.out.println("ID:" + identifier);

        // if user is supported redirect to homepage else show him a page that he
        // has to be added to system first by admin

    }

    private String decode(HttpServletRequest req, String authHeader) {
        //always wise to assert your assumptions
        assert authHeader.substring(0, 6).equals("Basic ");
        //will contain "Ym9iOnNlY3JldA=="
        String basicAuthEncoded = authHeader.substring(6);
        //will contain "bob:secret"
        String basicAuthAsString = new String(new Base64().decode(basicAuthEncoded.getBytes()));
        return basicAuthAsString;
    }

    private void requrireAuthentication(HttpServletResponse resp) throws IOException {
        resp.setHeader("WWW-Authenticate", "BASIC realm=\"users\"");
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}

/*
 * Metadata Editor
 * 
 * Metadata Editor - Rich internet application for editing metadata.
 * Copyright (C) 2011  Matous Jobanek (matous.jobanek@mzk.cz)
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

package cz.mzk.editor.server.janrain;

import java.io.IOException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.inject.Inject;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;

import cz.mzk.editor.server.config.EditorConfiguration;

/**
 * @author Matous Jobanek
 * @version Nov 22, 2012
 */
public class JanrainAuthenticationFilter
        extends AbstractAuthenticationProcessingFilter {

    @Inject
    private static EditorConfiguration configuration;

    /**
     * @param defaultFilterProcessesUrl
     */
    protected JanrainAuthenticationFilter() {
        super("/janrain_spring_security_check");
    }

    public static final String DEFAULT_CLAIMED_IDENTITY_FIELD = "token";

    private RpxConsumer consumer;
    private Set<String> returnToUrlParameters = Collections.emptySet();
    private String rpxBaseUrl = "https://metaeditor.rpxnow.com";
    private String rpxApiKey = null;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        //        Assert.notNull(rpxApiKey, "rpxApiKey must be specified.");

        if (consumer == null) {
            consumer = new RpxConsumerImpl();
        }

        if (returnToUrlParameters.isEmpty() && getRememberMeServices() instanceof AbstractRememberMeServices) {
            returnToUrlParameters = new HashSet<String>();
            returnToUrlParameters.add(((AbstractRememberMeServices) getRememberMeServices()).getParameter());
        }

    }

    public void setConsumer(RpxConsumer consumer) {
        this.consumer = consumer;
    }

    public void setRpxApiKey(String rpxApiKey) {
        this.rpxApiKey = rpxApiKey;
    }

    public void setRpxBaseUrl(String rpxBaseUrl) {
        this.rpxBaseUrl = rpxBaseUrl;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        OpenIDAuthenticationToken token;

        this.rpxApiKey = configuration.getOpenIDApiKey();
        this.rpxBaseUrl = configuration.getOpenIDApiURL();

        token = consumer.consume(rpxBaseUrl, rpxApiKey, request);

        token.setDetails(authenticationDetailsSource.buildDetails(request));

        // delegate to the authentication provider
        Authentication authentication = this.getAuthenticationManager().authenticate(token);

        if (authentication.isAuthenticated()) {
            setLastUsername(token.getIdentityUrl(), request);
        }

        UsernamePasswordAuthenticationFilter f;
        return authentication;
    }

    private void setLastUsername(String username, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null || getAllowSessionCreation()) {
            request.getSession()
                    .setAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY,
                                  username);
        }
    }

}

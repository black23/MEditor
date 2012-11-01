/*
 * Metadata Editor
 * @author Matous Jobanek
 * 
 * 
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

package cz.mzk.editor.client.presenter;

import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

import cz.mzk.editor.client.LangConstants;
import cz.mzk.editor.client.NameTokens;
import cz.mzk.editor.client.uihandlers.AdminHomeUiHandlers;
import cz.mzk.editor.shared.event.MenuButtonClickedEvent;
import cz.mzk.editor.shared.event.MenuButtonClickedEvent.MenuButtonClickedHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class HomePresenter.
 * 
 * @author Matous Jobanek
 * @version Oct 30, 2012
 */
public class AdminHomePresenter
        extends Presenter<AdminHomePresenter.MyView, AdminHomePresenter.MyProxy>
        implements AdminHomeUiHandlers {

    /**
     * The Interface MyView.
     */
    public interface MyView
            extends View, HasUiHandlers<AdminHomeUiHandlers> {

    }

    /**
     * The Interface MyProxy.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.ADMIN_HOME)
    public interface MyProxy
            extends ProxyPlace<AdminHomePresenter> {

    }

    /** The dispatcher. */
    private final DispatchAsync dispatcher;

    /** The left presenter. */
    private final AdminMenuPresenter leftPresenter;

    /** The place manager. */
    private final PlaceManager placeManager;

    /** The lang. */
    private final LangConstants lang;

    /**
     * Instantiates a new home presenter.
     * 
     * @param eventBus
     *        the event bus
     * @param view
     *        the view
     * @param proxy
     *        the proxy
     * @param leftPresenter
     *        the left presenter
     * @param dispatcher
     *        the dispatcher
     * @param placeManager
     *        the place manager
     * @param lang
     *        the lang
     */
    @Inject
    public AdminHomePresenter(final EventBus eventBus,
                              final MyView view,
                              final MyProxy proxy,
                              final AdminMenuPresenter leftPresenter,
                              final DispatchAsync dispatcher,
                              final PlaceManager placeManager,
                              final LangConstants lang) {
        super(eventBus, view, proxy);
        this.leftPresenter = leftPresenter;
        this.dispatcher = dispatcher;
        this.placeManager = placeManager;
        this.lang = lang;
        bind();
    }

    /**
     * On bind. {@inheritDoc}
     */
    @Override
    protected void onBind() {
        super.onBind();
        addRegisteredHandler(MenuButtonClickedEvent.getType(), new MenuButtonClickedHandler() {

            @Override
            public void onMenuButtonClicked(MenuButtonClickedEvent event) {
                placeManager.revealRelativePlace(new PlaceRequest(event.getMenuButtonType()));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onReset() {
        super.onReset();
        RevealContentEvent.fire(this, AdminPresenter.TYPE_ADMIN_LEFT_CONTENT, leftPresenter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, AdminPresenter.TYPE_ADMIN_MAIN_CONTENT, this);
    }

    /**
     * Open medit. {@inheritDoc}
     */
    @Override
    public void openMedit() {
        System.err.println(placeManager);
        placeManager.revealRelativePlace(new PlaceRequest(NameTokens.MEDIT_HOME));

    }

}
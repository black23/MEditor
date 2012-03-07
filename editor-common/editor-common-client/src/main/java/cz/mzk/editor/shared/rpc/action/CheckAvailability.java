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

package cz.mzk.editor.shared.rpc.action;

import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.In;
import com.gwtplatform.dispatch.annotation.Out;

// TODO: Auto-generated Javadoc
/**
 * The Class GetClientConfig.
 */
@GenDispatch(isSecure = false)
@SuppressWarnings("unused")
public class CheckAvailability {

    /** The Constant FEDORA_ID. */
    public static final int FEDORA_ID = 0;

    /** The Constant KRAMERIUS_ID. */
    public static final int KRAMERIUS_ID = 1;

    /** The config. */
    @In(1)
    private int serverId;

    /** The availability. */
    @Out(1)
    private boolean availability;

    /** The url. */
    @Out(2)
    private String url;
}

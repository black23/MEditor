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

package cz.mzk.editor.shared.rpc.action;

import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.In;
import com.gwtplatform.dispatch.annotation.Out;

import cz.mzk.editor.shared.rpc.DigitalObjectDetail;

/**
 * @author Matous Jobanek
 * @version $Id$
 */
@GenDispatch(isSecure = false)
@SuppressWarnings("unused")
public class DownloadDigitalObjectDetail {

    /** The Digital object detail */
    @In(1)
    private DigitalObjectDetail detail;

    /**
     * The array of Strings which contains a working copy of FOXML and
     * individual datastreams in this order: String[0] = FOXML, String[1] =
     * FOXML without the last version of the changed datastreams, String[2] = DC
     * datastream, String[3] = MODS datastream, String[4] = RELS-EXT datastream.
     */
    @Out(1)
    private String[] stringsWithXml;

}

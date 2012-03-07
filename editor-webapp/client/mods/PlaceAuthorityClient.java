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
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.11.13 at 05:02:55 odp. CET 
//

package cz.mzk.editor.client.mods;

import com.google.gwt.user.client.rpc.IsSerializable;

// TODO: Auto-generated Javadoc
/**
 * The Enum PlaceAuthorityClient.
 */
public enum PlaceAuthorityClient
        implements IsSerializable {

    /** The MARCGAC. */
    MARCGAC("marcgac"), /** The MARCCOUNTRY. */
    MARCCOUNTRY("marccountry"), /** The IS o_3166. */
    ISO_3166("iso3166");

    /** The value. */
    private final String value;

    /**
     * Instantiates a new place authority client.
     * 
     * @param v
     *        the v
     */
    PlaceAuthorityClient(String v) {
        value = v;
    }

    /**
     * Value.
     * 
     * @return the string
     */
    public String value() {
        return value;
    }

    /**
     * From value.
     * 
     * @param v
     *        the v
     * @return the place authority client
     */
    public static PlaceAuthorityClient fromValue(String v) {
        for (PlaceAuthorityClient c : PlaceAuthorityClient.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }

}

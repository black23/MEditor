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

package cz.mzk.editor.shared.rpc;

import java.io.Serializable;

import cz.mzk.editor.shared.domain.DigitalObjectModel;

/**
 * @author Matous Jobanek
 * @version $Id$
 */

public class StoredItem
        implements Serializable {

    private static final long serialVersionUID = -8933900314317857197L;

    /** The file_name. */
    private String fileName;

    /** The uuid. */
    private String uuid;

    /** The model. */
    private DigitalObjectModel model;

    /** The description. */
    private String description;

    /** The date of the storing */
    private String storedDate;

    /**
     * Instantiates a digital object basic inform.
     */
    public StoredItem() {

    }

    /**
     * Instantiates a new digital object basic inform.
     * 
     * @param fileName
     *        the file model
     * @param uuid
     *        the uuid
     * @param model
     *        the model
     * @param description
     *        the description
     */
    public StoredItem(String file_name,
                      String uuid,
                      DigitalObjectModel model,
                      String description,
                      String storedDate) {
        super();
        this.fileName = file_name;
        this.uuid = uuid;
        this.model = model;
        this.description = description;
        this.storedDate = storedDate;
    }

    /**
     * Instantiates a new digital object basic inform.
     * 
     * @param fileName
     */

    public StoredItem(String fileName) {
        super();
        this.fileName = fileName;
    }

    /**
     * Gets the file model.
     * 
     * @return the file model
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file model.
     * 
     * @param fileName
     *        the new file model
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the date of the last storing.
     * 
     * @return the stored date
     */
    public String getStoredDate() {
        return storedDate;
    }

    /**
     * Sets the date of the last storing.
     * 
     * @param storedDate
     *        the new stored date
     */
    public void setStoredDate(String storedDate) {
        this.storedDate = storedDate;
    }

    /**
     * Gets the uuid.
     * 
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the uuid.
     * 
     * @param uuid
     *        the new uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the model.
     * 
     * @return the model
     */
    public DigitalObjectModel getModel() {
        return model;
    }

    /**
     * Sets the model.
     * 
     * @param model
     *        the new model
     */
    public void setModel(DigitalObjectModel model) {
        this.model = model;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * 
     * @param description
     *        the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        StoredItem other = (StoredItem) obj;
        if (uuid == null) {
            if (other.uuid != null) return false;
        } else if (!uuid.equals(other.uuid)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "StoredItem [uuid=" + uuid + ", model=" + model + ", description=" + description
                + ", storedDate=" + storedDate + "]";
    }
}

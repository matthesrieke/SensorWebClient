/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
/**********************************************************************************
 Copyright (C) 2010
 by 52 North Initiative for Geospatial Open Source Software GmbH

 Contact: Andreas Wytzisk 
 52 North Initiative for Geospatial Open Source Software GmbH
 Martin-Luther-King-Weg 24
 48155 Muenster, Germany
 info@52north.org

 This program is free software; you can redistribute and/or modify it under the
 terms of the GNU General Public License version 2 as published by the Free
 Software Foundation.

 This program is distributed WITHOUT ANY WARRANTY; even without the implied
 WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License along with this 
 program (see gnu-gplv2.txt). If not, write to the Free Software Foundation, Inc., 
 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or visit the Free Software
 Foundation web page, http://www.fsf.org.
 
 Created on: 06.05.2010
 *********************************************************************************/
package org.n52.shared;

/**
 * The Class MetaException.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public abstract class MetaException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5951946069638446759L;

    /**
     * The Enum ExceptionWeight.
     */
    public static enum ExceptionWeight {

        /** The mild. */
        mild,
        /** The severe. */
        severe
    }

    /** The weight. */
    protected ExceptionWeight weight;

    /**
     * Instantiates a new meta exception.
     */
    protected MetaException() {
        // not needed
    }

    /**
     * Instantiates a new meta exception.
     * 
     * @param string
     *            the string
     * @pararm cause
     *            the cause
     */
    public MetaException(String string, Throwable cause) {
        super(string, cause);
    }
    
    

    public MetaException(String message) {
        super(message);
    }

    /**
     * Gets the weight.
     * 
     * @return the weight
     */
    public ExceptionWeight getWeight() {
        return this.weight;
    }

}

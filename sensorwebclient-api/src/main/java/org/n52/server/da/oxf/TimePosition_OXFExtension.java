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
package org.n52.server.da.oxf;

public class TimePosition_OXFExtension implements ITimePosition_OXFExtension {

    public static final String GET_OBSERVATION_TIME_PARAM_FIRST = "getFirst";

    public static final String GET_OBSERVATION_TIME_PARAM_LAST = "latest";

    private final String timeParam;

    public TimePosition_OXFExtension(String timeParam) {
        this.timeParam = timeParam;
    }

    public String toISO8601Format() {
        return this.timeParam;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (timeParam == null) ? 0 : timeParam.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TimePosition_OXFExtension other = (TimePosition_OXFExtension) obj;
        if (timeParam == null) {
            if (other.timeParam != null)
                return false;
        }
        else if ( !timeParam.equals(other.timeParam))
            return false;
        return true;
    }

    

}

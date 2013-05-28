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

package org.n52.server.service.rest;

import static java.lang.Integer.parseInt;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Represents a parameter object to request data from multiple timeseries.
 */
public class ParameterSet {

    /**
     * The timespan of interest (as <a href="http://en.wikipedia.org/wiki/ISO_8601#Time_intervals">ISO8601
     * interval</a> excluding the Period only version).
     */
    private String timespan;

    /**
     * The size of the requested timeseries graph.
     */
    private String size;

    /**
     * The timeseriesIds of interest.
     */
    private String[] timeseriesIds;

    /**
     * Creates an instance with non-null default values.
     */
    public ParameterSet() {
        size = createDefaultSize();
        timespan = createDefaultTimespan();
        timeseriesIds = new String[0];
    }

    private String createDefaultTimespan() {
        DateTime now = new DateTime();
        DateTime lastMonth = now.minusMonths(1);
        return new Interval(lastMonth, now).toString();
    }

    private String createDefaultSize() {
        return validateSize(null);
    }

    public String getTimespan() {
        return timespan;
    }

    public void setTimespan(String timespan) {
        if (timespan == null) {
            this.timespan = createDefaultTimespan();
        }
        else {
            this.timespan = validateTimespan(timespan);
        }
    }

    private String validateTimespan(String timespan) {
        return Interval.parse(timespan).toString();
    }

    /**
     * @return the requested width or negative number if no size was set.
     */
    public int getWidth() {
        return parseInt(getSizeValues(size)[0]);
    }

    /**
     * @return the requested height or negative number if no size was set.
     */
    public int getHeight() {
        return parseInt(getSizeValues(size)[1]);
    }

    public void setSize(String size) {
        this.size = validateSize(size);
    }

    private String validateSize(String size) {
        if (size == null) {
            return "-1,-1";
        }
        String[] values = getSizeValues(size);
        int width = parseInt(values[0]);
        int height = parseInt(values[1]);
        return width + "," + height;
    }

    private String[] getSizeValues(String size) {
        String[] values = size.trim().split(",");
        if (values.length != 2) {
            throw new ArrayIndexOutOfBoundsException("Invalid size! Must be of form '<width>,<height>'");
        }
        return values;
    }

    public String[] getTimeseriesIds() {
        return timeseriesIds;
    }

    public void setTimeseries(String[] timeseriesIds) {
        this.timeseriesIds = timeseriesIds;
    }

}

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
package org.n52.server.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO log statistics into a file
public class Statistics {
    
    public static Map<String, Integer> hostsInterval = new HashMap<String, Integer>();
    public static Map<String, Integer> hosts = new HashMap<String, Integer>();
    public static int hours = 1;
    private static Timer timer;

    public static void saveHostRequest(String host) {
        if (hosts.containsKey(host)) {
            int reqCount = hosts.get(host) + 1;
            hosts.put(host, reqCount);
        } else {
            hosts.put(host, 1);
        }
        if (hostsInterval.containsKey(host)) {
            int reqCount = hostsInterval.get(host) + 1;
            hostsInterval.put(host, reqCount);
        } else {
            hostsInterval.put(host, 1);
        }
    }
    
    public static void scheduleStatisticsLog(int minutes) {
        timer = new Timer("Statistics", true);
        hours = minutes/60;
        timer.scheduleAtFixedRate(new StatisticLogTimerTask(), 0, (1000 * 60 * minutes));
    }
    
    public static void shutdown() {
        timer.cancel();
    }

    
    static class StatisticLogTimerTask extends TimerTask {

        private static final Logger LOGGER = LoggerFactory.getLogger(StatisticLogTimerTask.class);
        
        @Override
        public void run() {
            LOGGER.info("************ STATISTICS ************");
            LOGGER.info("********** SINCE STARTUP ***********");
            logHosts(Statistics.hosts);
            LOGGER.info("************ STATISTICS ************");
            LOGGER.info("********** LAST "+Statistics.hours/60+" HOURS ***********");
            logHosts(Statistics.hostsInterval);
            Statistics.hostsInterval = new HashMap<String, Integer>();
        }
        
        private static void logHosts(Map<String, Integer> hosts) {
            int requests = 0;
            for (String host : hosts.keySet()) {
                requests += hosts.get(host);
            }
            LOGGER.info("Processed "+hosts.size()+" hosts with a count of "+requests+" requests");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Complete list of hosts and count of requests: ");
                for (String host : hosts.keySet()) {
                    LOGGER.debug(host+" with "+hosts.get(host)+" requests");
                }
            }
        }

    }
    
}

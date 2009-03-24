/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.synapse.aspects.statistics;

import org.apache.synapse.aspects.ComponentType;
import org.apache.synapse.aspects.statistics.mbean.StatisticsView;
import org.apache.synapse.aspects.statistics.view.InOutStatisticsView;
import org.apache.synapse.aspects.statistics.view.StatisticsViewStrategy;
import org.apache.synapse.commons.util.MBeanRegistrar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Collects statistics and provides those collected data
 */

public class StatisticsCollector {

    private final List<StatisticsRecord> statisticsCollection = new ArrayList<StatisticsRecord>();

    public StatisticsCollector() {
        MBeanRegistrar.getInstance().registerMBean(new StatisticsView(this),
                "StatisticsView", "StatisticsView");
    }

    /**
     * Registering a statistics record
     *
     * @param statisticsRecord statistics record instance
     */
    public void collect(StatisticsRecord statisticsRecord) {
        this.statisticsCollection.add(statisticsRecord);
    }

    /**
     * Check whether given statistics record has  already been registered
     *
     * @param statisticsRecord statisticsRecord statistics record instance
     * @return True if there
     */
    public boolean contains(StatisticsRecord statisticsRecord) {
        return statisticsCollection.contains(statisticsRecord);
    }

    /**
     * Clear all the existing statistics
     */
    public void clearStatistics() {
        this.statisticsCollection.clear();
    }

    /**
     * Returns a particular statistics view according to a given strategy for a given
     * resource with particular type
     *
     * @param id       Resource id
     * @param type     Type of the resource
     * @param strategy Statistics viewing strategy
     * @return Statistics view
     */
    public Map<String, InOutStatisticsView> getStatistics(String id,
                                                          ComponentType type,
                                                          StatisticsViewStrategy strategy) {
        return strategy.determineView(id, getCopy(), type);
    }

    /**
     * Returns a particular statistics view according to a given strategy for a given resource type
     *
     * @param type     type Type of the resource
     * @param strategy strategy Statistics viewing strategy
     * @return Statistics view
     */
    public Map<String, Map<String, InOutStatisticsView>> getStatistics(
            ComponentType type,
            StatisticsViewStrategy strategy) {
        return strategy.determineView(getCopy(), type);
    }

    private List<StatisticsRecord> getCopy() {
        List<StatisticsRecord> records = new ArrayList<StatisticsRecord>();
        records.addAll(statisticsCollection);
        return records;
    }
}
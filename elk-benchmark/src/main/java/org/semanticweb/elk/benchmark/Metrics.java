/**
 * 
 */
package org.semanticweb.elk.benchmark;
/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class Metrics {

	private int runCount_ = 0;
	private final Map<String, MetricBean> metricMap_ = new TreeMap<String, MetricBean>();
	
	public void incrementRunCount() {
		runCount_++;
	}
	
	public int getRunsNumber() {
		return runCount_;
	}

	public Map<String, MetricBean> getMetricsMap() {
		return metricMap_;
	}
	
	
	public void updateLongMetric(String metric, long measurement) {
		MetricBean bean = metricMap_.get(metric);

		bean = bean == null ? new MetricBean() : bean;
		bean.total += measurement;
		bean.min = Math.min(bean.min, measurement);
		bean.max = Math.max(bean.max, measurement);
		bean.count++;
		metricMap_.put(metric, bean);
	}
	
	public void updateDoubleMetric(String metric, double measurement) {
		MetricBean bean = metricMap_.get(metric);

		bean = bean == null ? new MetricBean() : bean;
		bean.total += measurement;
		bean.min = Math.min(bean.min, measurement);
		bean.max = Math.max(bean.max, measurement);
		bean.count++;
		metricMap_.put(metric, bean);
	}
	
	
	public void reset() {
		runCount_ = 0;
		metricMap_.clear();
	}
	
	public MetricBean getMetric(String name) {
		return metricMap_.get(name);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		String delim = System.getProperty("line.separator");
		
		buffer.append("Run count: " + runCount_).append(delim);
		
		for (Map.Entry<String, MetricBean> entry : metricMap_.entrySet()) {
			buffer.append(entry.getKey()).append(" : ").append(entry.getValue()).append(delim);
		}
		
		return buffer.toString();
	}
	
	/**
	 * @param logger
	 */
	public void printAverages(final Logger logger, LogLevel level) {
		StringBuffer buffer = new StringBuffer();
		String delim = System.getProperty("line.separator");
		
		buffer.append("Run count: " + runCount_).append(delim);
		
		for (Map.Entry<String, MetricBean> entry : metricMap_.entrySet()) {
			MetricBean value = entry.getValue();

			if (value.count > 0 && value.total > 0d) {
				buffer.append("Average " + entry.getKey()).append(" : ").append(value.printAverage()).append(delim);
			}
		}
		
		LoggerWrap.log(logger, level, buffer.toString());
	}
	
	public void add(Metrics metrics) {
		runCount_ += metrics.runCount_;
		
		if (metrics.runCount_ > 0) {
			for (Map.Entry<String, MetricBean> entry : metricMap_.entrySet()) {
				MetricBean other = metrics.getMetric(entry.getKey());

				if (other != null) {
					entry.getValue().add(other);
				}
			}

			for (Map.Entry<String, MetricBean> entry : metrics.getMetricsMap()
					.entrySet()) {
				MetricBean our = metricMap_.get(entry.getKey());

				if (our == null) {
					MetricBean bean = new MetricBean();

					bean.add(entry.getValue());
					metricMap_.put(entry.getKey(), bean);
				}
			}
		}
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class MetricBean {
		
		static DecimalFormat FORMAT = new DecimalFormat("#.##");
		
		double total = 0;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		int count = 0;
		
		@Override
		public String toString() {
			return total + (min < Double.MAX_VALUE ? "[" + min + ", " + max + "]" : "");
		}
		
		public String format(double value) {
			return FORMAT.format(value);
		}
		
		public String printAverage() {
			return format(total/count) + " [" + format(min) + "--" + format(max) + "] (" + count + ")";
		}
		
		public void add(MetricBean bean) {
			count += bean.count;
			total += bean.total;
			min = Math.min(min, bean.min);
			max = Math.max(max, bean.max);
		}
	}
}



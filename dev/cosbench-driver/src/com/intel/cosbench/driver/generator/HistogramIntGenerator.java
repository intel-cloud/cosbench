/** 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */
package com.intel.cosbench.driver.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.intel.cosbench.config.ConfigException;
//import com.intel.cosbench.driver.generator.RangeIntGenerator.TestThread;

/**
 * 
 * This class provides an weighted histogram int generator. To configure it, specify a comma separated list of buckets
 * where each bucket is defined by a range and an integer weight. For example:
 * 
 * h(1|64|10,64|512|70,512|2048|20)KB
 * 
 * @author Christophe Vedel <cv@scality.com>
 *
 */
public class HistogramIntGenerator implements IntGenerator {

	private static class LowerComparator implements Comparator<Bucket> {
		public int compare(Bucket b1, Bucket b2) {
			return b1.lower - b2.lower;
		}
	}

	private static class Bucket {

		private final int lower;
		private final UniformIntGenerator gen;
		private final int weight;
		private int cumulativeWeight;

		Bucket(int lower, int upper, int weight) {
			this.lower = lower;
			this.gen = new UniformIntGenerator(lower, upper);
			this.weight = weight;
			this.cumulativeWeight = 0;
		}
	}

	private final Bucket buckets[];
	private final int totalWeight;

	public HistogramIntGenerator(Bucket buckets[]) {
		this.buckets = buckets;
		totalWeight = buckets[buckets.length - 1].cumulativeWeight;

	}

	@Override
	public int next(Random random) {
		final int next = RandomUtils.nextInt(random, totalWeight);
		for (Bucket bucket : buckets) {
			if (next <= bucket.cumulativeWeight) {
				return bucket.gen.next(random);
			}
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.intel.cosbench.driver.random.IntGenerator#next(java.util.Random, int, int)
	 */
	@Override
	public int next(Random random, int idx, int all) {
		throw new NotImplementedException();
	}

	public static HistogramIntGenerator parse(String pattern) {
		if (!StringUtils.startsWith(pattern, "h("))
			return null;
		try {
			return tryParse(pattern);
		} catch (Exception e) {
		}
		String msg = "illegal histogram distribution pattern: " + pattern;
		throw new ConfigException(msg);
	}

	private static HistogramIntGenerator tryParse(String pattern) {
		pattern = StringUtils.substringBetween(pattern, "(", ")");
		String[] args = StringUtils.split(pattern, ',');
		ArrayList<Bucket> bucketsList = new ArrayList<Bucket>();
		for (String arg : args) {
			int v1 = StringUtils.indexOf(arg, '|');
			int v2 = StringUtils.lastIndexOf(arg, '|');
			boolean isOpenRange = ((v2 - v1) == 1) ? true : false;
			String[] values = StringUtils.split(arg, '|');
			int lower,upper,weight;
			if (isOpenRange) {
				lower = Integer.parseInt(values[0]);
				upper = UniformIntGenerator.getMAXupper();
				weight = Integer.parseInt(values[1]);
			} else if (values.length != 3) {
				throw new IllegalArgumentException();
			} else {
				lower = Integer.parseInt(values[0]);
				upper = Integer.parseInt(values[1]);
				weight = Integer.parseInt(values[2]);
			}
			bucketsList.add(new Bucket(lower, upper, weight));
		}
		if (bucketsList.isEmpty()) {
			throw new IllegalArgumentException();			
		}
		Collections.sort(bucketsList, new LowerComparator());
		final Bucket[] buckets = bucketsList.toArray(new Bucket[0]);
		int cumulativeWeight = 0;
		for (Bucket bucket : buckets) {
			cumulativeWeight += bucket.weight;
			bucket.cumulativeWeight = cumulativeWeight;
		}
		return new HistogramIntGenerator(buckets);
	}

}

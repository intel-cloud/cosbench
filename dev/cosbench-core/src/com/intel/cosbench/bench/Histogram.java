/** 
 
Copyright 2013 Intel Corporation, All Rights Reserved.

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

package com.intel.cosbench.bench;

/**
 * This class encapsulates calculation of different percentiles, 
 * so far it supports 60/80/90/95/99/100 percentiles.
 * 
 * @author ywang19, qzheng7
 *
 */
public class Histogram implements Cloneable {

    /* Percentile */

    private long[] _60; /* 60% */
    private long[] _80; /* 80% */
    private long[] _90; /* 90% */
    private long[] _95; /* 95% */
    private long[] _99; /* 99% */
    private long[] _100; /* 100% */

    /* Raw Data */

    private int[] histoData; /* raw histogram data */

    public Histogram() {
        /* empty */
    }

    public long[] get_60() {
        return _60;
    }

    public void set_60(long[] _60) {
        this._60 = _60;
    }

    public long[] get_80() {
        return _80;
    }

    public void set_80(long[] _80) {
        this._80 = _80;
    }

    public long[] get_90() {
        return _90;
    }

    public void set_90(long[] _90) {
        this._90 = _90;
    }

    public long[] get_95() {
        return _95;
    }

    public void set_95(long[] _95) {
        this._95 = _95;
    }

    public long[] get_99() {
        return _99;
    }

    public void set_99(long[] _99) {
        this._99 = _99;
    }

    public long[] get_100() {
        return _100;
    }

    public void set_100(long[] _100) {
        this._100 = _100;
    }

    public int[] getHistoData() {
        return histoData;
    }

    public void setHistoData(int[] histoData) {
        this.histoData = histoData;
    }

    /**
     * The method calculates 60/80/90/95/99 percentiles.
     * 
     * @param 
     * @return	
     */
    public void recalcPercentiles() {
        int curr = 0;
        int total = calcTotalCount();

        if (total == 0)
            return;

        // define thresholds
        final int T_99 = (int) (total * 0.99);
        final int T_95 = (int) (total * 0.95);
        final int T_90 = (int) (total * 0.90);
        final int T_80 = (int) (total * 0.80);
        final int T_60 = (int) (total * 0.60);

        // calculate percentiles
        for (int i = 0; i < histoData.length; i++) {
            curr += histoData[i];

            if (curr >= T_99 && _99 == null)
                _99 = Counter.getResTime(i);
            if (curr >= T_95 && _95 == null)
                _95 = Counter.getResTime(i);
            if (curr >= T_90 && _90 == null)
                _90 = Counter.getResTime(i);
            if (curr >= T_80 && _80 == null)
                _80 = Counter.getResTime(i);
            if (curr >= T_60 && _60 == null)
                _60 = Counter.getResTime(i);

            if (curr >= total) {
                _100 = Counter.getResTime(i);
                break;
            }

        }
    }

    private int calcTotalCount() {
        int total = 0;
        for (int i = 0; i < histoData.length; i++)
            total += histoData[i];
        return total;
    }

    @Override
    public Histogram clone() {
        try {
            return (Histogram) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return this;
    }

    public static Histogram convert(Counter counter) {
        Histogram histogram = new Histogram();
        histogram.setHistoData(retrieveData(counter));
        histogram.recalcPercentiles();
        return histogram;
    }

    private static int[] retrieveData(Counter counter) {
        int size = counter.size();
        int[] data = new int[size];
        for (int i = 0; i < size; i++)
            data[i] = counter.get(i);
        return data;
    }
}

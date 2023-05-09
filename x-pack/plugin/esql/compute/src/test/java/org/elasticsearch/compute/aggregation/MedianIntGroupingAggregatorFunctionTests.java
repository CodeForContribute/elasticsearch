/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.compute.aggregation;

import org.elasticsearch.common.Randomness;
import org.elasticsearch.compute.data.Block;
import org.elasticsearch.compute.data.DoubleBlock;
import org.elasticsearch.compute.data.Page;
import org.elasticsearch.compute.operator.LongIntBlockSourceOperator;
import org.elasticsearch.compute.operator.SourceOperator;
import org.elasticsearch.core.Tuple;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.compute.aggregation.MedianDoubleGroupingAggregatorFunctionTests.median;
import static org.hamcrest.Matchers.equalTo;

public class MedianIntGroupingAggregatorFunctionTests extends GroupingAggregatorFunctionTestCase {

    @Override
    protected SourceOperator simpleInput(int end) {
        int[][] samples = new int[][] {
            { 12, 20, 20, 43, 60, 90, 125 },
            { 1, 15, 20, 30, 40, 75, 1000 },
            { 2, 20, 25, 175 },
            { 5, 30, 30, 30, 43 },
            { 7, 15, 30 } };
        List<Tuple<Long, Integer>> values = new ArrayList<>();
        for (int i = 0; i < samples.length; i++) {
            for (int v : samples[i]) {
                values.add(Tuple.tuple((long) i, v));
            }
        }
        Randomness.shuffle(values);
        return new LongIntBlockSourceOperator(values);
    }

    @Override
    protected GroupingAggregatorFunction.Factory aggregatorFunction() {
        return GroupingAggregatorFunction.MEDIAN_INTS;
    }

    @Override
    protected String expectedDescriptionOfAggregator() {
        return "median of ints";
    }

    @Override
    protected void assertSimpleGroup(List<Page> input, Block result, int position, long group) {
        assertThat(
            ((DoubleBlock) result).getDouble(position),
            equalTo(median(input.stream().flatMapToInt(p -> allInts(p, group)).asDoubleStream()))
        );
    }
}

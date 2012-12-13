package com.opitzconsulting.soa.mdsviewer.util;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * User: str
 * Date: 06.12.12
 * Time: 15:54
 * Simple helperclass to diff two source files.
 */
public class DiffGenerator {
    public List<Delta> getDeltasBetween(String first, String second) {
        List<String> firstList = asList(first.split("\n"));
        List<String> secondList = asList(second.split("\n"));

        Patch patch = DiffUtils.diff(firstList, secondList);
        return patch.getDeltas();
    }
}

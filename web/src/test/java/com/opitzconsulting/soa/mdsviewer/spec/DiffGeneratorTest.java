package com.opitzconsulting.soa.mdsviewer.spec;

import com.opitzconsulting.soa.mdsviewer.util.DiffGenerator;
import difflib.Delta;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * User: str
 * Date: 06.12.12
 * Time: 15:55
 */
public class DiffGeneratorTest {
    private DiffGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new DiffGenerator();
    }

    @Test
    public void testGeneratingDiffOfTwoStrings() {
        String first = "Hello\nThere!\nFoo\nBar";
        String second = "Hello\nFoo\nBar";
        for (Delta delta : generator.getDeltasBetween(first, second)) {
            System.out.println(delta);
            Assert.assertEquals(delta.toString(), "[DeleteDelta, position: 1, lines: [There!]]");
        }
    }
}

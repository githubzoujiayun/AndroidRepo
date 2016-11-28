package com.worksum.android.tester;

import com.jobs.lib_v1.app.AppUtil;
import com.worksum.android.utils.Utils;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author chao.qin
 *         <p/>
 *         16/5/19
 */
public class SimpleTest extends TestCase{

    public   static int a = 5;

    class Simple extends SimpleTest {

    }

    public void testSimple() {
        boolean result = Utils.matchesPhone("51112222");
        assertTrue(result);
    }
}

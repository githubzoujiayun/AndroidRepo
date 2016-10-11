package com.worksum.android.tester;

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
        Simple.a = 100;

        System.out.println(Simple.a);
        System.out.println(SimpleTest.a);
    }
}

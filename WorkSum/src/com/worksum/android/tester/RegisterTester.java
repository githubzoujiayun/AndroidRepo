package com.worksum.android.tester;

import android.test.AndroidTestCase;

import com.worksum.android.RegisterFragment;

/**
 * @author chao.qin
 *         <p>
 *         16/4/14
 */
public class RegisterTester extends AndroidTestCase{

    public void testCheckPhoneNumber() {
        assertTrue(RegisterFragment.checkedPhoneNumber("12345678"));
        assertFalse(RegisterFragment.checkedPhoneNumber("123456789"));
        assertFalse(RegisterFragment.checkedPhoneNumber("abcdefgh"));
        assertFalse(RegisterFragment.checkedPhoneNumber("1234567a"));
        assertFalse(RegisterFragment.checkedPhoneNumber("12345678a"));
        assertFalse(RegisterFragment.checkedPhoneNumber("a12345678"));

    }
}

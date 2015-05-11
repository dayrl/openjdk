/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package jdk.one;

import java.lang.reflect.Module;

/**
 * Basic test of java.lang.reflect.Module#canRead and addReads.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Module m1 = Main.class.getModule();
        assertTrue(!m1.isUnnamed());

        // java.base is readable
        Module base = Object.class.getModule();
        assertTrue(m1.canRead(base));
        m1.addReads(base); // no-op
        assertTrue(m1.canRead(base));

        // a module can read itself
        assertTrue(m1.canRead(m1));
        m1.addReads(m1); // no-op
        assertTrue(m1.canRead(m1));

        // unnamed module is readable
        //assertTrue(m1.canRead(null));

        // module m2 is not readable
        Class<?> c = Class.forName("jdk.two.C");
        Module m2 = c.getModule();
        assertTrue(!m1.canRead(m2));

        // jdk.one.C is not accessible
        try {
            c.newInstance();
            expectedIllegalAccessException();
        } catch (IllegalAccessException e) { }

        // make m2 readable
        m1.addReads(m2);
        assertTrue(m1.canRead(m2));

        // jdk.one.C should be accessible now
        Object o = c.newInstance();
        System.out.println(o);

        // jdk.one.internal is not exported so not accessible
        c = Class.forName("jdk.two.internal.C");
        assertTrue(c.getModule() == m2);
        try {
            c.newInstance();
            expectedIllegalAccessException();
        } catch (IllegalAccessException e) { }
    }

    static void assertTrue(boolean expr) {
        if (!expr)
            throw new RuntimeException("Assertion failed");
    }

    static void expectedIllegalAccessException() {
        throw new RuntimeException("IllegalAccessException expected");
    }

    static void expectedIllegalArgumentException() {
        throw new RuntimeException("IllegalArgumentException expected");
    }
}

/*
 * This file is part of ClassGraph.
 *
 * Author: Luke Hutchison
 *
 * Hosted at: https://github.com/lukehutch/fast-classpath-scanner
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Luke Hutchison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.classgraph.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/** A replacement for Java 8's String.join() that will allow compilation on Java 7. */
public class Join {
    /**
     * A replacement for Java 8's String.join(). In the case of a set, sorts elements for consistency.
     * 
     * @param sep
     *            The separator string.
     * @param set
     *            The set to sort and join.
     * @return The string representation of the joined elements.
     */
    public static <T extends Comparable<T>> String join(final String sep, final Set<T> set) {
        final List<T> sorted = new ArrayList<>(set);
        Collections.sort(sorted);
        return join(sep, sorted);
    }

    /**
     * A replacement for Java 8's String.join().
     * 
     * @param sep
     *            The separator string.
     * @param iterable
     *            The {@link Iterable} to join.
     * @return The string representation of the joined elements.
     */
    public static String join(final String sep, final Iterable<?> iterable) {
        final StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (final Object item : iterable) {
            if (first) {
                first = false;
            } else {
                buf.append(sep);
            }
            buf.append(item);
        }
        return buf.toString();
    }

    /**
     * A replacement for Java 8's String.join().
     * 
     * @param sep
     *            The separator string.
     * @param items
     *            The items to join.
     * @return The string representation of the joined items.
     */
    public static String join(final String sep, final Object... items) {
        final StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (final Object item : items) {
            if (first) {
                first = false;
            } else {
                buf.append(sep);
            }
            buf.append(item.toString());
        }
        return buf.toString();
    }
}

/**
 * Copyright (C) 2013 Takahiro Yoshimura, Renjaya Raga Zenta
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gmail.altakey.dawne.util;

import java.io.Serializable;
import java.util.ArrayList;

public class StackRecentFiles implements Serializable {

    public static final int MAX_CAPACITY = 10;
    private ArrayList<RecentFile> stack;
    private int top;
    private static final long serialVersionUID = 7977738208421002767L;

    public StackRecentFiles() {
        this.stack = new ArrayList<RecentFile>(MAX_CAPACITY);
        this.top = -1;
    }

    public void pushRecentFile(RecentFile recentFile) {
        final int index = indexOf(recentFile);
        if (index == -1) {
            if (top >= MAX_CAPACITY - 1) {
                shiftElementToBottom();
                stack.set(top, recentFile);
            } else {
                stack.add(++top, recentFile);
            }
        } else {
            shiftElementToBottom(index);
        }
    }

    public RecentFile getRecentFile() {
        if (top < 0) {
            throw new IndexOutOfBoundsException();
        }
        return stack.get(top);
    }

    public RecentFile getRecentFileAt(int position) {
        final int size = stack.size();
        if (position < 0 || position >= size) {
            throw new IndexOutOfBoundsException();
        }
        return stack.get(size - position - 1);
    }

    public int size() {
        return stack.size();
    }

    public void clear() {
        stack.clear();
        top = -1;
    }

    private int indexOf(RecentFile recentFile) {
        final int size = stack.size();
        for (int i = 0; i < size; i++) {
            final RecentFile rf = stack.get(i);
            if (recentFile.getFileName().equals(rf.getFileName())) {
                return i;
            }
        }
        return -1;
    }

    // Only called if stack is full!
    private void shiftElementToBottom() {
        for (int i = 0; i < top; i++) {
            stack.set(i, stack.get(i + 1));
        }
        stack.set(top, null);
    }

    private void shiftElementToBottom(int index) {
        final RecentFile rotated = stack.get(index);
        for (int i = index; i < top; i++) {
            stack.set(i, stack.get(i + 1));
        }
        stack.set(top, rotated);
    }
}

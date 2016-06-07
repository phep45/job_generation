package com.luxoft.autosys.generator;

import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;

public class JilFileFilter implements IOFileFilter {

    public static final String JIL = ".jil";

    @Override
    public boolean accept(File file) {
        return file.getName().endsWith(JIL);
    }

    @Override
    public boolean accept(File dir, String name) {
        throw new UnsupportedOperationException();
    }
}


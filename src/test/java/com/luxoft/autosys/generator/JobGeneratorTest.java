package com.luxoft.autosys.generator;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class JobGeneratorTest {

    private static final String PROPERTIES_SRC_DIR = "src/test/resources/properties";
    private static final String TEMPLATES_SRC_DIR = "src/test/resources/templates";
    private static final String OUTPUT_DIR = "src/test/resources/output";
    private static final String EXPECTED_OUTPUT_DIR = "src/test/resources/output/test";
    private static final String EXPECTED_JOB_FILE = "src/test/resources/TEMPLATE_JIL_FILE_TEST.jil";
    private static final String EXPECTED_FILENAME = "TEMPLATE_JIL_FILE_TEST.jil";

    private JobGenerator jobGenerator;

    @Before
    public void setUp() {
        jobGenerator = new JobGenerator(PROPERTIES_SRC_DIR, TEMPLATES_SRC_DIR, OUTPUT_DIR);
    }

    @Test
    public void shouldGenerateFiles() throws IOException {

        jobGenerator.generateJobs();

        String expectedContent = FileUtils.readFileToString(new File(EXPECTED_JOB_FILE));
        String actualContent = FileUtils.readFileToString(new File(EXPECTED_OUTPUT_DIR + "/" + EXPECTED_FILENAME));

        Assert.assertEquals(expectedContent, actualContent);
    }

    @After
    public void cleanUp() throws IOException {
        FileUtils.deleteDirectory(new File(OUTPUT_DIR));
    }

}

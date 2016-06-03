package com.luxoft.autosys.generator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;

public class JobGeneratorTest {

    private static final String PROPERTIES_SRC_DIR = "src/test/resources/properties";
    private static final String NOT_EXISTING_DIRECTORY = "this/directory/do/not/exist";
    private static final String TEMPLATES_SRC_DIR = "src/test/resources/templates";
    private static final String OUTPUT_DIR = "src/test/resources/output";
    private static final String EXPECTED_OUTPUT_DIR = "src/test/resources/output/test";
    private static final String EXPECTED_JOB_FILE = "src/test/resources/TEMPLATE_JIL_FILE_TEST.jil";
    private static final String EXPECTED_FILENAME = "TEMPLATE_JIL_FILE_TEST.jil";
    private static final String DEFAULT_PROPERTIES = "src/test/resources/default.properties";

    private static final String PROPERTIES_DIR_PATH_FIELD = "propertiesDirPath";
    private static final String TEMPLATES_DIR_PATH_FIELD = "templatesDirPath";
    private static final String OUTPUT_DIR_PATH_FIELD = "outputDirPath";
    private static final String DEFAULT_PROPERTIES_FIELD = "defaultProperties";

    private JobGenerator jobGenerator;

    @Before
    public void setUp() {
        jobGenerator = new JobGenerator();
    }

    @Test
    public void shouldGenerateFiles() throws IOException {

        jobGenerator.generateJobs(PROPERTIES_SRC_DIR, TEMPLATES_SRC_DIR, OUTPUT_DIR);

        String expectedContent = FileUtils.readFileToString(new File(EXPECTED_JOB_FILE));
        String actualContent = FileUtils.readFileToString(new File(EXPECTED_OUTPUT_DIR + "/" + EXPECTED_FILENAME));

        assertThat(actualContent, is(expectedContent));
    }

    @Test
    public void shouldGenerateFilesBasedOnProperties() throws IOException {
        jobGenerator.generateJobs(DEFAULT_PROPERTIES);

        String expectedContent = FileUtils.readFileToString(new File(EXPECTED_JOB_FILE));
        String actualContent = FileUtils.readFileToString(new File(EXPECTED_OUTPUT_DIR + "/" + EXPECTED_FILENAME));

        assertThat(actualContent, is(expectedContent));
    }

    @Test
    public void shouldExecuteMojoWithProperties() throws MojoFailureException, MojoExecutionException {
        JobGenerator jobGenerator = Mockito.mock(JobGenerator.class);

        ReflectionTestUtils.setField(jobGenerator, DEFAULT_PROPERTIES_FIELD, DEFAULT_PROPERTIES);

        Mockito.doCallRealMethod().when(jobGenerator).execute();

        jobGenerator.execute();

        Mockito.verify(jobGenerator).generateJobs(DEFAULT_PROPERTIES);
    }

    @Test
    public void shouldExecuteMojoWithConfiguration() throws MojoFailureException, MojoExecutionException {
        JobGenerator jobGenerator = Mockito.mock(JobGenerator.class);

        ReflectionTestUtils.setField(jobGenerator, PROPERTIES_DIR_PATH_FIELD, PROPERTIES_SRC_DIR);
        ReflectionTestUtils.setField(jobGenerator, TEMPLATES_DIR_PATH_FIELD, TEMPLATES_SRC_DIR);
        ReflectionTestUtils.setField(jobGenerator, OUTPUT_DIR_PATH_FIELD, OUTPUT_DIR);

        Mockito.doCallRealMethod().when(jobGenerator).execute();

        jobGenerator.execute();

        Mockito.verify(jobGenerator).generateJobs(PROPERTIES_SRC_DIR, TEMPLATES_SRC_DIR, OUTPUT_DIR);
    }

    @Test
    public void shouldNotCreateOutputDirectory() throws IOException {
        jobGenerator.generateJobs(NOT_EXISTING_DIRECTORY, NOT_EXISTING_DIRECTORY, OUTPUT_DIR);

        File outputDirectory = new File(OUTPUT_DIR);

        assertThat(outputDirectory.list(), is(isNull()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowEmptyPropertiesDirPath() {
        jobGenerator.generateJobs(EMPTY, TEMPLATES_SRC_DIR, OUTPUT_DIR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowEmptyTemplatesDirPath() {
        jobGenerator.generateJobs(PROPERTIES_SRC_DIR, EMPTY, OUTPUT_DIR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowEmptyOutputDirPath() {
        jobGenerator.generateJobs(PROPERTIES_SRC_DIR, TEMPLATES_SRC_DIR, EMPTY);
    }

    @After
    public void cleanUp() throws IOException {
        FileUtils.deleteDirectory(new File(OUTPUT_DIR));
    }

}

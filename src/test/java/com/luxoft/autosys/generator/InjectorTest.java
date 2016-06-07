package com.luxoft.autosys.generator;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.fail;

public class InjectorTest {

    private static final String FILE_WITH_INJECTABLE_ENV = "src/test/resources/templates/dir/inside/moreinside/evenmoreinside/INSIDE_JOB.jil";
    private static final String EXPECTED_INJECT = "src/test/resources/injector-res/INJECTED.jil";
    private static final String FILE_WITH_NOT_INJECTABLE_ENV = "src/test/resources/templates/dir/inside/moreinside/evenmoreinside/WONT_INJECT_JOB.jil";
    private static final String EXPECTED_NO_INJECT = "src/test/resources/injector-res/NOT_INJECTED.jil";
    private static final String ENV = "TEST";
    private static final String NOT_APPLICABLE_ENV = "ENV";

    private static final String PROPERTIES = "src/test/resources/properties/test.properties";
    private static final String TEMPLATE = "src/test/resources/templates/TEMPLATE_JIL_FILE.jil";
    private static final String EXPECTED_INJECTED_PROPERTIES = "src/test/resources/TEMPLATE_JIL_FILE_TEST.jil";

    private static final String TEMPLATE_WITH_PRECONDITIONS = "src/test/resources/templates/TEMPLATE_WITH_PRECONDITIONS.jil";

    private Injector injector = new Injector();

    @Test
    public void shouldInjectBlock() throws IOException {

        String given = FileUtils.readFileToString(new File(FILE_WITH_INJECTABLE_ENV));
        String expected = FileUtils.readFileToString(new File(EXPECTED_INJECT));

        String actual = injector.inject(given, ENV);

        Assert.assertThat(actual, is(expected));

    }

    @Test
    public void shouldNotInjectBlock() throws IOException {
        String given = FileUtils.readFileToString(new File(FILE_WITH_NOT_INJECTABLE_ENV));
        String expected = FileUtils.readFileToString(new File(EXPECTED_NO_INJECT));

        String actual = injector.inject(given, ENV);

        Assert.assertThat(actual, is(expected));
    }

    @Test
    public void shouldInjectProperties() throws IOException {
        String given = FileUtils.readFileToString(new File(TEMPLATE));

        InputStream input = new FileInputStream(new File(PROPERTIES));
        Properties properties = new Properties();
        properties.load(input);

        String expected = FileUtils.readFileToString(new File(EXPECTED_INJECTED_PROPERTIES));

        String actual = injector.inject(given, properties);

        Assert.assertThat(actual, is(expected));

    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowException() throws IOException {
        String given = FileUtils.readFileToString(new File(TEMPLATE_WITH_PRECONDITIONS));

        injector.inject(given, NOT_APPLICABLE_ENV);

    }

}

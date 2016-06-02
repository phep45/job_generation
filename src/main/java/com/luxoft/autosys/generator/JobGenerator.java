package com.luxoft.autosys.generator;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * This goal will generate jil files.
 *
 * Pawel Rosner
 *
 * @goal jils-plg
 */
public class JobGenerator extends AbstractMojo {
    private static final Logger LOG = LoggerFactory.getLogger(JobGenerator.class);

    private static final String ENVIRONMENT = "ENV";
    private static final String PLACEHOLDER_BEGIN = "\\$\\{";
    private static final String PLACEHOLDER_END = "\\}";
    private static final String JIL_EXTENSION = ".jil";
    private static final String SEPARATOR = "/";
    private static final String PROPERTIES_EXTENSION = ".properties";
    private static final String PROPERTIES_SRC = "PROPERTIES_SRC";
    private static final String TEMPLATES_SRC = "TEMPLATES_SRC";
    private static final String OUTPUT_DIR = "FILES_DST";
    private static final String ENV = "\\%ENV\\%";
    private static final String LAST_UNDERSCORE = "_$";
    private static final String UNDERSCORE = "_";

    /**
     * Path to jils properties directory.
     *
     * @parameter
     *
     */
    private String propertiesDirPath;


    /**
     * Path to jils templates directory.
     *
     * @parameter
     */
    private String templatesDirPath;


    /**
     * Path to output directory for created jils.
     *
     * @parameter
     */
    private String outputDirPath;

    /**
     * Path to default properties.
     *
     * @parameter
     */
    private String defaultProperties = EMPTY;

    public void generateJobs(String propertiesDirPath, String templatesDirPath, String outputDir) {
        Preconditions.checkArgument(isNotBlank(propertiesDirPath), "propertiesDirPath should not be empty");
        Preconditions.checkArgument(isNotBlank(templatesDirPath), "templatesDirPath should not be empty");
        Preconditions.checkArgument(isNotBlank(outputDir), "outputDirPath should not be empty");

        File autosysPropertiesDir = new File(propertiesDirPath);
        File templatesDir = new File(templatesDirPath);

        if(isDirEmpty(autosysPropertiesDir, templatesDir)) {
            return;
        }

        for (File property : autosysPropertiesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(PROPERTIES_EXTENSION))) {
            for (File template : templatesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(JIL_EXTENSION))) {
                try {

                    generate(property, template, outputDir);

                } catch (IOException e) {
                    LOG.error("Could not read file.", e);
                }
            }
        }
        LOG.info("Job files created in: {}", outputDir);
    }

    public void generateJobs(String propertyFile) {
        initWithApplicationProperties(propertyFile);
        generateJobs(propertiesDirPath, templatesDirPath, outputDirPath);
    }

    private void generate(File propertyFile, File templateFile, String outputDir) throws IOException {
        LOG.debug("Generating jil for property file: {}, template file: {}", propertyFile.getName(), templateFile.getName());

        Properties properties = new Properties();
        String templateFileString = FileUtils.readFileToString(templateFile);

        String environment;

        try (InputStream input = new FileInputStream(propertyFile)) {
            properties.load(input);
            environment = properties.getProperty(ENVIRONMENT);
            for (String name : properties.stringPropertyNames()) {
                templateFileString = templateFileString.replaceAll(PLACEHOLDER_BEGIN + name + PLACEHOLDER_END, properties.getProperty(name));
            }
        }

        String pathname = outputDir + SEPARATOR + environment.toLowerCase() + SEPARATOR + templateFile.getName().replaceAll(ENV, EMPTY).replaceAll(JIL_EXTENSION, EMPTY).replaceAll(LAST_UNDERSCORE, EMPTY) + UNDERSCORE + environment + JIL_EXTENSION;
        File outputFile = new File(pathname);
        FileUtils.writeStringToFile(outputFile, templateFileString);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isNotBlank(defaultProperties)) {
            LOG.info("Executing mojo with property file: {}", defaultProperties);
            generateJobs(defaultProperties);
        } else {
            LOG.info("Executing mojo with configuration");
            generateJobs(propertiesDirPath, templatesDirPath, outputDirPath);
        }
    }

    private void initWithApplicationProperties(String defaultProperties) {
        Properties applicationProperties = new Properties();
        try (InputStream input = new FileInputStream(new File(defaultProperties))) {
            applicationProperties.load(input);

            propertiesDirPath = applicationProperties.getProperty(PROPERTIES_SRC);
            templatesDirPath = applicationProperties.getProperty(TEMPLATES_SRC);
            outputDirPath = applicationProperties.getProperty(OUTPUT_DIR);

        } catch (IOException e) {
            LOG.error("Could not read default application property file.", e);
        }
    }

    private boolean isDirEmpty(File ... dirs) {
        for(File f : dirs) {
            if(f.list() == null || f.list().length == 0) {
                LOG.warn("Directory {} is empty or does not exist. NO files will be generated.", f.getAbsolutePath());
                return true;
            }
        }
        return false;
    }

}

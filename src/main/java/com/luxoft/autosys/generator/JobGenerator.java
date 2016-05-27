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

/**
 * This goal will generate jil files.
 *
 * @goal jils-generator
 */
public class JobGenerator extends AbstractMojo {
    private static final Logger LOG = LoggerFactory.getLogger(JobGenerator.class);

    private static final String ENVIRONMENT = "ENV";
    private static final String PLACEHOLDER_BEGIN = "\\$\\{";
    private static final String PLACEHOLDER_END = "\\}";
    private static final String ENV_AND_EXTENSION = "\\%ENV\\%\\.jil";
    private static final String JIL_EXTENSION = ".jil";
    private static final String SEPARATOR = "/";
    private static final String PROPERTIES_EXTENSION = ".properties";

    private static final String DEFAULT_APPLICATION_PROPERTIES = "default.properties";
    private static final String PROPERTIES_SRC = "PROPERTIES_SRC";
    private static final String TEMPLATES_SRC = "TEMPLATES_SRC";
    private static final String OUTPUT_DIR = "FILES_DST";

    private File autosysPropertiesDir;
    private File templatesDir;
    private String outputDir;

    public JobGenerator() {
    }

    public JobGenerator(String propertiesDir, String templatesDir, String outputDir) {
        this.autosysPropertiesDir = new File(propertiesDir);
        this.templatesDir = new File(templatesDir);
        this.outputDir = outputDir;

        Preconditions.checkArgument(autosysPropertiesDir.isDirectory(), "propertiesDir is not a directory");
        Preconditions.checkArgument(this.templatesDir.isDirectory(), "templatesDir is not a directory");
    }

    public void generateJobs() {
        for (File property : autosysPropertiesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(PROPERTIES_EXTENSION))) {
            for (File template : templatesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(JIL_EXTENSION))) {
                try {

                    generate(property, template);

                } catch (IOException e) {
                    LOG.error("Could not read file.", e);
                }
            }
        }
        LOG.info("Job files created in: {}", outputDir);
    }

    private void generate(File propertyFile, File templateFile) throws IOException {
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

        File outputFile = new File(outputDir + SEPARATOR + environment.toLowerCase() + SEPARATOR + templateFile.getName().replaceAll(ENV_AND_EXTENSION, "") + environment + JIL_EXTENSION);
        FileUtils.writeStringToFile(outputFile, templateFileString);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Properties applicationProperties = new Properties();
        try (InputStream input = new FileInputStream(new File(DEFAULT_APPLICATION_PROPERTIES))) {
            applicationProperties.load(input);

            this.autosysPropertiesDir = new File(applicationProperties.getProperty("PROPERTIES_SRC"));
            this.templatesDir = new File(applicationProperties.getProperty("TEMPLATES_SRC"));
            this.outputDir = applicationProperties.getProperty("FILES_DST");

        } catch (IOException e) {
            LOG.error("Could not read default application property file.", e);
        }

        generateJobs();
    }

    public static void main(String[] args) {
        String propertiesSrc = "";
        String templatesSrc = "";
        String outputDst = "";

        if (hasThreeParameters(args)) {
            propertiesSrc = args[0];
            templatesSrc = args[1];
            outputDst = args[2];
        } else if (hasNoParameters(args)) {
            Properties applicationProperties = new Properties();
            try (InputStream input = new FileInputStream(new File(DEFAULT_APPLICATION_PROPERTIES))) {
                applicationProperties.load(input);

                propertiesSrc = applicationProperties.getProperty(PROPERTIES_SRC);
                templatesSrc = applicationProperties.getProperty(TEMPLATES_SRC);
                outputDst = applicationProperties.getProperty(OUTPUT_DIR);

            } catch (IOException e) {
                LOG.error("Could not read default application property file.", e);
            }
        } else {
            LOG.info("Usage: JobGenerator [properties-src-dir] [templates-src-dir] [output-dst-dir] OR no arguments for default properties");
            return;
        }

        if (EMPTY.equals(propertiesSrc) || EMPTY.equals(templatesSrc) || EMPTY.equals(outputDst)) {
            LOG.error("Invalid directory path");
            return;
        }


        new JobGenerator(propertiesSrc, templatesSrc, outputDst).generateJobs();
    }

    private static boolean hasNoParameters(String[] args) {
        return args.length == 0;
    }

    private static boolean hasThreeParameters(String[] args) {
        return args.length == 3;
    }


}

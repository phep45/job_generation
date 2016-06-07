package com.luxoft.autosys.generator;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.regex.Pattern;

public class Injector {
    private static final Logger LOG = LoggerFactory.getLogger(Injector.class);

    private static final String BLOCK_REGEX = "(@\\w+,?){1,100}\\s<<\\{(?s).*[\\n\\r].*\\}>>";
    private static final String BLOCK_PLACEHOLDER_BEGIN = "(@\\w{1,100},?){1,100} ?<<\\{[\\r\\n]{0,2}";
    private static final String BLOCK_PLACEHOLDER_END = "\\}>>[\\r\\n]{0,2}";
    private static final String DST_ENV_PLACEHOLDER = "@";

    private static final String PLACEHOLDER_BEGIN = "\\$\\{";
    private static final String PLACEHOLDER_END = "\\}";
    private static final String ENVIRONMENT_PRECONDITIONS = "^(@\\w{1,100},?)+";

    public String inject(String fileString, Properties properties) {
        String result = fileString;

        for (String name : properties.stringPropertyNames()) {
            result = result.replaceAll(PLACEHOLDER_BEGIN + name + PLACEHOLDER_END, properties.getProperty(name));
        }

        return result;
    }

    public String inject(String fileString, String environment) {
        if(hasPreconditions(fileString)) {
            if(hasProperEnvironment(fileString, environment)) {
                String str = cleanEnvironmentPreconditions(fileString);
                return process(str, environment);
            } else {
                throw new IllegalStateException("Environment {} not applicable for this template file");
            }
        } else {
            return process(fileString, environment);
        }
    }

    private String process(String fileString, String environment) {
        boolean hasBlock = Pattern.compile(BLOCK_REGEX).matcher(fileString).reset().find();
        if (hasBlock) {
            LOG.debug("Block detected");
            String dstEnvRegex = DST_ENV_PLACEHOLDER + environment.toLowerCase();
            boolean isForThisEnvironment = Pattern.compile(dstEnvRegex).matcher(fileString).find();
            if (isForThisEnvironment) {
                LOG.debug("Environments matches");
                return fileString.replaceAll(BLOCK_PLACEHOLDER_BEGIN, StringUtils.EMPTY).replaceAll(BLOCK_PLACEHOLDER_END, StringUtils.EMPTY);
            } else {
                LOG.debug("Environments do NOT matches");
                return fileString.replaceAll(BLOCK_REGEX, StringUtils.EMPTY);
            }
        }
        LOG.debug("Block NOT detected");
        return fileString;
    }


    private boolean hasPreconditions(String fileString) {
        return Pattern.compile(ENVIRONMENT_PRECONDITIONS).matcher(fileString).find();
    }

    private boolean hasProperEnvironment(String fileString, String environment) {
        String envRegex = DST_ENV_PLACEHOLDER + environment.toLowerCase();
        return Pattern.compile(envRegex).matcher(fileString).find();
    }

    private String cleanEnvironmentPreconditions(String fileString) {
        return fileString.replaceAll(ENVIRONMENT_PRECONDITIONS, StringUtils.EMPTY).replaceFirst("\r\n", StringUtils.EMPTY);
    }
}

package com.luxoft.autosys.generator;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Injector {
    private static final Logger LOG = LoggerFactory.getLogger(Injector.class);

    private static final String BLOCK_REGEX = "(@\\w+,?){1,100}\\s<<\\{([\\w\\s:=-_$\\{\\}]*)\\}\\>\\>";
    private static final String DST_ENV_PLACEHOLDER = "@";

    private static final String PLACEHOLDER_BEGIN = "\\$\\{";
    private static final String PLACEHOLDER_END = "\\}";
    private static final String ENVIRONMENT_PRECONDITIONS = "^(@\\w{1,100},?)+";
    private static final int BLOCK_CONTENT = 6;
    private static final String DOLLAR_SIGN = "\\$";
    private static final String TEMPORARY_VALUE = "//////DOLLAR//////SIGN//////STRING//////";
    private static final String SLASH_AND_DOLLAR = "\\\\\\$";
    private static final String BLOCK_REGEX_BEGIN = "(((@\\w{0,100},?){0,100})*";
    private static final String BLOCK_REGEX_END = "((,@\\w{0,100},?){0,100})* ?\\<\\<\\{([\\w\\s:=-_$\\{\\}]*)\\}\\>\\>)";

    public String inject(String fileString, Properties properties) {
        String result = fileString;

        for (String name : properties.stringPropertyNames()) {
            result = result.replaceAll(PLACEHOLDER_BEGIN + name + PLACEHOLDER_END, properties.getProperty(name));
        }

        return result;
    }

    public String inject(String fileString, String environment) {
        if (hasPreconditions(fileString)) {
            if (hasProperEnvironment(fileString, environment)) {
                String str = cleanEnvironmentPreconditions(fileString);
                return process(str, environment);
            } else {
                throw new IllegalStateException("Environment " + environment + " not applicable for this template file");
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
            String blockRegex = BLOCK_REGEX_BEGIN + dstEnvRegex + BLOCK_REGEX_END;
            boolean isForThisEnvironment = Pattern.compile(blockRegex).matcher(fileString).find();

            Matcher m = Pattern.compile(blockRegex).matcher(fileString);


            if (isForThisEnvironment && m.find()) {
                LOG.debug("Environments matches");
                String contentOfBlock = getContentOfBlock(m);
                return process(fileString.replaceFirst(blockRegex, contentOfBlock), environment);
            } else {
                LOG.debug("Environments do NOT matches");
                String str = fileString.replaceFirst(BLOCK_REGEX, StringUtils.EMPTY);
                return process(str, environment);
            }
        }
        LOG.debug("Block NOT detected");
        return fileString;
    }

    private String getContentOfBlock(Matcher m) {
        String tmp = m.group(BLOCK_CONTENT);

        //must be done because $ is part of regex syntax
        tmp = tmp.replaceAll(DOLLAR_SIGN, TEMPORARY_VALUE);
        tmp = tmp.replaceAll(TEMPORARY_VALUE, SLASH_AND_DOLLAR);
        return tmp;
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

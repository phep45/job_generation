# job_generation
Maven plugin for automatic generation autosys jobs

EXAMPLES:

when using custom configuration:

<plugins>
    <plugin>
        <groupId>com.luxoft</groupId>
        <artifactId>jils-generator-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
            <execution>
                <phase>compile</phase>
                <goals>
                    <goal>jils-plg</goal>
                </goals>
                <configuration>
                    <propertiesDirPath>src/main/resources/properties</propertiesDirPath>
                    <outputDirPath>target/jils</outputDirPath>
                    <templatesDirPath>src/main/resources/templates</templatesDirPath>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>


when using property file with configuration:

<plugins>
    <plugin>
        <groupId>com.luxoft</groupId>
        <artifactId>jils-generator-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
            <execution>
                <phase>compile</phase>
                <goals>
                    <goal>jils-plg</goal>
                </goals>
                <configuration>
                    <defaultProperties>src/main/resources/default.properties</defaultProperties>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>

property file:

PROPERTIES_SRC=src/main/resources/properties
TEMPLATES_SRC=src/main/resources/templates
FILES_DST=target/jils

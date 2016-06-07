# job_generation
Maven plugin for automatic generation autosys jobs

<h3>EXAMPLES:</h3>

when using custom configuration:

```xml
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
                    <!-- all three tags are required -->
                    <propertiesDirPath>src/main/resources/properties</propertiesDirPath>
                    <outputDirPath>target/jils</outputDirPath>
                    <templatesDirPath>src/main/resources/templates</templatesDirPath>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

when using property file with configuration:

```xml
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
```

property file:


<code>PROPERTIES_SRC=src/main/resources/properties</code></br>
<code>TEMPLATES_SRC=src/main/resources/templates</code></br>
<code>FILES_DST=target/jils</code></br>

NOTE:

```xml
<defaultProperties>
```
Have priority over:

```xml
<propertiesDirPath>
<outputDirPath>
<templatesDirPath>
```

when both are used.

<h3>IN-FILE CONFIGURATION</h3>

@env,@env2 <- at first line of file indicate for which environments jil should be prepared

```
@env,@env2,@env3 <<{
block of properties
to be injected into
indicated environments
}>>
```
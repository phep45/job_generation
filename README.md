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

```
@env,@env2 <- at first line of file indicate for which environments jil should be prepared
```

```
@env,@env2,@env3 <<{
block of properties
to be injected into
indicated environments
}>>
```

<h3>EXAMPLES</h3>

```
@dev,@qa,@prod
VERY_SERIOUS_AND_IMPORTANT_JOB_${ENV}

name:VERY_SERIOUS_AND_IMPORTANT_JOB_${ENV}
@dev <<{
property: very_important_property
location: much_secret
}>>
machine: ${MACHINE}
@qa,@dev <<{
task: so_not_obvious
}>>

```

output files will look like this:
```
VERY_SERIOUS_AND_IMPORTANT_JOB_DEV

name:VERY_SERIOUS_AND_IMPORTANT_JOB_DEV
property: very_important_property
location: much_secret
machine: SOME_DEV_MACHINE
task: so_not_obvious
```

```
VERY_SERIOUS_AND_IMPORTANT_JOB_QA

name:VERY_SERIOUS_AND_IMPORTANT_JOB_QA
machine: SOME_QA_MACHINE
task: so_not_obvious
```

```
VERY_SERIOUS_AND_IMPORTANT_JOB_PROD

name:VERY_SERIOUS_AND_IMPORTANT_JOB_PROD
machine: SOME_PROD_MACHINE

```
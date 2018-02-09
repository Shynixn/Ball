Including the Core Project
==========================

.. toctree::

Prerequisites
~~~~~~~~~~~~~

* You are familiar with a Java Build Tool like Maven or Gradle.
* You know how to create a FAT jar and relocate dependencies.

This guide does not explain the mentioned concepts above so if you are not sure it is probably better
to use the second method how to use the API.

Adding the dependency
~~~~~~~~~~~~~~~~~~~~~

* The dependency is available in the central maven repository so you do not have to add an additional repository.
* The project called ball-bukkit-core includes all necessary stuff to create a working ball.
* The Ball Plugin and the plugin BlockBall also use this method and dependency in order to work properly.

**Maven**:
::
    <dependency>
        <groupId>com.github.shynixn.ball</groupId>
        <artifactId>ball-bukkit-core</artifactId>
        <version>2018.2.1</version>
        <scope>compile</scope>
    </dependency>


Creating a FAT jar
~~~~~~~~~~~~~~~~~~

* Simply add this lines in order to create a FAT plugin.jar file when executing 'mvn package'
* The compiled code from the ball-bukkit-core plugin automatically gets added to your plugin.jar.

**Maven**:
::
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>


Relocating the API
~~~~~~~~~~~~~~~~~~

* This step is also necessary as it is very important to relocate the dependency. If you do not do this you might cause compatibility issues to other plugins on your server!
* You simply append this to your existing configuration.
* Replace <your.plugin.package.path> with your **unique** package names.

**Maven**:
::
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <configuration>
                    <relocations>
                        <relocation>
                            <pattern>com.github.shynixn.ball</pattern>
                            <shadedPattern><your.plugin.package.path>.com.github.shynixn.ball</shadedPattern>
                        </relocation>
                    </relocations>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

Call in your plugin onEnable() Method
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

* In order to initialize the Ball API simply call the following in your plugin onEnabled() function.
**Java**:
::
    @Override
    public void onEnable() {
       BallCoreManager ballCoreManager = new BallCoreManager(this);

       //your code
    }


Putting all together
~~~~~~~~~~~~~~~~~~~~

* You can now continue with using the API in the Developer API section.

**Final Maven pom.xml**:
::
    <dependencies>
        <dependency>
            <groupId>com.github.shynixn.ball</groupId>
            <artifactId>ball-bukkit-core</artifactId>
            <version>2018.2.1</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <configuration>
                    <relocations>
                        <relocation>
                            <pattern>com.github.shynixn.ball</pattern>
                            <shadedPattern><your.plugin.package.path>.com.github.shynixn.ball</shadedPattern>
                        </relocation>
                    </relocations>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
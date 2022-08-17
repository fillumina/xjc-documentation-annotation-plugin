# XJC plugin to bring XSD descriptions into annotations of generated classes


![Maven Central](https://img.shields.io/maven-central/v/com.fillumina/xjc-documentation-annotation-plugin.svg)

 - `2.0.0` Forked from [Hubbitus's xjc-documentation-annotation-plugin](https://github.com/Hubbitus/xjc-documentation-annotation-plugin), mavenized, refactored and bugfixed. A simple java test suite has been added. It's released on Maven central (see [usage](#mavenusage) ).

-------

## What it does: \<annotation>\<documentation> -> Java class annotations

Said we have this object described in XSD:

```xml
  <xs:complexType name="Customer">
    <xs:annotation>
      <xs:documentation>Пользователь</xs:documentation>
      </xs:annotation>
    <xs:sequence>
      <xs:element name="name" type="xs:string">
        <xs:annotation>
          <xs:documentation>Фамилия и имя</xs:documentation>
        </xs:annotation>
      </xs:element>
    </xs:sequence>
  </xs:complexType>
```

We run xjc like:

    xjc -npa -no-header -d src/main/generated-java/ -p xsd.generated scheme.xsd

And got class like (getters, setters and any annotations omitted for simplicity):

```java
public class Customer {
  @XmlElement(required = true)
  protected String name;
}
```

**But in my case I want known how to class and fields was named in source file!**
So it what this plugin do!

So you get:

```java
@XsdInfo(name = "Пользователь", xsdElementPart = "<complexType name=\"Customer\">\n  <complexContent>\n    <restriction base=\"{http://www.w3.org/2001/XMLSchema}anyType\">\n      <sequence>\n        <element name=\"name\" type=\"{http://www.w3.org/2001/XMLSchema}string\"/>\n      </sequence>\n    </restriction>\n  </complexContent>\n</complexType>")
public class Customer {

    @XmlElement(required = true)
    @XsdInfo(name = "Фамилия и имя")
    protected String name;
}
```

---

## How to use

### Manual call in commandline
If you want run it manually ensure jar class with plugin in run classpath and just add option `-XPluginDescriptionAnnotation`. F.e.:

    xjc -npa -no-header -d src/main/generated-java/ -p xsd.generated -XPluginDescriptionAnnotation scheme.xsd

### Call from Java/Groovy
```groovy
  Driver.run(
    [
       '-XPluginDescriptionAnnotation'
        ,'-d', generatedClassesDir.absolutePath
        ,'-p', 'info.hubbitus.generated.test'
        ,'Example.xsd'
    ] as String[]
    ,new XJCListener() {...}
  )
```

See test [XJCPluginDescriptionAnnotationTest](src/test/groovy/info/hubbitus/XJCPluginDescriptionAnnotationTest.groovy) for example.

### Use from Maven
<a id="mavenusage"></a>

The project is published on Maven Central, to be used it must be defined as a [maven-jaxb2-plugin](https://www.mojohaus.org/jaxb2-maven-plugin/Documentation/v2.2/index.html) plugin:

```xml
  <plugin>
    <groupId>org.jvnet.jaxb2.maven2</groupId>
    <artifactId>maven-jaxb2-plugin</artifactId>
    <version>0.14.0</version>
    <executions>
      <execution>
        <id>jaxb-generate</id>
        <goals>
          <goal>generate</goal>
        </goals>
        <configuration>
          <schemas>
            <schema>
                <url>file://${basedir}/src/main/resources/xsd/some-schema.xsd</url>
            </schema>
          </schemas>
          <bindingDirectory>${xsd.dir}</bindingDirectory>
          <bindingIncludes>
            <include>*.xjb</include>
          </bindingIncludes>
          <extension>true</extension>
          <args>
            <arg>-XPluginDescriptionAnnotation</arg>
          </args>
          <plugins>
          
            <!-- PluginDescriptionAnnotation -->
            <plugin>
              <groupId>com.fillumina</groupId>
              <artifactId>xjc-documentation-annotation-plugin</artifactId>
              <version>2.0.0</version>
            </plugin>
            
          </plugins>
          <generatePackage>com.whatever</generatePackage>
          <generateDirectory>${project.build.directory}/generated-sources/xjc</generateDirectory>
          <strict>false</strict>
          <verbose>true</verbose>
          <removeOldOutput>true</removeOldOutput>
          <clearOutputDir>true</clearOutputDir>
          <forceRegenerate>false</forceRegenerate>
        </configuration>
      </execution>
    </executions>
  </plugin>

```

And of course the created classes needs `info.hubbitus.annotation.XsdInfo` so the dependency must be added to them as well (be sure to use the same version):

```xml
<dependency>
  <groupId>com.fillumina</groupId>
  <artifactId>xjc-documentation-annotation-plugin</artifactId>
  <version>2.0.0</version>
</dependency>
```

---

## Rationale (why it is born)
For our integration we have task load big amount of `XSD` files into `MDM` software (proprietary [Unidata](https://unidata-platform.com/)).

`XJC` is good tool for generate Java `DTO` classes from `XSD` specification. It was first part ow way.
Then I got excellent [reflections](https://github.com/ronmamo/reflections) library and travers generated classes.

Problem was I was not be able name my model items with original annotations! Despite `XJC` place initial Javadoc which contains description and related part of `XML` element it have several problems:

1. That only for class, and absent fo fields.
2. Even for class I can't use javadoc in runtime

First approach to parse `XSD` for documentation on groovy works, but was very fragile and always require get updates and hacks.

I long time search way to bring such annotations into `DTO` classes itself to do not do work twice (generate classes and again parse `XSD` files manually).
I did not found solution. And it is the reason born of that plugin.

---

## Licensed under MIT

MIT License

Copyright (c) 2020 Pavel Alexeev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
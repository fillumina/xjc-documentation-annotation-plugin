package info.hubbitus;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SimpleClassCreatorTest {

    @Test
    public void testSimpleClass() {
        Class<?> clz = SimpleClassCreator.createClass("com.fillumina", "Example",
            new StringBuilder()
                .append("package com.fillumina;\n")
                .append("public class Example {}\n")
                .toString());

        assertEquals("com.fillumina", clz.getPackageName());
        assertEquals("Example", clz.getSimpleName());
        assertEquals("com.fillumina.Example", clz.getCanonicalName());
    }

    @Test
    public void testClassExtendingAnnotation() {
        Class<?> clz = SimpleClassCreator.createClass("com.fillumina", "AnnotationExample",
            new StringBuilder()
                .append("package com.fillumina;\n")
                .append("import java.lang.annotation.Retention;\n")
                .append("import java.lang.annotation.RetentionPolicy;\n")
                .append("@Retention(RetentionPolicy.RUNTIME)\n")
                .append("public @interface AnnotationExample {\n")
                .append("	String name();\n")
                .append("	String xsdElementPart() default \"\";\n")
                .append("}\n")
                .toString());

        assertEquals("com.fillumina", clz.getPackageName());
        assertEquals("AnnotationExample", clz.getSimpleName());
        assertEquals("com.fillumina.AnnotationExample", clz.getCanonicalName());
    }

}

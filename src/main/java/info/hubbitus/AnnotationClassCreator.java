package info.hubbitus;

/**
 *
 * @author Francesco Illuminati
 */
class AnnotationClassCreator {

    public static final AnnotationClassCreator INSTANCE = new AnnotationClassCreator();
    private AnnotationClassCreator() {}

    public static Class<?> create(String param) {
        int idx = param.lastIndexOf(".");
        String pkgName = param.substring(0, idx);
        String className = param.substring(idx + 1);
        String source = createSource(pkgName, className);
        Class<?> clz = SimpleClassCreator.createClass(pkgName, className, source);
        return clz;
    }

    private static String createSource(String pkgName, String className) {
        return new StringBuilder()
                .append("package ").append(pkgName).append(";\n")
                .append("import java.lang.annotation.Retention;\n")
                .append("import java.lang.annotation.RetentionPolicy;\n")
                .append("@Retention(RetentionPolicy.RUNTIME)\n")
                .append("public @interface ").append(className).append(" {\n")
                .append("	String name();\n")
                .append("	String xsdElementPart() default \"\";\n")
                .append("}\n")
                .toString();
    }

}

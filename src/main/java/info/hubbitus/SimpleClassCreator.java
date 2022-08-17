package info.hubbitus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/**
 * see: https://www.edureka.co/community/66796/how-to-generate-a-class-at-runtime
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SimpleClassCreator {

    public static final SimpleClassCreator INSTANCE = new SimpleClassCreator();
    private SimpleClassCreator() {}

    // see https://stackoverflow.com/questions/1781091/java-how-to-load-class-stored-as-byte-into-the-jvm
    private static class ByteClassLoader extends URLClassLoader {

        private final Map<String, byte[]> extraClassDefs;

        public ByteClassLoader(URL[] urls, ClassLoader parent, Map<String, byte[]> extraClassDefs) {
            super(urls, parent);
            this.extraClassDefs = new HashMap<String, byte[]>(extraClassDefs);
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            byte[] classBytes = this.extraClassDefs.remove(name);
            if (classBytes != null) {
                return defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }

    }

    /**
     * Creates a dynamic class into the class loader and returns it using only JDK.
     * This can be used as a mockup.
     *
     * @param pkg required package (i.e. 'com.fillumina')
     * @param name required class name (i.e. 'FooBar')
     * @return the created class
     */
    public static Class<?> createClass(String pkg, String name, String source) {

        final String canonicalName = pkg + "." + name;
        final String fullClassName = canonicalName.replace('.', '/');

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final URI uri = URI.create("file://" + fullClassName + ".java");

        final SimpleJavaFileObject simpleJavaFileObject = new SimpleJavaFileObject(
                uri,
                JavaFileObject.Kind.SOURCE) {

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }

            @Override
            public OutputStream openOutputStream() throws IOException {
                return byteArrayOutputStream;
            }
        };

        final JavaFileManager javaFileManager = new ForwardingJavaFileManager(
                ToolProvider
                        .getSystemJavaCompiler()
                        .getStandardFileManager(null, null, null)) {

            @Override
            public JavaFileObject getJavaFileForOutput(
                    Location location, String className,
                    JavaFileObject.Kind kind,
                    FileObject sibling) {
                return simpleJavaFileObject;
            }
        };

        ToolProvider.getSystemJavaCompiler().getTask(
                null, javaFileManager, null, null, null,
                List.of(simpleJavaFileObject)).call();

        // then the bytes that make up the class are loaded into the class loader
        final byte[] bytes = byteArrayOutputStream.toByteArray();

        Class<?> clz;
        try {
            clz = new ByteClassLoader(
                    new URL[]{uri.toURL()},
                    ClassLoader.getPlatformClassLoader(),
                    Map.of(canonicalName, bytes))
                    .findClass(canonicalName);
        } catch (ClassNotFoundException | MalformedURLException ex) {
            throw new RuntimeException(ex);
        }

        return clz;
    }

}

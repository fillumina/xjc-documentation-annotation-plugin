package info.hubbitus;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AnnotationClassCreatorTest {


    @Test
    public void testAnnotationClassCreation() {
        Class<?> clz =
                AnnotationClassCreator.INSTANCE.create("com.fillumina.AnnotationCreated");

        assertEquals("com.fillumina", clz.getPackageName());
        assertEquals("AnnotationCreated", clz.getSimpleName());
        assertEquals("com.fillumina.AnnotationCreated", clz.getCanonicalName());
    }

}

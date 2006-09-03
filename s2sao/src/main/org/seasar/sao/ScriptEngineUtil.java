package org.seasar.sao;

import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * @author bowez
 */
public class ScriptEngineUtil {

    public static final InputStream getScriptStream(String path) {
        if (path == null || path.length() == 0) {
            throw new IllegalArgumentException();
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream(path);
    }

    public static final String getScriptMethodName(Method method) {
        if (method == null) {
            throw new IllegalArgumentException();
        }
        if (method.isAnnotationPresent(MethodBinding.class)) {
            MethodBinding binding = method.getAnnotation(MethodBinding.class);
            String value = binding.value();
            if (value.length() > 0) {
                return value;
            }
        }
        return method.getName();
    }

}

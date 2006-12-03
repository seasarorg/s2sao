/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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

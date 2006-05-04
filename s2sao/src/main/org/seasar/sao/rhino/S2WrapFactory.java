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
package org.seasar.sao.rhino;

import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;
import org.seasar.framework.container.S2Container;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class S2WrapFactory extends WrapFactory {
	
    @SuppressWarnings("unchecked")
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope,
            Object javaObject, Class staticClass) {
        if (javaObject instanceof Map) {
            return new NativeMap(scope, Map.class.cast(javaObject));
        } else if (javaObject instanceof List) {
            return new NativeList(scope, List.class.cast(javaObject));
        } else if (javaObject instanceof S2Container) {
        	S2Container container = S2Container.class.cast(javaObject);
            return new NativeContainer(container);
        }
        return super.wrapAsJavaObject(cx, scope, javaObject, staticClass);
    }

}

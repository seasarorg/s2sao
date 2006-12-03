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
package org.seasar.sao.jruby;

import org.jruby.IRuby;
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Arity;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * @author bowez
 */
public class RubyComponentUtil {

    /**
     * JavaのオブジェクトをJRubyのオブジェクトに変換します。
     * 
     * @param runtime
     * @param javaComponent Javaオブジェクト
     * @return
     */
    public static IRubyObject convertJavaToRuby(IRuby runtime, Object javaComponent) {
        IRubyObject obj = JavaUtil.convertJavaToRuby(runtime, javaComponent);
        RubyModule proxy = getComponentProxy(runtime);
        if (!obj.isKindOf(proxy)) {
            obj.extendObject(proxy);
        }
        return obj;
    }

    public static RubyModule getComponentProxy(IRuby runtime) {
        RubyModule module = runtime.getModule("S2ComponentProxy");
        if (module == null) {
            module = runtime.defineModule("S2ComponentProxy");
            module.defineModuleFunction("method_missing", 
                    new JavaComponentCallback(runtime, Arity.required(1)));
        }
        return module;
    }

}

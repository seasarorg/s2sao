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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import org.jruby.IRuby;
import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Arity;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.callback.Callback;
import org.seasar.framework.container.S2Container;
import org.seasar.sao.ScriptBinding;
import org.seasar.sao.ScriptEngine;

import static org.seasar.sao.ScriptEngineUtil.*;

/**
 * 
 * @author bowez
 */
public class JRubyScriptEngine implements ScriptEngine {
    private static final long serialVersionUID = 8821201315656298440L;

    IRuby jRubyRuntime = Ruby.getDefaultInstance();
    S2Container container;
    
    public JRubyScriptEngine(S2Container container) {
        this.container = container;
    }

    public IRuby getRuntime() {
        return jRubyRuntime;
    }

    public void setRuntime(IRuby rubyRuntime) {
        if (jRubyRuntime == null) {
            throw new IllegalArgumentException("JRubyRuntime cannot be null.");
        }
        jRubyRuntime = rubyRuntime;
    }

    protected String getScriptFilePath(Class<?> sao) {
        if (sao == null) {
            throw new IllegalArgumentException();
        }
        if (sao.isAnnotationPresent(ScriptBinding.class)) {
            ScriptBinding binding = sao.getAnnotation(ScriptBinding.class);
            String value = binding.value();
            if (value.length() > 0) {
                return value;
            }
        }
        String path = sao.getName();
        path = path.replace('.', '/') + ".rb";
        return path;
    }

    public Object compile(Class<?> sao) throws Exception {
        String path = getScriptFilePath(sao);
        InputStream stream = getScriptStream(path);
        if (stream != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                jRubyRuntime.eval(jRubyRuntime.parse(reader, path));
                IRubyObject main = jRubyRuntime.getTopSelf();
                RubyModule s2module = getS2ContainerModule(jRubyRuntime, container);
                if (!main.isKindOf(s2module)) {
                    main.extendObject(s2module);
                }
                return main;
            }
            finally {
                stream.close();
            }
        }
        return null;
    }

    public boolean hasFunction(Object compiled, Method method) {
        IRubyObject main = IRubyObject.class.cast(compiled);
        return main.respondsTo(getScriptMethodName(method));
    }

    public Object invoke(Object compiled, Method method, Object[] args,
            Class<?> expectedClass) throws Throwable {
        IRubyObject main = IRubyObject.class.cast(compiled);
        IRubyObject[] rubyArgs = JavaUtil.convertJavaArrayToRuby(jRubyRuntime, args);
        IRubyObject result = main.callMethod(getScriptMethodName(method), rubyArgs);
        return JavaUtil.convertRubyToJava(result);
    }

    protected RubyModule getS2ContainerModule(IRuby runtime, S2Container container) {
        RubyModule module = runtime.getModule("S2Container");
        if (module == null) {
            module = runtime.defineModule("S2Container");
            module.defineModuleFunction("method_missing", 
                    new TopSelfMethodMissingHandler(container, runtime));
        }
        return module;
    }

    static class TopSelfMethodMissingHandler implements Callback {
        final S2Container container;
        final IRuby runtime;
        
        TopSelfMethodMissingHandler(S2Container container, IRuby runtime) {
            this.container = container;
            this.runtime = runtime;
        }

        public IRubyObject execute(IRubyObject recv, IRubyObject[] args) {
            if (0 < args.length) {
                String name = args[0].asSymbol();
                if (container.hasComponentDef(name)) {
                    Object component = container.getComponent(name);
                    return RubyComponentUtil.convertJavaToRuby(runtime, component);
                }
            }
            return recv.callMethod("method_missing", args);
        }

        public Arity getArity() {
            return Arity.required(1);
        }
    }
}

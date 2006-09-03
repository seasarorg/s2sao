package org.seasar.sao.jruby;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import org.jruby.IRuby;
import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Arity;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.callback.Callback;
import org.seasar.framework.container.S2Container;
import org.seasar.sao.ScriptBinding;
import org.seasar.sao.ScriptEngine;

import static org.seasar.sao.ScriptEngineUtil.*;

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
                IRubyObject main = getTopSelf(jRubyRuntime);
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

    protected IRubyObject getTopSelf(IRuby runtime) {
        IRubyObject main = jRubyRuntime.getTopSelf();
        RubyModule s2module = getS2ContainerModule(runtime);
        if (!main.isKindOf(s2module)) {
            main.extendObject(s2module);
        }
        return main;
    }
    
    RubyModule getS2ContainerModule(IRuby runtime) {
        RubyModule module = runtime.getModule("S2Continer");
        if (module == null) {
            module = runtime.defineModule("S2Container");
            module.defineModuleFunction("method_missing", 
                    new MethodMissingHandler(container, jRubyRuntime));
        }
        return module;
    }
    
    static class MethodMissingHandler implements Callback {
        final S2Container container;
        final IRuby runtime;
        MethodMissingHandler(S2Container container, IRuby runtime) {
            this.container = container;
            this.runtime = runtime;
        }
        public IRubyObject execute(IRubyObject recv, IRubyObject[] args) {
            if (0 < args.length) {
                String name = args[0].asSymbol();
                if (container.hasComponentDef(name)) {
                    Object component = container.getComponent(name);
                    IRubyObject obj = JavaUtil.convertJavaToRuby(runtime, component);
                    RubyModule javaBridge = getJavaMethodCallable(runtime, component);
                    if (!obj.isKindOf(javaBridge)) {
                        obj.extendObject(javaBridge);
                    }
                    return obj;
                }
            }
            return recv.callMethod("method_missing", args);
        }

        public Arity getArity() {
            return Arity.required(1);
        }
        
        RubyModule getJavaMethodCallable(IRuby runtime, Object component) {
            RubyModule module = runtime.getModule("JavaMethodCallable");
            if (module == null) {
                module = runtime.defineModule("JavaMethodCallable");
                module.defineModuleFunction("method_missing", new JavaMethodCallOnMethodMissing(runtime));
            }
            return module;
        }
    }
    
    static class JavaMethodCallOnMethodMissing implements Callback {
        final IRuby runtime;
        JavaMethodCallOnMethodMissing(IRuby runtime) {
            this.runtime = runtime;
        }
        public IRubyObject execute(IRubyObject recv, IRubyObject[] args) {
            if (args.length < 1) {
                return recv.callMethod("method_missing", args);
            }
            JavaObject javaObject = (JavaObject) recv;
            String methodName = args[0].asSymbol();
            Class[] argTypes = getArgTypes(args);
            Method method = loadMethod(recv.getJavaClass(), methodName, argTypes);
            if (method == null) {
                return recv.callMethod("method_missing", args);
            }
            Object result = null;
            try {
                result = method.invoke(javaObject.getValue(), getJavaArgs(args));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            return JavaUtil.convertJavaToRuby(runtime, result);
        }
        
        private Class[] getArgTypes(IRubyObject[] args) {
            if (args.length == 1) {
                return new Class[0];
            }
            Class[] argTypes = new Class[args.length-1];
            for (int i = 1; i < args.length; i++) {
                argTypes[i-1] = JavaUtil.convertRubyToJava(args[i]).getClass();
            }
            return argTypes;
        }
        
        private Object[] getJavaArgs(IRubyObject[] rubyArgs) {
            if (rubyArgs.length == 1) {
                return new Object[0];
            }
            Object[] args = new Object[rubyArgs.length-1];
            for (int i = 1; i < rubyArgs.length; i++) {
                args[i-1] = JavaUtil.convertRubyToJava(rubyArgs[i]);
            }
            return args;
        }
        
        private Method loadMethod(Class javaClass, String methodName, Class[] parameterTypes) {
            Method m;
            try {
                m = javaClass.getMethod(methodName, parameterTypes);
            }
            catch (SecurityException e) {
                throw new RuntimeException(e);
            }
            catch (NoSuchMethodException e) {
                return null;
            }
            return m;
        }
        
        public Arity getArity() {
            return Arity.required(1);
        }
        
    }
}

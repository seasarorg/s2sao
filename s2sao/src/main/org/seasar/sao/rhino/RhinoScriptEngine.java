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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaAdapter;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.WrappedException;
import org.seasar.framework.container.S2Container;
import org.seasar.sao.ScriptEngine;
import org.seasar.sao.ScriptBinding;
import org.seasar.sao.MethodBinding;

/**
 * RhinoによるSAOコントロールのエンジン。
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
public class RhinoScriptEngine implements ScriptEngine {

	private static final long serialVersionUID = -5724340206095852305L;
	private S2Container _container;
    private WrapFactory _wrap;
    private String _encoding;

	public void setContainer(S2Container container) {
		if(container == null) {
			throw new IllegalArgumentException();
		}
		_container = container;
	}
	
	protected S2Container getContainer() {
		if(_container == null) {
			throw new IllegalStateException();
		}
		return _container;
	}

	public void setEncoding(String encoding) {
		if(encoding == null || encoding.length() == 0) {
			throw new IllegalArgumentException();
		}
		_encoding = encoding;
	}
	
    protected String getEncoding() {
    	if(_encoding == null) {
    		_encoding = System.getProperty("file.encoding", "UTF-8");
    	}
    	return _encoding;
    }
	
    protected Context enter() {
        if (_wrap == null) {
        	_wrap  = new S2WrapFactory();
        }
        Context cx = Context.enter();
        cx.setWrapFactory(_wrap);
        return cx;
    }

    protected String getScriptFilePath(Class<?> sao) {
		if(sao == null) {
			throw new IllegalArgumentException();
		}
		if(sao.isAnnotationPresent(ScriptBinding.class)) {
			ScriptBinding binding = sao.getAnnotation(ScriptBinding.class);
			String value = binding.value();
			if(value.length() > 0) {
				return value;
			}
		}
		String path = sao.getName();
		path = path.replace('.', '/') + ".js";
		return path;
	}

	protected String getScriptMethodName(Method method) {
		if(method == null) {
			throw new IllegalArgumentException();
		}
		if(method.isAnnotationPresent(MethodBinding.class)) {
			MethodBinding binding = 
				method.getAnnotation(MethodBinding.class);
			String value = binding.value();
			if(value.length() > 0) {
				return value;
			}
		}
		return method.getName();
	}
	
	protected InputStream getScriptStream(String path) {
		if(path == null || path.length() == 0) {
			throw new IllegalArgumentException();
		}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return loader.getResourceAsStream(path);
	}
	
	public Object compile(Class<?> sao) throws Exception {
		String path = getScriptFilePath(sao);
		InputStream stream = getScriptStream(path);
		if(stream != null) {
            Context cx = enter();
	        try {
	            Reader reader = new InputStreamReader(stream, getEncoding());
	            Script script = cx.compileReader(reader, path, 1, null);
				Scriptable prototype = new NativeContainer(getContainer());
				ScriptableObject global = cx.initStandardObjects();
				global.setPrototype(prototype);
	    		script.exec(cx, global);
				global.sealObject();
				return global;
	        } finally {
	            Context.exit();
	            stream.close();
	        }
		}
		return null;
	}
	
	protected Scriptable castForScriptabe(Object compiled) {
		if(compiled == null || !(compiled instanceof Scriptable)) {
			throw new IllegalArgumentException();
		}
		return Scriptable.class.cast(compiled);		
	}
	
	protected Function getFunction(Object compiled, Method method) {
		Scriptable global = castForScriptabe(compiled);		
		String scriptMethodName = getScriptMethodName(method);
		Object func = global.get(scriptMethodName, null);
		if(func != null && func instanceof Function) {
			return Function.class.cast(func);
		}
		return null;
	}
	
	public boolean hasFunction(Object compiled, Method method) {
		Scriptable global = castForScriptabe(compiled);		
		String scriptMethodName = getScriptMethodName(method);
		return global.has(scriptMethodName, null);
	}

    protected Object convertResult(
    		Context cx, Class expectedClass, Object jsRet) {
        Object ret = null;
        if (expectedClass.equals(Boolean.TYPE)) {
            // workaround to ECMA1.3
            ret = JavaAdapter.convertResult(jsRet, Object.class);
        } else if (expectedClass == Void.class
                || expectedClass == void.class
                || (jsRet instanceof org.mozilla.javascript.Undefined)) {
            ret = null;
        } else {
            ret = JavaAdapter.convertResult(jsRet, expectedClass);
        }
        return ret;
    }
	
	public Object invoke(Object compiled, Method method, Object[] args,
			Class expectedClass) throws Throwable {
        Context cx = enter();
		try {
			Function function = getFunction(compiled, method);
			Scriptable global = castForScriptabe(compiled);		
			Object[] jsArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                jsArgs[i] = Context.javaToJS(args[i], global);
            }
			Object jsRet = function.call(cx, global, global, jsArgs);
			return convertResult(cx, expectedClass, jsRet);
        } catch (WrappedException e) {
            throw e.getWrappedException();
		} finally {
			Context.exit();
		}
	}
	
}

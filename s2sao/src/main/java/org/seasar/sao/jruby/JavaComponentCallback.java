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

import java.lang.reflect.Method;

import org.jruby.IRuby;
import org.jruby.javasupport.JavaObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Arity;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.callback.Callback;

import org.seasar.framework.container.S2Container;

/**
 * S2のコンポーネントのコールバックです。
 * org.jruby.javasupport.JavaObjectがラップしているオブジェクトのメソッドを呼び出します。
 * 
 * @author bowez
 */
class JavaComponentCallback implements Callback {
    final IRuby runtime;
    final Arity arity;
    
    public JavaComponentCallback(IRuby runtime, Arity arity) {
        this.runtime = runtime;
        this.arity = arity;
    }

    /**
     * レシーバのメソッドを呼び出します。
     * レシーバはorg.jruby.javasupport.JavaObjectのインスタンスである必要があります。
     * レシーバのJavaObjectがS2Containerをラップしている場合は、呼び出されたメソッド名を
     * コンポーネント名と判断してS2Containerからコンポーネントを取得します。
     * 
     * 該当するメソッドがない場合はレシーバのmethod_missingを呼び出します。
     * 
     * @param recv メソッドのレシーバ
     * @param args
     * @throws IllegalArgumentException argsがnullまたは長さ0の場合
     * @throws ClassCastException レシーバrecvがJavaObjectのインスタンスでない場合
     */
    public IRubyObject execute(IRubyObject recv, IRubyObject[] args) 
            throws IllegalArgumentException, ClassCastException {
        if (args == null || args.length < 1) {
            throw new IllegalArgumentException();
        }
        String name = args[0].asSymbol();
        JavaObject javaObject = (JavaObject) recv;
        if (javaObject.getValue() instanceof S2Container) {
            S2Container container = (S2Container) javaObject.getValue();
            if (container.hasComponentDef(name)) {
                Object component = container.getComponent(name);
                return RubyComponentUtil.convertJavaToRuby(runtime, component);
            }
        }
        Class[] argTypes = getArgTypes(args);
        Method method = loadMethod(recv.getJavaClass(), name, argTypes);
        if (method == null) {
            return recv.callMethod("method_missing", args);
        }
        Object result = null;
        try {
            result = method.invoke(javaObject.getValue(), getJavaArgs(args));
        }
        catch (Exception e) {
            throw new RuntimeException(e.getCause());
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
        return arity;
    }
}

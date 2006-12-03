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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * SAO������s���A�C���^�[�Z�v�^�B
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
public class S2SaoInterceptor implements MethodInterceptor, Serializable {

	private static final long serialVersionUID = -8933490145398891686L;
	private ScriptEngine _scriptEngine;
	private Map<Class<?>, Object> _scriptCache =
		new WeakHashMap<Class<?>, Object>();

	/**
	 * �X�N���v�g�G���W���̐ݒ�B
	 * @param scriptEngine �X�N���v�g�G���W���B
	 */
	public void setScriptEngine(ScriptEngine scriptEngine) {
		if(scriptEngine == null) {
			throw new IllegalArgumentException();
		}
		_scriptEngine = scriptEngine;
	}
	
	/**
	 * �X�N���v�g�G���W���̎擾�B���炩����DI����Ă��Ȃ��Ɨ�O�B
	 * @return �X�N���v�g�G���W���B 
	 */
	protected ScriptEngine getScriptEngine() {
		if(_scriptEngine == null) {
			throw new IllegalStateException();
		}
		return _scriptEngine;
	}
	
	/**
	 * �R���p�C���ς݃X�N���v�g���擾����B
	 * @param sao �����Ώۂ�SAO�N���X�^�B
	 * @return �Y������R���p�C���ς݃X�N���v�g�B
	 * @throws Exception �V�K�ɃX�N���v�g�R���p�C�������ۂɐ������O�B
	 */
	protected Object getCompiledScript(Class<?> sao) throws Throwable {
		Object script = _scriptCache.get(sao);
		if(script == null) {
			ScriptEngine engine = getScriptEngine();
			script = engine.compile(sao);
			_scriptCache.put(sao, script);
		}
		return script;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		if(invocation == null) {
			throw new IllegalArgumentException();
		}
		Method method = invocation.getMethod();
		Class<?> sao = method.getDeclaringClass();
		Object script = getCompiledScript(sao);
		if(script != null) {
			ScriptEngine engine = getScriptEngine();
			if(engine.hasFunction(script, method)) {
				Object[] args = invocation.getArguments();
				return engine.invoke(script, method, args,
						method.getReturnType());
			}
		}
		return invocation.proceed();
	}

}

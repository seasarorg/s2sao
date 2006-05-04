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

import java.lang.reflect.Method;

import org.seasar.sao.rhino.RhinoScriptEngine;
import org.seasar.sao.sample.Dummy;
import org.seasar.sao.sample.Speaker;

import junit.framework.TestCase;

/**
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
public class RhinoScriptEngineTest extends TestCase {

	private RhinoScriptEngine _engine;
	
	public RhinoScriptEngineTest(String name) {
		super(name);
	}
	
	protected void setUp() {
		_engine = new RhinoScriptEngine();
	}
	
	public void testGetScriptFilePath() {
		assertEquals("org/seasar/sao/sample/Speaker.js",
				_engine.getScriptFilePath(Speaker.class));
		assertEquals("org/seasar/sao/sample/Speaker.js",
				_engine.getScriptFilePath(Dummy.class));
	}
	
	public void testGetScriptMethodName() throws Exception {
		Method say = Speaker.class.getMethod("say", String.class);
		assertEquals("say", _engine.getScriptMethodName(say));
		Method greeting = Speaker.class.getMethod("greeting");
		assertEquals("hello", _engine.getScriptMethodName(greeting));
	}
	
}

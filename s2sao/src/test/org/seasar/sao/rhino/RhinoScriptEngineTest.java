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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.seasar.sao.rhino.RhinoScriptEngine;
import org.seasar.sao.sample.Dummy;
import org.seasar.sao.sample.Speaker;

/**
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
public class RhinoScriptEngineTest {

	private RhinoScriptEngine _engine;
	
	@Before
	public void setUp() {
		_engine = new RhinoScriptEngine();
	}
	
	@Test
	public void getScriptFilePath() {
		Assert.assertEquals("org/seasar/sao/sample/Speaker.js",
				_engine.getScriptFilePath(Speaker.class));
		Assert.assertEquals("org/seasar/sao/sample/Speaker.js",
				_engine.getScriptFilePath(Dummy.class));
	}
	
	@Test
	public void getScriptMethodName() throws Exception {
		Method say = Speaker.class.getMethod("say", String.class);
		Assert.assertEquals("say", _engine.getScriptMethodName(say));
		Method greeting = Speaker.class.getMethod("greeting");
		Assert.assertEquals("hello", _engine.getScriptMethodName(greeting));
	}
	
}

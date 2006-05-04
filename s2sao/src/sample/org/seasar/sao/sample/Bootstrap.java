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
package org.seasar.sao.sample;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

/**
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
public class Bootstrap {

	public static void main(String[] args) {
		String path = "org/seasar/sao/sample/sample-config.dicon";
		S2Container container = S2ContainerFactory.create(path);
		Speaker speaker = (Speaker)container.getComponent(Speaker.class);
		String msg = speaker.say("masataka");
		System.out.println(msg);
		String msg2 = speaker.say("kurihara");
		System.out.println(msg2);
	}

}

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

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;

/**
 * S2Containerをこのクラスでラップして、JavaScript風に直感的な、
 * s2Container.コンポーネント名 でアクセスできるようにする。
 * @author Masataka Kurihara (Gluegent,Inc.)
 */
public class NativeContainer extends NativeJavaObject {

	private static final long serialVersionUID = -8347548063811887938L;
	private S2Container _container;
	
	public NativeContainer(S2Container container) {
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

    public boolean has(String name, Scriptable start) {
        if (getContainer().hasComponentDef(name)) {
            return true;
        }
        return super.has(name, start);
    }

    public Object get(String name, Scriptable start) {
    	S2Container container = getContainer();
        if (container.hasComponentDef(name)) {
            return container.getComponent(name);
        }
        return super.get(name, start);
    }
    
    public Object[] getIds() {
    		int listSize = _container.getComponentDefSize();
    		Object[] ids = super.getIds();
    		Object[] ret = new Object[listSize + ids.length];
    		for (int i = 0; i < listSize; i++) {
    			ComponentDef def = _container.getComponentDef(i); 
    			String name = def.getComponentName();
    			if(name != null && name.length() > 0) {
    				ret[i] = name;
    			} else {
    				ret[i] = def.getComponentClass().getName();
    			}
    		}
    		for (int i = 0; i < ids.length; i++) {
    			ret[i + listSize] = ids[i];
    		}
    		return ret;
    }

    public String getClassName() {
        return "s2Container";
    }

}

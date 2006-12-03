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
package org.seasar.sao.jython;

import java.util.HashMap;
import java.util.Map;

import org.python.core.Py;
import org.python.core.PyObject;
import org.seasar.framework.container.S2Container;

/**
 * S2Containerをこのクラスでラップして、S2Containerのコンポーネントを
 * Pythonオブジェクトの属性としてアクセスできるようにします。
 * 
 * @author bowez
 */
class PyContainerWrapper extends PyObject {
    private static final long serialVersionUID = 3789048154314091310L;
    
    final S2Container container;
    final Map<String, PyContainerWrapper> childContainers = 
        new HashMap<String, PyContainerWrapper>();

    PyContainerWrapper(S2Container container) {
        this.container = container;
    }

    @Override
    public PyObject __findattr__(String name) {
        return __finditem__(name);
    }

    @Override
    public PyObject __finditem__(String key) {
        if (!container.hasComponentDef(key)) {
            return null;
        }
        Object component = container.getComponent(key);
        if (component instanceof S2Container) {
            PyContainerWrapper child = childContainers.get(key);
            if (child == null) {
                child = new PyContainerWrapper((S2Container) component);
                childContainers.put(key, child);
            }
            return child;
        }
        else {
            return Py.java2py(component);
        }
    }

}

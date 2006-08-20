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

package org.seasar.sao.jython;

import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyStringMap;
import org.seasar.framework.container.S2Container;

/**
 * {@link PyStringMap}で扱う通常のキーと値のセットに加え、S2Containerで
 * 管理するコンポーネントをアイテムとして提供するためのクラスです。
 * 
 * @author bowez
 */
public class PyContainerStringMap extends PyStringMap {
    private static final long serialVersionUID = -5515953169181260027L;
    private static final String DELETED_KEY = "<deleted key>";
    
    final S2Container container;
    
    public PyContainerStringMap(S2Container container) {
        if (container == null) {
            throw new IllegalArgumentException();
        }
        this.container = container;
    }
    
    @Override
    public synchronized PyObject __finditem__(String key) {
        /* 
         * PyStringMap#__finditem__(String) には、Stringの比較を
         * == でしているためitemを見つけられない場合がある。
         * そのため super.__finditem__(key) でなく自前で実装する。
         */
        PyList keys = keys();
        PyList values = values();
        for (int i = 0; i < keys.size(); i++) {
            String keystr = (String) keys.get(i);
            if (keystr != null && !keystr.equals(DELETED_KEY) && keystr.equals(key)) {
                return values.pyget(i);
            }
        }
        if (container.hasComponentDef(key)) {
            PyContainerWrapper wrapper = new PyContainerWrapper(container);
            __setitem__(key, wrapper);
            return wrapper.__finditem__(key);
        }
        return null;
    }

}

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

import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyStringMap;
import org.seasar.framework.container.S2Container;

/**
 * {@link PyStringMap}�ň����ʏ�̃L�[�ƒl�̃Z�b�g�ɉ����AS2Container��
 * �Ǘ�����R���|�[�l���g���A�C�e���Ƃ��Ē񋟂��邽�߂̃N���X�ł��B
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
         * PyStringMap#__finditem__(String) �ɂ́AString�̔�r��
         * == �ł��Ă��邽��item���������Ȃ��ꍇ������B
         * ���̂��� super.__finditem__(key) �łȂ����O�Ŏ�������B
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

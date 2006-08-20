package org.seasar.sao.jython;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.seasar.sao.MethodBinding;
import org.seasar.sao.ScriptBinding;
import org.seasar.sao.ScriptEngine;

/**
 * 
 * @author bowez
 */
public class JythonScriptEngine implements ScriptEngine {
    private static final long serialVersionUID = 2822589331359858203L;
    private static final String PYTHON_HOME_KEY = "python.home";
    
    final PythonInterpreter pythonInterpreter;
    final Properties properties = new Properties();
    
    public JythonScriptEngine() {
        this(null);
    }
    
    public JythonScriptEngine(PyObject dictionary) {
        PythonInterpreter.initialize(System.getProperties(), properties, new String[0]);
        pythonInterpreter = new PythonInterpreter(dictionary, new PySystemState());
    }
    
    public void setPythonHome(String path) {
        if (path != null && 0 < path.length()) {
            properties.setProperty(PYTHON_HOME_KEY, path);
        }
    }
    
    protected String getScriptFilePath(Class<?> sao) {
        if (sao == null) {
            throw new IllegalArgumentException();
        }
        if(sao.isAnnotationPresent(ScriptBinding.class)) {
            ScriptBinding binding = sao.getAnnotation(ScriptBinding.class);
            String value = binding.value();
            if(value.length() > 0) {
                return value;
            }
        }
        String path = sao.getName();
        path = path.replace('.', '/') + ".py";
        return path;
    }

    protected String getScriptMethodName(Method method) {
        if(method == null) {
            throw new IllegalArgumentException();
        }
        if(method.isAnnotationPresent(MethodBinding.class)) {
            MethodBinding binding = 
                method.getAnnotation(MethodBinding.class);
            String value = binding.value();
            if(value.length() > 0) {
                return value;
            }
        }
        return method.getName();
    }

    protected InputStream getScriptStream(String path) {
        if (path == null || path.length() == 0) {
            throw new IllegalArgumentException();
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream(path);
    }
    
    public Object compile(Class<?> sao) throws Throwable {
        String path = getScriptFilePath(sao);
        pythonInterpreter.execfile(getScriptStream(path));
        return pythonInterpreter.getLocals();
    }

    public boolean hasFunction(Object compiled, Method method) {
        PyObject locals = PyObject.class.cast(compiled);
        PyObject item = locals.__finditem__(getScriptMethodName(method));
        return item != null && item.isCallable();
    }

    public Object invoke(Object compiled, Method method, Object[] args,
            Class<?> expectedClass) throws Throwable {
        PyObject locals = PyObject.class.cast(compiled);
        PyObject attr = locals.__finditem__(getScriptMethodName(method));
        PyObject ret = attr.__call__(createPyObjects(args));
        return ret.__tojava__(expectedClass);
    }

    PyObject[] createPyObjects(Object[] args) {
        if (args == null) {
            return new PyObject[0];
        }
        PyObject[] pyObjects = new PyObject[args.length];
        for (int i = 0; i < args.length; i++) {
            pyObjects[i] = Py.java2py(args[i]);
        }
        return pyObjects;
    }
}

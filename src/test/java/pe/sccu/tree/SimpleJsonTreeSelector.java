package pe.sccu.tree;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SimpleJsonTreeSelector extends AbstractTreeSelector<Object> {

    SimpleJsonTreeSelector(Object element) {
        super(element);
    }

    SimpleJsonTreeSelector(Object element, boolean throwExceptionWhenNotFound) {
        super(element, throwExceptionWhenNotFound);
    }

    @Override
    protected Object getByName(Object element, String key) {
        return ((JSONObject) element).get(key);
    }

    @Override
    protected Object getByIndex(Object element, int index) {
        return ((JSONArray) element).get(index);
    }
}

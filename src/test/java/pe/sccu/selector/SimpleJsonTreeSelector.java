package pe.sccu.selector;

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
    protected Object getByName(Object element, String name) {
        return ((JSONObject) element).get(name);
    }

    @Override
    protected Object getByIndex(Object element, int index) {
        return ((JSONArray) element).get(index);
    }
}

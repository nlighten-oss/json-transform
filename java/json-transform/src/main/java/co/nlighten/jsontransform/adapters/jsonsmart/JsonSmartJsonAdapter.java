package co.nlighten.jsontransform.adapters.jsonsmart;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonAdapterHelpers;
import com.jayway.jsonpath.DocumentContext;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.math.BigDecimal;

public class JsonSmartJsonAdapter extends JsonAdapter<Object, JSONArray, JSONObject> {

    public JsonSmartJsonAdapter() {
        super(
                JsonSmartObjectAdapter::new,
                JsonSmartArrayAdapter::new,
                JsonSmartHelpers.getJsonPathConfig()
        );
    }

    @Override
    public boolean is(Object value) {
        return value == null ||
                value instanceof JSONAware ||
                value instanceof String ||
                value instanceof Number ||
                value instanceof Boolean;
    }

    @Override
    public boolean isJsonString(Object value) {
        return value instanceof String;
    }

    @Override
    public boolean isJsonNumber(Object value) {
        return value instanceof Number;
    }

    @Override
    public boolean isJsonBoolean(Object value) {
        return value instanceof Boolean;
    }

    @Override
    public boolean isNull(Object value) {
        return value == null;
    }

    @Override
    public Object jsonNull() {
        return null;
    }

    @Override
    public Object wrap(Object value) {
        return JsonSmartHelpers.convert(value, false);
    }

    @Override
    public Object unwrap(Object value, boolean reduceBigDecimals) {
        return JsonSmartHelpers.convert(value, true);
    }

    @Override
    public Object parse(String value) {
        return JSONValue.parse(value);
    }

    @Override
    public Object clone(Object value) {
        return wrap(value);
    }

    @Override
    public Number getNumber(Object value) {
        if (value instanceof Number n) {
            return n;
        }
        if (value instanceof String s) {
            return new BigDecimal(s);
        }
        return null;
    }

    @Override
    public BigDecimal getNumberAsBigDecimal(Object value) {
        if (value instanceof BigDecimal b) {
            return b;
        }
        if (value instanceof Number n) {
            return new BigDecimal(n.toString());
        }
        if (value instanceof String s) {
            return new BigDecimal(s);
        }
        return null;
    }

    @Override
    public Boolean getBoolean(Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof String s) {
            return Boolean.valueOf(s);
        }
        return null;
    }

    @Override
    public DocumentContext getDocumentContext(Object payload, Iterable<String> options) {
        if (isNull(payload)) {
            return NullDocumentContext.INSTANCE;
        }
        return super.getDocumentContext(payload, options);
    }

    @Override
    public String toString(Object value) {
        if (value instanceof String) {
            var arr = createArray(1);
            add(arr, value);
            var strInArr = JSONValue.toJSONString(arr);
            return strInArr.substring(1, strInArr.length() - 1);
        }
        return JSONValue.toJSONString(value);
    }
}

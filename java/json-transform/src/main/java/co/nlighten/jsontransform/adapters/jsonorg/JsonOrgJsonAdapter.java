package co.nlighten.jsontransform.adapters.jsonorg;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import com.jayway.jsonpath.DocumentContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONTokener;

import java.math.BigDecimal;
import java.math.BigInteger;

class JsonOrgJsonAdapter extends JsonAdapter<Object, JSONArray, JSONObject> {

    public JsonOrgJsonAdapter() {
        super(JsonOrgObjectAdapter::new, JsonOrgArrayAdapter::new);
    }

    @Override
    public String getName() {
        return "jsonorg";
    }

    @Override
    public boolean is(Object object) {
        // copied from JSONObject::wrap
        if (object instanceof JSONObject || object instanceof JSONArray
                || JSONObject.NULL.equals(object) || object instanceof JSONString
                || object instanceof Byte || object instanceof Character
                || object instanceof Short || object instanceof Integer
                || object instanceof Long || object instanceof Boolean
                || object instanceof Float || object instanceof Double
                || object instanceof String || object instanceof BigInteger
                || object instanceof BigDecimal || object instanceof Enum) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isJsonString(Object value) {
        return value instanceof JSONString || value instanceof String;
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
        return value == null || JSONObject.NULL.equals(value);
    }

    @Override
    public Object jsonNull() {
        return JSONObject.NULL;
    }

    @Override
    public Object wrap(Object value) {
        return JSONObject.wrap(value);
    }

    @Override
    public Object unwrap(Object value, boolean reduceBigDecimals) {
        if (JSONObject.NULL.equals(value)) {
            return null;
        }
        if (value instanceof JSONObject jo) {
            return jo.toMap();
        }
        if (value instanceof JSONArray ja) {
            return ja.toList();
        }
        if (value instanceof JSONString ja) {
            return ja.toString();
        }
        return value;
    }

    @Override
    public Object parse(String value) {
        return new JSONTokener(value).nextValue();
    }

    @Override
    public Object clone(Object value) {
        if (value instanceof JSONObject jo) {
            return new JSONObject(jo.toString());
        }
        if (value instanceof JSONArray ja) {
            return new JSONArray(ja.toString());
        }
        if (value instanceof JSONString
                || value instanceof Byte || value instanceof Character
                || value instanceof Short || value instanceof Integer
                || value instanceof Long || value instanceof Boolean
                || value instanceof Float || value instanceof Double
                || value instanceof String || value instanceof Enum) {
            return value;
        }
        if (value instanceof BigInteger) {
            return new BigInteger(value.toString());
        }
        if (value instanceof BigDecimal) {
            return new BigDecimal(value.toString());
        }
        return wrap(value);
    }

    @Override
    public Number getNumber(Object value) {
        if (value instanceof Number n) {
            return n;
        }
        if (value instanceof JSONString || value instanceof String) {
            return new BigDecimal(value.toString());
        }
        return null;
    }

    @Override
    public BigDecimal getNumberAsBigDecimal(Object value) {
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number n) {
            return new BigDecimal(n.toString());
        }
        if (value instanceof JSONString || value instanceof String) {
            return new BigDecimal(value.toString());
        }
        return null;
    }

    @Override
    public Boolean getBoolean(Object value) {
        return (Boolean)value;
    }

    @Override
    public void setupJsonPath() {
        JsonOrgJsonPathConfigurator.setup();
    }

    @Override
    public DocumentContext getDocumentContext(Object payload) {
        if (isNull(payload)) {
            return JsonOrgNullDocumentContext.INSTANCE;
        }
        return super.getDocumentContext(payload);
    }

    @Override
    public String toString(Object value) {
        var provider = JsonOrgJsonPathConfigurator.configuration().jsonProvider();
        if (value instanceof String) {
            var arr = provider.createArray();
            provider.setArrayIndex(arr, 0, value);
            var strInArr = provider.toJson(arr);
            return strInArr.substring(1, strInArr.length() - 1);
        }
        return provider.toJson(JSONObject.wrap(value));
    }
}

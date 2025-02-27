package co.nlighten.jsontransform.adapters.jsonorg;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonAdapterHelpers;
import co.nlighten.jsontransform.adapters.pojo.PojoMapper;
import com.jayway.jsonpath.DocumentContext;
import org.json.*;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonOrgJsonAdapter extends JsonAdapter<Object, JSONArray, JSONObject> {

    public JsonOrgJsonAdapter() {
        super(
                JsonOrgObjectAdapter::new,
                JsonOrgArrayAdapter::new,
                JsonOrgHelpers.getJsonPathConfig()
        );
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
        return JSONObject.wrap(JsonOrgHelpers.simplifyBeforeWrap(value));
    }

    @Override
    public Object unwrap(Object value, boolean reduceBigDecimals) {
        return JsonOrgHelpers.unwrap(value, reduceBigDecimals);
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
    public DocumentContext getDocumentContext(Object payload, Iterable<String> options) {
        if (isNull(payload)) {
            return JsonOrgNullDocumentContext.INSTANCE;
        }
        return super.getDocumentContext(payload, options);
    }

    @Override
    public boolean nodesComparable() {
        return false;
    }

    @Override
    public boolean areEqual(Object value, Object other) {
        if (value instanceof JSONObject jo) {
            return other instanceof JSONObject o && jo.toMap().equals(o.toMap());
        }
        if (value instanceof JSONArray ja) {
            if (!(other instanceof JSONArray o) || o.length() != ja.length()) {
                return false;
            }
            for (var i = 0 ; i < ja.length() ; i++) {
                if (!areEqual(ja.get(i), o.get(i))) {
                    return false;
                }
            }
            return true;
        }
        if (value instanceof JSONString) {
            return other instanceof JSONString && value.toString().equals(other.toString());
        }
        return super.areEqual(value, other);
    }

    @Override
    public int hashCode(Object value) {
        return JsonOrgHelpers.getContentsHashCode(value);
    }

    @Override
    public String toString(Object value) {
        if (value instanceof String) {
            var arr = createArray(1);
            add(arr, value);
            var strInArr = arr.toString();
            return strInArr.substring(1, strInArr.length() - 1);
        }
        return wrap(value).toString();
    }
}

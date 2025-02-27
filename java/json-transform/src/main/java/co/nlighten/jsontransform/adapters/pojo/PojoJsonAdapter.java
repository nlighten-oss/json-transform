package co.nlighten.jsontransform.adapters.pojo;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonAdapterHelpers;
import com.jayway.jsonpath.DocumentContext;

import java.math.BigDecimal;
import java.util.AbstractList;
import java.util.Map;

public class PojoJsonAdapter extends JsonAdapter<Object, AbstractList<Object>, Map<String, Object>> {

    public PojoJsonAdapter() {
        super(
                PojoObjectAdapter::new,
                PojoArrayAdapter::new,
                PojoHelpers.getJsonPathConfig()
        );
    }

    @Override
    public boolean is(Object value) {
        return value instanceof PojoNull ||
                value instanceof AbstractList<?> ||
                value instanceof Map<?, ?> ||
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
        return value == null || value instanceof PojoNull;
    }

    @Override
    public Object jsonNull() {
        return PojoNull.INSTANCE;
    }

    @Override
    public Object wrap(Object value) {
        return PojoMapper.convert(value);
    }

    @Override
    public Object unwrap(Object value, boolean reduceBigDecimals) {
        return PojoMapper.convert(value, true, reduceBigDecimals);
    }

    @Override
    public Object parse(String value) {
        if (value != null && value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
            return PojoMapper.convert(PojoHelpers.parse(
                JsonAdapterHelpers.singleQuotedStringToDoubleQuoted(value)
            ));
        }
        return PojoMapper.convert(PojoHelpers.parse(value));
    }

    @Override
    public Object clone(Object value) {
        return PojoMapper.deepClone(value);
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
            return PojoNullDocumentContext.INSTANCE;
        }
        return super.getDocumentContext(payload, options);
    }

    @Override
    public String toString(Object value) {
        if (value instanceof String) {
            var arr = createArray(1);
            add(arr, value);
            var strInArr = PojoHelpers.toJson(arr);
            return strInArr.substring(1, strInArr.length() - 1);
        }
        return PojoHelpers.toJson(PojoMapper.convert(value, true, false));
    }
}

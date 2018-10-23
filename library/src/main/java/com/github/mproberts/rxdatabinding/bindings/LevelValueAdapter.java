package com.github.mproberts.rxdatabinding.bindings;

import java.util.HashMap;
import java.util.Map;

interface LevelValueAdapter {

    int convertValue(Object value);
}

class EnumLevelValueAdapter<T> implements LevelValueAdapter {

    Map<T, Integer> _enumValues = new HashMap<>();

    @Override
    public int convertValue(Object value) {
        return _enumValues.get((T) value);
    }

    public EnumLevelValueAdapter<T> map(T enumValue, int level) {
        _enumValues.put(enumValue, level);

        return this;
    }
}

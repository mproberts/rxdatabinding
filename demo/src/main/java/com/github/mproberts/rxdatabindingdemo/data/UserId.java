package com.github.mproberts.rxdatabindingdemo.data;

import java.io.Serializable;

public class UserId implements Serializable {

    public final String value;

    public UserId(String value) {
        this.value = value;
    }
}

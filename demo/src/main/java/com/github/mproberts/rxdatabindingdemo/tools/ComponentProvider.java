package com.github.mproberts.rxdatabindingdemo.tools;

import com.github.mproberts.rxdatabindingdemo.di.DomainComponent;

interface ComponentProvider {
    DomainComponent getDomainComponent();
}

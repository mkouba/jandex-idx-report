package com.github.mkouba.jandex;

import javax.annotation.Resource;

public class Foo {

    @Resource
    private int age;

    @Deprecated
    public void dontUseMe() {
    }

}

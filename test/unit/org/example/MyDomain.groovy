package org.example

import org.example.sub.HasMany

class MyDomain {
    long id
    long version
    String name
    int age
    OtherDomain theOther
    List items

    static hasMany = [ items: HasMany ]
}

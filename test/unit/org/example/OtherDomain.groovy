package org.example

class OtherDomain {
    Long id
    Long version
    SomeType type

    static belongsTo = [ owner: MyDomain ]
}

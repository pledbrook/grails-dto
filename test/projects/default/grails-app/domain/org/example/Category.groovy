package org.example

class Category {
    String name
    
    static hasMany = [ posts: Post ]
}

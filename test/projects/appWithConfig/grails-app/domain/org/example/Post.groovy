package org.example

import org.example.security.SecUser

class Post {
    String content
    Category category
    PostType type
    int priority

    static belongsTo = [ user: SecUser ]

    static constraints = {
        content()
        category(nullable: true)
        type()
        priority()
    }
}

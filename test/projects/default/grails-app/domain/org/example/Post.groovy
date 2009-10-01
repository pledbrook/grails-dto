package org.example

import org.example.security.SecUser

class Post {
    String content
    Category category
    int priority

    static belongsTo = [ user: SecUser ]
}

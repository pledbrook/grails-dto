package org.example.security

import org.example.Post

class SecUser {
    String id
    String fullName
    String email
    Date dateOfBirth
    List posts

    static hasMany = [ posts: Post, roles: SecRole ]

    static mapping = {
        id generator: "assigned", column: "user_id"
        version false
    }
}

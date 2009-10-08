package org.example.security

class SecRole {
    String name

    static hasMany = [ users: SecUser ]
    static belongsTo = SecUser
}

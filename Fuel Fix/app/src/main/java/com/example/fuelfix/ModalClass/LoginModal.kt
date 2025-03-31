package com.example.fuelfix.ModalClass

class LoginModal {

    var email: String = ""
    var password: String = ""
    var fname: String = ""

    constructor(email: String, password: String, fname: String) {
        this.email = email
        this.password = password
        this.fname = fname
    }

    constructor()
    {

    }
}
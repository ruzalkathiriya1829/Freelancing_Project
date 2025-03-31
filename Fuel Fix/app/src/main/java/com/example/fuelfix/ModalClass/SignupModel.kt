package com.example.fuelfix.ModalClass

class SignupModel {
    var email: String = ""
    var password: String = ""
    var fname: String = ""
    var lname: String = ""
    var address: String = ""
    var mobileNo: String = ""
    var city: String = ""
    var gender: String = ""
    var images: String = ""
    var userType = 0
    var registrationTime: Long = 0 // Initialize registrationTime to 0

    constructor(email: String, password: String, fname: String, lname: String, address: String, mobileNo: String, city: String, gender: String, userType: Int, images: String, registrationTime: Long)
    {
        this.email = email
        this.password = password
        this.fname = fname
        this.lname = lname
        this.address = address
        this.mobileNo = mobileNo
        this.city = city
        this.gender = gender
        this.userType = userType
        this.images = images
        this.registrationTime = registrationTime
    }

    constructor() {

    }
}
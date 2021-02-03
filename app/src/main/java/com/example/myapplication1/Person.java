package com.example.myapplication1;

public class Person
{
    public int id;
    public String lastName;
    public String name;
    public String middleName;
    public String phone;

    Person(int id, String lastName, String name, String middleName, String phone)
    {
        this.id = id;
        this.lastName = lastName;
        this.name = name;
        this.middleName = middleName;
        this.phone = phone;
    }

    public int getID() {return id;}

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return name;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getPhone() {return phone;}
}

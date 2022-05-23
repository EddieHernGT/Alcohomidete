package com.automatizacion.alcohomidete.people;

public class People {
    private String Name="";
    private String Surname="";
    private String Age="";
    private String Gender="";


    public String getName() {
        return Name;
    }

    public String getSurname() {
        return Surname;
    }

    public String getAge() {return Age;}

    public String getGender() {return Gender;}

    public void setName(String name) {
        Name = name;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public void setAge(String age) {Age = age;}

    public void setGender(String gender) {Gender = gender;}

}

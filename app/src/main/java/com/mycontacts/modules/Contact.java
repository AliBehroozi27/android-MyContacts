package com.mycontacts.modules;

import java.io.Serializable;

public class Contact implements Serializable {
    public String name, number ,email , photoUri;
    public int id;
    public boolean bookMarked;

    public Contact(int id , String name, String number ,String email, boolean bookMarked , String photoUri) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.email = email;
        this.bookMarked = bookMarked;
        this.photoUri = photoUri;
    }
}

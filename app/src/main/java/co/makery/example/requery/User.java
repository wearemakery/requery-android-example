package co.makery.example.requery;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Index;
import io.requery.Key;

@Entity
public abstract class User {

    @Key @Generated
    int id;

    @Index(name = "name")
    String name;
}

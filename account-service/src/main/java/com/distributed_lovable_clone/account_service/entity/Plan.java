package com.distributed_lovable_clone.account_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;
     String name;
     @Column(unique = true)
     String stripePriceId;
     Integer maxProducts;
     Integer maxTokenPerDay;
     Integer maxPreview;
    Boolean unlimitedAi;

    Boolean active;


}

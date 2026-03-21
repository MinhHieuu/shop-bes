package com.beeshop.sd44.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mau_sac")
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotBlank(message = "ten khong duoc de trong")
    @Column(name = "ten")
    private String name;
    @OneToMany(mappedBy = "color")
    @JsonIgnore
    private List<ProductDetail> list;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductDetail> getList() {
        return list;
    }

    public void setList(List<ProductDetail> list) {
        this.list = list;
    }

    public Color(UUID id) {
        this.id = id;
    }

    public Color() {
    };
}

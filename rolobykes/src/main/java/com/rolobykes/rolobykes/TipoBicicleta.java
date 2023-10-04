package com.rolobykes.rolobykes;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter@Setter@NoArgsConstructor@AllArgsConstructor
@Entity
public class TipoBicicleta {
    @OneToMany
    private List<Bicicleta> Bicicletas;
    private String Nombre;

}
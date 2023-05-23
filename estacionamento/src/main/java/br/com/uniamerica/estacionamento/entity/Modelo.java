package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_modelo", schema = "public")
public class Modelo extends AbstractEntity{


    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "marca_Id", nullable = false)
    private Marca marcaId;


    @Getter @Setter
    @Column(name = "modelo", nullable = false, unique = true, length = 50)
    private String nome;
}
package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_marca", schema = "public")
public class Marca extends AbstractEntity{
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",nullable = false,unique = true)
    private Long id;
    @Getter  @Setter
    @Column(name = "nome",nullable = false)
    private String nome;


}

package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_modelo", schema = "public")
public class Modelo extends AbstractEntity {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",nullable = false,unique = true)
    private Long id;
    @Getter  @Setter
    @Column(name = "nome",nullable = false)
    private String nome;
    @Getter  @Setter
    @ManyToOne
    @JoinColumn(name = "marca",nullable = false)
    private Marca marca;


}

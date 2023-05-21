package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.UniqueElements;

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
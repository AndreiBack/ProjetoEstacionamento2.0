package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;
//import org.hibernate.envers.AuditTable;
//import org.hibernate.envers.Audited;
@Entity

@Table(name = "tb_condutor", schema = "public")
public class Condutor extends AbstractEntity {

    @Getter @Setter
    @Column(name = "nome",nullable = false, length = 100)
    private String nome;
    @Getter @Setter
    @Column(name = "cpf",nullable = false, length = 15)
    private String cpf;
    @Getter @Setter
    @Column(name = "telefone",nullable = false, length = 17)
    private String telefone;
    @Getter @Setter
    @Column(name = "tempogasto")
    private LocalTime tempoPago ;
    @Getter @Setter
    @Column(name = "tempoDesconto")
    private LocalTime tempoDesconto;

}

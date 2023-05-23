package br.com.uniamerica.estacionamento.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
@Entity
@Table(name = "tb_condutor", schema = "public")
public class Condutor extends AbstractEntity{



    @Getter @Setter
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Getter @Setter
    @Column(name = "cpf", nullable = false, unique = true, length = 20)
    private String cpf;

    @Getter @Setter
    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;


    @Getter @Setter
    @Column(name = "tempo_Pago")
    private Long tempoPago = 0L;
    @Getter @Setter
    @Column(name = "tempo_desconto")
    private Long tempoDesconto = 0L;
}

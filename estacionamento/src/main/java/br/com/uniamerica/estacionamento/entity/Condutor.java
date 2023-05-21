package br.com.uniamerica.estacionamento.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH'h'mm'm'")
    @Column(name = "tempo_pago")
    private LocalTime tempoPago = LocalTime.of(0, 0);

    @Getter @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH'h'mm'm'")
    @Column(name = "tempo_desconto")
    private LocalTime tempoDesconto = LocalTime.of(0, 0);

}

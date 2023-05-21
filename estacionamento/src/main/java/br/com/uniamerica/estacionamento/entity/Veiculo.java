package br.com.uniamerica.estacionamento.entity;
import jakarta.validation.constraints.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;


@Entity

@Table(name = "tb_veiculo", schema = "public")
public class Veiculo extends AbstractEntity {

    @Getter @Setter
    @Column(name = "placa", length = 10, nullable = false, unique = true)
    private String placa;

    @Getter @Setter
    @Column(name = "cor", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private Cor cor;

    @ManyToOne
    @Getter @Setter
    @JoinColumn(name = "modelo_id", nullable = false)
    private Modelo modeloId;


    @Getter @Setter
    @Column(name = "tipo", length = 5, nullable = false)
    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @Getter @Setter
    @Column(name = "ano", nullable = false)
    private int ano;
}

package br.com.uniamerica.estacionamento.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalTime;
@Entity
@Table(name = "tb_movimentacao", schema = "public")
public class Movimentacao extends AbstractEntity{
    @Getter @Setter
    @Column(name = "entrada", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH'h'mm'm'")
    private LocalTime  entrada;
    @Getter @Setter
    @Column(name = "saida")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH'h'mm'm'")
    private LocalTime  saida;
    @Getter @Setter
    @Column(name = "tempo_desconto")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH'h'mm'm'")
    private LocalTime tempoDesconto;
    @Getter @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH'h'mm'm'")
    @Column(name = "tempo")
    private LocalTime tempo;
    @Getter @Setter
    @Column(name = "tempo_multao")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH'h'mm'm'")
    private LocalTime tempoMulta;
    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;
    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "configuracao_id")
    private Configuracao configuracao;
    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "condutor_id")
    private Condutor condutor;
    @Getter @Setter
    @Column(name = "valor_multa")
    private BigDecimal valorMulta;
    @Getter @Setter
    @Column(name = "valor_total")
    private BigDecimal valorTotal;
    @Getter @Setter
    @Column(name = "valor_hora" )
    private BigDecimal valorHora;
    @Getter @Setter
    @Column(name = "valor_Desconto" )
    private BigDecimal valorDesconto = BigDecimal.ZERO;
}

package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "tb_movimentacao", schema = "public")
public class Movimentacao extends AbstractEntity{
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",nullable = false,unique = true)
    private Long id;
    @Getter  @Setter
    @ManyToOne
    @JoinColumn(name = "veiculo",nullable = false)
    private Veiculo veiculo;
    @Getter  @Setter
    @ManyToOne
    @JoinColumn(name = "condutor",nullable = false)
    private Condutor condutor;
    @Getter  @Setter
    @Column(name = "entrada",nullable = false)
    private LocalDateTime entrada;
    @Getter  @Setter
    @Column(name = "saida")
    private LocalDateTime saida;
    @Getter  @Setter
    @Column(name = "tempo")
    private LocalTime tempo;
    @Getter  @Setter
    @Column(name = "tempoMulta")
    private LocalTime tempoMulta;
    @Getter  @Setter
    @Column(name = "tempoDesconto")
    private LocalTime tempoDesconto;
    @Getter  @Setter
    @Column(name = "valorDesconto")
    private BigDecimal valorDesconto;
    @Getter  @Setter
    @Column(name = "valorMulta")
    private BigDecimal valorMulta;
    @Getter  @Setter
    @Column(name = "valorHora")
    private BigDecimal valorHora;
    @Getter  @Setter
    @Column(name = "valorHoraMulta")
    private BigDecimal valorHoraMulta;
    @Getter  @Setter
    @Column(name = "valorTotal")
    private BigDecimal valorTotal;


}

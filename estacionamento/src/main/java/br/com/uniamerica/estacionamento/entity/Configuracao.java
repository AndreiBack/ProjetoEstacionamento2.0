package br.com.uniamerica.estacionamento.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "tb_configuracao", schema = "public")
public class Configuracao extends AbstractEntity {
  @Id
  @Getter
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id",nullable = false,unique = true)
  private Long id;
    @Getter @Setter
    @Column(name = "valorHora")
  private BigDecimal valorHora;
  @Getter @Setter
  @Column(name = "valorMinutoMulta")
  private BigDecimal valorMinutoMulta;
  @Getter @Setter
  @Column(name = "FimExpediente")
  private LocalTime fimExpediente;
  @Getter @Setter
  @Column(name = "TempoParaDesconto")
  private LocalTime tempoParaDesconto;
  @Getter @Setter
  @Column(name = "TempoDeDesconto")
  private LocalTime tempoDeDesconto;
  @Getter @Setter
  @Column(name = "gerarDesconto")
  private boolean gerarDesconto;
  @Getter @Setter
  @Column(name = "vagasMoto")
  private int vagasMoto;
  @Getter @Setter
  @Column(name = "vagasCarro")
  private int vagasCarro;
  @Getter @Setter
  @Column(name = "vagasVan")
  private int vagasVan;


}

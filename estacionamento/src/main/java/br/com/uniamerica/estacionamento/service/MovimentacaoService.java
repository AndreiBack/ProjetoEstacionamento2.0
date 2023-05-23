package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.ConfiguracaoRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import br.com.uniamerica.estacionamento.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.Optional;


@Service
public class MovimentacaoService {

    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    @Autowired
    private CondutorRepository condutorRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional
    public Movimentacao cadastrar(Movimentacao movimentacao){

        final Configuracao configuracao = this.configuracaoRepository.findById(movimentacao.getConfiguracao().getId())
                .orElseThrow(() -> new IllegalArgumentException("Configuração não correspondente"));

        final Condutor condutor = this.condutorRepository.findById(movimentacao.getCondutor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Condutor não correspondente"));

        final Veiculo veiculo = this.veiculoRepository.findById(movimentacao.getVeiculo().getId())
                .orElseThrow(() -> new IllegalArgumentException("Veiculo não correspondente"));

        if (condutor == null || condutor.isAtivo()){
            throw new IllegalArgumentException("O condutor invalido");
        }
        if (veiculo == null || veiculo.isAtivo()){
            throw new IllegalArgumentException("O veiculo invalido");
        }
        BigDecimal valorTotal = BigDecimal.ZERO;
        BigDecimal valorMulta = BigDecimal.ZERO;
        LocalTime tempo = LocalTime.of(0, 0);
        LocalTime tempoDesconto = LocalTime.of(0, 0);

        movimentacao.setTempoDesconto(tempoDesconto);
        movimentacao.setSaida(tempo);
        movimentacao.setTempo(tempo);
        movimentacao.setTempoMulta(tempo);
        movimentacao.setTempoDesconto(tempo);
        movimentacao.setValorTotal(valorTotal);
        movimentacao.setValorMulta(valorMulta);
        movimentacao.setValorHora(configuracao.getValorHora());

        configuracao.setAtivo(true);
        veiculo.setAtivo(true);
        condutor.setAtivo(true);
        movimentacao.setAtivo(true);
        veiculoRepository.save(veiculo);
        condutorRepository.save(condutor);
        configuracaoRepository.save(configuracao);
        return this.movimentacaoRepository.save(movimentacao);
    }

    @Transactional
    public Movimentacao update(Movimentacao movimentacao) {
        final Configuracao configuracao = this.configuracaoRepository.findById(movimentacao.getConfiguracao().getId())
                .orElseThrow(() -> new IllegalArgumentException("Configuração não correspondente"));
        final Condutor condutor = this.condutorRepository.findById(movimentacao.getCondutor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Condutor não correspondente"));
        final Veiculo veiculo = this.veiculoRepository.findById(movimentacao.getVeiculo().getId())
                .orElseThrow(() -> new IllegalArgumentException("Veiculo não correspondente"));
        final Movimentacao movimentacaoDatabase = this.movimentacaoRepository.findById(movimentacao.getId())
                .orElseThrow(() -> new IllegalArgumentException("Movimentacao não correspondente"));
        if (!movimentacaoDatabase.isAtivo()){
            throw new IllegalArgumentException("Movimentacao ja encerada");
        }

        /*SET TEMPO*/
        LocalTime entrada = movimentacao.getEntrada();
        LocalTime saida = movimentacao.getSaida();
        int eem = entrada.getHour() * 60 + entrada.getMinute(); // EEM = Entrada-Em-Minutos
        int sem = saida.getHour() * 60 + saida.getMinute(); // SEM = Saida-Em-Minutos
        int eemSubSem = sem - eem; // Tempo total estacionado em minutos
        int hrsTempo = eemSubSem / 60; // O total de minutos dividido por 60 e as horas;
        int minTempo = eemSubSem % 60; // O Resto é os minutos;
        movimentacao.setTempo(LocalTime.of(hrsTempo, minTempo)); // set tempo;

        /*CALCULO VALOR TOTAL*/
        BigDecimal valorTotal = movimentacao.getValorTotal();
        BigDecimal valorHora = configuracao.getValorHora(); // Valor da hora que esta em configuraçao
        BigDecimal valorPorMinuto = configuracao.getValorHora().divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);//calculo com bigdecimal para saber o valor de um minuto pq nao em em configuralao. | scale:2 so vai pegar 2 numero apos o . e outro é para arredondar o valor
        BigDecimal valorTotalHoras = BigDecimal.valueOf(hrsTempo).multiply(valorHora);
        BigDecimal valorTotalMinutos = BigDecimal.valueOf(minTempo).multiply(valorPorMinuto);
        valorTotal = valorTotalHoras.add(valorTotalMinutos);

        /*CALCULA VALOR MULTA*/
        LocalTime fimExpediente = configuracao.getFimExpediente();
        BigDecimal vmpm = configuracao.getValorMultaMinuto(); // vmpm = valor multa por minuto;
        int fem = fimExpediente.getHour() * 60; // fim expediente em minutos
        int gmt = sem - fem; // gmt = gerar multa - quantidade em minutos o tempo que foi violado;
        BigDecimal valorTotalMultaMinutos = BigDecimal.ZERO;

        if (sem > fem) {
            valorTotalMultaMinutos = BigDecimal.valueOf(gmt).multiply(vmpm);
            valorTotal = valorTotal.add(valorTotalMultaMinutos);
        }

        /*SALVA O TEMPO PAGO NO CONDUTOR*/
        long tp = condutor.getTempoPago();
        tp += eemSubSem;
        condutor.setTempoPago(tp);

        /*SALVA O TEMPO DESCONTO DO CONDUTOR*/
        long td = condutor.getTempoDesconto();
        int verificador = (int) (tp / 3000); // 3000mim  = 50 hrs
        long longVerificador = (long) verificador;
        td = longVerificador * 300; // 300 min = 5 hrs

        /*CALCULA O VALOR DE  DESCONTO*/
        BigDecimal vd = movimentacao.getValorDesconto();
        LocalTime tempoDesconto = movimentacao.getTempoDesconto();
        long tdem = tempoDesconto.getHour() * 60 + tempoDesconto.getMinute(); // tdem = tempo desconto em minuto

        vd = BigDecimal.valueOf(tdem).multiply(valorPorMinuto);
        movimentacao.setValorDesconto(vd);

        if (tdem > td) {
            throw new IllegalArgumentException("Tempo excedente do permitido");
        }
        valorTotal = valorTotal.subtract(vd);
        td = td - tdem;

        configuracao.setAtivo(false);
        veiculo.setAtivo(false);
        condutor.setAtivo(false);
        condutor.setTempoDesconto(td);
        movimentacao.setValorMulta(valorTotalMultaMinutos);
        movimentacao.setValorTotal(valorTotal);
        configuracaoRepository.save(configuracao);
        veiculoRepository.save(veiculo);
        condutorRepository.save(condutor);
        return this.movimentacaoRepository.save(movimentacao);
    }


    @Transactional
    public void delete(Long id) {
        Optional<Movimentacao> optionalMovimentacao = movimentacaoRepository.findById(id);
        if (optionalMovimentacao.isPresent()) {
            Movimentacao movimentacao = optionalMovimentacao.get();
            if (movimentacao.getSaida() != LocalTime.of(0,0)) {
                movimentacao.setAtivo(false);
                movimentacaoRepository.save(movimentacao);
            } else {
                throw new IllegalArgumentException("Não é possível inativala uma movimentação sem data de saída preenchida");
            }
        } else {
            throw new IllegalArgumentException("movimentação não encontrada");
        }
    }
}
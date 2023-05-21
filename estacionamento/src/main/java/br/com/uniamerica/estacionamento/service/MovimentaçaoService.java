package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.ConfiguracaoRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import br.com.uniamerica.estacionamento.entity.*;
import com.sun.jdi.request.DuplicateRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;


@Service
public class MovimentaçaoService {

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

        movimentacao.setSaida(tempo);
        movimentacao.setTempo(tempo);
        movimentacao.setTempoMulta(tempo);
        movimentacao.setTempoDesconto(tempo);
        movimentacao.setValorTotal(valorTotal);
        movimentacao.setValorMulta(valorMulta);
        movimentacao.setValorHora(configuracao.getValorHora());

        veiculo.setAtivo(true);
        condutor.setAtivo(true);
        movimentacao.setAtivo(true);
        veiculoRepository.save(veiculo);
        condutorRepository.save(condutor);
        return this.movimentacaoRepository.save(movimentacao);
    }

    @Transactional
    public Movimentacao update(Movimentacao movimentacao){
        final Configuracao configuracao = this.configuracaoRepository.findById(movimentacao.getConfiguracao().getId())
                .orElseThrow(() -> new IllegalArgumentException("Configuração não correspondente"));

        final Condutor condutor = this.condutorRepository.findById(movimentacao.getCondutor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Condutor não correspondente"));

        final Veiculo veiculo = this.veiculoRepository.findById(movimentacao.getVeiculo().getId())
                .orElseThrow(() -> new IllegalArgumentException("Veiculo não correspondente"));

        BigDecimal valorTotal = movimentacao.getValorTotal();
        LocalTime entrada = movimentacao.getEntrada();
        LocalTime saida = movimentacao.getSaida();


        BigDecimal valorPorMinuto = configuracao.getValorHora().divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);


        Duration duracao = Duration.between(entrada, saida);
        long segundos = duracao.toSeconds();
        LocalTime tempo = LocalTime.ofSecondOfDay(segundos);

        long saidaMinutos = saida.getHour() * 60 + saida.getMinute();
        long fimExpedienteMin = configuracao.getFimExpediente().getHour() * 60;


        if (saidaMinutos > fimExpedienteMin){
            long timeOut = saidaMinutos - fimExpedienteMin;
            int min = (int) (timeOut % 60);
            BigDecimal valorMulta = BigDecimal.valueOf(min).multiply(configuracao.getValorMultaMinuto());

            long tempoEmMinutos = ChronoUnit.MINUTES.between(entrada, saida);
            int horas = (int) (tempoEmMinutos / 60);
            int minutos = (int) (tempoEmMinutos % 60);
            BigDecimal valorTotalHoras = BigDecimal.valueOf(horas).multiply(configuracao.getValorHora());
            BigDecimal valorTotalMinutos = BigDecimal.valueOf(minutos).multiply(valorPorMinuto);
            valorTotal = valorTotalHoras.add(valorTotalMinutos);

            valorTotal = valorTotal.add(valorMulta);
            movimentacao.setValorTotal(valorTotal);
            movimentacao.setValorMulta(valorMulta);

            LocalTime tempoPago = condutor.getTempoPago();
            long tempoPagoMin = tempoPago.getHour() * 60 + tempoPago.getMinute();
            tempoPagoMin = tempoPagoMin + segundos;
            LocalTime timeboy = LocalTime.ofSecondOfDay(tempoPagoMin);
            condutor.setTempoPago(timeboy);
            movimentacao.setValorTotal(valorTotal);

        } else {
            BigDecimal valorMulta = BigDecimal.ZERO;
            long tempoEmMinutos = ChronoUnit.MINUTES.between(entrada, saida);
            int horas = (int) (tempoEmMinutos / 60);
            int minutos = (int) (tempoEmMinutos % 60);
            BigDecimal valorTotalHoras = BigDecimal.valueOf(horas).multiply(configuracao.getValorHora());
            BigDecimal valorTotalMinutos = BigDecimal.valueOf(minutos).multiply(valorPorMinuto);
            valorTotal = valorTotalHoras.add(valorTotalMinutos);


            LocalTime tempoPago = condutor.getTempoPago();
            long tempoPagoMin = tempoPago.getHour() * 60 + tempoPago.getMinute();
            tempoPagoMin = tempoPagoMin + segundos;
            LocalTime timeboy = LocalTime.ofSecondOfDay(tempoPagoMin);
            condutor.setTempoPago(timeboy);
            movimentacao.setValorTotal(valorTotal);
        }

        veiculo.setAtivo(false);
        condutor.setAtivo(false);
        movimentacao.setAtivo(false);
        movimentacao.setValorHora(configuracao.getValorHora());
        movimentacao.setTempo(tempo);
        veiculoRepository.save(veiculo);
        condutorRepository.save(condutor);
        return this.movimentacaoRepository.save(movimentacao);
    }



    @Transactional
    public void delete(Long id) {
        Optional<Movimentacao> optionalMovimentacao = movimentacaoRepository.findById(id);
        if (optionalMovimentacao.isPresent()) {
            Movimentacao movimentacao = optionalMovimentacao.get();
            if (movimentacao.getSaida() == null) {
                movimentacaoRepository.delete(movimentacao);
            } else {
                throw new IllegalArgumentException("Não é possível excluir uma movimentação com data de saída preenchida");
            }
        } else {
            throw new IllegalArgumentException("Movimentação não encontrada");
        }
    }



}

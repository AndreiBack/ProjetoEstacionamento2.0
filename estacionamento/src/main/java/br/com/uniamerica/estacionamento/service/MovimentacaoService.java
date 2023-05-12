package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Configuracao;
import br.com.uniamerica.estacionamento.entity.Movimentacao;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.ConfiguracaoRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class MovimentacaoService {
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    CondutorRepository condutorRepository;
    @Autowired
    ConfiguracaoRepository configuracaoRepository;

    @Transactional
    public Movimentacao validaCadastro(Movimentacao movimentacao){
        Veiculo veiculo = veiculoRepository.findById(movimentacao.getVeiculo().getId())
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        Condutor condutor = condutorRepository.findById(movimentacao.getCondutor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Condutor não encontrado"));

        movimentacao.setVeiculo(veiculo);
        movimentacao.setCondutor(condutor);

        return movimentacaoRepository.save(movimentacao);


    }
    public Movimentacao finalizarMovimentacao(Long id) {
        Movimentacao movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movimentacao não encontrada"));

        if (movimentacao.getSaida() == null) {
            movimentacao.setSaida(LocalDateTime.now());
            movimentacao.setTempo(calcularTempoTotal(movimentacao.getEntrada(), movimentacao.getSaida()));
            movimentacao.setValorTotal(calcularValorTotal(id,movimentacao.getTempo()));
            Condutor condutor = movimentacao.getCondutor();
            LocalTime tempopago = condutor.getTempoPago();
            if (tempopago == null) {
                tempopago = movimentacao.getTempo();
            } else {
                tempopago = tempopago.plusHours(movimentacao.getTempo().getHour())
                        .plusMinutes(movimentacao.getTempo().getMinute());
            }
            condutor.setTempoPago(tempopago);

            return movimentacaoRepository.save(movimentacao);
        } else {
            throw new IllegalStateException("Movimentacao ja finalizada");
        }
    }

    private LocalTime calcularTempoTotal(LocalDateTime entrada, LocalDateTime saida) {
        return LocalTime.ofNanoOfDay(saida.toLocalTime().toNanoOfDay() - entrada.toLocalTime().toNanoOfDay());
    }

    private BigDecimal calcularValorTotal(Long id,LocalTime tempo) {
        Configuracao configuracao = configuracaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Configuracao nao encontrada"));

        BigDecimal valorHora = configuracao.getValorHora();
        return valorHora.multiply(BigDecimal.valueOf(tempo.getHour()));
    }

    public void validaDelete(Long id) {
        Movimentacao movimentacao = movimentacaoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Movimentação não encontrada"));
        if (movimentacao.getSaida() != null) {
            movimentacaoRepository.delete(movimentacao);
        } else {
            throw new IllegalArgumentException("Não é possível excluir uma movimentação na qual o veiculo ainda não saiu do estacionamento");
        }
    }
}

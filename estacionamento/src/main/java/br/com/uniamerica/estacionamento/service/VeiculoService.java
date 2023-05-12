package br.com.uniamerica.estacionamento.service;
import br.com.uniamerica.estacionamento.entity.Cor;
import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
public class VeiculoService {
    @Autowired
    private VeiculoRepository veiculoRepository;

    Veiculo veiculo;
    @Autowired
    private ModeloRepository modeloRepository;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;


    public Veiculo validaCadastro(Veiculo veiculo) {
        Modelo modelo = veiculo.getModelo();
        if (!modeloRepository.existsById(modelo.getId())) {
            throw new IllegalArgumentException("Modelo do veículo não existe no sistema");
        }
        if (!Arrays.asList(Cor.values()).contains(veiculo.getCor())) {
            throw new IllegalArgumentException("A cor do veículo não existe no sistema");
        }
        int ano = veiculo.getAno();
        if (ano > 2023) {
            throw new IllegalArgumentException("Ano do veículo não é valido.");
        }

        String placa = veiculo.getPlaca();

                if (!placa.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}")||!placa.matches("[A-Z]{3}[0-9]{4}")) {
                    throw new IllegalArgumentException("A placa deve seguir o padrão AAA0A00 ou AAA0000");
                }
        return this.veiculoRepository.save(veiculo);
        }

    @Transactional
    public Veiculo validaDelete(Long id) {
        Optional<Veiculo> opVeiculo = veiculoRepository.findById(id);
        if (opVeiculo.isPresent()) {
            veiculo = opVeiculo.get();
            if (movimentacaoRepository.existsByVeiculo(veiculo)) {
                throw new IllegalArgumentException("O veiculo está em alguma movimentação, erro na exclusão");
            } else {
                veiculoRepository.delete(veiculo);
            }
        } else {
            throw new IllegalArgumentException("Veiculo não existe no sistema");
        }
        return null;
    }


}

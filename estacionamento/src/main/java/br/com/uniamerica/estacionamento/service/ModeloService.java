package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.controller.ModeloController;
import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
public class ModeloService {
    @Autowired
    private ModeloRepository modeloRepository;

    private Modelo modelo;
    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Transactional
    public void validaCadastro(Modelo modelo) {
        if (modelo.getMarca() != null && modelo.getMarca().getId() != null) {
            Optional<Marca> opMarca = marcaRepository.findById(modelo.getMarca().getId());
            if (!opMarca.isPresent()) {
                throw new IllegalArgumentException("A marca informada não existe.");
            }
        } else {
            throw new IllegalArgumentException("A marca é obrigatória.");
        }


        String nome = modelo.getNome();
        if (!nome.matches("[a-z A-Z]+")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras e não pode ser vazio.");
        }
    }
    @Transactional
    public void validaDelete(Long id){
        Optional<Modelo> opModelo = modeloRepository.findById(id);
        if (!opModelo.isPresent()) {
            throw new IllegalArgumentException("Modelo não encontrado");
        }

        Modelo modelo = opModelo.get();

        List<Veiculo> veiculos = veiculoRepository.findByModelo(modelo);

        if (!veiculos.isEmpty()) {
            throw new IllegalArgumentException("Não é possível excluir o modelo, pois existem veículos vinculados a ele");
        }

        modeloRepository.delete(modelo);
    }

}


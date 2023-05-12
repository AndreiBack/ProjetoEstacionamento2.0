package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarcaService {
    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    ModeloRepository modeloRepository;

    @Transactional
    public void validaCadastro(Marca marca) {
        String nome = marca.getNome();
        if (!nome.matches("[a-z A-Z]+")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras e não pode ser vazio.");
        }
    }
    @Transactional
    public void validaDelete(Long id ) throws Exception {

        Marca marcaEncontrada = marcaRepository.findById(id).orElseThrow(() -> new Exception("Marca não encontrada"));

        // Verifica se existem modelos vinculados à marca
        List<Modelo> modelos = modeloRepository.findByMarca(marcaEncontrada);
        if (!modelos.isEmpty()) {
            throw new IllegalArgumentException("Não é possível excluir a marca pois existem modelos vinculados a ela");
        }

        // Exclui a marca
        marcaRepository.delete(marcaEncontrada);
    }
}


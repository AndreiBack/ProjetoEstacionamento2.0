package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import br.com.uniamerica.estacionamento.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ModeloService {

    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    private ModeloRepository modeloRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional(rollbackFor = Exception.class)
    public Modelo newModelo(Modelo modelo) {
        if (modelo.getMarcaId() == null) {
            throw new IllegalArgumentException("Marca não informada");
        }
        final Marca marca = this.marcaRepository.findById(modelo.getMarcaId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Marca não existente"));
        final Marca marcaData = this.marcaRepository.findById(modelo.getMarcaId().getId()).orElse(null);
        Optional<Modelo> nomeJaCadastrado = modeloRepository.findByNome(modelo.getNome());
        marca.setAtivo(true);

        if (nomeJaCadastrado.isPresent()){
            throw new IllegalArgumentException("Já há um modelo cadastrado com esse nome");
        }else if (modelo.getNome() == null ) {
            throw new IllegalArgumentException("O nome deve ser preenxido");
        }else if (modelo.getNome().length() > 50){
            throw new IllegalArgumentException("O nome não pode ser maior que 50 caracteres");
        } else if (!modelo.getNome().matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras");
        }

        marcaRepository.save(marca);
        return modeloRepository.save(modelo);
    }
    @Transactional(rollbackFor = Exception.class)
    public Modelo edicao(Modelo modelo) {
        if (modelo.getMarcaId() == null) {
            throw new IllegalArgumentException("Marca não informada");
        }
        Optional<Modelo> nomeJaCadastrado = modeloRepository.findByNome(modelo.getNome());
        LocalDateTime dataCadastro = modelo.getCadastro();
        if (nomeJaCadastrado.isPresent() && !nomeJaCadastrado.get().getId().equals(modelo.getId())){
            throw new IllegalArgumentException("Já há um modelo cadastrado com esse nome");
        }else if (modelo.getNome() == null ) {
            throw new IllegalArgumentException("O nome deve ser preenxido");
        }else if (modelo.getNome().length() > 50){
            throw new IllegalArgumentException("O nome não pode ser maior que 50 caracteres");
        }else if (!modelo.getNome().matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras");
        } else if (dataCadastro == null){
            throw new IllegalArgumentException("Por favor informe a data de cadastro");
        }
        return modeloRepository.save(modelo);
    }


    @Transactional(rollbackFor = Exception.class)
    public void deletar(Long id){
        Modelo modelo = modeloRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Modelo não encontrado"));
        Marca marca = marcaRepository.findById(modelo.getMarcaId().getId()).orElse(null);

        if (modelo.isAtivo()) {
            throw new IllegalStateException("Não é possível excluir este modelo pois existem veículos cadastrados com ele.");
        } else {
            marca.setAtivo(false);
            marcaRepository.save(marca);
            modeloRepository.delete(modelo);
        }
    }
}
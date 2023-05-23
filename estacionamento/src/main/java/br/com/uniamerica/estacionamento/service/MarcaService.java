package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.entity.Marca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class MarcaService {
    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    private ModeloRepository modeloRepository;

    @Transactional(rollbackFor = Exception.class)
    public Marca newMarca(final Marca marca) {

        Optional<Marca> nomeJaCadastrado = marcaRepository.findByNome(marca.getNome());

        if (nomeJaCadastrado.isPresent()){
            throw new IllegalArgumentException("Já há um modelo cadastrado com esse nome");
        }else if (marca.getNome() == null ) {
            throw new IllegalArgumentException("O nome deve ser preenxido");
        }else if (marca.getNome().length() > 50){
            throw new IllegalArgumentException("O nome não pode ser maior que 50 caracteres");
        }else if (!marca.getNome().matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras");
        }
        marca.setAtivo(false);

        return marcaRepository.save(marca);
    }


    @Transactional(rollbackFor = Exception.class)
    public Marca update (Marca marca){
        Optional<Marca> nomeJaCadastrado = marcaRepository.findByNome(marca.getNome());
        LocalDateTime dataCadastro = marca.getCadastro();

        if (nomeJaCadastrado.isPresent() && !nomeJaCadastrado.get().getId().equals(marca.getId())){
            throw new IllegalArgumentException("Já há um modelo cadastrado com esse nome");
        }else if (marca.getNome() == null ) {
            throw new IllegalArgumentException("O nome deve ser preenxido");
        }else if (marca.getNome().length() > 50){
            throw new IllegalArgumentException("O nome não pode ser maior que 50 caracteres");
        }else if (!marca.getNome().matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras");
        }else if (dataCadastro == null){
            throw new IllegalArgumentException("Por favor informe a data de cadastro");
        }

        return this.marcaRepository.save(marca);
    }
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marca não encontrada"));
        if (marca.isAtivo()) {
            throw new IllegalStateException("Não é possível excluir esta marca pois existem modelos cadastrados com ela.");
        } else {
            marcaRepository.delete(marca);
        }
    }



}
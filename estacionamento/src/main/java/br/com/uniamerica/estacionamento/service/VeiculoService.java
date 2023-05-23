package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import br.com.uniamerica.estacionamento.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Year;
import java.util.Arrays;
import java.util.Optional;


@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private ModeloRepository modeloRepository;
    @Autowired
    private MarcaRepository marcaRepository;



    @Transactional
    public Veiculo cadastrar (Veiculo veiculo){
        if (veiculo.getModeloId() == null ) {
            throw new IllegalArgumentException("Modelo não informada");
        }
        if (veiculo.getModeloId().getMarcaId() == null) {
            throw new IllegalArgumentException("Marca não informada");
        }

        final Modelo modeloDatabase = this.modeloRepository.findById(veiculo.getModeloId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Modelo não existente"));
        final Marca marcaDatabase = this.marcaRepository.findById(veiculo.getModeloId().getMarcaId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Marca não existente"));

        Optional<Veiculo> veiculoPlacaBanco = this.veiculoRepository.findByPlaca(veiculo.getPlaca());
        int ano = veiculo.getAno();
        String anoEmString = Integer.toString(ano);
        Cor[] cores = Cor.values();
        Tipo[] tipos = Tipo.values();

        if (veiculoPlacaBanco.isPresent()){
            throw new IllegalArgumentException("Existe outro veiculo com essa placa");
        } else if (veiculo.getPlaca() == null ){
            throw new IllegalArgumentException("A placa deve ser preenchida");
        } else if (!veiculo.getPlaca().matches("[A-Z]{3}[0-9][A-Z][0-9]{2}|([A-Z]{3}-[0-9]{4})")) {
            throw new IllegalArgumentException("A placa deve estar no formato XXX0X00 ou XXX-0000");
        } else if (anoEmString == null ) {
            throw new IllegalArgumentException("A ano deve ser preenchida");
        } else if (!anoEmString.matches("[0-9]{4}")) {
            throw new IllegalArgumentException("Ano tem somente 4 digitos");
        } else if (ano < 1900 || ano > Year.now().getValue()){
            throw new IllegalArgumentException("O ano deve ser maior que o ano de 1900 e menor ou igual que 2023");
        } else if (veiculo.getCor() == null) {
            throw new IllegalArgumentException("A cor do veiculo deve ser preenchida");
        } else if (!Arrays.asList(cores).contains(veiculo.getCor())){
            throw new IllegalArgumentException("A cor deve ser um valor válido (AZUL|PRETO|CINZA|MARRON|VERMELHO|PRATA|BRANCO|AMARELO|VERDE)");
        } else if (veiculo.getTipo() == null) {
            throw new IllegalArgumentException("O tipo deve ser preenchida");
        } else if (!Arrays.asList(tipos).contains(veiculo.getTipo())) {
            throw new IllegalArgumentException("O tipo deve ser um valor válido (CARRO|VAN|MOTO)");
        }

        veiculo.setAtivo(false);

        Modelo modelo = modeloRepository.findById(veiculo.getModeloId().getId()).orElse(null);
        modelo.setAtivo(true);
        modeloRepository.save(modelo);

        return this.veiculoRepository.save(veiculo);
    }

    @Transactional
    public Veiculo editar (Veiculo veiculo){
        if (veiculo.getModeloId() == null) {
            throw new IllegalArgumentException("Modelo não informada");
        }
        final Modelo modelo = this.modeloRepository.findById(veiculo.getModeloId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Modelo não existente"));

        Optional<Veiculo> veiculoPlacaBanco = this.veiculoRepository.findByPlaca(veiculo.getPlaca());
        int ano = veiculo.getAno();
        String anoEmString = Integer.toString(ano);
        Optional<Veiculo> placaJaCadastrada = this.veiculoRepository.findByPlaca(veiculo.getPlaca());
        Cor[] cores = Cor.values();
        Tipo[] tipos = Tipo.values();

        if (veiculoPlacaBanco.isPresent() && !placaJaCadastrada.get().getId().equals(veiculo.getId())){
            throw new IllegalArgumentException("Existe outro veiculo com essa placa");
        }else if (veiculo.getPlaca() == null ){
            throw new IllegalArgumentException("A placa deve ser preenchida");
        } else if (!veiculo.getPlaca().matches("[A-Z]{3}[0-9][A-Z][0-9]{2}|([A-Z]{3}-[0-9]{4})")) {
            throw new IllegalArgumentException("A placa deve estar no formato XXX0X00 ou XXX-0000");
        } else if (anoEmString == null ) {
            throw new IllegalArgumentException("A ano deve ser preenchida");
        } else if (!anoEmString.matches("[0-9]{4}")) {
            throw new IllegalArgumentException("Ano tem somente 4 digitos");
        } else if (ano < 1900 || ano > Year.now().getValue()){
            throw new IllegalArgumentException("O ano deve ser maior que o ano de 1900 e menor ou igual que 2023");
        } else if (veiculo.getCor() == null) {
            throw new IllegalArgumentException("A cor do veiculo deve ser preenchida");
        } else if (!Arrays.asList(cores).contains(veiculo.getCor())){
            throw new IllegalArgumentException("A cor deve ser um valor válido (AZUL|PRETO|CINZA|MARRON|VERMELHO|PRATA|BRANCO|AMARELO|VERDE)");
        } else if (veiculo.getTipo() == null) {
            throw new IllegalArgumentException("O tipo deve ser preenchida");
        } else if (!Arrays.asList(tipos).contains(veiculo.getTipo())) {
            throw new IllegalArgumentException("O tipo deve ser um valor válido (CARRO|VAN|MOTO)");
        } else if (veiculo.getCadastro() == null){
            throw new IllegalArgumentException("Por favor informe a data de cadastro");
        }

        return this.veiculoRepository.save(veiculo);
    }
    public void delete(Long id) {
        final Veiculo verificacao = this.veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Não foi possível identificar o registro informado"));
        final Modelo modelo = modeloRepository.findById(verificacao.getModeloId().getId()).orElse(null);
        if (verificacao.isAtivo()){
            throw new IllegalArgumentException("O Veiculo está presente em alguma movimentação e não pode ser excluído.");
        }else {
            modelo.setAtivo(false);
            modeloRepository.save(modelo);
            this.veiculoRepository.delete(verificacao);
        }
    }

}
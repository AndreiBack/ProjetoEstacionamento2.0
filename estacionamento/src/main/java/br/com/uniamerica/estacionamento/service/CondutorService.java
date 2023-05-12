package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CondutorService {
    private CondutorRepository condutorRepository;
    private MovimentacaoRepository movimentacaoRepository;
    private Condutor condutor;

    @Transactional
    public Condutor validaCadastro(Condutor condutor) {
        String nome = condutor.getNome();
        String telefone = condutor.getTelefone();
        String Cpf = condutor.getCpf();
        if (!nome.matches("[a-z A-Z]+")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras e não pode ser vazio");
        }
        if (!telefone.matches("\\+[1-9]{3}\\([1-9]{3}\\)[1-9]{4}-[1-9]{4}")) {
            throw new IllegalArgumentException("O numero de telefone deve estar no formato:+xxx(xxx)xxxx-xxxx");
        }
        if (!Cpf.matches("[0-9]{3}.[0-9]{3}.[0-9]{3}-[0-9]{2}")) {
            throw new IllegalArgumentException("O CPF deve estar no formato: xxx.xxx.xxx-xx");
        }
        return this.condutorRepository.save(condutor);
    }
    @Transactional
    public Condutor validaDelete(Long id) {
        Optional<Condutor> opCondutor = condutorRepository.findById(id);
        if (opCondutor.isPresent()) {
            condutor = opCondutor.get();

            if (movimentacaoRepository.existsByCondutor(condutor)) {
                throw new IllegalArgumentException("O condutor está em alguma movimentação, erro na exclusão");
            } else {
                condutorRepository.delete(condutor);
            }
        } else {
            throw new IllegalArgumentException("Condutor não existe no sistema");
        }
        return null;
    }

}
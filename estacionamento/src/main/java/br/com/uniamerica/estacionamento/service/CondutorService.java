package br.com.uniamerica.estacionamento.service;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Tipo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Optional;

@Service
public class CondutorService {

    @Autowired
    private CondutorRepository condutorRepository;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public Condutor newCondutor(Condutor condutor){

        Optional<Condutor> cpfJaCadastrado = condutorRepository.findByCpf(condutor.getCpf());

        String cpf = condutor.getCpf();
        String telefone = condutor.getTelefone();
        String nome = condutor.getNome();

        if (nome == null || !nome.matches("[a-zA-Z\\s]+") || nome.length() > 100) {
            throw new IllegalArgumentException("O nome deve ter apenas letras e ter menos de 100 caracteres");
        }
        if (telefone == null || !telefone.matches("\\(\\d{3}\\)\\d{5}-\\d{4}")) {
            throw new IllegalArgumentException("O telefone deve estar no formato (000)00000-0000");
        }
        if (cpfJaCadastrado.isPresent()){
            throw new IllegalArgumentException("Já há um condutor cadastrado com esse cpf");
        }else if (cpf == null || !cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new IllegalArgumentException("O CPF deve estar no formato 000.000.000-00");
        }
        LocalTime tempoPago = LocalTime.of(0,0);
        condutor.setTempoPago(tempoPago);
       return this.condutorRepository.save(condutor);
    }

    @Transactional
    public Condutor update(Condutor condutor){

        Optional<Condutor> cpfJaCadastrado = condutorRepository.findByCpf(condutor.getCpf());

        String cpf = condutor.getCpf();
        String telefone = condutor.getTelefone();
        String nome = condutor.getNome();

        if (nome == null || !nome.matches("[a-zA-Z\\s]+")) {
            throw new IllegalArgumentException("O nome deve ter apenas letras e ter menos de 100 caracteres");
        }
         if (telefone == null || !telefone.matches("\\(\\d{3}\\)\\d{5}-\\d{4}")) {
            throw new IllegalArgumentException("O telefone deve estar no formato (000)00000-0000");
        }
        if (cpfJaCadastrado.isPresent() && !cpfJaCadastrado.get().getId().equals(condutor.getId())){
            throw new IllegalArgumentException("Já há um condutor cadastrado com esse cpf");
        }else if (cpf == null || !cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new IllegalArgumentException("O CPF deve estar no formato XXX.XXX.XXX-XX");
        }

        return this.condutorRepository.save(condutor);
    }

    @Transactional
    public void delete(Long id) {
        final Condutor verificacao = this.condutorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Não foi possível identificar o registro informado"));
        if (verificacao.isAtivo()) {
                throw new IllegalArgumentException("O condutor está presente em alguma movimentação e não pode ser excluído.");
        } else {
                this.condutorRepository.delete(verificacao);
        }
    }
}


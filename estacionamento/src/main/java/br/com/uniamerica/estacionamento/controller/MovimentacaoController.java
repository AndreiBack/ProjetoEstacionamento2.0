package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.entity.Movimentacao;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping(value = "/api/movimentacao")
public class MovimentacaoController {

    @Autowired
    private br.com.uniamerica.estacionamento.repository.MovimentacaoRepository MovimentacaoRepository;
    private MovimentacaoRepository movimentacaoRepository;
    @Autowired
    MovimentacaoService movimentacaoService;

    Movimentacao movimentacao;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdPath(@PathVariable("id") final Long id) {
        final Movimentacao Movimentacao = this.MovimentacaoRepository.findById(id).orElse(null);
        return Movimentacao == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrado.")
                : ResponseEntity.ok(Movimentacao);
        //return ResponseEntity.ok(new Modelo());
    }

    @GetMapping
    public ResponseEntity<?> findByIdRequest(@RequestParam("id") final Long id) {
        final Movimentacao Movimentacao = this.MovimentacaoRepository.findById(id).orElse(null);

        return Movimentacao == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrado.")
                : ResponseEntity.ok(Movimentacao);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> findAll() {
        final List<Movimentacao> Movimentacao = this.MovimentacaoRepository.findAll();

        return ResponseEntity.ok(Movimentacao);
    }

    @GetMapping("/abertos")
    public ResponseEntity<?> findByAberto() {
        final List<Movimentacao> movimentacao = this.movimentacaoRepository.findBySaidaIsNull();

        return ResponseEntity.ok(movimentacao);
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Movimentacao Movimentacao) {
        try {
            movimentacaoService.validaCadastro(movimentacao);
            this.MovimentacaoRepository.save(Movimentacao);
            return ResponseEntity.ok("Registro cadastrado com sucesso");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body("Error" + e.getCause().getCause().getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editar(@RequestParam("id") final Long id, @RequestBody final Movimentacao Movimentacao) {
        try {
            final Movimentacao MovimentacaoBanco = this.MovimentacaoRepository.findById(id).orElse(null);

            if (MovimentacaoBanco == null || !MovimentacaoBanco.getId().equals(Movimentacao.getId())) {
                throw new RuntimeException("Não foi possível identificar o registro informado");
            }

            this.MovimentacaoRepository.save(Movimentacao);
            return ResponseEntity.ok("Registro editado com sucesso");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body("Error " + e.getCause().getCause().getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Error " + e.getMessage());
        }
    }

    @PutMapping("/{id}/finalizar")
    public Movimentacao finalizarMovimentacao(@PathVariable Long id) {

        return movimentacaoService.finalizarMovimentacao(id);
    }


    @DeleteMapping
    public ResponseEntity<?> excluir(@RequestParam("id") final Long id) {
        try {
            movimentacaoService.validaDelete(id);
            final Movimentacao movimentacao = this.movimentacaoRepository.findById(id).orElse(null);
            if (movimentacao == null) {
                throw new Exception("Registro inexistente");
            }

            movimentacao.setAtivo(false);
            this.movimentacaoRepository.delete(movimentacao);
            return ResponseEntity.ok("Registro não está mais ativo");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error" + e.getMessage());
        }
    }


}

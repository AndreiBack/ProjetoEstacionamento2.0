package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Configuracao;
import br.com.uniamerica.estacionamento.repository.ConfiguracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping(value = "/api/configuracao")
public class ConfiguracaoController {
    @Autowired
    private ConfiguracaoRepository ConfiguracaoRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdPath(@PathVariable("id") final Long id) {
        final Configuracao Configuracao = this.ConfiguracaoRepository.findById(id).orElse(null);
        return Configuracao == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrado.")
                : ResponseEntity.ok(Configuracao);
        //return ResponseEntity.ok(new Modelo());
    }


    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Configuracao Configuracao) {
        try {
            this.ConfiguracaoRepository.save(Configuracao);
            return ResponseEntity.ok("Registro cadastrado com sucesso");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body("Error" + e.getCause().getCause().getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editar(@RequestParam("id") final Long id, @RequestBody final Configuracao Configuracao) {
        try {
            final Configuracao ConfiguracaoBanco = this.ConfiguracaoRepository.findById(id).orElse(null);

            if (ConfiguracaoBanco == null || !ConfiguracaoBanco.getId().equals(Configuracao.getId())) {
                throw new RuntimeException("Não foi possível identificar o registro informado");
            }

            this.ConfiguracaoRepository.save(Configuracao);
            return ResponseEntity.ok("Registro editado com sucesso");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body("Error " + e.getCause().getCause().getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Error " + e.getMessage());
        }
    }


}

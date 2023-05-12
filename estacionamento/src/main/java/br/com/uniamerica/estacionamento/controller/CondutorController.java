package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Movimentacao;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.service.CondutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/api/condutor")
public class CondutorController {

    @Autowired
    private CondutorRepository condutorRepository;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    @Autowired
    private CondutorService condutorService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdPath(@PathVariable("id") final Long id) {
        final Condutor condutor = this.condutorRepository.findById(id).orElse(null);
        return condutor == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrado.")
                : ResponseEntity.ok(condutor);
        //return ResponseEntity.ok(new Modelo());
    }

    @GetMapping
    public ResponseEntity<?> findByIdRequest(@RequestParam("id") final Long id) {
        final Condutor condutor = this.condutorRepository.findById(id).orElse(null);

        return condutor == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrado.")
                : ResponseEntity.ok(condutor);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> findAll() {
        final List<Condutor> condutor = this.condutorRepository.findAll();

        return ResponseEntity.ok(condutor);
    }
    @GetMapping("/ativos")
    public ResponseEntity<?> findByAtivo(){
        final List<Condutor> condutores = this.condutorRepository.findByAtivo();

        return ResponseEntity.ok(condutores);
    }


    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Condutor condutor) {
        try {
            condutorService.validaCadastro(condutor);
            return ResponseEntity.ok("Registro cadastrado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body("Error" + e.getCause().getCause().getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editar(@RequestParam("id") final Long id, @RequestBody final Condutor condutor) {
        try {
            final Condutor condutorBanco = this.condutorRepository.findById(id).orElse(null);

            if (condutorBanco == null || !condutorBanco.getId().equals(condutor.getId())) {
                throw new RuntimeException("Não foi possível identificar o registro informado");
            }

            this.condutorRepository.save(condutor);
            return ResponseEntity.ok("Registro editado com sucesso");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body("Error " + e.getCause().getCause().getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Error " + e.getMessage());
        }
    }


    @DeleteMapping
    public ResponseEntity<?> excluir(@RequestParam("id") final Long id){
        try {
            condutorService.validaDelete(id);

            final Condutor condutor = this.condutorRepository.findById(id).orElse(null);
            if(condutor == null){
                throw new Exception("Registro inexistente");
            }

            final List<Movimentacao> movimentacaoL = movimentacaoRepository.findAll();
            for(Movimentacao movimentacao : movimentacaoL){
                if(condutor.equals(movimentacao.getCondutor())){
                    condutor.setAtivo(false);
                    this.condutorRepository.save(condutor);
                    return ResponseEntity.ok("Registro não está mais ativo");
                }
            }

            if(condutor.isAtivo()){
                this.condutorRepository.delete(condutor);
                return ResponseEntity.ok("Registro deletado com sucesso");
            }
            else{
                throw new Exception("Não foi possível excluir o registro");
            }
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error" + e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body("Error" + e.getMessage());
        }
    }
}




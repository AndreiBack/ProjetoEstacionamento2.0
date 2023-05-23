package br.com.uniamerica.estacionamento.controller;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.entity.*;
import br.com.uniamerica.estacionamento.service.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/api/modelo")
public class ModeloController {

    @Autowired
    private ModeloService modeloService;
    @Autowired
    private ModeloRepository modeloRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdPath(@PathVariable("id") final Long id){
        final Modelo modeloBanco = this.modeloRepository.findById(id).orElse(null);

        return modeloBanco == null
                ? ResponseEntity.badRequest().body("Veiculo não encontrado")
                : ResponseEntity.ok(modeloBanco);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> findall(){
        final List<Modelo> veiculo = this.modeloRepository.findAll();

        return ResponseEntity.ok(veiculo);
    }

    @GetMapping("/ativos")
    public ResponseEntity<?>findByAtivos(){
        final List<Modelo> modelosAtivos = this.modeloRepository.findByAtivoTrue();

        return ResponseEntity.ok(modelosAtivos);
    }

    @PostMapping
    public ResponseEntity<?> newModelo(@RequestBody final Modelo modelo){
        try{
            this.modeloService.newModelo(modelo);
            return ResponseEntity.ok("Registro cadastrado com sucesso");
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edicao(@PathVariable("id") final Long id, @RequestBody final Modelo modelo){
        try{
            final Modelo verificacao = this.modeloRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Não foi possível identificar o registro informado"));
            this.modeloService.edicao(modelo);
            return ResponseEntity.ok("Registro editado com sucesso");
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body( e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            modeloService.deletar(id);
            return ResponseEntity.ok("Modelo excluido com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}

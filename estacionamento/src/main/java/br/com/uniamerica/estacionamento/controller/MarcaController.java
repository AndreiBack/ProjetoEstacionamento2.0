package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/marca")
public class MarcaController {

    @Autowired
    private MarcaService marcaService;
    @Autowired
    private MarcaRepository marcaRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdPaht(@PathVariable ("id") Long id){
        final Marca marca = this.marcaRepository.findById(id).orElse(null);
        return marca == null
                ? ResponseEntity.badRequest().body("Marca não encontrada")
                : ResponseEntity.ok(marca);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> findAll(){
        final List<Marca> listMarca = this.marcaRepository.findAll();
        return ResponseEntity.ok(listMarca);
    }

    @GetMapping("/ativos")
    public ResponseEntity<?> findByAtivos(){
        final List<Marca> ativosMarca = this.marcaRepository.findByAtivoTrue();
        return ResponseEntity.ok(ativosMarca);
    }

    @PostMapping
    public ResponseEntity<?> newMarca(@RequestBody final Marca marca){
        try {
            this.marcaService.newMarca(marca);
            return ResponseEntity.ok("Marca cadastrada com sucesso");
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> uddate(@PathVariable ("id") Long id ,@RequestBody final Marca marca ){
        try {
            final Marca verificacao = this.marcaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Não foi possível identificar o registro informado"));
            this.marcaService.update(marca);
            return ResponseEntity.ok("Registro editado com sucesso");
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body( e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable ("id") Long id){
        try {
            this.marcaService.delete(id);
            return ResponseEntity.ok("Registro deletado com sucesso");
        } catch (DataIntegrityViolationException  e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body( e.getMessage());
        }
    }


}














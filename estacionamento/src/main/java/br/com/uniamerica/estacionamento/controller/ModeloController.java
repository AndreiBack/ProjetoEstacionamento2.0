package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import br.com.uniamerica.estacionamento.service.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/api/modelo")
public class ModeloController {
     private ModeloService modeloService;
    @Autowired
    private ModeloRepository modeloRepository;
    private VeiculoRepository veiculoRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdPath(@PathVariable("id") final Long id) {
        final Modelo modelo = this.modeloRepository.findById(id).orElse(null);
        return modelo == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrado.")
                : ResponseEntity.ok(modelo);
        //return ResponseEntity.ok(new Modelo());
    }

    @GetMapping
    public ResponseEntity<?> findByIdRequest(@RequestParam("id") final Long id) {
        final Modelo modelo = this.modeloRepository.findById(id).orElse(null);

        return modelo == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrado.")
                : ResponseEntity.ok(modelo);
    }
    @GetMapping("/ativos")
    public ResponseEntity<?> findByAtivo(){
        final List<Modelo> modelos = this.modeloRepository.findByAtivo();

        return ResponseEntity.ok(modelos);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> findAll() {
        final List<Modelo> modelo = this.modeloRepository.findAll();

        return ResponseEntity.ok(modelo);
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Modelo modelo) {
        try {
            modeloService.validaCadastro(modelo);
            this.modeloRepository.save(modelo);
            return ResponseEntity.ok("Registro cadastrado com sucesso");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body("Error" + e.getCause().getCause().getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editar(@RequestParam("id") final Long id, @RequestBody final Modelo modelo) {
        try {
            final Modelo modeloBanco = this.modeloRepository.findById(id).orElse(null);

            if (modeloBanco == null || !modeloBanco.getId().equals(modelo.getId())) {
                throw new RuntimeException("Não foi possível identificar o registro informado");
            }

            this.modeloRepository.save(modelo);
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
            modeloService.validaDelete(id);
            final Modelo modelo = this.modeloRepository.findById(id).orElse(null);
            if(modelo == null){
                throw new Exception("Registro inexistente");
            }

            final List<Veiculo> veiculos = this.veiculoRepository.findAll();

            for(Veiculo veiculo : veiculos){
                if(modelo.equals(veiculo.getModelo())){
                    modelo.setAtivo(false);
                    this.modeloRepository.save(modelo);
                    return ResponseEntity.ok("Registro não está mais ativo");
                }
            }

            if(modelo.isAtivo()){
                this.modeloRepository.delete(modelo);
                return ResponseEntity.ok("Registro deletado com sucesso");
            }
            else{
                throw new Exception("Não foi possível excluir o registro");
            }
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body("Error" + e.getMessage());
        }
    }
    }





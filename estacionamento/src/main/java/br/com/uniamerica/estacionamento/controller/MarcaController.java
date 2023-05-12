package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.service.MarcaService;
import br.com.uniamerica.estacionamento.service.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping(value = "/api/marca")
public class MarcaController {

    @Autowired
        private br.com.uniamerica.estacionamento.repository.MarcaRepository MarcaRepository;
    @Autowired
    private ModeloRepository modeloRepository;

    MarcaService marcaService;

    Marca marca;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdPath(@PathVariable("id") final Long id) {
        final Marca Marca = this.MarcaRepository.findById(id).orElse(null);
        return Marca == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrado.")
                : ResponseEntity.ok(Marca);
        //return ResponseEntity.ok(new Modelo());
    }

    @GetMapping
    public ResponseEntity<?> findByIdRequest(@RequestParam("id") final Long id) {
        final Marca Marca = this.MarcaRepository.findById(id).orElse(null);

        return Marca == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrado.")
                : ResponseEntity.ok(Marca);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> findAll() {
        final List<Marca> Marca = this.MarcaRepository.findAll();

        return ResponseEntity.ok(Marca);
    }
    @GetMapping("/ativos")
    public ResponseEntity<?> findByAtivo(){
        final List<Marca> marcas = this.MarcaRepository.findByAtivo();

        return ResponseEntity.ok(marcas);
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Marca Marca) {
        try {
            marcaService.validaCadastro(marca);
            this.MarcaRepository.save(Marca);
            return ResponseEntity.ok("Registro cadastrado com sucesso");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body("Error" + e.getCause().getCause().getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editar(@RequestParam("id") final Long id, @RequestBody final Marca Marca) {
        try {
            final Marca MarcaBanco = this.MarcaRepository.findById(id).orElse(null);

            if (MarcaBanco == null || !MarcaBanco.getId().equals(Marca.getId())) {
                throw new RuntimeException("Não foi possível identificar o registro informado");
            }

            this.MarcaRepository.save(Marca);
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
            marcaService.validaDelete(id);
            final Marca marca = this.MarcaRepository.findById(id).orElse(null);
            if(marca == null){
                throw new Exception("Registro inexistente");
            }

            final List<Modelo> modelos = this.modeloRepository.findAll();

            for(Modelo modelo : modelos){
                if(marca.equals(modelo.getMarca())){
                    marca.setAtivo(false);
                    this.MarcaRepository.save(marca);
                    return ResponseEntity.ok("Registro não está mais ativo");
                }
            }

            if(marca.isAtivo()){
                this.MarcaRepository.delete(marca);
                return ResponseEntity.ok("Registro deletado com sucesso");
            }
            else{
                throw new Exception("Não foi possível excluir o registro");
            }
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body("Error " + e.getMessage());
        }
    }
}




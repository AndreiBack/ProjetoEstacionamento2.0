package br.com.uniamerica.estacionamento.controller;


import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/veiculo")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;
    @Autowired
    private VeiculoRepository veiculoRepository;

   @GetMapping("/{id}")
   public ResponseEntity<?> findByIdPath(@PathVariable("id") final Long id){
       final Veiculo veiculoBanco = this.veiculoRepository.findById(id).orElse(null);

       return veiculoBanco == null
               ? ResponseEntity.badRequest().body("Veiculo não encontrado")
               : ResponseEntity.ok(veiculoBanco);
   }

   @GetMapping("/placa/{placa}")
    final ResponseEntity<?> findByPlacaPath(@PathVariable("placa") final String placa){
       final Optional<Veiculo> veiculoBanco = this.veiculoRepository.findByPlaca(placa);

       return veiculoBanco == null
               ? ResponseEntity.badRequest().body("Veiculo não encontrado")
               : ResponseEntity.ok(veiculoBanco);
   }

    @GetMapping("/lista")
    public ResponseEntity<?> findall(){
        final List<Veiculo> veiculo = this.veiculoRepository.findAll();

        return ResponseEntity.ok(veiculo);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Veiculo>>findByAtivos(){
        final List<Veiculo> veiculosAtivos = this.veiculoRepository.findByAtivoTrue();

        return ResponseEntity.ok(veiculosAtivos);
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Veiculo veiculo){
        try{
            this.veiculoService.cadastrar(veiculo);
            return ResponseEntity.ok("Registro cadastrado com sucesso");
        } catch (DataIntegrityViolationException  e) {
            return ResponseEntity.badRequest().body("Algum dado está incorreto ou faltando. ERRO BAD REQUEST");
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body( e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable("id") final Long id, @RequestBody final  Veiculo veiculo){
        try{
            final Veiculo verificacao = this.veiculoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Não foi possível identificar o registro informado"));
            this.veiculoService.editar(veiculo);
            return ResponseEntity.ok("Registro editado com sucesso");
        } catch (DataIntegrityViolationException  e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body( e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable final Long id) {
        try {
            this.veiculoService.delete(id);
            return ResponseEntity.ok("veiculo excluído com sucesso");
        } catch (DataIntegrityViolationException  e) {
            return ResponseEntity.badRequest().body("O Veiculo está presente em alguma movimentação e não pode ser excluído.");
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body( e.getMessage());
        }
    }
}

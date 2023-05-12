package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Movimentacao;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import br.com.uniamerica.estacionamento.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping(value = "/api/veiculo")
public class VeiculoController {

    private VeiculoService veiculoService;
    @Autowired
    private br.com.uniamerica.estacionamento.repository.VeiculoRepository VeiculoRepository;
    private MovimentacaoRepository movimentacaoRepository;
    private Veiculo veiculo;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdPath(@PathVariable("id") final Long id) {
        final Veiculo Veiculo = this.VeiculoRepository.findById(id).orElse(null);
        return Veiculo == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrado.")
                : ResponseEntity.ok(Veiculo);
        //return ResponseEntity.ok(new Modelo());
    }

    @GetMapping
    public ResponseEntity<?> findByIdRequest(@RequestParam("id") final Long id) {
        final Veiculo Veiculo = this.VeiculoRepository.findById(id).orElse(null);

        return Veiculo == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrado.")
                : ResponseEntity.ok(Veiculo);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> findAll() {
        final List<Veiculo> Veiculo = this.VeiculoRepository.findAll();

        return ResponseEntity.ok(Veiculo);
    }

    @GetMapping("/ativos")
    public ResponseEntity<?> findByAtivo(){
        final List<Veiculo> veiculos = this.VeiculoRepository.findByAtivo(true);

        return ResponseEntity.ok(veiculos);
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Veiculo Veiculo) {
        try {
            veiculoService.validaCadastro(veiculo);
            return ResponseEntity.ok("Registro cadastrado com sucesso");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body("Error" + e.getCause().getCause().getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> editar(@RequestParam("id") final Long id, @RequestBody final Veiculo Veiculo) {
        try {
            final Veiculo VeiculoBanco = this.VeiculoRepository.findById(id).orElse(null);

            if (VeiculoBanco == null || !VeiculoBanco.getId().equals(Veiculo.getId())) {
                throw new RuntimeException("Não foi possível identificar o registro informado");
            }

            this.VeiculoRepository.save(Veiculo);
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
            veiculoService.validaDelete(id);
            final Veiculo veiculo = this.VeiculoRepository.findById(id).orElse(null);
            if(veiculo == null){
                throw new Exception("Registro inexistente");
            }

            final List<Movimentacao> movimentacaos = this.movimentacaoRepository.findAll();
            for(Movimentacao movimentacao : movimentacaos){
                if(veiculo.equals(movimentacao.getVeiculo())){
                    veiculo.setAtivo(false);
                    this.VeiculoRepository.save(veiculo);
                    return ResponseEntity.ok("Registro não está mais ativo");
                }
            }

            if(veiculo.isAtivo()){
                this.VeiculoRepository.delete(veiculo);
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

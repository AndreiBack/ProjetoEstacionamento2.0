package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.entity.Movimentacao;
import br.com.uniamerica.estacionamento.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/api/movimentacao")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;


    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable ("id") final  Long id){
        final Movimentacao movimentacao = this.movimentacaoRepository.findById(id).orElse(null);

        return  movimentacao == null
                ? ResponseEntity.badRequest().body("Movimentação não encontrada")
                : ResponseEntity.ok(movimentacao);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> findByAll(){
        final List<Movimentacao> movimentacaos = this.movimentacaoRepository.findAll();
        return ResponseEntity.ok(movimentacaos);
    }

    @GetMapping("/abertos")
    public ResponseEntity<?> findByAbertos(){
        final List<Movimentacao> movimentacaos = this.movimentacaoRepository.findBySaidaIsNull();
        return ResponseEntity.ok(movimentacaos);
    }


    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Movimentacao movimentacao) {
        try {
            this.movimentacaoService.cadastrar(movimentacao);
            return ResponseEntity.ok("Registro cadastrado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getCause().getCause().getMessage());
        }
    }
@PutMapping("/finalizar/{id}")
    public ResponseEntity<?> finalizar(@PathVariable("id") final Long id, @RequestBody final Movimentacao movimentacaos) {
        try {
            final Movimentacao movimentacao = this.movimentacaoRepository.findById(id).orElse(null);
            this.movimentacaoService.update(movimentacaos);
            return ResponseEntity.ok("Data e Hora: " + movimentacao.getCadastro() + "\n" +
                    "entrada: " + movimentacao.getEntrada() + "\n" +
                    "saída: " + movimentacao.getSaida() + "\n" +
                    "Condutor: " + movimentacao.getCondutor().getNome() + " CPF: " + movimentacao.getCondutor().getCpf() + " TELEFONE: "
                    + movimentacao.getCondutor().getTelefone() + "TEMPO DESCONTO DISPONIVEL: " + movimentacao.getCondutor().getTempoDesconto() + " Minutos" + "\n" +
                    "Veiculo: " + movimentacao.getVeiculo().getPlaca() + " MODELO:" + movimentacao.getVeiculo().getModeloId().getNome()
                    + " COR:" + movimentacao.getVeiculo().getCor() + "\n" +
                    "Quantidade de Horas: " + movimentacao.getTempo() + "\n" +
                    "Quantidade de Horas Desconto: " + movimentacao.getTempoDesconto() + "\n" +
                    "Valor a Pagar: R$" + movimentacao.getValorTotal() + "\n" +
                    "Valor do Desconto: " + movimentacao.getValorDesconto() + "\n" +
                    "Valor da Multa: R$" + movimentacao.getValorMulta() + "\n");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getCause().getCause().getMessage());
        } catch (Exception e) {
            // Tratamento genérico para outros tipos de exceção
            return ResponseEntity.internalServerError().body("\"Erro no servidor ou algum dado está faltando,, sendo eles: \"id\" \"cadastro\" \"entrada\" \"saida\"  \"tempoDesconto\" \"veiculo\" \"configuracao\" \"condutor\"");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable final Long id) {
        try {
            this.movimentacaoService.delete(id);
            return ResponseEntity.ok("Movimentacao inativado com sucesso");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.internalServerError().body("Error " + e.getCause().getCause().getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body("Error " + e.getMessage());
        }
    }



}
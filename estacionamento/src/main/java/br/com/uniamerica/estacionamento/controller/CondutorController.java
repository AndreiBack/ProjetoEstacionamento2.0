package br.com.uniamerica.estacionamento.controller;
import br.com.uniamerica.estacionamento.repository.CondutorRepository;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Veiculo;
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

    /*
      @Autowired = Usado para acessar outras Classes
      @RequestMapping = Mapeia os metodos da controller / Define o URI para  qual o metodo deve responder
      @GetMapping = é usada para mapear uma solicitação HTTP GET
      ResponseEntity = usado para enviar informações adicionais no cabeçalho ou corpo da resposta
      <?> = usada para indicar que um tipo específico pode ser usado, mas que ainda não é conhecido ou não é relevante em um determinado contexto
      @PathVariable =  é usada para mapear variáveis de caminho de URL em parâmetros de método
      orElse =   é usado para fornecer um valor padrão quando um valor não está presente
      badRequest = é usado para criar uma resposta HTTP 400 (Bad Request)
    */


    @Autowired
    private  CondutorService condutorService;
    @Autowired
    private CondutorRepository condutorRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdPath(@PathVariable("id") final Long id) {
        final Condutor condutor = this.condutorRepository.findById(id).orElse(null);

        return condutor == null
                ? ResponseEntity.badRequest().body("Condutor não encontrado")
                : ResponseEntity.ok(condutor);
    }

    @GetMapping("/lista")
    public ResponseEntity<?> findAll() {
        List<Condutor> condutores = this.condutorRepository.findAll();
        return ResponseEntity.ok(condutores);
    }

    @GetMapping("/ativos")
    public ResponseEntity<?> findByAtivo(){
        List<Condutor> condutores = this.condutorRepository.findByAtivoTrue();

        return ResponseEntity.ok(condutores);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<?> findByCpf(@PathVariable("cpf") final String cpf){
        final Optional <Condutor> condutor = this.condutorRepository.findByCpf(cpf);

        return condutor == null
                ? ResponseEntity.badRequest().body("Condutor não encontrado")
                : ResponseEntity.ok(condutor);
    }

    @PostMapping
    public ResponseEntity<?> newCondutor(@RequestBody final Condutor condutor){
        try{
            this.condutorService.newCondutor(condutor);
            return ResponseEntity.ok("Registro cadastrado com sucesso");
        } catch (DataIntegrityViolationException  e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body( e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") final Long id, @RequestBody final Condutor condutor){
        try{
            final Condutor verificacao = this.condutorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Não foi possível identificar o registro informado"));
            this.condutorService.update(condutor);
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
            this.condutorService.delete(id);
            return ResponseEntity.ok("Condutor excluído com sucesso");
        }  catch (DataIntegrityViolationException  e) {
            return ResponseEntity.badRequest().body("O Condutor está presente em alguma movimentação e não pode ser excluído.");
        } catch (RuntimeException e){
            return ResponseEntity.internalServerError().body( e.getMessage());
        }
    }
}



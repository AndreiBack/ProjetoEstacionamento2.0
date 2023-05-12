package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Movimentacao;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    boolean existsByCondutor(Condutor condutor);
    List<Movimentacao> findBySaidaIsNull();
    @Query(value = "select * from tb_movimentacao where saida is null", nativeQuery = true)
    List<Movimentacao> findByAtivo();

    boolean existsByVeiculo(Veiculo veiculo);
}


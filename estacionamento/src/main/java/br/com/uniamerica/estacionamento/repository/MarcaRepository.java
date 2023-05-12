package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Configuracao;
import br.com.uniamerica.estacionamento.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    @Query(value = "select * from tb_marca where ativo=true", nativeQuery = true)
    List<Marca> findByAtivo();
}

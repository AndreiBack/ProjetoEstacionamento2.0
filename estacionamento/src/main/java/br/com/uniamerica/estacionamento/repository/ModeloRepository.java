package br.com.uniamerica.estacionamento.repository;

import br.com.uniamerica.estacionamento.entity.Configuracao;
import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.entity.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {
    @Query(value = "SELECT  FROM tb_modelo  WHERE ativo = 1",nativeQuery = true)
    public List<Modelo> findByAtivo();

    boolean findByNome(String nome);

    List<Modelo> findByMarca(Marca marca);
}

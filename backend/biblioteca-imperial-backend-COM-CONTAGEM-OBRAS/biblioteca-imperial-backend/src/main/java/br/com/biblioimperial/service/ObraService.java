package br.com.biblioimperial.service;

import br.com.biblioimperial.model.mysql.Obra;
import br.com.biblioimperial.repository.mysql.ObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para lógica de negócio relacionada a Obras
 *
 */
@Service
@RequiredArgsConstructor
public class ObraService {

    private final ObraRepository obraRepository;

    @Transactional(readOnly = true)
    public List<Obra> listarTodasObrasAtivas() {
        return obraRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Obra> buscarPorId(String idObra) {
        return obraRepository.findById(idObra);
    }

    @Transactional(readOnly = true)
    public List<Obra> buscarPorTitulo(String titulo) {
        return obraRepository.findByTituloContainingIgnoreCaseAndAtivoTrue(titulo);
    }

    @Transactional(readOnly = true)
    public List<Obra> buscarPorCategoria(String idCategoria) {
        return obraRepository.findByCategoria_IdCategoriaAndAtivoTrue(idCategoria);
    }

    @Transactional(readOnly = true)
    public List<Obra> buscarPorAutor(String idAutor) {
        return obraRepository.findByAutorIdAndAtivoTrue(idAutor);
    }

    @Transactional
    public Obra salvar(Obra obra) {
        return obraRepository.save(obra);
    }

    @Transactional
    public void deletar(String idObra) {
        obraRepository.deleteById(idObra);
    }

    @Transactional
    public Obra inativar(String idObra) {
        Optional<Obra> obraOpt = obraRepository.findById(idObra);
        if (obraOpt.isPresent()) {
            Obra obra = obraOpt.get();
            obra.setAtivo(false);
            return obraRepository.save(obra);
        }
        throw new RuntimeException("Obra não encontrada");
    }
}

package co.edu.uniquindio.logicapanaderia.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniquindio.logicapanaderia.model.Bitacora;
import co.edu.uniquindio.logicapanaderia.repository.BitacoraRepository;

@Service
@Transactional
public class BitacoraService {

    private final BitacoraRepository repo;

    public BitacoraService(BitacoraRepository repo) {
        this.repo = repo;
    }

    /**
     * Registra un evento en la bitácora con timestamp al momento.
     */
    public Bitacora registrarEvento(Long usuarioId, String evento, String detalle) {
        Bitacora log = new Bitacora();
        log.setTimestamp(LocalDateTime.now());
        log.setUsuarioId(usuarioId);
        log.setEvento(evento);
        log.setDetalle(detalle);
        return repo.save(log);
    }

    /**
     * Lista registros entre 'desde' y 'hasta'.
     * Asume que ambos parámetros no son null y vienen ya saneados.
     */
    public List<Bitacora> listarEntre(LocalDateTime desde, LocalDateTime hasta) {
        return repo.findByTimestampBetween(desde, hasta);
    }

    /**
     * Lista registros de un usuario entre dos fechas.
     */
    public List<Bitacora> listarPorUsuarioYFechas(Long usuarioId, LocalDateTime desde, LocalDateTime hasta) {
        return repo.findByUsuarioIdAndTimestampBetween(usuarioId, desde, hasta);
    }
}

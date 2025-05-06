package co.edu.uniquindio.logicapanaderia.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class StreamingService {

    private final List<SseEmitter> productoEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> clienteEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> pedidoEmitters = new CopyOnWriteArrayList<>();

    public SseEmitter createEmitter(String type) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        // Evitamos fugas liberándolo al completar o fallar
        emitter.onCompletion(() -> removeEmitter(type, emitter));
        emitter.onTimeout(() -> removeEmitter(type, emitter));
        getList(type).add(emitter);
        return emitter;
    }

    public void publishProduct(Object producto) {
        publish("producto", producto);
    }

    public void publishClient(Object cliente) {
        publish("cliente", cliente);
    }

    public void publishOrder(Object pedido) {
        publish("pedido", pedido);
    }

    private void publish(String type, Object payload) {
        List<SseEmitter> emitters = getList(type);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("new-" + type).data(payload));
            } catch (IOException e) {
                removeEmitter(type, emitter);
            }
        }
    }

    private List<SseEmitter> getList(String type) {
        switch (type) {
            case "cliente":
                return clienteEmitters;
            case "pedido":
                return pedidoEmitters;
            default:
                return productoEmitters;
        }
    }

    private void removeEmitter(String type, SseEmitter emitter) {
        getList(type).remove(emitter);
    }
}
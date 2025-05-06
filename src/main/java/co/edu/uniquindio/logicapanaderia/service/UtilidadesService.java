package co.edu.uniquindio.logicapanaderia.service;

import co.edu.uniquindio.logicapanaderia.dto.BackupRequest;
import co.edu.uniquindio.logicapanaderia.dto.ExportRequest;
import co.edu.uniquindio.logicapanaderia.dto.ImportRequest;
import co.edu.uniquindio.logicapanaderia.dto.RestoreRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import co.edu.uniquindio.logicapanaderia.model.*;
import co.edu.uniquindio.logicapanaderia.repository.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.*;

@Service
public class UtilidadesService {

    private final BackupHistoryRepository backupHistoryRepo;
    private final ClienteRepository clienteRepo;
    private final ProductoRepository productoRepo;
    private final PedidoRepository pedidoRepo;
    private final RepartidorRepository repartidorRepo;
    private final AdministradorRepository adminRepo;
    private final ObjectMapper objectMapper;

    public UtilidadesService(
            BackupHistoryRepository backupHistoryRepo,
            ClienteRepository clienteRepo,
            ObjectMapper objectMapper,
            ProductoRepository productoRepo,
            PedidoRepository pedidoRepo,
            RepartidorRepository repartidorRepo,
            AdministradorRepository adminRepo
    ) {
        this.backupHistoryRepo = backupHistoryRepo;
        this.clienteRepo = clienteRepo;
        this.objectMapper = objectMapper;
        this.productoRepo = productoRepo;
        this.pedidoRepo = pedidoRepo;
        this.repartidorRepo = repartidorRepo;
        this.adminRepo = adminRepo;
    }

    @PostConstruct
    public void initJackson() {
        // Esto forzará el registro, incluso si tu JacksonConfig no fue detectado
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // --- MÉTODOS UPSERT PARA IMPORTACION ---

    private void upsertCliente(Map<String,Object> rec, boolean update) {
        Long id = rec.get("id") != null ? Long.valueOf(rec.get("id").toString()) : null;
        Cliente entity = (id != null && update)
                ? clienteRepo.findById(id).orElse(new Cliente())
                : new Cliente();
        entity.setNombre((String) rec.get("nombre"));
        entity.setApellido((String) rec.get("apellido"));
        entity.setEmail((String) rec.get("email"));
        entity.setTelefono((String) rec.get("telefono"));
        entity.setDireccion((String) rec.get("direccion"));
        entity.setActivo(Boolean.parseBoolean(rec.getOrDefault("activo","true").toString()));
        clienteRepo.save(entity);
    }

    private void upsertProducto(Map<String,Object> rec, boolean update) {
        Long id = rec.get("id") != null ? Long.valueOf(rec.get("id").toString()) : null;
        Producto entity = (id != null && update)
                ? productoRepo.findById(id).orElse(new Producto())
                : new Producto();
        entity.setNombre((String) rec.get("nombre"));
        entity.setDescripcion((String) rec.get("descripcion"));
        entity.setPrecio(Double.parseDouble(rec.getOrDefault("precio","0").toString()));
        entity.setCategoria((String) rec.get("categoria"));
        entity.setStock(Integer.parseInt(rec.getOrDefault("stock","0").toString()));
        entity.setDisponible(Boolean.parseBoolean(rec.getOrDefault("disponible","true").toString()));
        productoRepo.save(entity);
    }

    private void upsertPedido(Map<String,Object> rec, boolean update) {
        // 1) Determinar id de Pedido
        Long id = rec.get("id") != null
                ? Long.valueOf(rec.get("id").toString())
                : null;

        // 2) Cargar o crear la entidad Pedido
        Pedido pedido = (id != null && update)
                ? pedidoRepo.findById(id).orElseGet(Pedido::new)
                : new Pedido();

        // 3) Asociar Cliente
        Long cliId = Long.valueOf(rec.get("clienteId").toString());
        Cliente cliente = clienteRepo.findById(cliId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + cliId));
        pedido.setCliente(cliente);

        // 4) Asociar Repartidor (opcional)
        if (rec.get("repartidorId") != null) {
            Long repId = Long.valueOf(rec.get("repartidorId").toString());
            Repartidor rep = repartidorRepo.findById(repId)
                    .orElseThrow(() -> new IllegalArgumentException("Repartidor no encontrado: " + repId));
            pedido.setRepartidor(rep);
        } else {
            pedido.setRepartidor(null);
        }

        // 5) Campos simples
        pedido.setFecha(LocalDateTime.parse(rec.get("fecha").toString()));
        pedido.setEstado(EstadoPedido.valueOf(rec.get("estado").toString()));
        pedido.setTotal(Double.parseDouble(rec.getOrDefault("total","0").toString()));
        pedido.setDireccionEntrega(rec.get("direccionEntrega").toString());
        pedido.setMetodoPago(rec.get("metodoPago").toString());
        pedido.setNotas((String) rec.get("notas"));

        // 6) Productos del pedido
        //    Se espera rec.get("productos") sea List<Map<String,Object>>
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> items = (List<Map<String,Object>>) rec.get("productos");
        // Limpia lista actual
        if (pedido.getProductos() != null) {
            pedido.getProductos().clear();
        } else {
            pedido.setProductos(new ArrayList<>());
        }
        for (Map<String,Object> it : items) {
            PedidoProducto pp = new PedidoProducto();
            // Cada PedidoProducto tiene referencia al Pedido
            pp.setPedido(pedido);
            // Producto por su ID (no la entidad completa aquí)
            pp.setProductoId(Integer.valueOf(it.get("productoId").toString()));
            pp.setNombre(it.get("nombre").toString());
            pp.setCantidad(Integer.valueOf(it.get("cantidad").toString()));
            pp.setPrecioUnitario(Double.parseDouble(it.getOrDefault("precioUnitario","0").toString()));
            pp.setSubtotal(Double.parseDouble(it.getOrDefault("subtotal","0").toString()));
            pedido.getProductos().add(pp);
        }

        // 7) Persistir el pedido (cascade guardará los PedidoProducto)
        pedidoRepo.save(pedido);
    }

    private void upsertRepartidor(Map<String,Object> rec, boolean update) {
        Long id = rec.get("id") != null ? Long.valueOf(rec.get("id").toString()) : null;
        Repartidor entity = (id != null && update)
                ? repartidorRepo.findById(id).orElse(new Repartidor())
                : new Repartidor();
        entity.setNombre((String) rec.get("nombre"));
        entity.setApellido((String) rec.get("apellido"));
        entity.setTelefono((String) rec.get("telefono"));
        entity.setEmail((String) rec.get("email"));
        entity.setVehiculo((String) rec.get("vehiculo"));
        entity.setLicencia((String) rec.get("licencia"));
        entity.setDisponible(Boolean.parseBoolean(rec.getOrDefault("disponible","true").toString()));
        repartidorRepo.save(entity);
    }

    private void upsertAdmin(Map<String,Object> rec, boolean update) {
        Long id = rec.get("id") != null ? Long.valueOf(rec.get("id").toString()) : null;
        Administrador entity = (id != null && update)
                ? adminRepo.findById(id).orElse(new Administrador())
                : new Administrador();
        entity.setNombre((String) rec.get("nombre"));
        entity.setApellido((String) rec.get("apellido"));
        entity.setEmail((String) rec.get("email"));
        entity.setTelefono((String) rec.get("telefono"));
        entity.setRol((String) rec.get("rol"));
        entity.setActivo(Boolean.parseBoolean(rec.getOrDefault("activo","true").toString()));
        adminRepo.save(entity);
    }

    // Helper para serializar listas
    private <T> byte[] serialize(List<T> list, String format, boolean headers, List<String> fieldOrder) throws IOException {
        switch (format.toLowerCase()) {
            case "json":
                return objectMapper.writeValueAsBytes(list);

            case "csv":
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(baos), CSVFormat.DEFAULT)) {
                    if (headers) printer.printRecord(fieldOrder);
                    for (T obj : list) {
                        var map = objectMapper.convertValue(obj, Map.class);
                        List<Object> row = new ArrayList<>();
                        for (String fld : fieldOrder) row.add(map.getOrDefault(fld, ""));
                        printer.printRecord(row);
                    }
                    printer.flush();
                    return baos.toByteArray();
                }

            case "excel":
                try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    Sheet sheet = wb.createSheet("data");
                    int r = 0;
                    if (headers) {
                        Row header = sheet.createRow(r++);
                        for (int i = 0; i < fieldOrder.size(); i++) {
                            header.createCell(i).setCellValue(fieldOrder.get(i));
                        }
                    }
                    for (T obj : list) {
                        Row row = sheet.createRow(r++);
                        var map = objectMapper.convertValue(obj, Map.class);
                        for (int i = 0; i < fieldOrder.size(); i++) {
                            Object v = map.get(fieldOrder.get(i));
                            if (v != null) {
                                if (v instanceof Number) row.createCell(i).setCellValue(((Number) v).doubleValue());
                                else row.createCell(i).setCellValue(v.toString());
                            }
                        }
                    }
                    wb.write(baos);
                    return baos.toByteArray();
                }

            default:
                throw new IllegalArgumentException("Formato no soportado: " + format);
        }
    }

    // --- IMPORTACIÓN ---
    /**
     * Importa registros desde un archivo multipart.
     */
    public void importData(ImportRequest req, MultipartFile file) throws IOException {
        String filename = Objects.requireNonNull(file.getOriginalFilename());
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        List<Map<String, Object>> records;
        switch (ext) {
            case "json":
                // Leemos directamente una lista de mapas
                records = objectMapper.readValue(
                        file.getInputStream(),
                        new TypeReference<List<Map<String, Object>>>() {}
                );
                break;

            case "csv":
                records = parseCsv(file.getInputStream(), req.isHasHeaders());
                break;

            case "xlsx":
            case "xls":
                records = parseExcel(file.getInputStream(), req.isHasHeaders());
                break;

            default:
                throw new IllegalArgumentException("Extensión no soportada: " + ext);
        }

        for (Map<String, Object> rec : records) {
            try {
                String type = req.getDataType().toLowerCase();
                boolean upd  = req.isUpdateExisting();
                switch (type) {
                    case "clientes":
                        upsertCliente(rec, upd);
                        break;
                    case "productos":
                        upsertProducto(rec, upd);
                        break;
                    case "pedidos":
                        upsertPedido(rec, upd);
                        break;
                    case "repartidores":
                        upsertRepartidor(rec, upd);
                        break;
                    case "administradores":
                        upsertAdmin(rec, upd);
                        break;
                    default:
                        throw new IllegalArgumentException("Tipo de datos no soportado: " + req.getDataType());
                }
            } catch (Exception e) {
                if (!req.isSkipErrors()) {
                    throw e;
                }
                // si skipErrors, ignoramos este registro
            }
        }
    }

    private List<Map<String,Object>> parseExcel(InputStream in, boolean hasHeaders) throws IOException {
        try (Workbook wb = WorkbookFactory.create(in)) {
            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> rows = sheet.rowIterator();
            List<String> headers = new ArrayList<>();
            if (hasHeaders && rows.hasNext()) {
                for (Cell c : rows.next()) headers.add(c.getStringCellValue());
            }
            List<Map<String,Object>> list = new ArrayList<>();
            while (rows.hasNext()) {
                Row row = rows.next();
                Map<String,Object> m = new HashMap<>();
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell c = row.getCell(i);
                    Object v = switch (c.getCellType()) {
                        case STRING -> c.getStringCellValue();
                        case NUMERIC -> c.getNumericCellValue();
                        case BOOLEAN -> c.getBooleanCellValue();
                        default -> null;
                    };
                    String key = hasHeaders ? headers.get(i) : "col" + i;
                    m.put(key, v);
                }
                list.add(m);
            }
            return list;
        }
    }

// --- RESPALDO ---

    /**
     * Crea un respaldo (full o parcial) empaquetando las entidades indicadas en un ZIP,
     * persiste el ZIP en la tabla backup_history y devuelve el registro guardado.
     */
    public BackupHistory createBackup(BackupRequest req) throws IOException {
        // 1) Generamos el ZIP con todas las entidades solicitadas en formato JSON
        byte[] zip = generateExport(req.getEntities(), "json", true, true);

        // 2) Construimos la entidad y guardamos
        BackupHistory hist = new BackupHistory();
        hist.setType(req.getType());
        hist.setDescription(req.getDescription());
        hist.setSizeBytes((long) zip.length);
        hist.setCreatedAt(LocalDateTime.now());
        hist.setData(zip);  // <-- campo @Lob byte[]

        BackupHistory saved = backupHistoryRepo.save(hist);
        return saved;
    }

    /**
     * Descarga el ZIP de un respaldo previamente creado.
     * @param id ID del respaldo
     * @return array de bytes del ZIP
     */
    public byte[] downloadBackup(Long id) {
        BackupHistory hist = backupHistoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Backup no encontrado: " + id));
        return hist.getData();
    }

    /**
     * Restaura un respaldo existente, filtrando por tipo ("full" o "partial")
     * y, en caso de parcial, respetando únicamente las entidades indicadas.
     *
     * @param entities Lista de nombres de entidades a restaurar (solo para partial).
     * @param type     "full" o "partial".
     */
    public void restoreBackup(List<String> entities, String type) {
        // 1) Obtener el backup más reciente de ese tipo
        BackupHistory hist = backupHistoryRepo
                .findFirstByTypeOrderByCreatedAtDesc(type)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró respaldo de tipo: " + type));

        byte[] zipData = hist.getData();

        // 2) Leer el ZIP
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String filename = entry.getName();            // e.g. "clientes.json"
                String entityName = filename.substring(0, filename.indexOf('.'));
                // Si es full, restauramos todo; si partial, solo los enlistados
                if ("full".equalsIgnoreCase(type) ||
                        entities.stream().anyMatch(e -> e.equalsIgnoreCase(entityName))) {

                    // 3) Extraer bytes para cada archivo y procesar import
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    zis.transferTo(baos);
                    byte[] fileBytes = baos.toByteArray();

                    // Usamos el mismo nombre de archivo para deducir formato y encabezados
                    processImport(
                            entityName,
                            fileBytes,
                            filename,
                            true,   // actualizar existentes
                            true,   // omitir errores
                            true    // asumimos contiene encabezados
                    );
                }
                zis.closeEntry();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error restaurando respaldo", ex);
        }
    }

    public void restoreFromFile(MultipartFile file) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                zis.transferTo(baos);
                String name = e.getName();
                String type = name.substring(0, name.indexOf('.'));
                ImportRequest ir = new ImportRequest(type, true, true, true);
                importData(ir, new org.springframework.web.multipart.MultipartFile() {
                    private final byte[] b = baos.toByteArray();
                    @Override public String getName(){return name;}
                    @Override public String getOriginalFilename(){return name;}
                    @Override public String getContentType(){return "application/json";}
                    @Override public boolean isEmpty(){return b.length==0;}
                    @Override public long getSize(){return b.length;}
                    @Override public byte[] getBytes(){return b;}
                    @Override public InputStream getInputStream(){ return new ByteArrayInputStream(b); }
                    @Override public void transferTo(File dest){}
                });
                zis.closeEntry();
            }
        }
    }

    /**
     * Genera la exportación de las entidades solicitadas.
     * @param entities      lista de nombres: "clientes", "productos", "pedidos", "repartidores", "administradores"
     * @param format        "json", "csv" o "excel"
     * @param includeHeaders si es CSV/Excel, incluir la fila de encabezados
     * @param compress      si true empaqueta en ZIP varios archivos
     * @return bytes del archivo resultante (ZIP o único)
     * @throws IOException
     */
    public byte[] generateExport(List<String> entities, String format, boolean includeHeaders, boolean compress) throws IOException {
        // Map<nombreArchivo, contenido>
        Map<String, byte[]> outputs = new LinkedHashMap<>();

        for (String ent : entities) {
            switch (ent.toLowerCase()) {
                case "clientes":
                    List<?> clientes = clienteRepo.findAll();
                    outputs.put("clientes." + extension(format), renderData("clientes", clientes, format, includeHeaders));
                    break;
                case "productos":
                    List<?> productos = productoRepo.findAll();
                    outputs.put("productos." + extension(format), renderData("productos", productos, format, includeHeaders));
                    break;
                case "pedidos":
                    List<?> pedidos = pedidoRepo.findAll();
                    outputs.put("pedidos." + extension(format), renderData("pedidos", pedidos, format, includeHeaders));
                    break;
                case "repartidores":
                    List<?> reps = repartidorRepo.findAll();
                    outputs.put("repartidores." + extension(format), renderData("repartidores", reps, format, includeHeaders));
                    break;
                case "administradores":
                    List<?> admins = adminRepo.findAll();
                    outputs.put("administradores." + extension(format), renderData("administradores", admins, format, includeHeaders));
                    break;
                default:
                    throw new IllegalArgumentException("Entidad no soportada: " + ent);
            }
        }

        if (compress) {
            // Empaqueta todos en ZIP
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (Map.Entry<String, byte[]> e : outputs.entrySet()) {
                    ZipEntry entry = new ZipEntry(e.getKey());
                    zos.putNextEntry(entry);
                    zos.write(e.getValue());
                    zos.closeEntry();
                }
                zos.finish();
                return baos.toByteArray();
            }
        } else {
            // Si sólo hay uno, devuelve directamente sus bytes
            if (outputs.size() == 1) {
                return outputs.values().iterator().next();
            }
            // Si hay varios pero no comprimir, lanza error
            throw new IllegalArgumentException("Para múltiples entidades debe habilitar compress=true");
        }
    }

    /** Devuelve la extensión de archivo según el formato */
    private String extension(String format) {
        return switch (format.toLowerCase()) {
            case "csv"   -> "csv";
            case "excel" -> "xlsx";
            case "json"  -> "json";
            default      -> throw new IllegalArgumentException("Formato no soportado: " + format);
        };
    }

    /**
     * Convierte la lista de objetos a bytes según el formato.
     */
    private byte[] renderData(String sheetName, List<?> data, String format, boolean includeHeaders) throws IOException {
        switch (format.toLowerCase()) {
            case "json":
                // escribe JSON "pretty" si quieres
                return objectMapper.writeValueAsBytes(data);

            case "csv":
                try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                     OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                     CSVPrinter csv = new CSVPrinter(writer,
                             includeHeaders
                                     ? CSVFormat.DEFAULT.withHeader(getCsvHeaders(data))
                                     : CSVFormat.DEFAULT
                     )) {
                    for (Object obj : data) {
                        Map<String, Object> map = objectMapper.convertValue(obj, new TypeReference<>() {});
                        csv.printRecord(map.values());
                    }
                    csv.flush();
                    return out.toByteArray();
                }

            case "excel":
                try (Workbook wb = new XSSFWorkbook();
                     ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    Sheet sheet = wb.createSheet(sheetName);
                    int rowIdx = 0;
                    if (includeHeaders && !data.isEmpty()) {
                        Row header = sheet.createRow(rowIdx++);
                        List<String> headers = Arrays.asList(getCsvHeaders(data));
                        for (int i = 0; i < headers.size(); i++) {
                            header.createCell(i).setCellValue(headers.get(i));
                        }
                    }
                    for (Object obj : data) {
                        Map<String, Object> map = objectMapper.convertValue(obj, new TypeReference<>() {});
                        Row row = sheet.createRow(rowIdx++);
                        int cellIdx = 0;
                        for (Object val : map.values()) {
                            Cell c = row.createCell(cellIdx++);
                            if (val != null) c.setCellValue(val.toString());
                        }
                    }
                    wb.write(out);
                    return out.toByteArray();
                }

            default:
                throw new IllegalArgumentException("Formato no soportado: " + format);
        }
    }

    /** Obtiene las cabeceras (keys) de un registro de ejemplo */
    private String[] getCsvHeaders(List<?> data) {
        Object first = data.stream().findFirst().orElse(null);
        if (first == null) return new String[0];
        Map<String, Object> map = objectMapper.convertValue(first, new TypeReference<>() {});
        return map.keySet().toArray(new String[0]);
    }

    /**
     * Procesa la importación de un archivo cargado.
     * @param dataType        tipo de dato: "clientes","productos",...
     * @param fileBytes       contenido del archivo
     * @param fileName        nombre original (para deducir extensión)
     * @param updateExisting  actualizar existentes?
     * @param skipErrors      omitir registros con error?
     * @param hasHeaders      el archivo incluye encabezados?
     */
    public void processImport(String dataType,
                              byte[] fileBytes,
                              String fileName,
                              boolean updateExisting,
                              boolean skipErrors,
                              boolean hasHeaders) {
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        List<Map<String,Object>> records;

        try {
            if ("csv".equals(ext)) {
                records = parseCsv(new ByteArrayInputStream(fileBytes), hasHeaders);
            } else if ("json".equals(ext)) {
                records = parseJson(new String(fileBytes));
            } else if ("xlsx".equals(ext) || "xls".equals(ext)) {
                records = parseExcel(fileBytes, hasHeaders);
            } else {
                throw new IllegalArgumentException("Formato no soportado: " + ext);
            }

            for (var rec : records) {
                try {
                    switch (dataType.toLowerCase()) {
                        case "clientes":       upsertCliente(rec, updateExisting); break;
                        case "productos":      upsertProducto(rec, updateExisting); break;
                        case "pedidos":        upsertPedido(rec, updateExisting); break;
                        case "repartidores":   upsertRepartidor(rec, updateExisting); break;
                        case "administradores":upsertAdmin(rec, updateExisting); break;
                        default: throw new IllegalArgumentException("Tipo no soportado: " + dataType);
                    }
                } catch (Exception e) {
                    if (!skipErrors) throw e;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo archivo", e);
        }
    }

    /**
     * Restaura un respaldo a partir de un array de bytes y el nombre de archivo.
     */
    public void restoreFromFile(byte[] fileBytes, String fileName) throws IOException {
        if (fileName.toLowerCase().endsWith(".zip")) {
            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fileBytes))) {
                ZipEntry ze;
                while ((ze = zis.getNextEntry()) != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    zis.transferTo(baos);
                    String name = ze.getName();
                    String type = name.substring(0, name.indexOf('.'));
                    processImport(type, baos.toByteArray(), name, true, true, true);
                    zis.closeEntry();
                }
            }
        } else {
            processImport(
                    fileName.substring(0, fileName.indexOf('.')),
                    fileBytes,
                    fileName,
                    true,
                    false,
                    true
            );
        }
    }

    // — Métodos auxiliares (stubs) —
    private byte[] exportClientes(String fmt, boolean hdr) { /* TODO */ return new byte[0]; }
    private byte[] exportProductos(String fmt, boolean hdr) { /* TODO */ return new byte[0]; }
    private byte[] exportPedidos(String fmt, boolean hdr) { /* TODO */ return new byte[0]; }
    private byte[] exportRepartidores(String fmt, boolean hdr) { /* TODO */ return new byte[0]; }
    private byte[] exportAdministradores(String fmt, boolean hdr) { /* TODO */ return new byte[0]; }

    private List<Map<String,Object>> parseCsv(InputStream in, boolean hdr) throws IOException {
        CSVParser parser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(new InputStreamReader(in));
        var out = new ArrayList<Map<String,Object>>();
        for (var rec : parser) {
            Map<String,Object> m = new HashMap<>();
            rec.toMap().forEach(m::put);
            out.add(m);
        }
        return out;
    }
    private List<Map<String,Object>> parseJson(String json) {
        // TODO: usar Jackson ObjectMapper para List<Map>
        return List.of();
    }
    private List<Map<String,Object>> parseExcel(byte[] b, boolean hdr) {
        // TODO: usar Apache POI para parsear hoja 0
        return List.of();
    }

    public List<BackupHistory> listBackups() {
        return backupHistoryRepo.findAll();
    }

    public void restoreBackupById(Long id) throws IOException {
        BackupHistory hist = backupHistoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Backup no encontrado: " + id));
        restoreFromFile(hist.getData(), "backup-" + id + ".zip");
    }

}

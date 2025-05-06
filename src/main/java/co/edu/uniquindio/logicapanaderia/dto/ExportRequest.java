package co.edu.uniquindio.logicapanaderia.dto;

import java.util.List;

public class ExportRequest {

    private List<String> entities;
    private String format;
    private boolean includeHeaders;
    private boolean compress;

    public ExportRequest() {}

    public ExportRequest(List<String> entities, String format, boolean includeHeaders, boolean compress) {
        this.entities = entities;
        this.format = format;
        this.includeHeaders = includeHeaders;
        this.compress = compress;
    }

    public List<String> getEntities() {
        return entities;
    }

    public void setEntities(List<String> entities) {
        this.entities = entities;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }
}

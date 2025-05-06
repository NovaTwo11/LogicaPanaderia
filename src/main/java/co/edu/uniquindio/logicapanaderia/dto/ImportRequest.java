package co.edu.uniquindio.logicapanaderia.dto;

/**
 * DTO para solicitudes de importación de datos.
 */
public class ImportRequest {

    private String dataType;
    private boolean updateExisting;
    private boolean skipErrors;
    private boolean hasHeaders;

    public ImportRequest() {
    }

    public ImportRequest(String dataType,
                         boolean updateExisting,
                         boolean skipErrors,
                         boolean hasHeaders) {
        this.dataType       = dataType;
        this.updateExisting = updateExisting;
        this.skipErrors     = skipErrors;
        this.hasHeaders     = hasHeaders;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isUpdateExisting() {
        return updateExisting;
    }

    public void setUpdateExisting(boolean updateExisting) {
        this.updateExisting = updateExisting;
    }

    public boolean isSkipErrors() {
        return skipErrors;
    }

    public void setSkipErrors(boolean skipErrors) {
        this.skipErrors = skipErrors;
    }

    public boolean isHasHeaders() {
        return hasHeaders;
    }

    public void setHasHeaders(boolean hasHeaders) {
        this.hasHeaders = hasHeaders;
    }
}

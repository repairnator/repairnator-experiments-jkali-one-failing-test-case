package org.javabeanstack.error;

import java.io.Serializable;

/**
 *
 * @author Jorge Enciso
 */
public interface IErrorReg extends Serializable {
    public String getEntity();    
    public String getFieldName();
    public String[] getFieldNames();    
    public String getMessage();
    public Integer getErrorNumber();
    public Exception getException();    
    public void setEntity(String entity);
    public void setFieldName(String fieldName);
    public void setFieldNames(String[] fieldNames);    
    public void setMessage(String message);
    public void setErrorNumber(int errorNumber);
    public void setException(Exception exp);
}

package io.swagger.oas.models;


import io.swagger.oas.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Schema(title = "aaa")
public class ApiFirstRequiredFieldModel {
    @Schema(title = "bla", required = true)
    @XmlElement(name = "a")
    public String getA() {
        return "aaa";
    }

    public String getC() {
        return "kkk";
    }
}

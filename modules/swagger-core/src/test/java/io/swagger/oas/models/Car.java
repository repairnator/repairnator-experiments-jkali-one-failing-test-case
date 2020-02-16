package io.swagger.oas.models;

import io.swagger.oas.annotations.media.Schema;

public class Car {
    @Schema(readOnly = true)
    public Integer getWheelCount() {
        return new Integer(4);
    }

    public void setWheelCount(Integer wheelCount) {
        // does nothing
    }
}
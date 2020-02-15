package org.evomaster.clientJava.controller.db.dsl;

import org.evomaster.clientJava.controllerApi.dto.database.operations.InsertionDto;

import java.util.List;

public interface StatementDsl {

    /**
     * Add a value to insert
     *
     * @param variableName  name of column in the table
     * @param printableValue  the value that is going to be inserted, as
     *                        it would be printed as string.
     *                        This means that 5 is represented with "5",
     *                        whereas "5" with "'5'"
     * @return
     */
    StatementDsl d(String variableName, String printableValue);

    /**
     *
     * @param variableName  name of column in the table
     * @param insertionId   id of an insertion operation done previously.
     *                      This field represents a foreign key to that row,
     *                      where the primary key is dynamically computed by
     *                      the database (eg, auto-increment).
     * @return
     */
    StatementDsl r(String variableName, long insertionId);

    /**
     * Concatenate a new SQL command
     * @return
     */
    SequenceDsl and();

    /**
     * Build the DTOs (Data Transfer Object) from this DSL,
     * closing it (ie, not usable any longer).
     * @return
     */
    List<InsertionDto> dtos();

}

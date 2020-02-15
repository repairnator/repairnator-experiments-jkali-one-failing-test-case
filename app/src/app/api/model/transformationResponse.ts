/**
 * TOSCAna
 * To be Done!
 *
 * OpenAPI spec version: 1.0.0-SNAPSHOT
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { LifecyclePhase } from './lifecyclePhase';


export interface TransformationResponse {
    /**
     * The phases of the transformation
     */
    phases: Array<LifecyclePhase>;
    /**
     * The platform identifier for this transformation
     */
    platform: string;
    /**
     * The Current State of the transformation. Has to be one of the following: \"READY\", \"INPUT_REQUIRED\", \"TRANSFORMING\", \"DONE\" or \"ERROR\"
     */
    state: TransformationResponse.StateEnum;
}
export namespace TransformationResponse {
    export type StateEnum = 'READY' | 'INPUT_REQUIRED' | 'TRANSFORMING' | 'DONE' | 'ERROR';
    export const StateEnum = {
        READY: 'READY' as StateEnum,
        INPUTREQUIRED: 'INPUT_REQUIRED' as StateEnum,
        TRANSFORMING: 'TRANSFORMING' as StateEnum,
        DONE: 'DONE' as StateEnum,
        ERROR: 'ERROR' as StateEnum
    }
}

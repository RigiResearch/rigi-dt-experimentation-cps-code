package com.rigiresearch.dt.experimentation.evolution.genetic;

import lombok.Getter;

/**
 * The changing properties during the evolution process.
 *
 * @author Felipe Rivera (rivera@uvic.ca)
 * @version $Id$
 * @since 0.1.0
 */
@Getter
public enum EvolvingProperties {

    HEADWAY("headway"),
    NUM_BUSES("buses"),
    SIM_FITNESS("simulation.fitness");

    /**
     * The id of the property in the configuration file.
     */
    private String id;

    /**
     * Constructor of the enum.
     * @param id The id of the property in the configuration file.
     */
    EvolvingProperties(String id) {
        this.id  = id;
    }

}

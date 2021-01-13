package com.rigiresearch.dt.experimentation.simulation;

import lombok.Getter;

/**
 * Constants for creating a random variable based on a particular distribution.
 */
@Getter
public enum Distribution {
    BERNOULLI,
    BINOMIAL,
    CONSTANT,
    EMPIRICAL,
    UNIFORM,
    GEOMETRIC,
    NEGATIVE_BINOMIAL,
    POISSON,
    SHIFTED_GEOMETRIC,
    UPDATABLE_CONSTANT;

    /**
     * Returns the appropriate constant based on its name.
     * @param name The constant's name
     * @return A constant
     */
    public static Distribution from(final String name) {
        final Distribution distribution;
        switch (name) {
            case "BernoulliRV":
                distribution = Distribution.BERNOULLI;
                break;
            case "BinomialRV":
                distribution = Distribution.BINOMIAL;
                break;
            case "ConstantRV":
                distribution = Distribution.CONSTANT;
                break;
            case "DEmpiricalRV":
                distribution = Distribution.EMPIRICAL;
                break;
            case "DUniformRV":
                distribution = Distribution.UNIFORM;
                break;
            case "GeometricRV":
                distribution = Distribution.GEOMETRIC;
                break;
            case "NegativeBinomialRV":
                distribution = Distribution.NEGATIVE_BINOMIAL;
                break;
            case "PoissonRV":
                distribution = Distribution.POISSON;
                break;
            case "ShiftedGeometricRV":
                distribution = Distribution.SHIFTED_GEOMETRIC;
                break;
            case "VConstantRV":
                distribution = Distribution.UPDATABLE_CONSTANT;
                break;
            default:
                throw new IllegalArgumentException(
                    String.format("Unknown name %s", name)
                );
        }
        return distribution;
    }

}
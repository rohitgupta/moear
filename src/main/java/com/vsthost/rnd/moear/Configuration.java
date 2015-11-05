package com.vsthost.rnd.moear;

/**
 * Defines a configuration representation for the problem in hand.
 */
public class Configuration {
    /**
     * Defines the name of the problem.
     */
    private String name;

    /**
     * Defines the description of the problem.
     */
    private String description;

    /**
     * Defines the version of the problem specification.
     */
    private String version;

    /**
     * Defines the author of the problem.
     */
    private String author;

    /**
     * Defines the algorithm to be used.
     */
    private String algorithm;

    /**
     * Defines the number of generations.
     */
    private int generations;


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public int getGenerations() {
        return generations;
    }
}

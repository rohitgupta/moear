package com.vsthost.rnd.moear;

import com.google.gson.Gson;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Defines a class representing command line arguments for the CLI application.
 */
public class CLIArguments {
    /**
     * Indicates if the application will run in silent mode.
     */
    @Option(name="-s", aliases={"--silent"}, usage="silent mode")
    private boolean silent = false;

    /**
     * Defines the R file which the problem is specified in.
     */
    @Option(name="-p", aliases={"--problem"}, usage="problem specification file")
    private File problemFile = new File("problem.R");

    /**
     * Defines the json file which contains the problem configuration.
     */
    @Option(name="-c", aliases={"--configuration"}, usage="problem configuration file")
    private File configurationFile = new File("configuration.json");

    /**
     * Indicates if the application will run in silent mode.
     *
     * @return True if the application shall run in silent mode, false otherwise.
     */
    public boolean isSilent () {
        return this.silent;
    }

    /**
     * Returns the R file which the problem is specified in.
     *
     * @return The R file which the problem is specified in.
     */
    public File getProblemFile () {
        return this.problemFile;
    }

    /**
     * Returns the JSON file which the problem configuration is specified in.
     *
     * @return The JSON file which the problem configuration is specified in.
     */
    public File getConfigurationFile () {
        return this.configurationFile;
    }

    /**
     * Returns the configuration file.
     *
     * @return The configuration file.
     */
    public Configuration getConfiguration () {
        try {
            return new Gson().fromJson(new FileReader(this.getConfigurationFile()), Configuration.class);
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }
}

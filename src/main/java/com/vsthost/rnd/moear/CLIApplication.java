package com.vsthost.rnd.moear;

import com.vsthost.rnd.jribinding.RBinding;
import com.vsthost.rnd.jribinding.RBindingBootstrapException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.OptionalInt;

/**
 * Provides the CLI application for the CLIApplication library.
 */
public class CLIApplication {
    /**
     * Defines the CLIApplication library version.
     */
    public static final String VERSION = "0.0.2-SNAPSHOT";

    /**
     * Prints an error message and exits.
     *
     * @param message The error message to be shown.
     */
    private static void exitOnError(String message) {
        System.err.println(message);
        System.err.println("Exiting...");
        System.exit(-1);
    }

    /**
     * Prints a message to stdout if we are not silent.
     *
     * @param arguments CLI arguments.
     * @param message The message to be printed.
     */
    private static void printMessage (CLIArguments arguments, String message) {
        // Are we silent?
        if (arguments.isSilent()) {
            return;
        }

        // Print the message:
        System.out.println(message);
    }

    /**
     * Prints a message to stdout if we are not silent.
     *
     * @param arguments CLI arguments.
     * @param messages Messages to be printed.
     */
    private static void printMessages (CLIArguments arguments, LinkedHashMap<String, String> messages) {
        // Are we silent?
        if (arguments.isSilent()) {
            return;
        }

        // Get the longest key length:
        final OptionalInt length = messages.keySet().stream().mapToInt(String::length).max();

        // If we don't have length, return:
        if (!length.isPresent()) {
            return;
        }

        // Get the format string with padding:
        final String lineFormat = String.format("%%-%ds : %%s", length.getAsInt());

        // Iterate over messages and print:
        messages.forEach((key, value) -> {
            System.out.println(String.format(lineFormat, key, value));
        });
    }

    /**
     * Provides the main loop of the CLI application.
     *
     * @param args Command line arguments.
     * @throws RBindingBootstrapException Indicates that R is not able to bootstrap.
     * @throws CmdLineException Indicates that command line argument parsing has failed.
     * @throws IOException Indicates an IO problem.
     */
    public static void main (String[] args) throws RBindingBootstrapException, CmdLineException, IOException {
        // Parse arguments:
        final CLIArguments cliArguments = new CLIArguments();

        // Parse arguments:
        new CmdLineParser(cliArguments).parseArgument(args);

        // Open the R binding:
        if (cliArguments.isSilent()) {
            RBinding.getInstance();
        }
        else {
            RBinding.getInstance(true);
        }

        // Print the preamble:
        CLIApplication.printMessage(cliArguments, String.format("MOEAR v%s using %s", CLIApplication.VERSION, RBinding.getInstance().getRVersion()));

        // Does the problem file exist?
        if (!cliArguments.getProblemFile().exists()) {
            CLIApplication.exitOnError(String.format("Problem file does not exist: %s", cliArguments.getProblemFile().getAbsolutePath()));
        }

        // Does the configuration file exist?
        if (!cliArguments.getConfigurationFile().exists()) {
            CLIApplication.exitOnError(String.format("Configuration file does not exist: %s", cliArguments.getConfigurationFile().getAbsolutePath()));
        }

        // Attempt to get the configuration:
        final Configuration configuration = cliArguments.getConfiguration();

        // Continue printing preamble:
        CLIApplication.printMessages(cliArguments, new LinkedHashMap<String, String>() {
            {
                put("Name", configuration.getName());
                put("Description", configuration.getDescription());
                put("Version", configuration.getVersion());
                put("Author", configuration.getAuthor());
                put("Problem", cliArguments.getProblemFile().getAbsolutePath());
                put("Configuration", cliArguments.getConfigurationFile().getAbsolutePath());
                put("Algorithm", configuration.getAlgorithm());
                put("Generations", String.valueOf(configuration.getGenerations()));
            }
        });

        // Define an output formatter:
        ConsoleOutputFormatter formatter = new ConsoleOutputFormatter();

        // Execute the algorithm and get the result:
        NondominatedPopulation result = new Executor()
            .withProblemClass(RScriptedProblem.class, cliArguments.getProblemFile().getAbsolutePath(), formatter)
            .withAlgorithm(configuration.getAlgorithm())
            .withMaxEvaluations(configuration.getGenerations())
            .run();

        // Display the results:
        System.out.println("\n=== Solutions ===\n");
        for (Solution solution : result) {
            formatter.toConsole(solution);
        }

        // Close the R binding:
        RBinding.closeREngine();
    }
}

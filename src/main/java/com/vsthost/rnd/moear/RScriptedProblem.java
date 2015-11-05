package com.vsthost.rnd.moear;

import com.vsthost.rnd.jribinding.RBinding;
import com.vsthost.rnd.jribinding.RBindingBootstrapException;
import com.vsthost.rnd.jribinding.RBindingParseAndEvalException;
import com.vsthost.rnd.jribinding.RBindingScriptLoadException;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.rosuda.REngine.*;

/**
 * Defines a scripted problem which prepares an evaluation environment and evaluates candidates in R.
 */
public class RScriptedProblem implements Problem {
    /**
     * Defines the RBinding instance.
     */
    final private RBinding rbinding;

    /**
     * Defines the specification coming from within the R script.
     */
    final private REXP specification;

    /**
     * Defines the name of the problem.
     */
    final private String name;

    /**
     * Defines the number of variables.
     */
    final private int variableCount;

    /**
     * Defines the number of objectives.
     */
    final private int objectiveCount;

    /**
     * Defines the number of constraints.
     */
    final private int constraintsCount;

    /**
     * Defines a reference to the R function which evaluates candidate solutions.
     */
    final private String evaluator;

    /**
     * Defines the name of the R function which generates new solutions.
     */
    final private String generator;

    /**
     * Defines the name of the R function which formats solutions for console output.
     */
    final private String formatter;

    /**
     * Initializes the problem.
     *
     * @param script The script to initialize the problem.
     * @param formatter The formatter object.
     * @throws RBindingBootstrapException Indicates that bootstrapping R has failed.
     * @throws RBindingScriptLoadException Indicates that loading the R script has failed.
     * @throws RBindingParseAndEvalException Indicates that parsing and evaluating the R expression has failed.
     * @throws REXPMismatchException Indicates that R eval return does not match what we are hopelessly waiting to match to.
     */
    public RScriptedProblem (String script, ConsoleOutputFormatter formatter) throws RBindingBootstrapException, RBindingScriptLoadException, RBindingParseAndEvalException, REXPMismatchException {
        // Get the R binding instance:
        this.rbinding = RBinding.getInstance();

        // Run the script:
        this.rbinding.loadScript(script);

        // Get the object:
        this.specification = this.rbinding.evalExpr("MOEAR");

        // Get the name:
        this.name = this.specification.asList().at("name").asString();

        // Get the number of variables:
        this.variableCount = this.specification.asList().at("variables").asInteger();

        // Get the number of objectives:
        this.objectiveCount = this.specification.asList().at("objectives").asInteger();

        // Get the number of constraints:
        this.constraintsCount = this.specification.asList().at("constraints").asInteger();

        // Get the evaluator:
        this.evaluator = this.specification.asList().at("evaluator").asString();

        // Get the generator:
        this.generator = this.specification.asList().at("generator").asString();

        // Get the formatter:
        this.formatter = this.specification.asList().at("formatter").asString();

        // Set the formatter:
        formatter.setFormatter(this.formatter);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getNumberOfVariables() {
        return this.variableCount;
    }

    @Override
    public int getNumberOfObjectives() {
        return this.objectiveCount;
    }

    @Override
    public int getNumberOfConstraints() {
        return this.constraintsCount;
    }

    @Override
    public void evaluate(Solution solution) {
        try {
            final REXP evaluatorCall = REXP.asCall(this.evaluator, new REXPJavaReference(solution));
            this.rbinding.getREngine().eval(evaluatorCall, null, true);
        }
        catch (REngineException e) {
            e.printStackTrace();
        }
        catch (REXPMismatchException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Solution newSolution() {
        try {
            return (Solution) this.rbinding.evalExpr(String.format("%s()", this.generator)).asNativeJavaObject();
        }
        catch (REXPMismatchException e) {
            e.printStackTrace();
        }
        catch (RBindingParseAndEvalException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
        // Nothing to be done...
    }
}

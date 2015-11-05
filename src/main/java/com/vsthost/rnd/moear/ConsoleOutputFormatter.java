package com.vsthost.rnd.moear;

import com.vsthost.rnd.jribinding.RBinding;
import com.vsthost.rnd.jribinding.RBindingBootstrapException;
import org.moeaframework.core.Solution;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPJavaReference;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

/**
 * Defines a console output formatter.
 */
public class ConsoleOutputFormatter {
    /**
     * Defines the name of the R function which is going to format the solution.
     */
    private String formatter;

    /**
     * Sets the formatter.
     *
     * @param formatter The formatter to be set.
     */
    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    /**
     * Consumes a solution and prints onto the console.
     *
     * @param solution The solution to be printed out.
     */
    public void toConsole (Solution solution) {
        try {
            final REXP evaluatorCall = REXP.asCall(this.formatter, new REXPJavaReference(solution));
            RBinding.getInstance().getREngine().eval(evaluatorCall, null, true);
        }
        catch (REngineException e) {
            e.printStackTrace();
        }
        catch (REXPMismatchException e) {
            e.printStackTrace();
        }
        catch (RBindingBootstrapException e) {
            e.printStackTrace();
        }
    }
}

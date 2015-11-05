## Load the rJava:
library(rJava)

## Initialize the JVM. We are assuming that the current JVM is set with proper classpaths:
.jinit()

## Defines a problem specification to be passed to Java.
defineProblem <- function (name, variables, objectives, constraints, evaluator, generator, formatter) {
	list(name=name,
    	 variables=variables,
    	 objectives=objectives,
    	 constraints=constraints,
    	 evaluator=evaluator,
    	 generator=generator,
    	 formatter=formatter)
}

## Defines the objective function.
objectiveFunction <- function (x) {
	abs(x^2 + 2*x + 1)
}

## Defines an evaluator.
evaluator <- function (solution) {
	## Get the value:
	x <- .jcall("org/moeaframework/core/variable/EncodingUtils", "[D", "getReal", solution)
	.jcall(solution, "V", "setObjective", 0L, objectiveFunction(x))
}

## Defines a solution generator.
generator <- function () {
	## Initialize the solution:
	solution = .jnew("org/moeaframework/core/Solution", 1L, 1L);

	## Set variable:
	.jcall(solution, "V", "setVariable", 0L, .jcast(.jnew("org/moeaframework/core/variable/RealVariable", -2.0, 2.0), "org/moeaframework/core/Variable"))

	## Done, return the solution:
	solution
}

## Defines a solution formatter.
formatter <- function (solution) {
	## Get the objective score:
	objective <- .jcall(solution, "D", "getObjective", 0L)

	## Get the candidate solution representation:
	variable <- .jcall(.jcast(.jcall(solution, "Lorg/moeaframework/core/Variable;", "getVariable", 0L), "org/moeaframework/core/variable/RealVariable"), "D", "getValue")

	## Printout:
	cat(paste("[", objective, "] ", variable, "\n", sep=""))
}

## Defines the problem specification.
MOEAR <- defineProblem("Sample Problem Definition", 1, 1, 0, "evaluator", "generator", "formatter")


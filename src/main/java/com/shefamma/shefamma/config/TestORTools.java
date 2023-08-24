package com.shefamma.shefamma.config;

import com.google.ortools.constraintsolver.*;

public class TestORTools {

    static {
        System.loadLibrary("jniortools");
    }

//    public static void main(String[] args) {
//        // Create the routing index manager.
//        int numLocations = 4;
//        int numVehicles = 2;
//        int depot = 0; // Assuming a central depot
//        RoutingIndexManager manager = new RoutingIndexManager(numLocations, numVehicles, depot);
//
//        // Create Routing Model.
//        RoutingModel routing = new RoutingModel(manager);
//
//        int[][] distanceMatrix = {
//                {0, 2, 9, 10},
//                {1, 0, 6, 4},
//                {15, 7, 0, 8},
//                {6, 3, 12, 0}
//        };
//
//        int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
//            // Returns the distance between the two nodes.
//            return distanceMatrix[(int) manager.indexToNode(fromIndex)][(int) manager.indexToNode(toIndex)];
//        });
//
//        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);
//
//        // Define search parameters.
//        RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
//                .toBuilder()
//                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
//                .build();
//
//        // Solve the problem.
//        Assignment solution = routing.solveWithParameters(searchParameters);
//
//        // Print solution on console.
//        printSolution(routing, manager, solution);
//    }

    public static void printSolution(RoutingModel routing, RoutingIndexManager manager, Assignment solution) {
        // Solution cost
        System.out.println("Objective: " + solution.objectiveValue());
        // Inspect solution
        for (int i = 0; i < routing.vehicles(); ++i) {
            long index = routing.start(i);
            System.out.println("Route for Vehicle " + i + ":");
            long routeDist = 0; // Corrected here, renamed the variable
            while (!routing.isEnd(index)) {
                System.out.print(manager.indexToNode(index) + " -> ");
                long previousIndex = index;
                index = solution.value(routing.nextVar(index));
                routeDist += routing.getArcCostForVehicle(previousIndex, index, i); // Used the renamed variable here
            }
            System.out.println(manager.indexToNode(index));
            System.out.println("Distance of the route: " + routeDist + "m"); // And here
        }
    }

}
//-Djava.library.path=C:\Users\Sweta\.m2\repository\com\google\ortools\ortools-win32-x86-64\9.5.2237\ortools-win32-x86-64



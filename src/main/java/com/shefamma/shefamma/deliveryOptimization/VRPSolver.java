package com.shefamma.shefamma.deliveryOptimization;
import com.google.ortools.constraintsolver.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntToLongFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;

public class VRPSolver {
    static {
        System.loadLibrary("jniortools");
    }

    private final int[][] distanceMatrix;

    public VRPSolver(int[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public List<List<Integer>> solve(int numberOfDevBoys, int[] demands) {
        RoutingIndexManager manager = new RoutingIndexManager(distanceMatrix.length, numberOfDevBoys, 0);
        RoutingModel routing = new RoutingModel(manager);

        // Define the distance callback
        LongBinaryOperator distanceCallback = (a, b) -> distanceMatrix[manager.indexToNode((int) a)][manager.indexToNode((int) b)];
        routing.setArcCostEvaluatorOfAllVehicles(routing.registerTransitCallback(distanceCallback));

        final long vehicleCapacity = 10L;  // Assuming a devBoy can carry at most 10 orders
        long[] capacitiesArray = new long[numberOfDevBoys];  // Assuming numDevBoys is the number of your DevBoys
        Arrays.fill(capacitiesArray, vehicleCapacity);

        LongUnaryOperator demandCallback = index -> demands[manager.indexToNode((int) index)];
        routing.addDimensionWithVehicleCapacity(routing.registerUnaryTransitCallback(demandCallback), 0L, capacitiesArray, true, "Capacity");

        // Solve
        RoutingSearchParameters searchParameters = RoutingSearchParameters.newBuilder()
                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                .build();

        Assignment solution = routing.solveWithParameters(searchParameters);
        return extractSolution(solution, routing, manager);
    }

    private List<List<Integer>> extractSolution(Assignment solution, RoutingModel routing, RoutingIndexManager manager) {
        List<List<Integer>> routes = new ArrayList<>();
        for (int vehicleId = 0; vehicleId < routing.vehicles(); vehicleId++) {
            List<Integer> route = new ArrayList<>();
            long index = routing.start(vehicleId);
            while (!routing.isEnd(index)) {
                route.add(manager.indexToNode(index));
                index = solution.value(routing.nextVar(index));
            }
            routes.add(route);
        }
        return routes;
    }
}


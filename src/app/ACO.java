package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jfree.ui.RefineryUtilities;

import models.Ant;
import models.Model;
import utils.IOManager;
import utils.Scheduler;
import utils.Visualizer;

public class ACO {
    public static void main(String[] args) throws Exception {
        IOManager manager = new IOManager();
        Model model = new Model();
        manager.parseFile("test_data/4.txt", model);
        model.generateJobObjects();
        Scheduler scheduler = new Scheduler();

        // variables
        int maxIterations = 2000;
        int antAmount = 40;
        double alpha = 0.01;
        double beta = 0.01;
        double pheromoneInitialValue = 0.5;
        double evaporation = 0.01;
        boolean earlyStopping = true;
        //int threshold = 62; //1.txt 56  62.72
        int threshold = 1186; //2.txt 1059  1186.08
        //int threshold = 1276; //3.txt 1276 1429.12
        int printEveryIteration = 100;

        model.setAlpha(alpha);
        model.setbeta(beta);
        model.generatePheromoneMatix(pheromoneInitialValue);

        int iterationCount = 0;
        int globalShortestLength = Integer.MAX_VALUE;
        List<Integer> globalBestSolution = null;

        int localShortestLength = Integer.MAX_VALUE;
        List<Integer> localBestSolution = null;

        int antCurrentMakespan;

        List<Ant> ants = new ArrayList<Ant>();

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        while (iterationCount < maxIterations) {

            localShortestLength = Integer.MAX_VALUE;
            localBestSolution = null;
            // initialize ants
            ants = model.generateAnts(antAmount);

            // every ant finds a route
            for (Ant ant : ants) {
                ant.findRoute();
                antCurrentMakespan = scheduler.calculateMakespan(ant.scheduledOperations);
                if (antCurrentMakespan < globalShortestLength) {
                    globalShortestLength = antCurrentMakespan;
                    globalBestSolution = ant.scheduledOperations;
                    //System.out.println("Iteration " + iterationCount + " new global found = " + antCurrentMakespan);
                }
                if (antCurrentMakespan <= globalShortestLength) {
                    globalShortestLength = antCurrentMakespan;
                    globalBestSolution = ant.scheduledOperations;
                    System.out.println("Iteration " + iterationCount + " new global found = " + antCurrentMakespan);
                }
                if (antCurrentMakespan < localShortestLength) {
                    localShortestLength = antCurrentMakespan;
                    localBestSolution = ant.scheduledOperations;
                }
            }

            //Early stopping

            if(globalShortestLength<=threshold && earlyStopping){
                System.out.println("Stoppped early at iteration " + iterationCount + " with makespan = " + globalShortestLength);
                break;
            }



            updatePheromoneMatrix(localBestSolution, localShortestLength, globalShortestLength, model, evaporation);

            iterationCount++;

            if(iterationCount%printEveryIteration==0){
                System.out.println("Iteration: " + iterationCount + "/" + maxIterations);
            }

        }
        //globalBestSolution = Arrays.asList(1, 2, 0, 2, 0, 3, 1, 2, 3, 5, 4, 1, 0, 5, 2, 5, 4, 3, 4, 2, 3, 1, 5, 0, 3, 1, 2, 0, 4, 5, 3, 4, 1, 5, 0, 4);
        //globalShortestLength = scheduler.calculateMakespan(globalBestSolution);
        System.out.println(globalShortestLength);
        System.out.println(globalBestSolution);
        //System.out.println(Arrays.deepToString(model.getPheromoneMatix()));
        Visualizer visualizer = new Visualizer("ACO Gantt", globalBestSolution, globalShortestLength, model.getJobs());
        visualizer.pack();
        RefineryUtilities.centerFrameOnScreen(visualizer);
        visualizer.setVisible(true);
    }

    public static void updatePheromoneMatrix(List<Integer> bestSolution, int makespan, int globalMakespan,
            Model model, double p) {
        double[][] pheromoneMatrix = model.getPheromoneMatix();
        for (int i = 0; i < pheromoneMatrix.length; i++) {
            for (int j = 0; j < pheromoneMatrix[0].length; j++) {
                pheromoneMatrix[i][j] = (1-p)*pheromoneMatrix[i][j];
            }

        }
        // We always start in the zero-node
        int currentIndex = 0;
        int nextIndex;

        List<Integer> indices = new ArrayList<Integer>();
        for(int i=0; i<Model.technicalMatrix.length; i++){
            indices.add(-1);
        }

        for (int currentJob: bestSolution){
           indices.set(currentJob, indices.get(currentJob)+1);
           nextIndex = Ant.getIndexOnRowCol(currentJob, indices.get(currentJob));
           pheromoneMatrix[currentIndex][nextIndex] = pheromoneMatrix[currentIndex][nextIndex] + 1/(double) makespan;
           currentIndex = nextIndex;
        }
    }
}
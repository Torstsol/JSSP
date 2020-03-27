package models;

import java.util.ArrayList;
import java.util.List;

public class Model {

        int jobs;
        int machines;

        public static int[][] technicalMatrix;
        public static int[][] processingMatrix;

        double[][] pheromoneMatrix;
        double alpha;
        double beta;

        List<Job> jobList;

        public void setJobs(int jobs) {
                this.jobs = jobs;
        }

        public void setMachines(int machines) {
                this.machines = machines;
        }

        public void setTechnicalMatrix(int[][] technicalMatrix) {
                Model.technicalMatrix = technicalMatrix;
        }

        public void setProcessingMatrix(int[][] processingMatrix) {
                Model.processingMatrix = processingMatrix;
        }

        public void generateJobObjects() {
                Job.processingMatrix = Model.processingMatrix;
                Job.technicalMatrix = Model.technicalMatrix;
                List<Job> jobList = new ArrayList<Job>();
                for (int i = 0; i < jobs; i++) {
                        jobList.add(new Job(i));
                }
                this.jobList = jobList;
        }

        public List<Job> copyJobList() {
                List<Job> copy = new ArrayList<Job>();

                for (Job job : this.jobList) {
                        copy.add(job.copySelf());
                }

                return copy;
        }

        public List<Ant> generateAnts(int amount) {
                List<Ant> ants = new ArrayList<Ant>();
                for (int i = 0; i < amount; i++) {
                        ants.add(new Ant(pheromoneMatrix, copyJobList(), alpha, beta));
                }
                return ants;
        }

        public void setAlpha(double alpha){
                this.alpha = alpha;
        }

        public void setbeta(double beta){
                this.beta = beta;
        }

        public double getAlpha(){
                return this.alpha;
        }

        public double getbeta(){
                return this.beta;
        }

        public void generatePheromoneMatix(){
                this.pheromoneMatrix = new double[jobs*machines+1][jobs*machines+1];
                for (int i=0; i<this.pheromoneMatrix.length; i++){
                        for(int j=0; j<this.pheromoneMatrix[0].length; j++){
                                this.pheromoneMatrix[i][j] = 420.69;
                        }
                }
        }

}
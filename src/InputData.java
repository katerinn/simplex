import java.util.Random;

public class InputData {

    int machineWorkingTime[];
    int numberOfDetailsByType[];
    int processingCost[][];
    int processingTime[][];
    int preparationTime[][];
    int totalTime[][];

    Random random = new Random();

    public void initInputData (int m, int n) {
        machineWorkingTime = new int[m];
        numberOfDetailsByType = new int[n];
        processingCost = new  int [m][n];
        processingTime = new  int [m][n];
        preparationTime = new  int [m][n];
        totalTime = new int [m][n];
    }

    public void setMachineWorkingTime(int workingTime[]) {
        for (int i = 0; i<this.machineWorkingTime.length; i++) {
           this.machineWorkingTime[i] = workingTime[i];
        }
    }

    public void setNumberOfDetailsByType(int details[]) {
        for (int i = 0; i<this.numberOfDetailsByType.length; i++) {
            this.numberOfDetailsByType[i] = details[i];
        }
    }

    public void setProcessingCost() {
        for (int i = 0; i<this.processingCost.length - 1; i++) {
            for (int j =0; j<this.processingCost[i].length; j++) {
                this.processingCost[i][j] = random.nextInt(4) + 6; // values from 3 to 10
            }
        }
        for (int j =0; j<this.processingCost[0].length; j++) {
            this.processingCost[processingCost.length - 1][j] = random.nextInt(9) + 6; // values from 8 to 15
        }
    }

    public int [][] getProcessingCost() {
        return this.processingCost;
    }

    public void setProcessingTime() {
        for (int i = 0; i<this.processingTime.length; i++) {
            for (int j =0; j<this.processingTime[i].length; j++) {
                this.processingTime[i][j] = random.nextInt(3) + 2; // values from 2 to 5
            }
        }
    }

    public int [][] getProcessingTime() {
        return this.processingTime;
    }

    public void setPreparationTime() {
        for (int i = 0; i<this.preparationTime.length; i++) {
            for (int j =0; j<this.preparationTime[i].length; j++) {
                this.preparationTime[i][j] = random.nextInt(2) + 1; // values from 1 to 3
            }
        }
    }

    public void setTotalTime() {
        for (int i = 0; i<this.totalTime.length; i++) {
            for (int j =0; j<this.totalTime[i].length; j++) {
                this.totalTime[i][j] = processingTime[i][j] + preparationTime[i][j];
            }
        }
    }

    public int [][] getPreparationTime() {
        return this.preparationTime;
    }
}

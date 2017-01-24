import java.util.ArrayList;

public class Main {

    int simplexTableRows;
    int simplexTableColumns;
    int indexOfVariablesCorrespondingRows[];
    int indexOfVariablesCorrespondingColumns[];
    int amputationCallCounter = 0;
    boolean isNewAmputation = false;
    static ArrayList<Table> tables = new ArrayList<>();

    public void main(InputData inputData, OutputData outputData, int m, int n) {
        //int m = 7;
        //int n = 8;
        //InputData inputData = new InputData();
        int numberOfVariables = m * n + m; // m additional variables
        int objectiveFunctionCoefficients[] = new int[numberOfVariables];
        int amountOfDetailsConditionCoefficients[][] = new int[n][numberOfVariables];
        int timeConditionCoefficients[][] = new int[m][numberOfVariables];

        setPrimordialCoefficients(m, n, objectiveFunctionCoefficients, amountOfDetailsConditionCoefficients,
                timeConditionCoefficients, inputData);

        if (!isTaskFeasible(inputData, m, n)) {
            System.out.println("There is no solution");
            System.exit(0);
        }

        /***************************************************************************************
         *                                                                                     *
         *                                    CANONICAL FORM                                   *
         ***************************************************************************************/
        /*********
         *   UP  *
         *********/
        /*  basic variables and target function in rows, free coefficients and free variables in columns
         * first  (m * n - n) variable is free, other m + n is basic
         *    bi x...
         * x1
         * .
         * .
         * L          */
        simplexTableRows = m + n + 1;
        simplexTableColumns = m * n - n + 1; // +1 means free coefficients in first table column (bi)
        // variables holding names of rows and columns, 0 means L (target function), -65535 means b
        indexOfVariablesCorrespondingRows = new int[simplexTableRows];
        indexOfVariablesCorrespondingColumns = new int[simplexTableColumns];

        /* m+n+1 rows (target function + m time conditions + n details conditions) */
        TableCell simplexTable[][] = new TableCell[simplexTableRows][simplexTableColumns];
        for (int i = 0; i < simplexTable.length; i++) {
            for (int j = 0; j < simplexTable[i].length; j++) {
                simplexTable[i][j] = new TableCell();
            }
        }

        for (int i = 0; i < m + n; i++) {
            indexOfVariablesCorrespondingRows[i] = i + (m * n - n) + 1;
        }
        indexOfVariablesCorrespondingRows[m + n] = 0; // means L
        indexOfVariablesCorrespondingColumns[0] = -65535; //means b
        for (int j = 1; j < m * n - n + 1; j++) {
            indexOfVariablesCorrespondingColumns[j] = j;
        }

        /** first column **/
        // 1. free coefficients in conditions b
        for (int i = 0; i < n; i++) {
            simplexTable[i][0].up = inputData.numberOfDetailsByType[i];
        }
        // 2. free coefficients in conditions A
        for (int i = 0; i < m - 1; i++) {
            simplexTable[i + n][0].up = inputData.machineWorkingTime[i];
        }
        simplexTable[m + n - 1][0].up = inputData.machineWorkingTime[m - 1];
        for (int i = 0; i < n; i++) {
            simplexTable[m + n - 1][0].up -= inputData.totalTime[m - 1][i] * inputData.numberOfDetailsByType[i];
        }
        // 3. free coefficient in target function (gamma 0)
        simplexTable[simplexTableRows - 1][0].up = 0;

        for (int i = 0; i < n; i++) {
            simplexTable[simplexTableRows - 1][0].up += objectiveFunctionCoefficients[m * n - n + i] * inputData.numberOfDetailsByType[i];
        }

        /** other cells **/
        // 1. coefficients in conditions b
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < simplexTableColumns; j++) {
                simplexTable[i][j].up = amountOfDetailsConditionCoefficients[i][j - 1]; // j-1 because in this array index start with 0
            }
        }
        // 2. coefficients in conditions A
        for (int i = 0; i < m; i++) {
            for (int j = 1; j < simplexTableColumns; j++) {
                if (i == m - 1) {
                   // simplexTable[i + n][j].up = (-1) * timeConditionCoefficients[i][j - 1 + i * n];
                    simplexTable[i + n][j].up = (-1) * inputData.totalTime[m - 1][(j - 1) % n];
                } else {
                    //simplexTable[i + n][j].up = timeConditionCoefficients[i][j - 1 + i * n];
                    simplexTable[i + n][j].up = inputData.totalTime[m - 1][(j - 1) % n];
                }
            }
        }

        // 3. coefficient in target function (gamma)
        for (int j = 0; j < simplexTableColumns - 1; j++) {
            // additional variable
            simplexTable[simplexTableRows - 1][j + 1].up = objectiveFunctionCoefficients[m * n - n + j % n] - objectiveFunctionCoefficients[j];
        }

        if (isItRequiresToUseVmethod(simplexTable)) {
            System.out.println("There is negative b, it's necessary to implement V method");
            System.exit(0);
        }

        // recalculation
        while (!isExitConditionperformed(simplexTable)) {
            simplexTable = tableRecalculation(simplexTable);
        }
        while (!isfTableValuesIsWholeNumbers(simplexTable)) {
            simplexTable = addAmputationRow(simplexTable);
            simplexTable = tableRecalculation(simplexTable);
            while (!isExitConditionperformed(simplexTable)) {
                simplexTable = tableRecalculation(simplexTable);
            }
        }

        if (simplexTable == null) {
            System.out.println("Optimal solution doesn't exist");
            System.exit(0);
        } else {
            for (int i = 0; i < simplexTableRows; i++) {
                System.out.println();
                for (int j = 0; j < simplexTableColumns; j++) {
                    System.out.print(simplexTable[i][j].up + " ");
                }
            }
            System.out.println("\n\n");
            for (int i = 0; i < simplexTableRows; i++) {
                System.out.println(indexOfVariablesCorrespondingRows[i] + " ");
            }
            System.out.println("\n\n");
            for (int i = 0; i < simplexTableColumns; i++) {
                System.out.println(indexOfVariablesCorrespondingColumns[i] + " ");
            }

            outputData.setSolution(formSolutionToUser(m, n, simplexTable));
        }

    }

    public void setPrimordialCoefficients(int m, int n, int objectiveFunctionCoefficients[], int amountOfDetailsConditionCoefficients[][], int timeConditionCoefficients[][], InputData inputData) {
        /* initialize array of objective function coefficients, m*n + m */
        for (int i = 0; i < objectiveFunctionCoefficients.length; i++) {
            objectiveFunctionCoefficients[i] = 0;
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                objectiveFunctionCoefficients[i * n + j] = inputData.processingCost[i][j];
            }
        }
        /* conditions b = , length m*n */
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m * n + m; j++) {
                if (((i == j) || (j - i) % n == 0) && j < n * m) {
                    amountOfDetailsConditionCoefficients[i][j] = 1;
                } else {
                    amountOfDetailsConditionCoefficients[i][j] = 0;
                }
            }
        }
        /* conditions A = , length m*n */
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                timeConditionCoefficients[i][i * n + j] = inputData.totalTime[i][j];
            }
            timeConditionCoefficients[i][m * n + i] = 1;
        }
    }

    TableCell[][] tableRecalculation(TableCell[][] simplexTable) {
//        if (isExitConditionperformed(simplexTable)) {
//            return simplexTable;
//        }
        tables.add(new Table(simplexTable, indexOfVariablesCorrespondingRows, indexOfVariablesCorrespondingColumns));
        // permissive cell
        int permissiveColumn;
        int permissiveRow;
        if (isDualMethod(simplexTable) || isNewAmputation) {
            if (isNewAmputation) {
                permissiveRow = simplexTableRows - 1;
                permissiveColumn = choosePermissiveColumnDualMethod(simplexTable, permissiveRow);
                isNewAmputation = false;
            }
            else {
                permissiveRow = choosePermissiveRowDualMethod(simplexTable);
                permissiveColumn = choosePermissiveColumnDualMethod(simplexTable, permissiveRow);
            }
        }
        else {
            permissiveColumn = choosePermissiveColumn(simplexTable);
            permissiveRow = choosePermissiveRow(simplexTable, permissiveColumn);
            if (permissiveRow == -111111) {
                // issue has not optimal solutions
                return null;
            }
        }
        double lambda = 1 / simplexTable[permissiveRow][permissiveColumn].up;
        simplexTable[permissiveRow][permissiveColumn].down = lambda;

        for (int j = 0; j < simplexTableColumns; j++) {
            if (j != permissiveColumn) {
                simplexTable[permissiveRow][j].down = simplexTable[permissiveRow][j].up * lambda;
            }
        }

        for (int i = 0; i < simplexTableRows; i++) {
            if (i != permissiveRow) {
                simplexTable[i][permissiveColumn].down = simplexTable[i][permissiveColumn].up * (-1) * lambda;
            }
        }

        for (int i = 0; i < simplexTableRows; i++) {
            if (i != permissiveRow) {
                for (int j = 0; j < simplexTableColumns; j++) {
                    if (j != permissiveColumn) {
                        simplexTable[i][j].down = simplexTable[permissiveRow][j].up * simplexTable[i][permissiveColumn].down;
                    }

                }
            }
        }
        // now table is full

        swapRowAndColumn(permissiveRow, permissiveColumn);

        for (int i = 0; i < simplexTableRows; i++) {
            for (int j = 0; j < simplexTableColumns; j++) {
                if (i == permissiveRow || j == permissiveColumn) {
                    simplexTable[i][j].up = simplexTable[i][j].down;
                } else {
                    simplexTable[i][j].up = simplexTable[i][j].up + simplexTable[i][j].down;
                }
                simplexTable[i][j].down = 0;
            }
        }
        // recursive call recalculation
        return simplexTable;
    }

    boolean isfTableValuesIsWholeNumbers(TableCell[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (Math.abs(table[i][j].up - Math.round(table[i][j].up)) >= 0.0001) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean isExitConditionperformed(TableCell[][] table) {
        int indexOfFunction = -111111;
        for (int i = 0; i < indexOfVariablesCorrespondingRows.length; i++) {
            if (indexOfVariablesCorrespondingRows[i] == 0) {
                indexOfFunction = i;
                break;
            }
        }
        for (int j = 1; j < table[0].length; j++) {
            if (table[indexOfFunction][j].up > 0) {
                return false;
            }
        }
        return true;
    }

    int choosePermissiveColumn(TableCell[][] table) {
        int permissiveColumn = -1;
        int functionRow = -1;
        for (int i = 0; i < indexOfVariablesCorrespondingRows.length; i++) {
            if (indexOfVariablesCorrespondingRows[i] == 0) {
                functionRow = i;
            }
        }
        for (int j = 1; j < simplexTableColumns; j++) {
            if (table[functionRow][j].up > 0) {
                permissiveColumn = j;
            }
        }
        return permissiveColumn;
    }

    int choosePermissiveRow(TableCell[][] table, int permissiveColumn) {
        int permissiveRow = -111111;
        double ratioOfBtoA = 65535;
        for (int i = 0; i < simplexTableRows; i++) {
            if (table[i][permissiveColumn].up > 0 && indexOfVariablesCorrespondingRows[i] != 0) {
                if (ratioOfBtoA > table[i][0].up / table[i][permissiveColumn].up) {
                    ratioOfBtoA = table[i][0].up / table[i][permissiveColumn].up;
                    permissiveRow = i;
                }
            }
        }
        return permissiveRow;
    }

    int choosePermissiveColumnDualMethod (TableCell [][] table, int permissiveRow) {
        int permissiveColumn = -1;
        double min = Double.MAX_VALUE;
        int indexOfFunctionRow = -1;
        for (int i = 0; i<indexOfVariablesCorrespondingRows.length; i++) {
            if (indexOfVariablesCorrespondingRows[i] == 0) {
                indexOfFunctionRow = i;
            }
        }
        for (int j = 1; j < simplexTableColumns; j ++) {
            if (table[simplexTableRows-1][j].up <= 0) {
                if (min > table[indexOfFunctionRow][j].up / table[permissiveRow][j].up && table[permissiveRow][j].up < 0) {
                    min =  table[indexOfFunctionRow][j].up / table[permissiveRow][j].up;
                    permissiveColumn = j;
                }
            }
        }
        return permissiveColumn;
    }

    int choosePermissiveRowDualMethod(TableCell[][] table) {
        int permissiveRow = -1;
        for (int i = 0; i < table.length; i++) {
            if (indexOfVariablesCorrespondingRows[i] != 0 && table[i][0].up < 0) {
                permissiveRow = i;
                break;
            }
        }
        return permissiveRow;
    }

    void swapRowAndColumn(int row, int column) {
        int t = indexOfVariablesCorrespondingRows[row];
        indexOfVariablesCorrespondingRows[row] = indexOfVariablesCorrespondingColumns[column];
        indexOfVariablesCorrespondingColumns[column] = t;
    }

    TableCell[][] addAmputationRow(TableCell[][] simplexTable) {
        amputationCallCounter++;
        if (amputationCallCounter > 15) {
            System.out.println("Fucking shit, it's impossible!");
            System.exit(0);
        }
        simplexTableRows++;
        TableCell[][] newSimplexTable = new TableCell[simplexTableRows][simplexTableColumns];
        for (int i = 0; i < newSimplexTable.length; i++) {
            for (int j = 0; j < newSimplexTable[i].length; j++) {
                newSimplexTable[i][j] = new TableCell();
            }
        }

        for (int i = 0; i < simplexTableRows - 1; i++) {
            for (int j = 0; j < simplexTableColumns; j++) {
                newSimplexTable[i][j] = simplexTable[i][j];
            }
        }
        TableCell [] newRow = formProperPruning(simplexTable);
        if (newRow != null) {
            for (int j = 0; j < simplexTableColumns; j++) {
                newSimplexTable[simplexTableRows - 1][j] = newRow[j];
            }
        }
        else {
            System.out.println("There is no solution: " + Thread.currentThread().getStackTrace()[1].getMethodName());
            System.exit(0);
        }

        int indexArray[] = new int[simplexTableRows];
        for (int i = 0; i < simplexTableRows - 1; i++) {
            indexArray[i] = indexOfVariablesCorrespondingRows[i];
        }
        indexArray[simplexTableRows - 1] = (-1) * amputationCallCounter;
        indexOfVariablesCorrespondingRows = indexArray;
        isNewAmputation = true;
        return newSimplexTable;
    }

    int findRowToAmputation(TableCell[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (Math.abs(table[i][j].up - Math.round(table[i][j].up)) > 0.0001 && indexOfVariablesCorrespondingRows[i] > 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    TableCell[] formProperPruning(TableCell[][] simplexTable) {
        TableCell newRow[] = new TableCell[simplexTable[0].length];
        for (int i = 0; i < newRow.length; i++) {
            newRow[i] = new TableCell();
        }
        int index = findRowToAmputation(simplexTable);
        if (index == -1) {
            return null;
        }
        for (int i = 0; i < newRow.length; i++) {
            newRow[i].up = (-1) * separateFractionalPartsFromDouble(simplexTable[index][i].up);
        }
        return newRow;
    }

    double separateFractionalPartsFromDouble(double number) {
        int decimal = (int) number;
        double fractional = number - decimal;
        if (number >= 0) {
            return fractional;
        } else {
            return fractional + 1;
        }
    }

    int[][] formSolutionToUser(int m, int n, TableCell[][] simplexTable) {
        int array[][] = new int[m][n];
        int ar[] = new int[m * n];
        for (int i = 0; i < indexOfVariablesCorrespondingRows.length; i++) {
            if (indexOfVariablesCorrespondingRows[i] <= m * n && indexOfVariablesCorrespondingRows[i] > 0) {
                ar[indexOfVariablesCorrespondingRows[i] - 1] = (int) simplexTable[i][0].up;
            }
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                array[i][j] = ar[i * n + j];
            }
        }
        return array;
    }

     private boolean isBnegative(TableCell[][] table) {
        for (int i = 0; i < table.length; i++) {
            if (indexOfVariablesCorrespondingRows[i] > 0 && table[i][0].up < 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isDualMethod(TableCell[][] table) {
        if (isBnegative(table) && isExitConditionperformed(table)) {
            return true;
        }
        else {
            return false;
        }
    }

    boolean isTaskFeasible(InputData inputData, int m, int n) {
        int totalMachinesWorkingTime = 0;
        for (int i = 0; i < m; i++) {
            totalMachinesWorkingTime+=inputData.machineWorkingTime[i];
        }
        int totalTimeOfProcessing = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                totalTimeOfProcessing+=inputData.totalTime[i][j];
            }
        }
        if (totalMachinesWorkingTime < totalTimeOfProcessing) {
           return false;
        }
        else {
            return true;
        }
    }

    boolean isItRequiresToUseVmethod(TableCell[][] table) {
        for (int i = 0; i < table.length; i++) {
            if (indexOfVariablesCorrespondingRows[i] != 0 && table[i][0].up < 0) {
                return true;
            }
        }
        return false;
    }
}

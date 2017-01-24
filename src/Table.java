public class Table {
    static TableCell[][] table;
    static int indexOfVariablesCorrespondingRows[];
    static int indexOfVariablesCorrespondingColumns[];

    public Table( TableCell[][] table, int rows[], int columns[]) {
        this.table = table;
        this.indexOfVariablesCorrespondingRows = rows;
        this.indexOfVariablesCorrespondingColumns = columns;
    }

    String [] castToReadableView(int array[]) {
        String stringArray[] = new String[array.length];
        for (int i = 0; i < array.length; i++){
            if (array[i] > 0){
                stringArray[i] = "x" + array[i];
            }
            if (array[i] == 0){
                stringArray[i] = "L";
            }
            if(array[i] == -65535){
                stringArray[i] = "b";
            }
            else {
                if (array[i] < 0){
                    stringArray[i] = "z" + (array[i] * (-1));
                }
            }
        }
            return stringArray;
    }
}

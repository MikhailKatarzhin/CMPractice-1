import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class SLAEalHandler {

    public static ArrayList<BigDecimal> computeXzg(SLAEal systemLinearAlgebraicEquation){
        long millis = System.nanoTime();
        try {
            if (systemLinearAlgebraicEquation == null) throw new NullPointerException();
            ArrayList<ArrayList<BigDecimal>> matrix = systemLinearAlgebraicEquation.getMatrix();
            ArrayList<BigDecimal> freeTerms         = systemLinearAlgebraicEquation.getFreeTerms();
            final int scale = 10;
            if(!toDiagonalMatrix(matrix, freeTerms, scale))
                return null;
            for (int i = 0; i < freeTerms.size(); i++){
                if (matrix.get(i).get(i).equals(new BigDecimal(0)) && !(matrix.get(i).get(i).equals(freeTerms.get(i))))
                    return null;
                else
                    freeTerms.set(i, freeTerms.get(i).divide(matrix.get(i).get(i), scale, RoundingMode.UP));
            }
            System.out.println("\n\tDiagonal matrix:\n");
            showSLAE(new SLAEal(matrix,freeTerms));
            System.out.println("Timer of compute: " + (1.0*(System.nanoTime() - millis)/1000000));
            return freeTerms;
        }catch (NullPointerException exception){
            System.out.println("SLAE must not be nullptr");
        }
        return null;
    }
    public static boolean toDiagonalMatrix(ArrayList<ArrayList<BigDecimal>> matrix, ArrayList<BigDecimal> freeTerms, int scale){
        for (int i = 0; i < freeTerms.size(); i++){
            if (matrix.get(i).get(i).equals(new BigDecimal(0)))
                for (int j = i + 1; j < freeTerms.size(); j++){
                    if (!matrix.get(j).get(i).equals(new BigDecimal(0))){
                        addrows(matrix, freeTerms, j, i, new BigDecimal(1));
                        break;
                    }
                    return false;
                }
            for (int j = 0; j < freeTerms.size(); j++){
                if (j != i && !matrix.get(j).get(i).equals(new BigDecimal(0)))
                    addrows(matrix, freeTerms, i, j, matrix.get(j).get(i)
                            .divide(matrix.get(i).get(i), scale, RoundingMode.UP).multiply(new BigDecimal(-1)));
            }
        }
        return true;
    }

    public static void addrows(ArrayList<ArrayList<BigDecimal>> matrix, ArrayList<BigDecimal> freeTerms, int rowFrom, int rowTo, BigDecimal multiplier) {
    /*
        if (matrix == null) {
            System.out.println("Canceled. Matrix is nullptr.");
            return;
        }
        if (freeTerms == null) {
            System.out.println("Canceled. FreeTerms is nullptr.");
            return;
        }
        if (matrix.length != freeTerms.length){
            System.out.println("Canceled. Matrix size != freeTerms size.");
            return;
        }
        if (rowFrom < 0 || rowFrom > freeTerms.length){
            System.out.println("Canceled. RowFrom is out of bounders.");
            return;
        }
        if (rowTo < 0 || rowTo > freeTerms.length){
            System.out.println("Canceled. RowTo is out of bounders.");
            return;
        }
    */
        for (int i = 0; i < matrix.size(); i++){
            BigDecimal bd = matrix.get(rowTo).get(i);
            BigDecimal db = matrix.get(rowFrom).get(i);
            db = db.multiply(multiplier);
            bd = bd.add(db);
            //matrix[rowTo][i] = matrix[rowTo][i].add(matrix[rowFrom][i].multiply(multiplier));
            matrix.get(rowTo).set(i, bd);
        }
        freeTerms.set(rowTo, freeTerms.get(rowTo).add(freeTerms.get(rowFrom).multiply(multiplier)));
    }

    public static void showSLAE(SLAEal systemLinearAlgebraicEquation) throws NullPointerException{
        if (systemLinearAlgebraicEquation == null) throw new NullPointerException();
        ArrayList<ArrayList<BigDecimal>> matrix = systemLinearAlgebraicEquation.getMatrix();
        ArrayList<BigDecimal> freeTerms         = systemLinearAlgebraicEquation.getFreeTerms();
        int size                                = matrix.size();
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                System.out.print("\t" + ((matrix.get(i).get(j).compareTo(new BigDecimal("0")) == -1) ? "" : "+") + matrix.get(i).get(j).setScale(4, RoundingMode.HALF_DOWN) + "_x" + (j + 1) + " ");
            }
            System.out.println("= " + ((freeTerms.get(i).compareTo(new BigDecimal("0")) == -1) ? "" : "+") + freeTerms.get(i).setScale(4, RoundingMode.HALF_DOWN));
        }
    }
}

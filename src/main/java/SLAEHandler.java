import java.math.BigDecimal;
import java.math.RoundingMode;
public class SLAEHandler {
    public static BigDecimal[] computeXg(SystemLinearAlgebraicEquation systemLinearAlgebraicEquation){
        BigDecimal[] xs;
        try {
            if (systemLinearAlgebraicEquation == null) throw new NullPointerException();
            BigDecimal[][]  matrix      = systemLinearAlgebraicEquation.getMatrix();
            BigDecimal[]    freeTerms   = systemLinearAlgebraicEquation.getFreeTerms();
            xs                          = new BigDecimal[freeTerms.length];
            ///Выстраиваем треугольную матрицу
            final int scale = 10;
            if(!toTriangularMatrix(matrix, freeTerms, scale))
                return null;
            ///Отображаем треугольную матрицу
            System.out.println("\n\tTriangular matrix:\n");
            showSLAE(new SystemLinearAlgebraicEquation(matrix,freeTerms));
            for (int i = xs.length - 1; i >= 0; i--){
                xs[i] = new BigDecimal(0);
                xs[i] = xs[i].add(freeTerms[i]);
                for (int j = i+1; j < xs.length; j++){
                    xs[i] = xs[i].subtract(matrix[i][j].multiply(xs[j]));
                }
                xs[i] = xs[i].divide(matrix[i][i], scale, RoundingMode.UP);
            }
        }catch (NullPointerException exception){
            System.out.println("SLAE must not be nullptr");
            xs = null;
        }
        return xs;
    }

    public static BigDecimal[] computeXzg(SystemLinearAlgebraicEquation systemLinearAlgebraicEquation){
        long millis = System.nanoTime();
        try {
            if (systemLinearAlgebraicEquation == null) throw new NullPointerException();
            BigDecimal[][]  matrix      = systemLinearAlgebraicEquation.getMatrix();
            BigDecimal[]    freeTerms   = systemLinearAlgebraicEquation.getFreeTerms();
            final int scale = 10;
            if(!toDiagonalMatrix(matrix, freeTerms, scale))
                return null;
            for (int i = 0; i < freeTerms.length; i++){
                if (matrix[i][i].equals(new BigDecimal(0)) && !(matrix[i][i].equals(freeTerms[i])))
                    return null;
                else
                    freeTerms[i] = freeTerms[i].divide(matrix[i][i], scale, RoundingMode.UP);
            }
            System.out.println("\n\tDiagonal matrix:\n");
            showSLAE(new SystemLinearAlgebraicEquation(matrix,freeTerms));
            System.out.println("Timer: " + (1.0*(System.nanoTime() - millis)/1000000));
            return freeTerms;
        }catch (NullPointerException exception){
            System.out.println("SLAE must not be nullptr");
        }
        return null;
    }

    public static boolean toTriangularMatrix(BigDecimal[][] matrix, BigDecimal[] freeTerms, int scale){
        for (int i = 0; i < freeTerms.length; i++){
            if (matrix[i][i].equals(new BigDecimal(0)))
                for (int j = i + 1; j < freeTerms.length; j++)
                    if (!matrix[j][i].equals(new BigDecimal(0))){
                        addrows(matrix, freeTerms, j, i, new BigDecimal(1));
                        break;
                    }
            if (matrix[i][i].equals(new BigDecimal(0))){
                return false;
            }
            for (int j = i+1; j < freeTerms.length; j++){
                if (matrix[j][i].equals(new BigDecimal(0)))
                    continue;
                addrows(matrix, freeTerms, i, j, matrix[j][i]
                        .divide(matrix[i][i], scale, RoundingMode.UP).multiply(new BigDecimal(-1)));
            }
        }
        return true;
    }

    public static boolean toDiagonalMatrix(BigDecimal[][] matrix, BigDecimal[] freeTerms, int scale){
        for (int i = 0; i < freeTerms.length; i++){
            if (matrix[i][i].equals(new BigDecimal(0)))
                for (int j = i + 1; j < freeTerms.length; j++){
                    if (!matrix[j][i].equals(new BigDecimal(0))){
                        addrows(matrix, freeTerms, j, i, new BigDecimal(1));
                        break;
                    }
                    return false;
                }
            for (int j = 0; j < freeTerms.length; j++){
                if (j != i && !matrix[j][i].equals(new BigDecimal(0)))
                    addrows(matrix, freeTerms, i, j, matrix[j][i]
                            .divide(matrix[i][i], scale, RoundingMode.UP).multiply(new BigDecimal(-1)));
            }
        }
        return true;
    }

    public static void addrows(BigDecimal[][] matrix, BigDecimal[] freeTerms, int rowFrom, int rowTo, BigDecimal multiplier) {
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
        for (int i = 0; i < matrix.length; i++){
            BigDecimal bd = matrix[rowTo][i];
            BigDecimal db = matrix[rowFrom][i];
            db = db.multiply(multiplier);
            bd = bd.add(db);
            //matrix[rowTo][i] = matrix[rowTo][i].add(matrix[rowFrom][i].multiply(multiplier));
            matrix[rowTo][i] = bd;
        }
        freeTerms[rowTo] = freeTerms[rowTo].add(freeTerms[rowFrom].multiply(multiplier));
    }

    public static void showSLAE(SystemLinearAlgebraicEquation systemLinearAlgebraicEquation) throws NullPointerException{
        if (systemLinearAlgebraicEquation == null) throw new NullPointerException();
        BigDecimal[][] matrix   = systemLinearAlgebraicEquation.getMatrix();
        BigDecimal[] freeTerms  = systemLinearAlgebraicEquation.getFreeTerms();
        int size                = matrix.length;
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                //System.out.print(matrix[i][j].setScale(4, RoundingMode.HALF_DOWN) + "x" + j + " ");
                System.out.print("\t" + ((matrix[i][j].compareTo(new BigDecimal("0")) == -1) ? "" : "+") + matrix[i][j].setScale(4, RoundingMode.HALF_DOWN) + "_x" + (j + 1) + " ");
            }
            System.out.println("= " + ((freeTerms[i].compareTo(new BigDecimal("0")) == -1) ? "" : "+") + freeTerms[i].setScale(4, RoundingMode.HALF_DOWN));
        }
    }
}

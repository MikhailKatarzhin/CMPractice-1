import java.math.BigDecimal;
import java.math.RoundingMode;

public class SLAEHandler {
    /**
     * Метод вычисляет неизвестные переменные поступившей СЛАУ и возвращает их в виде массива.
     * В случае отсутсвия единственного решения возвращает null
     * @param systemLinearAlgebraicEquation должен быть ненулевым, иначе бросится исключение.
     */
    public static BigDecimal[] computeX(SystemLinearAlgebraicEquation systemLinearAlgebraicEquation){

        BigDecimal[] xs;
        try {
            if (systemLinearAlgebraicEquation == null) throw new NullPointerException();
            BigDecimal[][]  matrix      = systemLinearAlgebraicEquation.getMatrix();
            BigDecimal[]    freeTerms   = systemLinearAlgebraicEquation.getFreeTerms();
            xs                          = new BigDecimal[freeTerms.length];

            ///Выстраиваем треугольную матрицу
            final int scale = 4;
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

    /**
     * Метод преобразовывающий расширенную матрицу в треугольную расширенную матрицу
     * @param matrix        матрица аргументов
     * @param freeTerms     столбец свободных членов
     * @param scale         число знаков после запятой
     * @return              в случае успеха возвращает true, в случае невозможности - false, не нагружен восстановлением
     */
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

    /**
     * Метод складывающий две строки в расширенной матрице, переданной в виде матрицы и столбца свободных членов
     * @param matrix        матрица аргументов расширенной матрицы не должна быть нулевой
     * @param freeTerms     столбце свободных членов расширенной матрицы не должен быть нулевым
     * @param rowFrom       номер строки, которую мы складываем
     * @param rowTo         номер строки, к которой мы складываем
     * @param multiplier    множитель складываемой строки
     */
    public static void addrows(BigDecimal[][] matrix, BigDecimal[] freeTerms, int rowFrom, int rowTo, BigDecimal multiplier) {
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

        for (int i = 0; i < matrix.length; i++){
            matrix[rowTo][i] = matrix[rowTo][i].add(matrix[rowFrom][i].multiply(multiplier));
        }
        freeTerms[rowTo] = freeTerms[rowTo].add(freeTerms[rowFrom].multiply(multiplier));
    }

    /**
     * Выводит на экрран польхователю СЛАУ на основе переданного СЛАУ
     * @param systemLinearAlgebraicEquation должен быть ненулевым, иначе бросится исключение
     */
    public static void showSLAE(SystemLinearAlgebraicEquation systemLinearAlgebraicEquation) throws NullPointerException{
        if (systemLinearAlgebraicEquation == null) throw new NullPointerException();

        BigDecimal[][] matrix   = systemLinearAlgebraicEquation.getMatrix();
        BigDecimal[] freeTerms  = systemLinearAlgebraicEquation.getFreeTerms();
        int size                = matrix.length;

        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                System.out.print(matrix[i][j] + "x" + j + " ");
            }
            System.out.println("= " + freeTerms[i]);
        }
    }
}

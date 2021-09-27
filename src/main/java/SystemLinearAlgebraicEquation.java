import java.math.BigDecimal;

/**
 * Класс хранит в себе расширенную матрицу СЛАУ в двух компонентах: матрица и столбец свободных членов
 */
public class SystemLinearAlgebraicEquation {
    private final BigDecimal[][]    matrix;
    private final BigDecimal[]      freeTerms;

    /**
     * Конструктор создаёт расширенную расширенную матрицу, клонируя соответсвующие аргументы
     * @param matrix подразумевается квадратная матрица, но способна хранить и неквадратную
     */
    public SystemLinearAlgebraicEquation(BigDecimal[][] matrix, BigDecimal[] freeTerms) {
        this.matrix     = matrix.clone();
        this.freeTerms  = freeTerms.clone();
    }

    /**
     * Метод возвращает клон матрицы, содержащейся в объекте
     */
    public BigDecimal[][] getMatrix() {
        return matrix.clone();
    }

    /**
     * Метод возвращает клон столбца свободных членов, содержащийся в объекте
     */
    public BigDecimal[] getFreeTerms() {
        return freeTerms.clone();
    }

    /**
     * Метод возвращает новый объект данного класса, созданный на основе переданной матрицы
     * и столбца свободных членов, содержащегося в объекте-исходнике, при этом не меняя исходный объект.
     */
    public SystemLinearAlgebraicEquation setMatrix(BigDecimal[][] matrix){
        return new SystemLinearAlgebraicEquation(matrix, this.freeTerms);
    }

    /**
     * Метод возвращает новый объект данного класса, созданный на основе матрицы, содержащейся в объекте-исходнике,
     * и переданного столбца свободных членов, при этом не меняя исходный объект
     */
    public SystemLinearAlgebraicEquation setFreeTerms(BigDecimal[] freeTerms){
        return new SystemLinearAlgebraicEquation(this.matrix, freeTerms);
    }

    /**
     * Метод возвращает новый объект данного класса, созданный на основе матрицы и столбца свободных членов,
     * хранящихся в объекте-исходнике
     */
    public SystemLinearAlgebraicEquation clone(){
        return new SystemLinearAlgebraicEquation(matrix, freeTerms);
    }
}

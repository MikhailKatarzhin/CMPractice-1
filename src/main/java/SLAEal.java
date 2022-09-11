import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Класс хранит в себе расширенную матрицу СЛАУ в двух компонентах: матрица и столбец свободных членов
 */
public class SLAEal {
    private final ArrayList<ArrayList<BigDecimal>>  matrix;
    private final ArrayList<BigDecimal>             freeTerms;

    /**
     * Конструктор создаёт расширенную расширенную матрицу, клонируя соответсвующие аргументы
     * @param matrix подразумевается квадратная матрица, но способна хранить и неквадратную
     */
    public SLAEal(ArrayList<ArrayList<BigDecimal>> matrix, ArrayList<BigDecimal> freeTerms) {
        this.matrix     = matrix;
        this.freeTerms  = freeTerms;
    }

    /**
     * Метод возвращает клон матрицы, содержащейся в объекте
     */
    public ArrayList<ArrayList<BigDecimal>> getMatrix() {
        return matrix;
    }

    /**
     * Метод возвращает клон столбца свободных членов, содержащийся в объекте
     */
    public ArrayList<BigDecimal> getFreeTerms() {
        return freeTerms;
    }

    /**
     * Метод возвращает новый объект данного класса, созданный на основе переданной матрицы
     * и столбца свободных членов, содержащегося в объекте-исходнике, при этом не меняя исходный объект.
     */
    public SLAEal setMatrix(ArrayList<ArrayList<BigDecimal>> matrix){
        return new SLAEal(matrix, this.freeTerms);
    }

    /**
     * Метод возвращает новый объект данного класса, созданный на основе матрицы, содержащейся в объекте-исходнике,
     * и переданного столбца свободных членов, при этом не меняя исходный объект
     */
    public SLAEal setFreeTerms(ArrayList<BigDecimal> freeTerms){
        return new SLAEal(this.matrix, freeTerms);
    }

    /**
     * Метод возвращает новый объект данного класса, созданный на основе матрицы и столбца свободных членов,
     * хранящихся в объекте-исходнике
     */
    public SLAEal clone(){
        return new SLAEal(matrix, freeTerms);
    }
}

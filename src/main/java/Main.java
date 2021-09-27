import java.math.BigDecimal;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        var21();
    }

    public static void var21(){
        /// Создание матрицы на основе задания
        BigDecimal[][]  matrix = {
                {new BigDecimal("0"), new BigDecimal("-2"), new BigDecimal("-1"), new BigDecimal("1")}
                , {new BigDecimal("-6"), new BigDecimal("0"), new BigDecimal("3"), new BigDecimal("-1")}
                , {new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("-1"), new BigDecimal("-1")}
                , {new BigDecimal("0"), new BigDecimal("2"), new BigDecimal("-1"), new BigDecimal("-1")}
        };

        /// Создание столбца свободных членов на основе задания
        BigDecimal[]    freeTerms = {
                new BigDecimal("1")
                , new BigDecimal("0")
                , new BigDecimal("-6")
                , new BigDecimal("2")
        };

        ///создаём СЛАУ на основе матрицы и столбце свободных членов
        SystemLinearAlgebraicEquation slae = new SystemLinearAlgebraicEquation(matrix, freeTerms);

        ///Отображаем созданную матрицу
        SLAEHandler.showSLAE(slae);

        ///Вычисляем неизвестные
        BigDecimal[] xs = SLAEHandler.computeX(slae);
        if (xs == null)
            System.out.println("Resolve count not 1");
        else
            ///Вывод неизвестных на экран в консоль
            System.out.println("\n\tVariables:\n" + Arrays.toString(xs));
    }
}

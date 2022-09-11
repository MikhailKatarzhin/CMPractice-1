import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        long millis = System.nanoTime();

        File file = new File("C:\\Users\\maikl\\Documents\\GitHub\\CMPractice-1\\src\\main\\java\\input.txt");
        try (Scanner sc = new Scanner(file)){
            String line = sc.nextLine();

            final int n = Integer.parseInt(line);
            ArrayList<ArrayList<BigDecimal>> matrix = new ArrayList<>(n);
            ArrayList<BigDecimal> freeTerms = new ArrayList<>(n);

            for (int i = 0; i < n; i++){
                matrix.add(new ArrayList<BigDecimal>(n));
                line = sc.nextLine();
                String[] sublines = line.split(" ", n+1);
                for (int j = 0; j < n; j++){
                    matrix.get(i).add(new BigDecimal(Integer.parseInt(sublines[j])));
                }
                freeTerms.add(new BigDecimal(Integer.parseInt(sublines[n])));
            }

            ///создаём СЛАУ на основе матрицы и столбце свободных членов
            SLAEal slae = new SLAEal(matrix, freeTerms);

            ///Отображаем созданную матрицу
            SLAEalHandler.showSLAE(slae);

            ///Вычисляем неизвестные
            ArrayList<BigDecimal> xs = SLAEalHandler.computeXzg(slae);

            for (int i = 0; i < n; i++)
                System.out.println(((slae.getFreeTerms().get(i).compareTo(new BigDecimal("0")) == -1) ? "" : "+") + slae.getFreeTerms().get(i).setScale(4, RoundingMode.HALF_DOWN));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Timer of program: " + 1.0*(System.nanoTime() - millis)/1000000);
    }
}

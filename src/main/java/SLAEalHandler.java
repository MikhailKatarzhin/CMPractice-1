import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;

public class SLAEalHandler {

    public final static int scale;
    static {
        scale = 10;
    }
    public ArrayList<BigDecimal> computeXzg(SLAEal systemLinearAlgebraicEquation){
        long millis = System.nanoTime();
        try {
            if (systemLinearAlgebraicEquation == null) throw new NullPointerException();
            ArrayList<ArrayList<BigDecimal>> matrix = systemLinearAlgebraicEquation.getMatrix();
            ArrayList<BigDecimal> freeTerms         = systemLinearAlgebraicEquation.getFreeTerms();


            /** Start computing toDiagonalMatrix (+ nothing xor MT xor OMT)
             * toDiagonalMatrix - 1-thread computing
             * toDiagonalMatrixMT - multi-thread computing, one compute line adding in one thread. Faster than 1-thread since 175*175
             * toDiagonalMatrixOMT - multi-thread computing, n thread compute n line adding with offset n line. Faster than 1-thread since 75*75
             */
            if(!toDiagonalMatrixOMT(matrix, freeTerms))
                return null;
            for (int i = 0; i < freeTerms.size(); i++){
                if (matrix.get(i).get(i).compareTo(new BigDecimal(0)) == 0) {
                    if ((matrix.get(i).get(i).compareTo(freeTerms.get(i))) != 0)
                        return null;
                }else{
                    freeTerms.set(i, freeTerms.get(i).divide(matrix.get(i).get(i), scale, RoundingMode.UP));
                    matrix.get(i).set(i, new BigDecimal(1));
                }
            }
            System.out.println("Timer of compute: " + (1.0*(System.nanoTime() - millis)/1000000));
            System.out.println("\n\tDiagonal matrix:\n");
            showSLAE(systemLinearAlgebraicEquation);
            return freeTerms;
        }catch (NullPointerException exception){
            System.out.println("SLAE must not be nullptr");
        }
        return null;
    }
    public boolean toDiagonalMatrix(ArrayList<ArrayList<BigDecimal>> matrix, ArrayList<BigDecimal> freeTerms){
        for (int i = 0; i < freeTerms.size(); i++){
            if (matrix.get(i).get(i).compareTo(new BigDecimal(0)) == 0)
                for (int j = i + 1; j < freeTerms.size(); j++)
                    if (matrix.get(j).get(i).compareTo(new BigDecimal(0)) != 0){
                        addrows(matrix, freeTerms, j, i, new BigDecimal(1));
                        break;
                    }
            if (matrix.get(i).get(i).compareTo(new BigDecimal(0)) == 0)
                return false;
            for (int j = 0; j < freeTerms.size(); j++){
                if (j != i && matrix.get(j).get(i).compareTo(new BigDecimal(0)) != 0)
                    addrows(matrix, freeTerms, i, j, matrix.get(j).get(i)
                            .divide(matrix.get(i).get(i), scale, RoundingMode.UP).multiply(new BigDecimal(-1)));
            }
        }
        return true;
    }
    public boolean toDiagonalMatrixMT(ArrayList<ArrayList<BigDecimal>> matrix, ArrayList<BigDecimal> freeTerms){
        int cores = Runtime.getRuntime().availableProcessors();
        if (cores < 2)
            return toDiagonalMatrix(matrix,freeTerms);
        for (int i = 0; i < freeTerms.size(); i++){
            if (matrix.get(i).get(i).compareTo(new BigDecimal(0)) == 0)
                for (int j = i + 1; j < freeTerms.size(); j++)
                    if (matrix.get(j).get(i).compareTo(new BigDecimal(0)) != 0){
                        addrows(matrix, freeTerms, j, i, new BigDecimal(1));
                        break;
                    }
            if (matrix.get(i).get(i).compareTo(new BigDecimal(0)) == 0)
                return false;
            for (int j = 0; j < freeTerms.size(); j++){
                new Thread(new AddRunnable(matrix, freeTerms, i, j)).start();
                while (java.lang.Thread.activeCount() == cores){};
            }
            while (java.lang.Thread.activeCount() > 1){}
        }
        return true;
    }
    public boolean toDiagonalMatrixOMT(ArrayList<ArrayList<BigDecimal>> matrix, ArrayList<BigDecimal> freeTerms){
        int cores = Runtime.getRuntime().availableProcessors()-1;
        if (cores < 1)
            return toDiagonalMatrix(matrix,freeTerms);
        for (int i = 0; i < freeTerms.size(); i++){
            if (matrix.get(i).get(i).compareTo(new BigDecimal(0)) == 0)
                for (int j = i + 1; j < freeTerms.size(); j++)
                    if (matrix.get(j).get(i).compareTo(new BigDecimal(0)) != 0){
                        addrows(matrix, freeTerms, j, i, new BigDecimal(1));
                        break;
                    }
            if (matrix.get(i).get(i).compareTo(new BigDecimal(0)) == 0)
                return false;
            for (int offset = 0; offset < cores; offset++){
                new Thread(new OffsetAddRunnable(matrix, freeTerms, i, offset, cores)).start();
            }
            while (java.lang.Thread.activeCount() > 1){}
        }
        return true;
    }

    class AddRunnable implements Runnable {

        ArrayList<ArrayList<BigDecimal>> matrix;
        ArrayList<BigDecimal> freeTerms;
        int i, j;

        public AddRunnable(ArrayList<ArrayList<BigDecimal>> matrix, ArrayList<BigDecimal> freeTerms, int i, int j) {
            this.matrix = matrix;
            this.freeTerms = freeTerms;
            this.i = i;
            this.j = j;
        }

        public void run() {
            if(j != i && matrix.get(j).get(i).compareTo(new BigDecimal(0)) != 0)
                addrows(matrix, freeTerms, i, j, matrix.get(j).get(i)
                        .divide(matrix.get(i).get(i), scale, RoundingMode.UP).multiply(new BigDecimal(-1)));
        }
    }

    class OffsetAddRunnable implements Runnable {

        ArrayList<ArrayList<BigDecimal>> matrix;
        ArrayList<BigDecimal> freeTerms;
        int i, offset, cores;

        public OffsetAddRunnable(ArrayList<ArrayList<BigDecimal>> matrix, ArrayList<BigDecimal> freeTerms, int i, int offset, int cores) {
            this.matrix = matrix;
            this.freeTerms = freeTerms;
            this.i = i;
            this.offset = offset;
            this.cores = cores;
        }

        public void run() {
            for (int j = offset; j <freeTerms.size(); j += cores)
                if(j != i && matrix.get(j).get(i).compareTo(new BigDecimal(0)) != 0)
                    addrows(matrix, freeTerms, i, j, matrix.get(j).get(i)
                            .divide(matrix.get(i).get(i), scale, RoundingMode.UP).multiply(new BigDecimal(-1)));
        }
    }
    public void addrows(ArrayList<ArrayList<BigDecimal>> matrix, ArrayList<BigDecimal> freeTerms, int rowFrom, int rowTo, BigDecimal multiplier) {
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
        for (int i = 0; i < matrix.size(); i++)
            matrix.get(rowTo).set(i, matrix.get(rowTo).get(i).add(matrix.get(rowFrom).get(i).multiply(multiplier)));
        freeTerms.set(rowTo, freeTerms.get(rowTo).add(freeTerms.get(rowFrom).multiply(multiplier)));
    }

    public static void showSLAE(SLAEal systemLinearAlgebraicEquation) throws NullPointerException{
        if (systemLinearAlgebraicEquation == null) throw new NullPointerException();
        ArrayList<ArrayList<BigDecimal>> matrix = systemLinearAlgebraicEquation.getMatrix();
        ArrayList<BigDecimal> freeTerms         = systemLinearAlgebraicEquation.getFreeTerms();
        int size                                = matrix.size();
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++)
                if (matrix.get(i).get(j).multiply(new BigDecimal(10000)).toBigInteger().compareTo(new BigInteger("0")) != 0)
                    System.out.print("\t" + ((matrix.get(i).get(j).compareTo(new BigDecimal("0")) != -1) ? "+" : "")
                            + matrix.get(i).get(j).setScale(4, RoundingMode.HALF_DOWN) + "_x" + (j + 1) + " ");
            System.out.println("= " + freeTerms.get(i).setScale(4, RoundingMode.HALF_DOWN));
        }
    }
}

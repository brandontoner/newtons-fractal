import com.brandontoner.newtons.fractal.Fractal;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.IntStream;

enum Main {
    ;

    public static void main(String[] args) {
        int width = 3840;
        int height = 1600;
        double xrange = 10;
        double yranage = height * xrange / width;
        IntStream.range(0, 360)
                 .parallel()
                 .mapToObj(offset -> new Fractal(width, height, -1 * xrange, xrange, -1 * yranage, yranage, offset))
                 .forEach(f -> {
                     try {
                         f.compute();
                     } catch (IOException e) {
                         throw new UncheckedIOException(e);
                     }
                 });
    }
}

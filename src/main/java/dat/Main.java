package dat;

import dat.services.MovieService;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {

            //Fill database up with movies, total pages is around 58
            MovieService.FillDBUpLast5yearsDanish(5);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

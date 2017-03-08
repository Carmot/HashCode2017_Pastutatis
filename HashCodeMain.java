import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;

/**
 * Created by usuario on 23/02/2017.
 */
public class HashCodeMain {


    public static void main(String[] args) {
        //Declaración de constantes
        //final String fileNameSmall = "me_at_the_zoo.in";
        final String fileNameSmall = "kittens.in";
        //final String fileNameSmall = "trending_today.in";
        //final String fileNameSmall = "videos_worth_spreading.in";
        Integer[] videosSize;
        EndPoint[] endPoints;
        Integer[] requestDescriptions;
        Integer[] caches;
        int cacheSize;

        //Empezamos leyendo el fichero de datos.
        Path path = FileSystems.getDefault().getPath("/Users/joseangelgariburo/hashcode2017/src/Resources/" + fileNameSmall, "");
        int numLine = 0;
        try {
            List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
            //Primera linea tiene información de servidores, enpoints, caches
            String[] firstLine = lines.get(numLine++).split(" ");
            videosSize = new Integer[Integer.valueOf(firstLine[0])];
            endPoints = new EndPoint[Integer.valueOf(firstLine[1])];
            requestDescriptions = new Integer[Integer.valueOf(firstLine[2])];
            caches = new Integer[Integer.valueOf(firstLine[3])];
            cacheSize = Integer.valueOf(firstLine[4]);
            //Segunda línea: tamaños de los vídeos.
            String[] secondLine = lines.get(numLine++).split(" ");
            for (int i = 0; i < secondLine.length; i++) {
                videosSize[i] = Integer.valueOf(secondLine[i]);
            }
            //Bloque de Endpoints
            for (int i = 0; i < endPoints.length; i++) {
                String[] endPointLine = lines.get(numLine++).split(" ");
                Integer numCaches = Integer.valueOf(endPointLine[1]);
                endPoints[i] = new EndPoint(i, Integer.valueOf(endPointLine[0]), numCaches, videosSize.length);
                for (int j = 0; j < numCaches; j++) {
                    String[] cacheLine = lines.get(numLine++).split(" ");
                    endPoints[i].cacheIds[j] = new Cache();
                    endPoints[i].cacheIds[j].id = Integer.valueOf(cacheLine[0]);
                    // Almaceno la latencia ganada en cada cache: dataCenterLatency - cacheLatency;
                    endPoints[i].cacheLatencies[j] = endPoints[i].dataCenterLatency - Integer.valueOf(cacheLine[1]);
                }
            }
            //Bloque Request Descriptions
            for (int i = 0; i < requestDescriptions.length; i++) {
                String[] requestLine = lines.get(numLine++).split(" ");
                if (endPoints[Integer.valueOf(requestLine[1])].videoRequest[Integer.valueOf(requestLine[0])] == null) {
                    endPoints[Integer.valueOf(requestLine[1])].videoRequest[Integer.valueOf(requestLine[0])] = Integer.valueOf(requestLine[2]);
                } else {
                    endPoints[Integer.valueOf(requestLine[1])].videoRequest[Integer.valueOf(requestLine[0])] += Integer.valueOf(requestLine[2]);
                }
            }
            System.out.println("File loaded");
            BaseOptimizer optimizer = new BaseOptimizer(cacheSize, caches.length, endPoints);
            optimizer.optimize(videosSize, endPoints);
            optimizer.printSolution();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

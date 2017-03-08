import java.util.List;

/**
 * Created by usuario on 23/02/2017.
 */
public class EndPoint {
    Integer id;
    Integer dataCenterLatency;
    Cache[] cacheIds;
    Integer[] cacheLatencies;
    Integer[] videoRequest;

    public EndPoint(int id, Integer dataCenterLatency, Integer numCaches, Integer numVideos){
        this.id = id;
        this.dataCenterLatency = dataCenterLatency;
        this.cacheIds = new Cache[numCaches];
        this.cacheLatencies = new Integer[numCaches];
        this.videoRequest = new Integer[numVideos];
    }

}

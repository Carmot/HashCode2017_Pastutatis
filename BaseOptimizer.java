import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by usuario on 23/02/2017.
 */
public class BaseOptimizer {

    Integer[] cache_space;
    int cache_size;
    ArrayList<Integer>[] cache_videos;
    int caches_used;
    ArrayList<Integer> endpointVideos;
    ArrayList<Integer> endpointCaches;
    ArrayList<Integer> random;

    public BaseOptimizer(int cache_size, int num_caches, EndPoint[] endpoints_list) {
        this.cache_space = new Integer[num_caches];
        this.cache_size = cache_size;
        Arrays.fill(cache_space, cache_size);
        this.cache_videos = new ArrayList[this.cache_space.length];
        for (int index = 0; index < this.cache_videos.length; ++index) {
            this.cache_videos[index] = new ArrayList<Integer>();
        }
        this.caches_used = 0;
        this.endpointVideos = new ArrayList<Integer>();
        this.endpointCaches = new ArrayList<Integer>();
        this.random = new ArrayList<Integer>();
        for (int index = 0; index < endpoints_list.length; ++index) {
            this.random.add(index, index);
        }
        Collections.shuffle(this.random);
    }

    public void printSolution() {
        System.out.println(this.caches_used);
        for (int i = 0; i < this.cache_space.length; i++) {
            if (this.cache_videos[i].size() != 0) {
                System.out.print(i);
                for (int j = 0; j < this.cache_videos[i].size(); j++) {
                    System.out.print(" " + this.cache_videos[i].get(j));
                }
                System.out.println();
            }
        }
    }

    // Devuelve la mejor cache.
    public int getBestCache(EndPoint[] endpoints_list, int index) {
        int result = -1;
        int maxCacheIndex = -1;
        boolean forUse = true;
        // Recorro las caches del endpoint
        for (int icache = 0; icache < this.endpointCaches.size(); ++icache) {
            if (this.endpointCaches.get(icache) != -1) {
                if (maxCacheIndex == -1 || endpoints_list[index].cacheLatencies[icache] > endpoints_list[index].cacheLatencies[maxCacheIndex]) {
                    maxCacheIndex = icache;
                    result = endpoints_list[index].cacheIds[icache].id;
                }
            }
        }
        return result;
    }

    // Devuelve el mejor video.
    public int getBestVideo(Integer[] video_size_list) {
        int result = 0;
        for (int index = 0; index < this.endpointVideos.size(); ++index) {
            if (this.endpointVideos.get(index) != -1) {
                if (this.endpointVideos.get(index)/video_size_list[index] > this.endpointVideos.get(result)/video_size_list[result]) {
                    result = index;
                }
            }
        }
        return result;
    }

    // Devuelve true si el video se encuentra en una cache, false en otro caso.
    public boolean isVideoInAnyCache(EndPoint[] endpoints_list, int index, int video) {
        // Saber si alguna de las caches del endpoint ya contiene el video
        boolean result = false;
        int i = 0;
        while (!result && i < endpoints_list[index].cacheIds.length) {
            result = this.cache_videos[endpoints_list[index].cacheIds[i].id].contains(video);
            ++i;
        }
        return result;
    }

    public void optimize(Integer[] video_size_list, EndPoint[] endpoints_list) {
        for (int index = 0; index < endpoints_list.length; ++index) {
            int i = this.random.get(index);
            // Vaciamos la lista de vídeos del endpoint anterior.
            this.endpointVideos.clear();
            int activeVideosEndpoint = 0;
            // Llenamos la lista con los videos del nuevo endpoint.
            for (int rindex = 0; rindex < endpoints_list[i].videoRequest.length; ++rindex) {
                if (endpoints_list[i].videoRequest[rindex] != null) {
                    this.endpointVideos.add(endpoints_list[i].videoRequest[rindex]);
                    ++activeVideosEndpoint;
                } else {
                    this.endpointVideos.add(-1);
                }
            }


            // Para los videos los metemos en las caches de mejor a peor.
            int nVideos = 0;
            while (nVideos < activeVideosEndpoint) {
                int nCaches = 0;
                this.endpointCaches.clear();
                // Almacenamos temporalmente las caches para calcular la mejor
                for (int cindex = 0; cindex < endpoints_list[i].cacheLatencies.length; ++cindex) {
                    if (endpoints_list[i].cacheLatencies[cindex] != null) {
                        this.endpointCaches.add(endpoints_list[i].cacheIds[cindex].id);
                        ++nCaches;
                    } else {
                        this.endpointCaches.add(-1);
                    }
                }
                int bestVideo = this.getBestVideo(video_size_list);
                boolean isVideo = this.isVideoInAnyCache(endpoints_list, i, bestVideo);
                int cachesVisited = 0;
                if (!isVideo) {
                    boolean allocated = false;
                    while (cachesVisited < nCaches && !allocated) {
                        // Buscamos la mejor cache.
                        int bestCache = this.getBestCache(endpoints_list, i);
                        if (bestCache != -1) {
                            // Si me cabe en la mejor cache lo meto.
                            if (this.cache_space[bestCache] >= video_size_list[bestVideo]) {
                                // Cabe -> lo meto. Si la cache tiene el tamaño maximo (no ha sido usada) la marco como usada.
                                if (this.cache_space[bestCache] == this.cache_size) {
                                    this.caches_used++;
                                }
                                this.cache_videos[bestCache].add(bestVideo);
                                this.cache_space[bestCache] -= video_size_list[bestVideo];
                                allocated = true;
                            }
                            if (!allocated) {
                                for (int removeIndex = 0; removeIndex < this.endpointCaches.size(); removeIndex++) {
                                    if (this.endpointCaches.get(removeIndex) == bestCache) {
                                        this.endpointCaches.set(removeIndex, -1);
                                    }
                                }
                            }
                        }
                        ++cachesVisited;
                    }
                }
                ++nVideos;
                this.endpointVideos.set(bestVideo, -1);
            }
        }
    }

}

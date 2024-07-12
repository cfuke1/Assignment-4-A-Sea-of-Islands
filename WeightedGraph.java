import java.util.*;

class WeightedGraph {
    private final Map<String, Vertex> vertices;

    static class Vertex {
        String id;
        int time;
        List<Edge> edges;

        Vertex(String id, int time) {
            this.id = id;
            this.time = time;
            this.edges = new ArrayList<>();
        }
    }

    static class Edge {
        String target;
        int weight;

        Edge(String target, int weight) {
            this.target = target;
            this.weight = weight;
        }
    }

    public WeightedGraph() {
        this.vertices = new HashMap<>();
    }

    public void addVertex(String id, int time) {
        vertices.putIfAbsent(id, new Vertex(id, time));
    }

    public void addEdge(String sourceId, String targetId, int weight) {
        Vertex source = vertices.get(sourceId);
        Vertex target = vertices.get(targetId);
        if (source != null && target != null) {
            source.edges.add(new Edge(targetId, weight));
            target.edges.add(new Edge(sourceId, weight)); // Undirected
        }
    }

    // Dijkstra's algorithm to find shortest paths from a source vertex
    public Map<String, Integer> dijkstra(String sourceId) {
        Map<String, Integer> distances = new HashMap<>();
        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(edge -> edge.weight));

        for (String vertexId : vertices.keySet()) {
            distances.put(vertexId, Integer.MAX_VALUE); // Set all distances to infinity
        }
        distances.put(sourceId, 0); // Distance to the source is 0
        priorityQueue.add(new Edge(sourceId, 0));

        while (!priorityQueue.isEmpty()) {
            Edge currentEdge = priorityQueue.poll();
            String currentVertexId = currentEdge.target;

            // Explore neighbors
            for (Edge edge : vertices.get(currentVertexId).edges) {
                int newDist = distances.get(currentVertexId) + edge.weight;
                if (newDist < distances.get(edge.target)) {
                    distances.put(edge.target, newDist);
                    priorityQueue.add(new Edge(edge.target, newDist));
                }
            }
        }

        return distances;
    }

    public void display() {
        for (Vertex vertex : vertices.values()) {
            System.out.print(vertex.id + " (" + vertex.time + ") -> ");
            for (Edge edge : vertex.edges) {
                System.out.print("(" + edge.target + ", " + edge.weight + ") ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        WeightedGraph graph = new WeightedGraph();
        graph.addVertex("Hawaii", 10);
        graph.addVertex("New Zealand", 20);
        graph.addVertex("Easter Island", 30);
        graph.addVertex("Tahiti", 40);
        graph.addVertex("Samoa", 50);

        graph.addEdge("Hawaii", "New Zealand", 5);
        graph.addEdge("Hawaii", "Easter Island", 10);
        graph.addEdge("New Zealand", "Tahiti", 3);
        graph.addEdge("Easter Island", "Samoa", 1);
        graph.addEdge("Tahiti", "Samoa", 8);

        graph.display();

        // Run Dijkstra's algorithm from Hawaii
        Map<String, Integer> distances = graph.dijkstra("Hawaii");
        System.out.println("Shortest distances from Hawaii:");
        for (Map.Entry<String, Integer> entry : distances.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
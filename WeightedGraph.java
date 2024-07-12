import java.util.*;

class WeightedGraph {
    private final Map<String, Vertex> vertices;

    static class Vertex {
        String id; // Island name
        int time; // Attraction time per island
        List<Edge> edges;

        Vertex(String id, int time) {
            this.id = id;
            this.time = time;
            this.edges = new ArrayList<>();
        }
    }

    static class Edge {
        String target; // Destination island
        int weight; // Distance in miles

        Edge(String target, int weight) {
            this.target = target;
            this.weight = weight;
        }
    }

    public WeightedGraph() {
        this.vertices = new HashMap<>(); // Create Hashmap of all vertices
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

    // Dijkstra's algorithm to find the shortest path from source to target
    public PathResult dijkstra(String sourceId, String targetId) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(edge -> edge.weight));

        for (String vertexId : vertices.keySet()) {
            distances.put(vertexId, Integer.MAX_VALUE);
            previous.put(vertexId, null);
        }
        distances.put(sourceId, 0);
        priorityQueue.add(new Edge(sourceId, 0));

        while (!priorityQueue.isEmpty()) {
            Edge currentEdge = priorityQueue.poll();
            String currentVertexId = currentEdge.target;

            if (currentVertexId.equals(targetId)) {
                break;
            }

            for (Edge edge : vertices.get(currentVertexId).edges) {
                int newDist = distances.get(currentVertexId) + edge.weight;
                if (newDist < distances.get(edge.target)) {
                    distances.put(edge.target, newDist);
                    previous.put(edge.target, currentVertexId);
                    priorityQueue.add(new Edge(edge.target, newDist));
                }
            }
        }

        List<String> path = new ArrayList<>();
        for (String at = targetId; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return new PathResult(path, distances.get(targetId));
    }

    // Class to hold the result of the path and the total distance
    static class PathResult {
        List<String> path;
        int totalDistance;

        PathResult(List<String> path, int totalDistance) {
            this.path = path;
            this.totalDistance = totalDistance;
        }
    }

    // Method to find the fastest path visiting all targets using TSP approach
    public List<String> findFastestPath(String startIsland, List<String> targets) {
        List<String> bestPath = null;
        int bestDistance = Integer.MAX_VALUE;

        // Generate all permutations of target islands
        List<List<String>> permutations = new ArrayList<>();
        permute(targets, 0, permutations);

        for (List<String> perm : permutations) {
            List<String> fullPath = new ArrayList<>();
            fullPath.add(startIsland);
            fullPath.addAll(perm);

            int totalDistance = calculateTotalDistance(fullPath);
            if (totalDistance < bestDistance) {
                bestDistance = totalDistance;
                bestPath = fullPath;
            }
        }

        System.out.println("Fastest path visiting all islands: " + bestPath + " with total distance: " + bestDistance + " miles");
        return bestPath;
    }

    // Method to calculate the total distance of a path
    private int calculateTotalDistance(List<String> path) {
        int totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            PathResult result = dijkstra(path.get(i), path.get(i + 1));
            totalDistance += result.totalDistance;
        }
        return totalDistance;
    }

    // Permutation method to generate all possible orders of targets
    private void permute(List<String> targets, int start, List<List<String>> result) {
        if (start == targets.size() - 1) {
            result.add(new ArrayList<>(targets));
            return;
        }
        for (int i = start; i < targets.size(); i++) {
            Collections.swap(targets, start, i);
            permute(targets, start + 1, result);
            Collections.swap(targets, start, i); // backtrack
        }
    }

    // Method to find the maximum number of islands that can be visited within an hour limit
    public List<String> maxIslandsInChain(String startIsland, int hourLimit) {
        List<String> visited = new ArrayList<>();
        List<String> bestPath = new ArrayList<>();
        boolean[] visitedMap = new boolean[vertices.size()];
        int[] totalHoursSpent = {0}; // Array to hold total hours spent

        dfs(startIsland, hourLimit, 0, visited, bestPath, visitedMap, totalHoursSpent);

        System.out.println("Maximum islands visited in chain: " + bestPath + " with total hours spent: " + totalHoursSpent[0]);
        return bestPath;
    }

    private void dfs(String currentIsland, int hourLimit, int currentTime,
                     List<String> visited, List<String> bestPath, boolean[] visitedMap, int[] totalHoursSpent) {
        visited.add(currentIsland);
        visitedMap[vertices.get(currentIsland).hashCode() % visitedMap.length] = true;

        // Add island attraction time
        currentTime += vertices.get(currentIsland).time;

        if (currentTime <= hourLimit && visited.size() > bestPath.size()) {
            bestPath.clear();
            bestPath.addAll(visited);
            totalHoursSpent[0] = currentTime; // Update total hours spent
        }

        for (Edge edge : vertices.get(currentIsland).edges) {
            if (!visitedMap[vertices.get(edge.target).hashCode() % visitedMap.length]) {
                PathResult result = dijkstra(currentIsland, edge.target);
                int travelTime = result.totalDistance / 500; // Assuming average speed of 500 miles/hour
                int newTime = currentTime + travelTime;

                if (newTime <= hourLimit) {
                    dfs(edge.target, hourLimit, newTime, visited, bestPath, visitedMap, totalHoursSpent);
                }
            }
        }

        visited.remove(visited.size() - 1);
        visitedMap[vertices.get(currentIsland).hashCode() % visitedMap.length] = false;
    }

    public void display() {
        for (Vertex vertex : vertices.values()) {
            System.out.print(vertex.id + " (" + vertex.time + " hours) -> ");
            for (Edge edge : vertex.edges) {
                System.out.print("(" + edge.target + ", " + edge.weight + " miles) ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        WeightedGraph graph = new WeightedGraph();
        graph.addVertex("Hawaii", 240);
        graph.addVertex("New Zealand", 72);
        graph.addVertex("Easter Island", 72);
        graph.addVertex("Tahiti", 336);
        graph.addVertex("Samoa", 192);
        graph.addVertex("Fiji", 168);
        graph.addVertex("Guam", 144);
        graph.addVertex("Palau", 120);
        graph.addVertex("Bora Bora", 336);
        graph.addVertex("Solomon Islands", 144);

        graph.addEdge("Hawaii", "New Zealand", 4660);
        graph.addEdge("Hawaii", "Easter Island", 4840);
        graph.addEdge("Hawaii", "Tahiti", 2734);
        graph.addEdge("Hawaii", "Samoa", 2609);
        graph.addEdge("Hawaii", "Fiji", 3178);
        graph.addEdge("Hawaii", "Guam", 3801);
        graph.addEdge("Hawaii", "Palau", 4340);
        graph.addEdge("Hawaii", "Bora Bora", 2610);
        graph.addEdge("Hawaii", "Solomon Islands", 3600);
        graph.addEdge("New Zealand", "Easter Island", 4349);
        graph.addEdge("New Zealand", "Tahiti", 2485);
        graph.addEdge("New Zealand", "Samoa", 1802);
        graph.addEdge("New Zealand", "Fiji", 1600);
        graph.addEdge("New Zealand", "Guam", 3385);
        graph.addEdge("New Zealand", "Palau", 3820);
        graph.addEdge("New Zealand", "Bora Bora", 2570);
        graph.addEdge("New Zealand", "Solomon Islands", 2925);
        graph.addEdge("Easter Island", "Tahiti", 2609);
        graph.addEdge("Easter Island", "Samoa", 2920);
        graph.addEdge("Easter Island", "Fiji", 4070);
        graph.addEdge("Easter Island", "Guam", 4976);
        graph.addEdge("Easter Island", "Palau", 5488);
        graph.addEdge("Easter Island", "Bora Bora", 2580);
        graph.addEdge("Easter Island", "Solomon Islands", 4798);
        graph.addEdge("Tahiti", "Samoa", 1616);
        graph.addEdge("Tahiti", "Fiji", 2027);
        graph.addEdge("Tahiti", "Guam", 3718);
        graph.addEdge("Tahiti", "Palau", 4217);
        graph.addEdge("Tahiti", "Bora Bora", 257);
        graph.addEdge("Tahiti", "Solomon Islands", 3531);
        graph.addEdge("Samoa", "Fiji", 737);
        graph.addEdge("Samoa", "Guam", 2985);
        graph.addEdge("Samoa", "Palau", 3477);
        graph.addEdge("Samoa", "Bora Bora", 1742);
        graph.addEdge("Samoa", "Solomon Islands", 1795);
        graph.addEdge("Fiji", "Guam", 3053);
        graph.addEdge("Fiji", "Palau", 3558);
        graph.addEdge("Fiji", "Bora Bora", 1981);
        graph.addEdge("Fiji", "Solomon Islands", 1198);
        graph.addEdge("Guam", "Palau", 807);
        graph.addEdge("Guam", "Bora Bora", 4047);
        graph.addEdge("Guam", "Solomon Islands", 2365);
        graph.addEdge("Palau", "Bora Bora", 4264);
        graph.addEdge("Palau", "Solomon Islands", 2064);
        graph.addEdge("Bora Bora", "Solomon Islands", 3513);

        // Display all islands and their distance
        // graph.display();

        // Define all islands that can be visited
        List<String> targets = Arrays.asList("New Zealand", "Tahiti", "Samoa", "Fiji", "Guam", "Palau", "Bora Bora", "Solomon Islands");

        // Find the fastest path visiting all islands starting from Hawaii
        graph.findFastestPath("Hawaii", targets);

        // Define hour limit and find maximum islands in a chain
        int hourLimit = 1000; // Example: 48 hours
        graph.maxIslandsInChain("Hawaii", hourLimit);
    }
}


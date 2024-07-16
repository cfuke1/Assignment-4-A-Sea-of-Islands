import java.util.*;

class WeightedGraph {

    ////* CODE FOR DEFINING EDGES AND VERTICES *////

    private final Map<String, Vertex> vertices = new HashMap<>();
    static class Vertex {
        String id;
        int time;
        List<Edge> edges = new ArrayList<>();

        Vertex(String id, int time) {
            this.id = id;
            this.time = time;
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

    public void addVertex(String id, int time) {
        vertices.putIfAbsent(id, new Vertex(id, time));
    }

    public void addEdge(String sourceId, String targetId, int weight) {
        Vertex source = vertices.get(sourceId);
        Vertex target = vertices.get(targetId);
        if (source != null && target != null) {
            source.edges.add(new Edge(targetId, weight));
            target.edges.add(new Edge(sourceId, weight)); // Undirected edges
        }
    }

    ////* OBJECTIVE 2: FIND FASTEST PATH VISITING ALL ISLANDS *////

    public void findFastestPath(String startIsland, List<String> targets) {
        // Calls dfs with island set
        List<String> bestPath = new ArrayList<>();
        int[] bestDistance = {Integer.MAX_VALUE};
        dfsFindPath(startIsland, targets, new ArrayList<>(), new HashSet<>(), 0, bestPath, bestDistance);
        System.out.println("Fastest path visiting all islands: " + bestPath + " with total distance: " + bestDistance[0] + " miles");
    }

    private void dfsFindPath(String currentIsland, List<String> targets, List<String> currentPath, Set<String> visited, int currentDistance, List<String> bestPath, int[] bestDistance) {
        visited.add(currentIsland);
        currentPath.add(currentIsland);

        if (visited.containsAll(targets) && currentDistance < bestDistance[0]) {
            // Replaces the bestPath with current if current is faster
            bestPath.clear();
            bestPath.addAll(currentPath);
            bestDistance[0] = currentDistance;
        }

        for (Edge edge : vertices.get(currentIsland).edges) {
            // Recursively call on all adjacent islands
            if (!visited.contains(edge.target)) {
                dfsFindPath(edge.target, targets, currentPath, visited, currentDistance + edge.weight, bestPath, bestDistance);
            }
        }

        visited.remove(currentIsland);
        currentPath.removeLast();
    }

    ////* OBJECTIVE 4: FIND MOST ISLANDS IN LEAST TIME *////

    public void maxIslandsInChain(String startIsland, int hourLimit) {
        List<String> visited = new ArrayList<>(), bestPath = new ArrayList<>();
        int[] totalHoursSpent = {0};
        dfsMaxIslands(startIsland, hourLimit, 0, visited, bestPath, new HashSet<>(), totalHoursSpent);
        System.out.println("Maximum islands visited in chain: " + bestPath + " with total hours spent: " + totalHoursSpent[0]);
    }

    private void dfsMaxIslands(String currentIsland, int hourLimit, int currentTime, List<String> visited, List<String> bestPath, Set<String> visitedSet, int[] totalHoursSpent) {
        visited.add(currentIsland);
        visitedSet.add(currentIsland);
        currentTime += vertices.get(currentIsland).time;

        if (currentTime <= hourLimit && visited.size() > bestPath.size()) {
            // Replaces the bestPath with visited if current is faster
            bestPath.clear();
            bestPath.addAll(visited);
            totalHoursSpent[0] = currentTime;
        }

        for (Edge edge : vertices.get(currentIsland).edges) {
            // Recursively call while calculating for hours
            if (!visitedSet.contains(edge.target)) {
                int travelTime = edge.weight / 500; // Assume average plane speed of 500 miles/hour
                if (currentTime + travelTime <= hourLimit) {
                    dfsMaxIslands(edge.target, hourLimit, currentTime + travelTime, visited, bestPath, visitedSet, totalHoursSpent);
                }
            }
        }

        visited.removeLast();
        visitedSet.remove(currentIsland);
    }

    public static void main(String[] args) {

        ////* ISLANDS AND THEIR DISTANCES INFORMATION *////

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

        graph.addEdge("Hawaii", "Tahiti", 2734);
        graph.addEdge("Hawaii", "Samoa", 2609);
        graph.addEdge("Hawaii", "Fiji", 3178);
        graph.addEdge("Hawaii", "Bora Bora", 2610);
        graph.addEdge("New Zealand", "Tahiti", 2485);
        graph.addEdge("New Zealand", "Samoa", 1802);
        graph.addEdge("New Zealand", "Fiji", 1600);
        graph.addEdge("New Zealand", "Guam", 3385);
        graph.addEdge("New Zealand", "Bora Bora", 2570);
        graph.addEdge("New Zealand", "Solomon Islands", 2925);
        graph.addEdge("Easter Island", "Tahiti", 2609);
        graph.addEdge("Easter Island", "Samoa", 2920);
        graph.addEdge("Easter Island", "Bora Bora", 2580);
        graph.addEdge("Tahiti", "Samoa", 1616);
        graph.addEdge("Tahiti", "Fiji", 2027);
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
        graph.addEdge("Guam", "Solomon Islands", 2365);
        graph.addEdge("Palau", "Solomon Islands", 2064);

        List<String> targets = Arrays.asList("Hawaii", "New Zealand", "Tahiti", "Samoa", "Fiji", "Guam", "Palau", "Bora Bora", "Solomon Islands");
        graph.findFastestPath("Hawaii", targets);

        int hourLimit = 1000;
        graph.maxIslandsInChain("Hawaii", hourLimit);
    }
}

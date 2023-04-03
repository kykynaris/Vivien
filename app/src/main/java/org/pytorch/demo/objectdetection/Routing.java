package org.pytorch.demo.objectdetection;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Routing extends AppCompatActivity {
    private final Map<String, Map<String, Integer>> graph;
    private TextView shortestPathText;
    public Routing(Map<String, Map<String, Integer>> graph) {
        this.graph = graph;
    }

    public List<String> shortestPath(String start, String end) {
        // Initialize distances and visited nodes
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        Set<String> visited = new HashSet<>();

        // Set start distance to 0 and all other distances to infinity
        distances.put(start, 0);
        for (String node : graph.keySet()) {
            if (!node.equals(start)) {
                distances.put(node, Integer.MAX_VALUE);
            }
            queue.add(node);
        }

        // Traverse the graph
        while (!queue.isEmpty()) {
            // Dequeue the node with the shortest distance
            String current = queue.poll();

            // Stop if we reached the end node
            if (current.equals(end)) {
                break;
            }

            // Skip visited nodes
            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);

            // Update distances for neighbors
            for (Map.Entry<String, Integer> neighbor : graph.get(current).entrySet()) {
                int distance = distances.get(current) + neighbor.getValue();
                if (distance < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), distance);
                    previous.put(neighbor.getKey(), current);
                }
            }
        }

        // Build the shortest path from start to end
        List<String> path = new ArrayList<>();
        String current = end;
        while (previous.containsKey(current)) {
            path.add(current);
            current = previous.get(current);
        }
        path.add(start);
        Collections.reverse(path);

        return path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routingpath);

        Button button = findViewById(R.id.routing);

        // Retrieve the TextView
        shortestPathText = findViewById(R.id.path);

        // Define the graph
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        graph.put("elevator", Map.of("sakgasit", 2, "corridor_main1", 3));
        graph.put("sakgasit", Map.of("restroom", 3));
        graph.put("restroom", Map.of());
        graph.put("corridor_main1", Map.of("admin_room", 3, "Zone 422", 2, "401", 5));
        graph.put("Zone 422", Map.of());
        graph.put("401", Map.of("402", 2));
        graph.put("402", Map.of());
        graph.put("admin_room", Map.of("coorridor_main2", 1));
        graph.put("coorridor_main2", Map.of("coorridor_1", 1, "server", 1));
        graph.put("server", Map.of("coorridor_4", 4));
        graph.put("coorridor_4", Map.of("413A", 2));
        graph.put("413A", Map.of("413", 3));
        graph.put("413", Map.of("Teacher3", 3));
        graph.put("Teacher3", Map.of("412", 1));
        graph.put("coorridor_1", Map.of("fire_escape", 2));
        graph.put("fire_escape", Map.of("corridor_2", 1));
        graph.put("corridor_2", Map.of("Teacher2", 1, "411", 2));
        graph.put("411", Map.of("lab", 2));
        graph.put("lab", Map.of("412", 2));
        graph.put("412", Map.of());

        // Find the shortest path from "elevator" to "lab"
        Routing dijkstra = new Routing(graph);
        List<String> path = dijkstra.shortestPath("elevator", "lab");
        String pathString = "Shortest path: " + path.toString();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shortestPathText.setText(pathString);
            }
        });
    }
}
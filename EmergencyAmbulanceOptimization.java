import java.util.*;

public class EmergencyAmbulanceOptimization {

    // ============================================
    // Travel Cost Matrix (Adjacency Matrix)
    // ============================================
    static int[][] costMatrix = {
        {0, 15, 25, 35},
        {15, 0, 30, 28},
        {25, 30, 0, 20},
        {35, 28, 20, 0}
    };

    // Location names
    static String[] locations = {
        "Hospital",
        "Emergency Location B",
        "Emergency Location C",
        "Emergency Location D"
    };


    // ============================================
    // GROUP TASK 1 - GREEDY
    // ============================================
    public static String greedyEAROP(int[][] dist) {

        int n = dist.length;
        boolean[] visited = new boolean[n];

        int current = 0;
        int totalCost = 0;

        StringBuilder route = new StringBuilder(locations[0]);
        visited[0] = true;

        // Visit all emergency locations
        for (int count = 1; count < n; count++) {

            int nearest = -1;
            int minCost = Integer.MAX_VALUE;

            // Find the nearest unvisited location
            for (int i = 0; i < n; i++) {

                if (!visited[i] && dist[current][i] < minCost) {

                    minCost = dist[current][i];
                    nearest = i;
                }
            }

            // Visit the selected location
            visited[nearest] = true;
            totalCost += minCost;

            route.append(" -> ")
                 .append(locations[nearest]);

            current = nearest;
        }

        // Return to Hospital
        totalCost += dist[current][0];

        route.append(" -> ")
             .append(locations[0]);

        return "Greedy Ambulance Route: "
                + route
                + " | Total Cost: "
                + totalCost;
    }


    // ============================================
    // GROUP TASK 2 - DYNAMIC PROGRAMMING
    // ============================================
    public static String dynamicProgrammingEAROP(int[][] dist) {

        int n = dist.length;

        int VISITED_ALL = (1 << n) - 1;

        int[][] memo = new int[n][1 << n];

        String[][] paths = new String[n][1 << n];

        // Initialize memo table
        for (int[] row : memo) {

            Arrays.fill(row, -1);
        }

        // Start from Hospital
        int minCost = dynamicProgrammingEAROPHelper(
            0,
            1,
            dist,
            memo,
            VISITED_ALL,
            paths
        );

        // Build the route
        String route = "Hospital";

        int pos = 0;
        int mask = 1;

        while (mask != VISITED_ALL) {

            String next = paths[pos][mask];

            if (next == null) {
                break;
            }

            int nextLocation = Integer.parseInt(next);

            route += " -> " + locations[nextLocation];

            mask = mask | (1 << nextLocation);

            pos = nextLocation;
        }

        // Return to Hospital
        route += " -> Hospital";

        return "Dynamic Programming Ambulance Route: "
                + route
                + " | Total Cost: "
                + minCost;
    }


    // ============================================
    // Dynamic Programming Helper Method
    // ============================================
    private static int dynamicProgrammingEAROPHelper(
        int pos,
        int mask,
        int[][] dist,
        int[][] memo,
        int VISITED_ALL,
        String[][] paths) {

        // All locations have been visited
        if (mask == VISITED_ALL) {

            return dist[pos][0];
        }

        // Return stored result
        if (memo[pos][mask] != -1) {

            return memo[pos][mask];
        }

        int minCost = Integer.MAX_VALUE;

        int bestNext = -1;

        // Try every unvisited location
        for (int city = 0; city < dist.length; city++) {

            if ((mask & (1 << city)) == 0) {

                int newCost =
                    dist[pos][city]
                    +
                    dynamicProgrammingEAROPHelper(
                        city,
                        mask | (1 << city),
                        dist,
                        memo,
                        VISITED_ALL,
                        paths
                    );

                if (newCost < minCost) {

                    minCost = newCost;
                    bestNext = city;
                }
            }
        }

        // Store best next location
        paths[pos][mask] =
            String.valueOf(bestNext);

        // Store minimum cost
        memo[pos][mask] = minCost;

        return minCost;
    }


    // ============================================
    // GROUP TASK 3 - BACKTRACKING
    // ============================================

    static int minCost;
    static String bestPath;

    public static String backtrackingEAROP(int[][] dist) {

        int n = dist.length;

        boolean[] visited = new boolean[n];

        // Start from Hospital
        visited[0] = true;

        minCost = Integer.MAX_VALUE;
        bestPath = "";

        StringBuilder path =
            new StringBuilder(locations[0]);

        earopBacktracking(
            0,
            dist,
            visited,
            n,
            1,
            0,
            path
        );

        return "Backtracking Ambulance Route: "
                + bestPath
                + " | Total Cost: "
                + minCost;
    }


    // ============================================
    // Backtracking Helper Method
    // ============================================
    private static int earopBacktracking(
        int pos,
        int[][] dist,
        boolean[] visited,
        int n,
        int count,
        int cost,
        StringBuilder path) {

        // All locations have been visited
        if (count == n) {

            int totalCost =
                cost + dist[pos][0];

            if (totalCost < minCost) {

                minCost = totalCost;

                bestPath =
                    path.toString()
                    + " -> "
                    + locations[0];
            }

            return totalCost;
        }

        // Explore every emergency location
        for (int next = 1; next < n; next++) {

            if (!visited[next]) {

                // CHOOSE
                visited[next] = true;

                int oldLength = path.length();

                path.append(" -> ")
                    .append(locations[next]);

                // EXPLORE
                earopBacktracking(
                    next,
                    dist,
                    visited,
                    n,
                    count + 1,
                    cost + dist[pos][next],
                    path
                );

                // BACKTRACK
                visited[next] = false;

                path.setLength(oldLength);
            }
        }

        return minCost;
    }


    // ============================================
    // GROUP TASK 4 - DIVIDE AND CONQUER
    // ============================================

    static int bestDivideCost;
    static String bestDividePath;

    public static String divideAndConquerEAROP(int[][] dist) {

        int n = dist.length;

        boolean[] visited = new boolean[n];

        // Start from Hospital
        visited[0] = true;

        bestDivideCost = Integer.MAX_VALUE;
        bestDividePath = "";

        StringBuilder path =
            new StringBuilder(locations[0]);

        divideAndConquerHelper(
            0,
            visited,
            0,
            dist,
            n,
            path
        );

        return "Divide & Conquer Ambulance Route: "
                + bestDividePath
                + " | Total Cost: "
                + bestDivideCost;
    }


    // ============================================
    // Divide and Conquer Helper Method
    // ============================================
    private static int divideAndConquerHelper(
        int pos,
        boolean[] visited,
        int currentCost,
        int[][] dist,
        int n,
        StringBuilder path) {

        // Check whether all locations have been visited
        if (allVisited(visited)) {

            int totalCost =
                currentCost + dist[pos][0];

            if (totalCost < bestDivideCost) {

                bestDivideCost = totalCost;

                bestDividePath =
                    path.toString()
                    + " -> "
                    + locations[0];
            }

            return totalCost;
        }

        // Visit remaining locations
        for (int city = 1; city < n; city++) {

            if (!visited[city]) {

                visited[city] = true;

                int oldLength = path.length();

                path.append(" -> ")
                    .append(locations[city]);

                divideAndConquerHelper(
                    city,
                    visited,
                    currentCost + dist[pos][city],
                    dist,
                    n,
                    path
                );

                path.setLength(oldLength);

                visited[city] = false;
            }
        }

        return bestDivideCost;
    }


    // ============================================
    // Check All Locations Visited
    // ============================================
    private static boolean allVisited(boolean[] visited) {

        for (boolean location : visited) {

            if (!location) {

                return false;
            }
        }

        return true;
    }


    // ============================================
    // GROUP TASK 5 - INSERTION SORT
    // ============================================
    public static String insertionSort(int[] arr) {

        for (int i = 1; i < arr.length; i++) {

            int temp = arr[i];

            int j;

            for (j = i - 1;
                 j >= 0 && temp < arr[j];
                 j--) {

                arr[j + 1] = arr[j];
            }

            arr[j + 1] = temp;
        }

        return Arrays.toString(arr);
    }


    // ============================================
    // GROUP TASK 6 - BINARY SEARCH
    // ============================================
    public static String binarySearch(
        int[] arr,
        int target) {

        int first = 0;

        int last = arr.length - 1;

        while (first <= last) {

            int mid = (first + last) / 2;

            if (arr[mid] == target) {

                return String.valueOf(mid);
            }

            else if (arr[mid] < target) {

                first = mid + 1;
            }

            else {

                last = mid - 1;
            }
        }

        // Target not found
        return "-1";
    }


    // ============================================
    // INDIVIDUAL TASK 1 - MIN-HEAP
    // ============================================
    static class MinHeap {

        private PriorityQueue<Integer> heap =
            new PriorityQueue<>();


        // Insert value into Min-Heap
        public void insert(int value) {

            heap.add(value);
        }


        // Extract minimum value
        public int extractMin() {

            if (heap.isEmpty()) {

                return -1;
            }

            return heap.poll();
        }
    }


    // ============================================
    // INDIVIDUAL TASK 2 - SPLAY TREE
    // ============================================
    static class SplayTree {


        // ========================================
        // Node Class
        // ========================================
        private class Node {

            int value;

            Node left;
            Node right;

            Node(int value) {

                this.value = value;
            }
        }


        private Node root;


        // ========================================
        // Right Rotation
        // ========================================
        private Node rotateRight(Node node) {

            Node newRoot = node.left;

            node.left = newRoot.right;

            newRoot.right = node;

            return newRoot;
        }


        // ========================================
        // Left Rotation
        // ========================================
        private Node rotateLeft(Node node) {

            Node newRoot = node.right;

            node.right = newRoot.left;

            newRoot.left = node;

            return newRoot;
        }


        // ========================================
        // Splay Operation
        // ========================================
        private Node splay(
            Node root,
            int value) {

            // Empty tree or value already at root
            if (root == null
                    || root.value == value) {

                return root;
            }


            // ====================================
            // LEFT SUBTREE
            // ====================================
            if (value < root.value) {

                if (root.left == null) {

                    return root;
                }


                // Zig-Zig
                if (value < root.left.value) {

                    root.left.left =
                        splay(
                            root.left.left,
                            value
                        );

                    root =
                        rotateRight(root);
                }


                // Zig-Zag
                else if (value > root.left.value) {

                    root.left.right =
                        splay(
                            root.left.right,
                            value
                        );

                    if (root.left.right != null) {

                        root.left =
                            rotateLeft(root.left);
                    }
                }


                if (root.left == null) {

                    return root;
                }

                return rotateRight(root);
            }


            // ====================================
            // RIGHT SUBTREE
            // ====================================
            else {

                if (root.right == null) {

                    return root;
                }


                // Zag-Zag
                if (value > root.right.value) {

                    root.right.right =
                        splay(
                            root.right.right,
                            value
                        );

                    root =
                        rotateLeft(root);
                }


                // Zag-Zig
                else if (value < root.right.value) {

                    root.right.left =
                        splay(
                            root.right.left,
                            value
                        );

                    if (root.right.left != null) {

                        root.right =
                            rotateRight(root.right);
                    }
                }


                if (root.right == null) {

                    return root;
                }

                return rotateLeft(root);
            }
        }


        // ========================================
        // Insert
        // ========================================
        public void insert(int value) {

            if (root == null) {

                root = new Node(value);

                return;
            }

            // Splay value
            root = splay(root, value);

            // Value already exists
            if (root.value == value) {

                return;
            }

            Node newNode =
                new Node(value);


            // Value smaller than root
            if (value < root.value) {

                newNode.right = root;

                newNode.left = root.left;

                root.left = null;
            }


            // Value larger than root
            else {

                newNode.left = root;

                newNode.right = root.right;

                root.right = null;
            }

            // New node becomes root
            root = newNode;
        }


        // ========================================
        // Search
        // ========================================
        public boolean search(int value) {

            if (root == null) {

                return false;
            }

            // Move searched value towards root
            root = splay(root, value);

            return root.value == value;
        }
    }


    // ============================================
    // MAIN METHOD
    // GROUP + INDIVIDUAL
    // ============================================
    public static void main(String[] args) {


        // ========================================
        // GROUP TASK
        // ========================================

        System.out.println(
            greedyEAROP(costMatrix)
        );

        System.out.println(
            dynamicProgrammingEAROP(costMatrix)
        );

        System.out.println(
            backtrackingEAROP(costMatrix)
        );

        System.out.println(
            divideAndConquerEAROP(costMatrix)
        );


        // ========================================
        // GROUP TASK
        // SORTING AND SEARCHING
        // ========================================

        int[] arr = {
            8, 3, 5, 1, 9, 2
        };

        insertionSort(arr);

        System.out.println(
            "Sorted Emergency Response Times: "
            + Arrays.toString(arr)
        );

        System.out.println(
            "Binary Search (Response Time 5 found at index): "
            + binarySearch(arr, 5)
        );


        // ========================================
        // INDIVIDUAL TASK
        // MIN-HEAP
        // ========================================

        MinHeap heap = new MinHeap();

        heap.insert(10);
        heap.insert(3);
        heap.insert(15);

        System.out.println(
            "Min-Heap Extract Minimum Priority Value: "
            + heap.extractMin()
        );


        // ========================================
        // INDIVIDUAL TASK
        // SPLAY TREE
        // ========================================

        SplayTree tree = new SplayTree();

        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        System.out.println(
            "Splay Tree Search (Emergency Case 10 found): "
            + tree.search(10)
        );
    }
}
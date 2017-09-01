# Simple Routing Service
It is able to answer two type of queries:
* **Path**. Find shortest path from station A to station B. Syntax is *"path A -> B"*
* **Nearby stations**. Find stations reachable from A within 60. Syntax is *"near A, 60"*.

It is based on Dijkstra single-source shortest-path algorithm, which asymptotically consumes O(E*logV) time for each query.

## Execution
`RoutingService.findPath(InputStream taskDescription, OutputStream outputStream)` is an enrypoint. Below is an example of task 
description:
<pre>
8
A -> B: 240
A -> C: 70
A -> D: 120
C -> B: 60
D -> E: 480
C -> E: 240
B -> E: 210
E -> A: 300
path A -> B
near A, 130
</pre>
and output:
<pre>
A -> C -> B: 130
C: 70, D: 120, B: 130
</pre>
For more examples take a look on `RoutingTest`.

## Workloads
There are 3 random workloads located in ./test/resources. Number in the name of the file is percent of edges (100% - when every node connected to every node).
* test10.txt - sparse graph with 21 000 edges
* test30.txt - sparse graph with 61 000 edges
* test90.txt - dense graph with 190 000 edges

Each workload contains 5 path + 3 nearby queries. Each node name is a name of Berlin U-bahn or S-bahn station. The whole list of Berlin U-bahn or S-bahn stations
can be found here ./test/resources.

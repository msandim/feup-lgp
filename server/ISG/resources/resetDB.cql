// Delete all the nodes:
MATCH (n) DETACH
DELETE n;

CREATE (x:AlgorithmParameters {alpha: 0.333, beta: 0.333, gamma: 0.333, numberOfProducts: 10, numberOfQuestions: 3});
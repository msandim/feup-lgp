// Delete all the nodes:
MATCH (n) DETACH
DELETE n;

CREATE (x:AlgorithmParameters {alpha: 0.333, beta: 0.333, gamma: 0.333, numberOfProducts: 10, numberOfQuestions: 3})

// Create categories:
CREATE (c1:Category {code: 'tvs', name: 'Televisions'})
CREATE (c2:Category {code: 'dws', name: 'Dish Washers'})

// ** Create products **
CREATE (c1)-[:HAS_PRODUCTS]->(tv1:Product {name: 'TV1', ean: '001', price: "49.5"})
CREATE (c1)-[:HAS_PRODUCTS]->(tv2:Product {name: 'TV2', ean: '002', price: "120.5"})
CREATE (c1)-[:HAS_PRODUCTS]->(tv3:Product {name: 'TV3', ean: '003', price: "50.0"})
CREATE (c2)-[:HAS_PRODUCTS]->(dw1:Product {name: 'DW1', ean: '004', price: "99.5"})
CREATE (c2)-[:HAS_PRODUCTS]->(dw2:Product {name: 'DW2', ean: '005', price: "128.5"})
CREATE (c2)-[:HAS_PRODUCTS]->(dw3:Product {name: 'DW3', ean: '006', price: "10.0"})

// Create attributes: (TV)
CREATE (at1: Attribute {name: 'Width (cm)', type: 'numeric'})
CREATE (at2: Attribute {name: 'Height (cm)', type: 'numeric'})
CREATE (at3: Attribute {name: 'Quality', type: 'categorical'})

// (TV 1)
CREATE (tv1)-[:VALUES {value: '45'}]->(at1)
CREATE (tv1)-[:VALUES {value: '120'}]->(at2)
CREATE (tv1)-[:VALUES {value: '720p'}]->(at3)
// (TV 2)
CREATE (tv2)-[:VALUES {value: '80'}]->(at1)
CREATE (tv2)-[:VALUES {value: '10'}]->(at2)
CREATE (tv2)-[:VALUES {value: '1080p'}]->(at3)
// (TV 3)
CREATE (tv3)-[:VALUES {value: '120'}]->(at1)
CREATE (tv3)-[:VALUES {value: '5'}]->(at2)
CREATE (tv3)-[:VALUES {value: '1440p'}]->(at3)

// ** Create questions: **
CREATE (c1)-[:HAS_QUESTIONS]->(q1:Question {code: 'q1', text: 'Tamanho da sala?', numberOfTimesChosen: 10})
CREATE (c1)-[:HAS_QUESTIONS]->(q2:Question {code: 'q2', text: 'Numero de crianças?', numberOfTimesChosen: 4})
CREATE (c1)-[:HAS_QUESTIONS]->(q3:Question {code: 'q3', text: 'É casado(a)?', numberOfTimesChosen: 3})

// (Q1)
CREATE (q1)-[:HAS]->(a11:Answer {code: '1', text: 'Pequena', numberOfTimesChosen: 8})
CREATE (q1)-[:HAS]->(a12:Answer {code: '2', text: 'Grande', numberOfTimesChosen: 2})
CREATE (a11)-[:INFLUENCES {operator: '<', value: '60', score: 0.5}]->(at1)
CREATE (a11)-[:INFLUENCES {operator: '=', value: '720p', score: 0.2}]->(at3)
CREATE (a12)-[:INFLUENCES {operator: '!=', value: '1080p', score: 0.3}]->(at3)
// (Q2)
CREATE (q2)-[:HAS]->(a21:Answer {code: '1', text: 'Nenhuma', numberOfTimesChosen: 2})
CREATE (q2)-[:HAS]->(a22:Answer {code: '2', text: '1 ou Mais', numberOfTimesChosen: 2})
CREATE (a21)-[:INFLUENCES {operator: '>=', value: '60', score: 0.1}]->(at1)
CREATE (a22)-[:INFLUENCES {operator: '>', value: '160', score: 0.2}]->(at2)
// (Q3)
CREATE (q3)-[:HAS]->(a31:Answer {code: '1', text: 'Sim', numberOfTimesChosen: 3})
CREATE (q3)-[:HAS]->(a32:Answer {code: '2', text: 'Não', numberOfTimesChosen: 0})
CREATE (a31)-[:INFLUENCES {operator: '=', value: '1440p', score: 0.8}]->(at3)
CREATE (a32)-[:INFLUENCES {operator: '>', value: '70', score: 0.3}]->(at1)

// Interconnect questions
CREATE (q1)-[:CONNECTS {varianceGainMean: 1.3, numberOfTimesChosen: 2, numberOfTimesGoodFeedback: 1}]->(q2)
CREATE (q1)-[:CONNECTS {varianceGainMean: 0.6, numberOfTimesChosen: 4, numberOfTimesGoodFeedback: 1}]->(q3)
CREATE (q2)-[:CONNECTS {varianceGainMean: 1.7, numberOfTimesChosen: 1, numberOfTimesGoodFeedback: 1}]->(q1)
CREATE (q2)-[:CONNECTS {varianceGainMean: 0.9, numberOfTimesChosen: 4, numberOfTimesGoodFeedback: 1}]->(q3)
CREATE (q3)-[:CONNECTS {varianceGainMean: 1.5, numberOfTimesChosen: 2, numberOfTimesGoodFeedback: 1}]->(q1)
CREATE (q3)-[:CONNECTS {varianceGainMean: 1.3, numberOfTimesChosen: 2, numberOfTimesGoodFeedback: 1}]->(q2);
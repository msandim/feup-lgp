// Delete all the nodes:
MATCH (n) DETACH
DELETE n;

// Restriction: every Attribute node is only connected to products of the same category:

CREATE (x:AlgorithmParameters {alfa: 0.333, beta: 0.333, gamma: 0.333})

// Create categories:
CREATE (c1:Category {code: 'tvs', name: 'Televisions'})
CREATE (c2:Category {code: 'dws', name: 'Dish Washers'})

// ** Create products **
CREATE (c1)-[:HAS_PRODUCTS]->(tv1:Product {name: 'TV1', EAN: '001', price: 49.5})
CREATE (c1)-[:HAS_PRODUCTS]->(tv2:Product {name: 'TV2', EAN: '002', price: 120.5})
CREATE (c1)-[:HAS_PRODUCTS]->(tv3:Product {name: 'TV3', EAN: '003', price: 50.0})
CREATE (c2)-[:HAS_PRODUCTS]->(dw1:Product {name: 'DW1', EAN: '004', price: 99.5})
CREATE (c2)-[:HAS_PRODUCTS]->(dw2:Product {name: 'DW2', EAN: '005', price: 128.5})
CREATE (c2)-[:HAS_PRODUCTS]->(dw3:Product {name: 'DW3', EAN: '006', price: 10.0})

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
CREATE (c1)-[:HAS_QUESTIONS]->(q1:Question {code: 'q1', text: 'Tamanho da sala?'})
CREATE (c1)-[:HAS_QUESTIONS]->(q2:Question {code: 'q2', text: 'Numero de crianças?'})

// (Q1)
CREATE (q1)-[:HAS]->(a11:Answer {code: '1', text: 'Pequena', frequency: 0.45})
CREATE (q1)-[:HAS]->(a12:Answer {code: '2', text: 'Grande', frequency: 0.2})
CREATE (a11)-[:INFLUENCES {operator: '<', value: '60', score: 0.5}]->(at1)
CREATE (a11)-[:INFLUENCES {operator: '=', value: '720p', score: 0.2}]->(at3)
CREATE (a12)-[:INFLUENCES {operator: '!=', value: '1080p', score: 0.3}]->(at3)
// (Q2)
CREATE (q2)-[:HAS]->(a21:Answer {code: '1', text: 'Nenhuma', frequency: 0.3})
CREATE (q2)-[:HAS]->(a22:Answer {code: '2', text: '1 ou Mais', frequency: 0.7})
CREATE (a21)-[:INFLUENCES {operator: '>=', value: '60', score: 0.1}]->(at1)
CREATE (a22)-[:INFLUENCES {operator: '>', value: '160', score: 0.2}]->(at2)

// Interconnect questions
CREATE (q1)-[:CONNECTS {varianceGain: 1.3, goodSequenceRatio: 0.8}]->(q2)
CREATE (q2)-[:CONNECTS {varianceGain: 1.7, goodSequenceRatio: 0.1}]->(q1)
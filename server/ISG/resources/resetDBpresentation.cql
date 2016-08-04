// Delete all the nodes:
MATCH (n) DETACH
DELETE n;

CREATE (x:AlgorithmParameters {alpha: 0.333, beta: 0.333, gamma: 0.333, numberOfProducts: 5, numberOfQuestions: 3})

// Create categories:
CREATE (c1:Category {code: 'tvs', name: 'Televisões'})
CREATE (c2:Category {code: 'pts', name: 'Portáteis'})
CREATE (c3:Category {code: 'tlms', name: 'Telemóveis'})

// ** Create products **
CREATE (c1)-[:HAS_PRODUCTS]->(tv1:Product {name: 'TV LED Smart TV Excelente', ean: '001', price: "1499.99"})
CREATE (c1)-[:HAS_PRODUCTS]->(tv2:Product {name: 'TV LED Smart TV Mediana', ean: '002', price: "999.99"})
CREATE (c1)-[:HAS_PRODUCTS]->(tv3:Product {name: 'TV LED Som&Imagem Excelentes', ean: '003', price: "1399.99"})
CREATE (c1)-[:HAS_PRODUCTS]->(tv4:Product {name: 'TV LED Som&Imagem Medianos', ean: '004', price: "649.99"})
CREATE (c1)-[:HAS_PRODUCTS]->(tv5:Product {name: 'TV LCD Som Excelente', ean: '005', price: "649.99"})
CREATE (c1)-[:HAS_PRODUCTS]->(tv6:Product {name: 'TV LCD Som Mediano', ean: '006', price: "399.99"})

// Create attributes: (TV)
CREATE (atSom: Attribute {name: 'Som', type: 'categorical'})
CREATE (atIm: Attribute {name: 'Imagem', type: 'categorical'})
CREATE (atTam: Attribute {name: 'Tamanho', type: 'categorical'})
CREATE (atSmart: Attribute {name: 'SmartTV', type: 'categorical'})

// (TV 1)
CREATE (tv1)-[:VALUES {value: 'Bom'}]->(atSom)
CREATE (tv1)-[:VALUES {value: 'Bom'}]->(atIm)
CREATE (tv1)-[:VALUES {value: 'Grande'}]->(atTam)
CREATE (tv1)-[:VALUES {value: 'Sim'}]->(atSmart)
// (TV 2)
CREATE (tv2)-[:VALUES {value: 'Grande'}]->(atTam)
CREATE (tv2)-[:VALUES {value: 'Sim'}]->(atSmart)
// (TV 3)
CREATE (tv3)-[:VALUES {value: 'Bom'}]->(atSom)
CREATE (tv3)-[:VALUES {value: 'Bom'}]->(atIm)
CREATE (tv3)-[:VALUES {value: 'Grande'}]->(atTam)
// (TV 4)
CREATE (tv4)-[:VALUES {value: 'Grande'}]->(atTam)
// (TV 5)
CREATE (tv5)-[:VALUES {value: 'Bom'}]->(atSom)
CREATE (tv5)-[:VALUES {value: 'Pequena'}]->(atTam)
// (TV 6)
CREATE (tv5)-[:VALUES {value: 'Pequena'}]->(atTam)

// ** Create questions: **
CREATE (c1)-[:HAS_QUESTIONS]->(q1:Question {code: 'q1', text: 'Que tipo de filmes gosta de ver?', numberOfTimesChosen: 0})
CREATE (c1)-[:HAS_QUESTIONS]->(q2:Question {code: 'q2', text: 'Qual a divisão da casa onde será instalado o produto?', numberOfTimesChosen: 0})
CREATE (c1)-[:HAS_QUESTIONS]->(q3:Question {code: 'q3', text: 'Usa serviços de streaming de video/música?', numberOfTimesChosen: 0})

// (Q1)
CREATE (q1)-[:HAS]->(a11:Answer {code: '1', text: 'Acção', numberOfTimesChosen: 0})
CREATE (q1)-[:HAS]->(a12:Answer {code: '2', text: 'Ficção Cientifica', numberOfTimesChosen: 0})
CREATE (q1)-[:HAS]->(a13:Answer {code: '3', text: 'Musical', numberOfTimesChosen: 0})
CREATE (q1)-[:HAS]->(a14:Answer {code: '4', text: 'Outro', numberOfTimesChosen: 0})
CREATE (a11)-[:INFLUENCES {operator: '=', value: 'Bom', score: 0.6}]->(atSom)
CREATE (a11)-[:INFLUENCES {operator: '=', value: 'Bom', score: 0.6}]->(atIm)
CREATE (a12)-[:INFLUENCES {operator: '=', value: 'Bom', score: 0.6}]->(atIm)
CREATE (a13)-[:INFLUENCES {operator: '=', value: 'Bom', score: 0.4}]->(atSom)
// (Q2)
CREATE (q2)-[:HAS]->(a21:Answer {code: '1', text: 'Sala', numberOfTimesChosen: 0})
CREATE (q2)-[:HAS]->(a22:Answer {code: '2', text: 'Outro', numberOfTimesChosen: 0})
CREATE (a21)-[:INFLUENCES {operator: '=', value: 'Grande', score: 0.8}]->(atTam)
// (Q3)
CREATE (q3)-[:HAS]->(a31:Answer {code: '1', text: 'Sim', numberOfTimesChosen: 3})
CREATE (q3)-[:HAS]->(a32:Answer {code: '2', text: 'Não', numberOfTimesChosen: 0})
CREATE (a31)-[:INFLUENCES {operator: '=', value: 'Sim', score: 0.5}]->(atSmart)

// Interconnect questions
CREATE (q1)-[:CONNECTS {varianceGainMean: 0.0, numberOfTimesChosen: 0, numberOfTimesGoodFeedback: 0}]->(q2)
CREATE (q1)-[:CONNECTS {varianceGainMean: 0.0, numberOfTimesChosen: 0, numberOfTimesGoodFeedback: 0}]->(q3)
CREATE (q2)-[:CONNECTS {varianceGainMean: 0.0, numberOfTimesChosen: 0, numberOfTimesGoodFeedback: 0}]->(q1)
CREATE (q2)-[:CONNECTS {varianceGainMean: 0.0, numberOfTimesChosen: 0, numberOfTimesGoodFeedback: 0}]->(q3)
CREATE (q3)-[:CONNECTS {varianceGainMean: 0.0, numberOfTimesChosen: 0, numberOfTimesGoodFeedback: 0}]->(q1)
CREATE (q3)-[:CONNECTS {varianceGainMean: 0.0, numberOfTimesChosen: 0, numberOfTimesGoodFeedback: 0}]->(q2);
package neo4j.services;

import neo4j.Neo4jSessionFactory;
import neo4j.models.edges.AnswerAttribute;
import neo4j.models.edges.ProductAttribute;
import neo4j.models.nodes.Answer;
import neo4j.models.nodes.Attribute;
import neo4j.models.nodes.Product;
import neo4j.services.utils.GenericService;
import scala.Console;

import java.util.*;

/**
 * Created by Lycantropus on 17-04-2016.
 */
public class ProductService extends GenericService<Product>
{
    @Override
    public Class<Product> getEntityType() {
        return Product.class;
    }

    public Long getNumberOfProductsByCategory(String code)
    {
        String query = new StringBuilder("MATCH (n:Product)<-[:HAS_PRODUCTS]-(c:Category) WHERE c.code = \'")
                .append(code)
                .append("\' RETURN count(n)")
                .toString();
        Iterable<Long> valor = Neo4jSessionFactory.getInstance().getNeo4jSession().query(Long.class, query, Collections.EMPTY_MAP);

        // If the category doesn't have products, the result is 0:
        if (!valor.iterator().hasNext())
            return (long) 0;

        return valor.iterator().next();
    }

    public List<Product> findByCategoryCode(String code)
    {
        String query = new StringBuilder("MATCH (n:Product)<-[:HAS_PRODUCTS]-(c:Category) WHERE c.code = \'")
                .append(code)
                .append("\' RETURN n")
                .toString();
        Iterable<Product> productsIterator = Neo4jSessionFactory.getInstance().getNeo4jSession().query(Product.class, query, Collections.EMPTY_MAP);

        List<Product> products = new ArrayList<>();
        productsIterator.forEach(products::add);

        return products;
    }

    public Map<Product,Float> initializeProductScores(String category)
    {
        // MATCH (c:Category)-[:HAS_PRODUCTS]->(p:Product) WHERE c.code = 'tvs' RETURN p
        String query = new StringBuilder("MATCH (c:Category)-[:HAS_PRODUCTS]->(p:Product) WHERE c.code = \'")
                .append(category)
                .append("\' RETURN p")
                .toString();
        Iterable<Product> products = Neo4jSessionFactory.getInstance().getNeo4jSession().query(getEntityType(), query, Collections.EMPTY_MAP);

        // Initialize the map:
        Map<Product,Float> returnMap = new HashMap<>();
        products.forEach(product -> returnMap.put(product, (float) 0.0));

        return returnMap;
    }

    public boolean updateScores(String questionCode, String answerCode, Map<Product, Float> productScores)
    {
        // MATCH (q:Question)-[:HAS]->(an:Answer)-[i:INFLUENCES]->(at:Attribute)<-[v:VALUES]-(p:Product) WHERE q.code = 'q1' AND an.code = '1' RETURN at,v,p
        // For this answer, see the affected products:
        String query = new StringBuilder("MATCH (q:Question)-[:HAS]->(an:Answer) WHERE q.code = '")
                .append(questionCode)
                .append("' AND an.code = '")
                .append(answerCode)
                .append("' OPTIONAL MATCH (an)-[i:INFLUENCES]->(at:Attribute)")
                .append(" OPTIONAL MATCH (at)<-[v:VALUES]-(p:Product)")
                .append(" RETURN an,i,at,v,p")
                .toString();

        Iterator<Answer> answerIterator = Neo4jSessionFactory.getInstance().getNeo4jSession().query(Answer.class, query, Collections.EMPTY_MAP).iterator();

        // If no answer was found, then the questionCode and/or answerCode are invalid:
        if (!answerIterator.hasNext())
            return false;

        // Get the answer queried:
        Answer answer = answerIterator.next();

        // For each attribute this answer enhances:
        for(AnswerAttribute answerAttribute: answer.getAttributes())
        {
            Attribute attribute = answerAttribute.getAttribute();

            String attributeType = attribute.getType();
            String conditionOperator = answerAttribute.getOperator();
            String conditionValue = answerAttribute.getValue();
            Float conditionScore = answerAttribute.getScore();

            // For each product that has a value for this attribute:
            for(ProductAttribute productAttribute: attribute.getProducts())
            {
                Product product = productAttribute.getProduct();
                String productValue = productAttribute.getValue();

                // If this product is selected by the condition implied in the relationship between
                // the Answer and the Attribute, let's update its score:
                if (isProductSelected(attributeType, conditionOperator, conditionValue, productValue))
                {
                    Float newScore = productScores.get(product) + conditionScore;
                    productScores.put(product, newScore);
                }
            }
        }

        return true;
    }

    public int getNumProductsAffected(Answer answer)
    {
        Set<Product> totalProducts = new HashSet<>();

        for(AnswerAttribute answerAttribute: answer.getAttributes())
        {
            Attribute attribute = answerAttribute.getAttribute();

            String attributeType = attribute.getType();
            String conditionOperator = answerAttribute.getOperator();
            String conditionValue = answerAttribute.getValue();

            // For each product that has a value for this attribute:
            for (ProductAttribute productAttribute : attribute.getProducts())
            {
                Product product = productAttribute.getProduct();
                String productValue = productAttribute.getValue();

                // If this product is selected by the condition implied in the relationship between
                // the Answer and the Attribute, let's add to our set (which filters repeated ones):
                if (isProductSelected(attributeType, conditionOperator, conditionValue, productValue))
                    totalProducts.add(product);
            }
        }

        return totalProducts.size();
    }

    public Float getMediumScore(Answer answer)
    {
        if (answer.getAttributes().isEmpty())
            return (float) 0.0;

        Float mediumScore = (float) 0.0;
        for(AnswerAttribute answerAttribute: answer.getAttributes())
            mediumScore += answerAttribute.getScore();

        return mediumScore / answer.getAttributes().size();
    }

    private boolean isProductSelected(String attributeType, String conditionOperator, String conditionValue, String productValue)
    {
        if (attributeType.equals(Attribute.Type.CATEGORICAL))
        {
            switch (conditionOperator)
            {
                case AnswerAttribute.Operators.EQUAL:
                    return productValue.equals(conditionValue);
                case AnswerAttribute.Operators.NOT_EQUAL:
                    return !productValue.equals(conditionValue);
                default:
                    return false;
            }
        }
        else // Attribute.Type.NUMERICAL
        {
            Float productValueNumber = Float.parseFloat(productValue);
            Float conditionValueNumber = Float.parseFloat(conditionValue);

            switch (conditionOperator)
            {
                // Note: in the EQUAL and NOT_EQUAL operator, the String comparison was done, since this type of Float is reliable:
                case AnswerAttribute.Operators.EQUAL:
                    return productValue.equals(conditionOperator);
                case AnswerAttribute.Operators.NOT_EQUAL:
                    return !productValue.equals(conditionValue);
                case AnswerAttribute.Operators.ABOVE:
                    return productValueNumber > conditionValueNumber;
                case AnswerAttribute.Operators.ABOVE_EQUAL:
                    return productValueNumber >= conditionValueNumber;
                case AnswerAttribute.Operators.LESS:
                    return productValueNumber < conditionValueNumber;
                case AnswerAttribute.Operators.LESS_EQUAL:
                    return productValueNumber <= conditionValueNumber;
                default:
                    return false;
            }
        }
    }

    public Product findByEan(String ean)
    {
        String query = new StringBuilder("MATCH (p: Product) where p.ean = \'").append(ean).append("\' return p").toString();

        Iterator<Product> iterator =
                Neo4jSessionFactory.getInstance().getNeo4jSession().query(getEntityType(), query, Collections.EMPTY_MAP).iterator();

        if (iterator.hasNext())
            return iterator.next();
        else
            return null;
    }

    public void deleteAllProductsByCategoryCode(String code)
    {
        String query = new StringBuilder("MATCH (c:Category{code: \'" + code + "\'})-[]->(p:Product) OPTIONAL MATCH (p)-[]->(a:Attribute) detach delete p, a").toString();
        Neo4jSessionFactory.getInstance().getNeo4jSession().query(query, Collections.EMPTY_MAP);

        // Delete leftover attributes:
        new AttributeService().deleteNotConnectedAttributes();
    }
}

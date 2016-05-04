package neo4j.models.edges;

import neo4j.models.Entity;
import neo4j.models.nodes.*;
import org.neo4j.ogm.annotation.*;

/**
 * Created by Miguel on 27-04-2016.
 */
@RelationshipEntity(type = "INFLUENCES")
public class AnswerAttribute extends Entity {
    @StartNode
    private Answer answer;

    @EndNode
    private Attribute attribute;

    private String operator;

    private String value;

    private Float score;

    public interface Operators {
        String LESS = "<";
        String LESS_EQUAL = "<=";
        String ABOVE = ">";
        String ABOVE_EQUAL = ">=";
        String EQUAL = "=";
        String NOT_EQUAL = "!=";

        static boolean isValid(String op) {
            if(op.equals(LESS) || op.equals(LESS_EQUAL) || op.equals(ABOVE) || op.equals(ABOVE_EQUAL) || op.equals(EQUAL) || op.equals(NOT_EQUAL))
                return true;
            else return false;
        }
    }

    public AnswerAttribute() { }

    public AnswerAttribute(Answer answer, Attribute attribute, String operator, String value, Float score) {
        this.answer = answer;
        this.attribute = attribute;
        this.operator = operator;
        this.value = value;
        this.score = score;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Float getScore() { return score; }

    public void setScore(Float score) { this.score = score; }
}



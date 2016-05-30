package algorithm;

import neo4j.models.nodes.Answer;
import neo4j.models.nodes.Question;
import neo4j.models.nodes.SequenceNode;
import neo4j.models.nodes.StartSequenceNode;
import neo4j.services.SequenceNodeService;
import neo4j.services.StartSequenceNodeService;

import java.util.Map;

/**
 * Created by Miguel on 12-05-2016.
 */
public class SequenceBuilder
{
    public static void build(Map<Question, Answer> sequence, Integer feedback)
    {
        // Initiate Services:
        StartSequenceNodeService startSequenceNodeService = new StartSequenceNodeService();
        SequenceNodeService sequenceNodeService = new SequenceNodeService();

        // Create sequence:
        StartSequenceNode startNode = null;
        SequenceNode lastSequenceNode = null;

        for (Map.Entry<Question, Answer> entry : sequence.entrySet()) {
            Question question = entry.getKey();
            Answer answer = entry.getValue();

            if (startNode == null)
            {
                SequenceNode firstSequenceNode = new SequenceNode(question, answer);
                startNode = new StartSequenceNode(firstSequenceNode, feedback);

                // Save the start node pointing to the first element of the sequence:
                startNode = startSequenceNodeService.createOrUpdate(startNode, 2);
                lastSequenceNode = startNode.getNextNode();
            }
            else
            {
                SequenceNode nextSequenceNode = new SequenceNode(question, answer);
                lastSequenceNode.setNextNode(nextSequenceNode);

                // Update the last node (pointing to the new one):
                lastSequenceNode = sequenceNodeService.createOrUpdate(lastSequenceNode, 2);
                lastSequenceNode = lastSequenceNode.getNextNode();
            }
        }
    }
}

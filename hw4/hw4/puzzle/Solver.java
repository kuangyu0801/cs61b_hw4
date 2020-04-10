package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Solver {
    /** Constructor which solves the puzzle, computing
     * everything necessary for moves() and solution() to
     * not have to solve the problem again. Solves the
     * puzzle using the A* algorithm. Assumes a solution exists.*/
    private int solutionMoves;
    private ArrayList<WorldState> solutionWorldState;
    private SearchNode headNode;
    private SearchNode goalNode;
    private WorldState startState;
    private MinPQ<SearchNode> nodePQ;
    private int enqueueNum; // used for debugging
    private static final String DEBUGTAG = "[DBG]";

    /**
     * Constructor
     * */
    public Solver(WorldState startState) {
        solutionWorldState = new ArrayList();
        Comparator mySearchNodeComparator = new SearchNodeComparator();
        nodePQ = new MinPQ<SearchNode>(mySearchNodeComparator);
        this.startState = startState;
        solutionMoves = 0;
        enqueueNum = 0;
        headNode = new SearchNode(startState, 0, null, 0);
        nodePQ.insert(headNode);
        solve();
        //System.out.println(DEBUGTAG + "Total enqueued nodes:" + enqueueNum);
    }

    private void solutionHelper() {
        solutionWorldState.add(goalNode.nodeState);
        SearchNode parentNode = goalNode.prevNode;
        while (!parentNode.nodeState.equals(startState)) {
            goalNode = goalNode.prevNode;
            parentNode = goalNode.prevNode;
            solutionWorldState.add(goalNode.nodeState);
        }
        solutionWorldState.add(startState);
        Collections.reverse(solutionWorldState);
    }

    private int countMoves(SearchNode inNode) {
        int countMoves = 0;
        if (inNode.nodeState.equals(startState)) {
            return countMoves;
        }

        countMoves = 1;
        SearchNode parentNode = inNode.prevNode;
        while (!parentNode.nodeState.equals(startState)) {
            inNode = inNode.prevNode;
            parentNode = inNode.prevNode;
            countMoves += 1;
        }

        return countMoves;
    }

    private void solve() {
        if (startState.isGoal()) {
            solutionWorldState.add(startState);
            return;
        }
        while (nodePQ.size() != 0) {
            /**
             * Remove the search node with minimum priority.
             * If it is the goal node, then we’re done.
             * */
            SearchNode minNode = nodePQ.delMin();
            WorldState curState =  minNode.nodeState;
            int curMoves = minNode.nodeMoves;
            //System.out.println("[Dequeue] (" + curState + ", " + minNode.nodeMoves + ")");
            //System.out.println("[Current Moves] " + solutionMoves);

            // if this is a startState, then it doesn't has parent and grandparet
            // thus setting it to itself
            //WorldState preState = ;
            boolean isStart = curState.equals(startState);
            WorldState grandParentState = (isStart) ? curState : minNode.prevNode.nodeState;
            if (curState.isGoal()) {
                goalNode = minNode;
                solutionMoves = countMoves(goalNode);
                solutionHelper();
                return;
            }

            Iterable<WorldState> curNeighbors = curState.neighbors();

            /**
             *  for each neighbor of X’s world state,
             *  create a new search node that obeys the description above and
             *  insert it into the priority queue.
             * */
            for (WorldState ws: curNeighbors) {
                /** To avoid enqueuing its own grandparent*/
                boolean isNoDuplicate = !(ws.equals(grandParentState));
                if (isNoDuplicate) {
                    int priority = curMoves + ws.estimatedDistanceToGoal();
                    nodePQ.insert(new SearchNode(ws, priority, minNode, curMoves + 1));
                    enqueueNum += 1;
                }
            }
        }
    }

    /**
     * declaring a Comparator for minPQ
     * */
    private static class SearchNodeComparator implements Comparator<SearchNode> {

        private SearchNodeComparator() {

        }

        @Override
        public int compare(SearchNode nodeA, SearchNode nodeB) {
            return nodeA.compareTo(nodeB);
        }
    }

    /**
     * making SearchNode comparable, is another way to avoid declaring Comparator
     * */
    private class SearchNode implements Comparable<SearchNode> {
        private WorldState nodeState;
        private int nodeMoves;
        private SearchNode prevNode;
        private int priority;

        private SearchNode(WorldState nodeState, int priority, SearchNode prevNode, int nodeMoves) {
            this.nodeState = nodeState;
            this.nodeMoves = nodeMoves;
            this.prevNode = prevNode;
            this.priority = priority;
        }

        @Override
        public int compareTo(SearchNode otherNode) {
            if (this.priority > otherNode.priority) {
                return 1;
            } else if (this.priority < otherNode.priority) {
                return -1;
            }
            return 0;
        }
    }

    /** Returns the minimum number of moves to solve the puzzle starting
     *  at the initial WorldState.*/
    public int moves() {
        //printSolution();
        return solutionMoves;
    }

    /** Returns a sequence of WorldStates from the initial WorldState
     *  to the solution.*/
    public Iterable<WorldState> solution() {
        //printSolution();
        return solutionWorldState;
    }

    private void printSolution() {
        System.out.println("[Solution]: ");
        for (WorldState ws: solutionWorldState) {
            System.out.print(ws + ", ");
        }
        System.out.println();
    }
}

package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedBlackTree {

    private static final Logger logger = LoggerFactory.getLogger(RedBlackTree.class);

    private static final int BLACK = 0;
    private static final int RED = 1;

    private static final String BLACK_STRING = "BLACK";
    private static final String RED_STRING = "RED";

    private static Node root = null;

    ////////////////////////////////////////////////////////////////////////////////

    private static class Node {

        private final long value;
        private int color = BLACK;

        private Node left = null;
        private Node right = null;
        private Node parent = null;

        private Node(long value) {
            this.value = value;
        }

        private Node() {
            this(-1);
        }

        long getValue() {
            return value;
        }

        String getColor() {
            return color == RED ? RED_STRING : BLACK_STRING;
        }

        void setColor(int color) {
            this.color = color;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void printTree(Node node) {
        if (node == null) {
            return;
        }

        logger.debug("[{}]", node.getValue());

        printTree(node.left);
        printTree(node.right);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public Node findNodeByValue(long goalValue, Node node) {
        if (node == null) {
            return null;
        }

        if (goalValue < node.getValue()) {
            if (node.left != null) {
                return findNodeByValue(goalValue, node.left);
            }
        } else if (goalValue > node.getValue()) {
            if (node.right != null) {
                return findNodeByValue(goalValue, node.right);
            }
        } else {
            return node;
        }

        return null;
    }


    public Node findNodeByNode(Node goal, Node node) {
        if (node == null) {
            return null;
        }

        if (goal.getValue() < node.getValue()) {
            if (node.left != null) {
                return findNodeByNode(goal, node.left);
            }
        } else if (goal.getValue() > node.getValue()) {
            if (node.right != null) {
                return findNodeByNode(goal, node.right);
            }
        } else {
            return node;
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void insertNode(Node node) {
        if (root == null) {
            root = node;
        } else {
            Node parent = root;
            node.setColor(RED);

            while (true) {
                if (parent.getValue() < node.getValue()) {
                    if (parent.right == null) {
                        parent.right = node;
                        node.parent = parent;
                        break;
                    } else {
                        parent = parent.right;
                    }
                }
                else {
                    if (parent.left == null) {
                        parent.left = node;
                        node.parent = parent;
                        break;
                    } else {
                        parent = parent.left;
                    }
                }
            }

            recolorTree(node);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    private void recolorTree(Node node) {
        while (node.parent != null && RED_STRING.equals(node.parent.getColor())) {
            Node next;

            if (node.parent == node.parent.parent.left) {
                next = node.parent.parent.right;

                if (next != null && RED_STRING.equals(next.getColor())) {
                    node.parent.setColor(BLACK);
                    next.setColor(BLACK);
                    node.parent.parent.setColor(RED);
                    node = node.parent.parent;
                    continue;
                }

                if (node == node.parent.right) {
                    node = node.parent;

                    rotateLeft(node);
                }

                node.parent.setColor(BLACK);
                node.parent.parent.setColor(RED);

                rotateRight(node.parent.parent);
            } else {
                next = node.parent.parent.left;

                if (next != null && RED_STRING.equals(next.getColor())) {
                    node.parent.setColor(BLACK);
                    next.setColor(BLACK);
                    node.parent.parent.setColor(RED);
                    node = node.parent.parent;
                    continue;
                }

                if (node == node.parent.left) {
                    node = node.parent;

                    rotateRight(node);
                }

                node.parent.setColor(BLACK);
                node.parent.parent.setColor(RED);

                rotateLeft(node.parent.parent);
            }
            break;
        }

        root.setColor(BLACK);
    }

    ////////////////////////////////////////////////////////////////////////////////

    private void rotateLeft(Node node) {
        if (node.parent == null) {
            Node right = root.right;
            root.right = root.right.left;
            right.left = new Node();
            right.left.parent = root;
            root.parent = right;
            right.left = root;
            right.parent = null;
            root = right;
        } else {
            if (node == node.parent.left) {
                node.parent.left = node.right;
            } else {
                node.parent.right = node.right;
            }

            node.right.parent = node.parent;
            node.parent = node.right;

            if (node.right.left != null) {
                node.right.left.parent = node;
            }

            node.right = node.right.left;
            node.parent.left = node;
        }
    }

    private void rotateRight(Node node) {
        if (node.parent == null) {
            Node left = root.left;
            root.left = root.left.right;
            left.right = new Node();
            left.right.parent = root;
            root.parent = left;
            left.right = root;
            left.parent = null;
            root = left;
        } else {
            if (node == node.parent.left) {
                node.parent.left = node.left;
            } else {
                node.parent.right = node.left;
            }

            node.left.parent = node.parent;
            node.parent = node.left;

            if (node.left.right != null) {
                node.left.right.parent = node;
            }

            node.left = node.left.right;
            node.parent.right = node;
        }
    }

}

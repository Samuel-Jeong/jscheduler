package util;

import job.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RedBlackTree {

    private static final Logger logger = LoggerFactory.getLogger(RedBlackTree.class);

    private static final int BLACK = 0;
    private static final int RED = 1;

    private static Node root = null;
    private int size = 0;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty;

    ////////////////////////////////////////////////////////////////////////////////

    public RedBlackTree() {
        this.notEmpty = lock.newCondition();
    }

    private static class Node {
        private final Job job;
        private long data;
        private int color = BLACK;

        private Node left = null;
        private Node right = null;
        private Node parent = null;

        private Node(Job job, long data) {
            this.job = job;
            this.data = data;
        }

        public Job getJob() {
            return job;
        }

        long getData() {
            return data;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void printTree(Node node) {
        if (node == null) {
            return;
        }

        printTree(node.left);
        logger.info("[{}]", node.getData());
        printTree(node.right);
    }

    public Job take() {
        final ReentrantLock lock = this.lock;
        Node result;

        try {
            lock.lockInterruptibly();

            while ( (result = findMinimum(root)) == null) {
                notEmpty.await();
            }

            deleteNode(result.getData());
        } catch (InterruptedException e) {
            logger.warn("take", e);
            return null;
        } finally {
            lock.unlock();
        }

        return result.getJob();
    }

    public Job poll(long timeout, TimeUnit unit) {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        Node result;

        try {
            lock.lockInterruptibly();

            while ( (result = findMinimum(root)) == null && nanos > 0) {
                nanos = notEmpty.awaitNanos(nanos);
            }

            if (result != null) {
                deleteNode(result.getData());
            }
        } catch (InterruptedException e) {
            logger.warn("poll", e);
            return null;
        } finally {
            lock.unlock();
        }

        if (result == null) {
            return null;
        }

        return result.getJob();
    }

    ////////////////////////////////////////////////////////////////////////////////

    private void rotateRight(Node node) {
        Node parent = node.parent;
        Node leftChild = node.left;

        node.left = leftChild.right;
        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }

        leftChild.right = node;
        node.parent = leftChild;

        replaceParentsChild(parent, node, leftChild);
    }

    private void rotateLeft(Node node) {
        Node parent = node.parent;
        Node rightChild = node.right;

        node.right = rightChild.left;
        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }

        rightChild.left = node;
        node.parent = rightChild;

        replaceParentsChild(parent, node, rightChild);
    }

    private void replaceParentsChild(Node parent, Node oldChild, Node newChild) {
        if (parent == null) {
            root = newChild;
        } else if (parent.left == oldChild) {
            parent.left = newChild;
        } else if (parent.right == oldChild) {
            parent.right = newChild;
        } else {
            throw new IllegalStateException("[RB-TREE] Node is not a child of its parent");
        }

        if (newChild != null) {
            newChild.parent = parent;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public Node searchNode(long key) {
        Node node = root;
        while (node != null) {
            if (key == node.data) {
                return node;
            } else if (key < node.data) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean insertNode(Job job) {
        Node node = root;
        Node parent = null;

        final ReentrantLock lock = this.lock;
        lock.lock();

        long key = job.getPriority();

        try {
            // Traverse the tree to the left or right depending on the key
            while (node != null) {
                parent = node;
                if (key < node.data) {
                    node = node.left;
                } else if (key > node.data) {
                    node = node.right;
                } else {
                    throw new IllegalArgumentException("[RB-TREE] Already contains a node with key " + key);
                }
            }

            // Insert new node
            Node newNode = new Node(job, key);
            newNode.color = RED;
            if (parent == null) {
                root = newNode;
            } else if (key < parent.data) {
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }
            newNode.parent = parent;

            fixRedBlackPropertiesAfterInsert(newNode);

            size++;
            notEmpty.signal();
        } catch (Exception e) {
            return false;
        } finally {
            lock.unlock();
        }

        return true;
    }

    private void fixRedBlackPropertiesAfterInsert(Node node) {
        Node parent = node.parent;

        // Case 1: Parent is null, we've reached the root, the end of the recursion
        if (parent == null) {
            // Uncomment the following line if you want to enforce black roots (rule 2):
            // node.color = BLACK;
            return;
        }

        // Parent is black --> nothing to do
        if (parent.color == BLACK) {
            return;
        }

        // From here on, parent is red
        Node grandparent = parent.parent;

        // Case 2:
        // Not having a grandparent means that parent is the root. If we enforce black roots
        // (rule 2), grandparent will never be null, and the following if-then block can be
        // removed.
        if (grandparent == null) {
            // As this method is only called on red nodes (either on newly inserted ones - or -
            // recursively on red grandparents), all we have to do is to recolor the root black.
            parent.color = BLACK;
            return;
        }

        // Get the uncle (may be null/nil, in which case its color is BLACK)
        Node uncle = getUncle(parent);

        // Case 3: Uncle is red -> recolor parent, grandparent and uncle
        if (uncle != null && uncle.color == RED) {
            parent.color = BLACK;
            grandparent.color = RED;
            uncle.color = BLACK;

            // Call recursively for grandparent, which is now red.
            // It might be root or have a red parent, in which case we need to fix more...
            fixRedBlackPropertiesAfterInsert(grandparent);
        }

        // Parent is left child of grandparent
        else if (parent == grandparent.left) {
            // Case 4a: Uncle is black and node is left->right "inner child" of its grandparent
            if (node == parent.right) {
                rotateLeft(parent);

                // Let "parent" point to the new root node of the rotated sub-tree.
                // It will be recolored in the next step, which we're going to fall-through to.
                parent = node;
            }

            // Case 5a: Uncle is black and node is left->left "outer child" of its grandparent
            rotateRight(grandparent);

            // Recolor original parent and grandparent
            parent.color = BLACK;
            grandparent.color = RED;
        }

        // Parent is right child of grandparent
        else {
            // Case 4b: Uncle is black and node is right->left "inner child" of its grandparent
            if (node == parent.left) {
                rotateRight(parent);

                // Let "parent" point to the new root node of the rotated sub-tree.
                // It will be recolored in the next step, which we're going to fall-through to.
                parent = node;
            }

            // Case 5b: Uncle is black and node is right->right "outer child" of its grandparent
            rotateLeft(grandparent);

            // Recolor original parent and grandparent
            parent.color = BLACK;
            grandparent.color = RED;
        }
    }

    private Node getUncle(Node parent) {
        Node grandparent = parent.parent;
        if (grandparent.left == parent) {
            return grandparent.right;
        } else if (grandparent.right == parent) {
            return grandparent.left;
        } else {
            throw new IllegalStateException("[RB-TREE] Parent is not a child of its grandparent");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void deleteNode(long key) {
        Node node = root;

       /* final ReentrantLock lock = this.lock;
        lock.lock();*/

        try {
            // Find the node to be deleted
            while (node != null && node.data != key) {
                // Traverse the tree to the left or right depending on the key
                if (key < node.data) {
                    node = node.left;
                } else {
                    node = node.right;
                }
            }

            // Node not found?
            if (node == null) {
                return;
            }

            // At this point, "node" is the node to be deleted

            // In this variable, we'll store the node at which we're going to start to fix the R-B
            // properties after deleting a node.
            Node movedUpNode;
            int deletedNodeColor;

            // Node has zero or one child
            if (node.left == null || node.right == null) {
                movedUpNode = deleteNodeWithZeroOrOneChild(node);
                deletedNodeColor = node.color;
            }

            // Node has two children
            else {
                // Find minimum node of right subtree ("inorder successor" of current node)
                Node inOrderSuccessor = findMinimum(node.right);

                // Copy inorder successor's data to current node (keep its color!)
                node.data = inOrderSuccessor.data;

                // Delete inorder successor just as we would delete a node with 0 or 1 child
                movedUpNode = deleteNodeWithZeroOrOneChild(inOrderSuccessor);
                deletedNodeColor = inOrderSuccessor.color;
            }

            if (deletedNodeColor == BLACK) {
                fixRedBlackPropertiesAfterDelete(movedUpNode);

                // Remove the temporary NIL node
                if (movedUpNode.getClass() == NilNode.class) {
                    replaceParentsChild(movedUpNode.parent, movedUpNode, null);
                }
            }

            if (size > 0) {
                size--;
            }
        } catch (Exception e) {
            // ignore
        }
        /*finally {
            lock.unlock();
        }*/
    }

    private Node deleteNodeWithZeroOrOneChild(Node node) {
        // Node has ONLY a left child --> replace by its left child
        if (node.left != null) {
            replaceParentsChild(node.parent, node, node.left);
            return node.left; // moved-up node
        }

        // Node has ONLY a right child --> replace by its right child
        else if (node.right != null) {
            replaceParentsChild(node.parent, node, node.right);
            return node.right; // moved-up node
        }

        // Node has no children -->
        // * node is red --> just remove it
        // * node is black --> replace it by a temporary NIL node (needed to fix the R-B rules)
        else {
            Node newChild = node.color == BLACK ? new NilNode() : null;
            replaceParentsChild(node.parent, node, newChild);
            return newChild;
        }
    }

    private void fixRedBlackPropertiesAfterDelete(Node node) {
        // Case 1: Examined node is root, end of recursion
        if (node == root) {
            // Uncomment the following line if you want to enforce black roots (rule 2):
            // node.color = BLACK;
            return;
        }

        Node sibling = getSibling(node);

        // Case 2: Red sibling
        if (sibling.color == RED) {
            handleRedSibling(node, sibling);
            sibling = getSibling(node); // Get new sibling for fall-through to cases 3-6
        }

        // Cases 3+4: Black sibling with two black children
        if (isBlack(sibling.left) && isBlack(sibling.right)) {
            sibling.color = RED;

            // Case 3: Black sibling with two black children + red parent
            if (node.parent.color == RED) {
                node.parent.color = BLACK;
            }

            // Case 4: Black sibling with two black children + black parent
            else {
                fixRedBlackPropertiesAfterDelete(node.parent);
            }
        }

        // Case 5+6: Black sibling with at least one red child
        else {
            handleBlackSiblingWithAtLeastOneRedChild(node, sibling);
        }
    }

    private Node getSibling(Node node) {
        Node parent = node.parent;
        if (node == parent.left) {
            return parent.right;
        } else if (node == parent.right) {
            return parent.left;
        } else {
            throw new IllegalStateException("[RB-TREE] Parent is not a child of its grandparent");
        }
    }

    private void handleRedSibling(Node node, Node sibling) {
        // Recolor...
        sibling.color = BLACK;
        node.parent.color = RED;

        // ... and rotate
        if (node == node.parent.left) {
            rotateLeft(node.parent);
        } else {
            rotateRight(node.parent);
        }
    }

    private void handleBlackSiblingWithAtLeastOneRedChild(Node node, Node sibling) {
        boolean nodeIsLeftChild = node == node.parent.left;

        // Case 5: Black sibling with at least one red child + "outer nephew" is black
        // --> Recolor sibling and its child, and rotate around sibling
        if (nodeIsLeftChild && isBlack(sibling.right)) {
            sibling.left.color = BLACK;
            sibling.color = RED;
            rotateRight(sibling);
            sibling = node.parent.right;
        } else if (!nodeIsLeftChild && isBlack(sibling.left)) {
            sibling.right.color = BLACK;
            sibling.color = RED;
            rotateLeft(sibling);
            sibling = node.parent.left;
        }

        // Fall-through to case 6...

        // Case 6: Black sibling with at least one red child + "outer nephew" is red
        // --> Recolor sibling + parent + sibling's child, and rotate around parent
        sibling.color = node.parent.color;
        node.parent.color = BLACK;
        if (nodeIsLeftChild) {
            sibling.right.color = BLACK;
            rotateLeft(node.parent);
        } else {
            sibling.left.color = BLACK;
            rotateRight(node.parent);
        }
    }

    private boolean isBlack(Node node) {
        return node == null || node.color == BLACK;
    }

    private static class NilNode extends Node {
        private NilNode() {
            super(null, 0);
        }
    }

    private Node findMinimum(Node node) {
        if (node == null) {
            return null;
        }

        while (node.left != null) {
            node = node.left;
        }

        return node;
    }

    ////////////////////////////////////////////////////////////////////////////////

}

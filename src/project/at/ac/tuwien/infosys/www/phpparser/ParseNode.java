/*
 * ParseNode.java
 *
 * Copyright (C) 2005 Nenad Jovanovic
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License. See the file
 * COPYRIGHT for more information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package at.ac.tuwien.infosys.www.phpparser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An interior or leaf (token) node in the parse tree. Not all methods are meaningful
 * for both interior and token nodes, so they may throw an
 * <code>UnsupportedOperationException</code> in such cases.
 *
 * @author Nenad Jovanovic
 */
public final class ParseNode implements Serializable {
    /**
     * symbol (token or rule) number
     */
    private final int symbol;

    /**
     * name (useful for output)
     */
    private final String name;

    /**
     * name of the parsed file
     */
    private final String fileName;

    /**
     * child nodes
     */
    private final List<ParseNode> children;

    /**
     * parent node; available for all nodes except the root of the parse tree
     */
    private ParseNode parent;

    /**
     * lexeme; available for tokens only (i.e. parse tree leaves)
     */
    private final String lexeme;
    /**
     * line number; available for tokens only (i.e. parse tree leaves)
     */
    private final int lineNumber;

    /**
     * node ID for ID generation
     */
    private final int id;
    /**
     * static helper field for ID generation
     */
    private static int minFreeId = 0;

    /**
     * whether this node is a token node
     */
    private final boolean isToken;

    /**
     * Constructs an interior node.
     *
     * @param symbol   symbol number
     * @param name     node name (useful for output)
     * @param fileName name of the parsed file
     */
    public ParseNode(int symbol, String name, String fileName) {
        this.symbol = symbol;
        this.name = name;
        this.fileName = fileName;
        this.children = new ArrayList<>();
        this.parent = null;
        this.lexeme = null;
        this.lineNumber = -1;
        this.id = ParseNode.minFreeId++;
        this.isToken = false;
    }

    /**
     * Constructs a leaf (token) node.
     *
     * @param symbol     symbol number
     * @param name       node name (useful for output)
     * @param fileName   name of the parsed file
     * @param lexeme     lexeme (the literal string in the parsed file)
     * @param lineNumber the lexeme's line number in the parsed file
     */
    public ParseNode(int symbol, String name, String fileName, String lexeme, int lineNumber) {
        this.symbol = symbol;
        this.name = name;
        this.fileName = fileName;
        this.children = Collections.emptyList();
        this.parent = null;
        this.lexeme = lexeme;
        this.lineNumber = lineNumber;
        this.id = ParseNode.minFreeId++;
        this.isToken = true;
    }

    /**
     * Returns this node's name.
     *
     * @return this node's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the name of the scanned file.
     *
     * @return the name of the scanned file
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Returns this node's symbol number.
     *
     * @return this node's symbol number
     */
    public int getSymbol() {
        return this.symbol;
    }

    /**
     * Returns this node's children.
     *
     * @return this node's children
     */
    public List<ParseNode> getChildren() {
        return this.children;
    }

    /**
     * Returns the number of children.
     *
     * @return the number of children
     */
    public int getNumChildren() {
        return this.children.size();
    }

    /**
     * Returns the child node at the given index (for non-token nodes only).
     *
     * @param index the desired child node's index
     *
     * @return the child node at the given index
     *
     * @throws UnsupportedOperationException if this node is a token node
     */
    public ParseNode getChild(int index) {
        if (this.isToken) {
            throw new UnsupportedOperationException("Call to getChild for token node " + this.name);
        }

        return this.children.get(index);
    }

    /**
     * Returns this node's parent node.
     *
     * @return this node's parent node, <code>null</code> if this node is the root node
     */
    public ParseNode getParent() {
        return this.parent;
    }

    /**
     * Sets this node's parent node.
     *
     * @param parent the parse node that shall become this node's parent
     */
    public void setParent(ParseNode parent) {
        this.parent = parent;
    }

    /**
     * Returns this node's lexeme (for token nodes only).
     *
     * @return this node's lexeme
     *
     * @throws UnsupportedOperationException if this node is not a token node
     */
    public String getLexeme() {
        if (!this.isToken) {
            throw new UnsupportedOperationException();
        }

        return this.lexeme;
    }

    /**
     * Returns this node's line number (for token nodes only).
     * <p/>
     * Note that epsilon nodes have line number -2.
     *
     * @return this node's line number
     *
     * @throws UnsupportedOperationException if this node is not a token node
     */
    public int getLineno() {
        if (!this.isToken) {
            throw new UnsupportedOperationException();
        }

        return this.lineNumber;
    }

    /**
     * Returns this node's line number if it is a token node,
     * and the line number of the leftmost token node reachable from
     * this node otherwise. Note that epsilon nodes have line number -2.
     *
     * @return a reasonable line number
     */
    public int getLinenoLeft() {
        if (this.isToken) {
            return this.lineNumber;
        }

        return this.getChild(0).getLinenoLeft();
    }

    /**
     * Gets the file name and the node's line number.
     *
     * @return the file name and the node's line number.
     */
    public String getLoc() {
        return this.fileName + ":" + getLinenoLeft();
    }

    /**
     * Returns this node's ID.
     *
     * @return this node's ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns true if this node is a token node.
     *
     * @return <code>true</code> if this node is a token node, <code>false</code> otherwise
     */
    public boolean isToken() {
        return this.isToken;
    }

    /**
     * Adds a node to this node's children and makes this node the given node's parent (for non-token nodes only).
     *
     * @param child the parse node that shall become this node's child
     *
     * @throws UnsupportedOperationException if this node is a token node
     */
    public ParseNode addChild(ParseNode child) {
        if (this.isToken) {
            throw new UnsupportedOperationException();
        }

        this.children.add(child);
        child.setParent(this);

        return child;
    }
}
/* Copyright Rupert Smith, 2005 to 2008, all rights reserved. */
package org.codehaus.mojo.simian;

import au.com.redhillconsulting.simian.SourceFile;

/**
 * BlockInstance provides information on a single occurrence of a repeated code block.
 *
 * <table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide the file in which the block occurs.
 * <tr><td> Provide the start and end location of the block.
 * <tr><td> Provide a flag to indicate if the block is contained within a larger block.
 * </table>
 *
 * @author Miguel Griffa
 * @author Rupert Smith
 */
public class BlockInstance extends BlockSummary
{
    /** The file in which the repeat block occurs. */
    private SourceFile sourcefile;

    /** The start line at which the repeat block occurs in the file. */
    private int startLine;

    /** The end line at which the repeat block occurs in the file. */
    private int endLine;

    /** A flag to indicate if the block is contained within a larger repeat block. */
    private boolean subsumed;

    /**
     * Creates a detailed report on a single occurrence of a repeat block.
     *
     * @param id         The unique id of the block.
     * @param size       The size of the block in lines.
     * @param sourcefile The file in which the block occurs.
     * @param startLine  The start line at which it occurs.
     * @param endLine    The end line at which it occurs.
     * @param subsumed   <tt>true</tt> if the block lies within a larger repeat block.
     */
    public BlockInstance(int id, int size, SourceFile sourcefile, int startLine, int endLine, boolean subsumed)
    {
        super(id, size);
        this.sourcefile = sourcefile;
        this.startLine = startLine;
        this.endLine = endLine;
        this.subsumed = subsumed;
    }

    /**
     * Provides the file in which the block lies.
     *
     * @return The file in which the block lies.
     */
    public SourceFile getSourcefile()
    {
        return sourcefile;
    }

    /**
     * Provides the blocks start line.
     *
     * @return The blocks start line.
     */
    public int getStartLine()
    {
        return startLine;
    }

    /**
     * Provides the blocks end line.
     *
     * @return The blocks end line.
     */
    public int getEndLine()
    {
        return endLine;
    }

    /**
     * Indicates whether of not the block lies within a larger block.
     *
     * @return <tt>true</tt> if the block lies within a larger block.
     */
    public boolean isSubsumed()
    {
        return subsumed;
    }
}

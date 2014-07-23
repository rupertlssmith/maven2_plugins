/* Copyright Rupert Smith, 2005 to 2008, all rights reserved. */
package org.codehaus.mojo.simian;

/**
 * BlockSummary summarizes the properties of a repeat block.
 *
 * <p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide a unique id to identify a repeat block.
 * <tr><td> Provide the size of the block.
 * </table>
 *
 * @author Rupert Smith
 */
public class BlockSummary
{
    /** The id of the block. */
    private int id;

    /** The size of the block. */
    private int size;

    /**
     * Creates a block summary with the specified id and size.
     *
     * @param id   The id of the block.
     * @param size The size of the block.
     */
    public BlockSummary(int id, int size)
    {
        this.id = id;
        this.size = size;
    }

    /**
     * Provides a unique id to identify the block by.
     *
     * @return A unique id to identify the block by.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Provides the size of the block.
     *
     * @return The size of the block.
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Compares this summary to another for equality by the block id.
     *
     * @param o The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is a block summary with the same id as this one.
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        BlockSummary that = (BlockSummary) o;

        return id == that.id;
    }

    /**
     * Implements a hash code compatible with the {@link #equals(Object)} method, that is, based on id alone.
     *
     * @return A hash code based on id alone.
     */
    public int hashCode()
    {
        return id;
    }
}
